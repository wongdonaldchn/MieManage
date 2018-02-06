package jp.co.tis.opal.web.ss113A;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import nablarch.core.date.SystemTimeUtil;
import nablarch.core.db.statement.ParameterizedSqlPStatement;
import nablarch.core.db.statement.SqlResultSet;
import nablarch.core.message.MessageLevel;
import nablarch.core.message.MessageUtil;
import nablarch.core.repository.SystemRepository;
import nablarch.fw.web.HttpErrorResponse;
import nablarch.fw.web.HttpResponse;

import jp.co.tis.opal.common.component.CM010001Component;
import jp.co.tis.opal.common.component.CM010004Component;
import jp.co.tis.opal.common.constants.OpalCodeConstants;
import jp.co.tis.opal.common.constants.OpalCodeConstants.MailDeliverType;
import jp.co.tis.opal.common.constants.OpalDefaultConstants;
import jp.co.tis.opal.common.utility.IdGeneratorUtil;
import jp.co.tis.opal.common.utility.MailAddressAuthUtil;
import jp.co.tis.opal.web.common.ResponseData.ResponseErrorData;
import jp.co.tis.opal.web.common.constants.CheckResultConstants;
import jp.co.tis.opal.web.common.rest.AbstractRestBaseAction;

/**
 * {@link A113A031Action} メールアドレス一時変更APIのアクションクラス。
 *
 * @author 唐
 * @since 1.0
 */
public class A113A031Action extends AbstractRestBaseAction<A113AACRRequestData> {

    /** API */
    private static final String API_SERVER_ID = "A113A031";

    /**
     * メールアドレス一時変更
     * <p>
     * 応答にJSONを使用する。
     * </p>
     *
     * @param requestData
     *            メールアドレス一時変更要求電文
     * @return HTTPレスポンス
     */
    @Consumes(MediaType.APPLICATION_JSON)
    @Valid
    public HttpResponse changeMailAddressTemp(A113AACRRequestData requestData) {
        HttpResponse responseData = super.execute(requestData);
        return responseData;
    }

    /**
     * 処理詳細ロジック実行
     *
     * @param requestData
     *            メールアドレス一時変更要求電文
     * @return 処理結果
     */
    @Override
    protected int executeLogic(A113AACRRequestData requestData) {
        // アプリ会員情報存在チェック
        Boolean aplMemInfoIsExist = checkAplMemInfoIsExist(requestData.getAplData().getApplicationMemberId());
        if (!aplMemInfoIsExist) {
            return CheckResultConstants.APL_MEM_DATA_ISNULL;
        }

        // メールアドレス重複チェック
        Boolean mailAddressIsExist = checkMailAddressIsExist(requestData.getAplData().getMailAddress());
        if (mailAddressIsExist) {
            return CheckResultConstants.MAIL_ADDRESS_EXIST;
        }

        // メールアドレス変更受付IDを採番する。(採番対象ID：1104)
        Long mailAddressChangeRctpId = IdGeneratorUtil.generateMailAddressChangeRcptId();

        // メールアドレス変更一時情報登録
        insertMailAddressChangeTempInfo(requestData, mailAddressChangeRctpId);

        // メールアドレス認証メール生成・送信依頼
        makeMailAddressAuthMail(mailAddressChangeRctpId, requestData);

        return CheckResultConstants.CHECK_OK;
    }

    /**
     * アプリ会員情報存在チェック
     *
     * @param applicationMemberId
     *            メールアドレス一時変更要求電文.アプリ会員ID
     *
     * @return 処理結果（存在する：true 存在しない：false）
     */
    private Boolean checkAplMemInfoIsExist(String applicationMemberId) {

        // 存在チェック用のSQLの条件を設定する
        Map<String, Object> condition = new HashMap<String, Object>();
        // アプリ会員ID
        condition.put("applicationMemberId", applicationMemberId);
        // アプリ会員状態コード（OP認証済みのアプリ会員）
        condition.put("statusA", OpalCodeConstants.AplMemStatusCode.OP_AUTH_APL_MEM);
        // アプリ会員状態コード（OP非会員)
        condition.put("statusD", OpalCodeConstants.AplMemStatusCode.NOT_OP_MEM);
        // アプリ会員情報TBL存在チェックを実行する。
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("SELECT_APL_MEM_INFO");
        SqlResultSet aplMemInfoResult = statement.retrieve(condition);
        // アプリ会員情報存在する場合
        if (!aplMemInfoResult.isEmpty()) {
            return true;
        }

        // アプリ会員情報存在しない場合
        return false;
    }

    /**
     * メールアドレス重複チェック
     *
     * @param mailAddress
     *            メールアドレス一時変更要求電文.メールアドレス
     *
     * @return 処理結果（存在する：true 存在しない：false）
     */
    private Boolean checkMailAddressIsExist(String mailAddress) {

        // 存在チェック用のSQLの条件を設定する
        Map<String, Object> condition = new HashMap<String, Object>();
        // メールアドレス
        condition.put("mailAddress", mailAddress);

        // アプリ会員情報TBLにメールアドレスの存在チェックを実行する。
        ParameterizedSqlPStatement aplMemStatement = getParameterizedSqlStatement("SELECT_APPLICATION_MEMBER_ID");
        SqlResultSet aplMemInfoResult = aplMemStatement.retrieve(condition);
        // アプリ会員情報存在する場合
        if (!aplMemInfoResult.isEmpty()) {
            return true;
        }

        // システム日時
        condition.put("sysDateTime", SystemTimeUtil.getTimestamp());
        // 処理区分
        condition.put("procseeDivision", OpalCodeConstants.MailAddressAuthProcessDivision.APL_MEM_REGIST);

        // アプリ会員一時情報TBLにメールアドレスの存在チェックを実行する。
        ParameterizedSqlPStatement aplMemTempStatement = getParameterizedSqlStatement("SELECT_APL_MEM_TEMP_INFO");
        SqlResultSet aplMemTempInfoResult = aplMemTempStatement.retrieve(condition);
        // アプリ会員一時情報存在する場合
        if (!aplMemTempInfoResult.isEmpty()) {
            return true;
        }

        // 処理区分
        condition.replace("procseeDivision", OpalCodeConstants.MailAddressAuthProcessDivision.MAIL_ADDRESS_CHANGE);
        // メールアドレス変更一時情報TBLにメールアドレスの存在チェックを実行する。
        ParameterizedSqlPStatement mailAddressChangeTempInfoStatement = getParameterizedSqlStatement(
                "SELECT_MAIL_ADDRESS_CHANGE_TEMP_INFO");
        SqlResultSet mailAddressChangeTempInfoResult = mailAddressChangeTempInfoStatement.retrieve(condition);
        // メールアドレス変更一時情報存在する場合
        if (!mailAddressChangeTempInfoResult.isEmpty()) {
            return true;
        }

        // メールアドレス存在しない場合
        return false;
    }

    /**
     * メールアドレス変更一時情報登録
     *
     * @param data
     *            メールアドレス一時変更要求電文
     * @param mailAddressChangeRctpId
     *            メールアドレス変更受付ID
     */
    private void insertMailAddressChangeTempInfo(A113AACRRequestData data, Long mailAddressChangeRctpId) {

        // 論理削除日を導出する。
        CM010004Component cm010004Component = new CM010004Component();
        String deletedDate = cm010004Component.getDeletedDateMonthly(
                Integer.parseInt(SystemRepository.get("mail_address_auth_info_retention_period_change")));

        // メールアドレス変更一時情報登録の項目内容を設定する。
        Map<String, Object> condition = new HashMap<String, Object>();
        // メールアドレス変更受付ID
        condition.put("mailAddressChangeRcptId", mailAddressChangeRctpId);
        // アプリ会員ID
        condition.put("applicationMemberId", data.getAplData().getApplicationMemberId());
        // メールアドレス
        condition.put("mailAddress", data.getAplData().getMailAddress());
        // 処理済フラグ
        condition.put("processedFlag", OpalDefaultConstants.PROCESSED_FLAG_0);
        // 登録者ID
        condition.put("insertUserId", API_SERVER_ID);
        // 登録日時
        condition.put("insertDateTime", SystemTimeUtil.getTimestamp());
        // 最終更新者
        condition.put("updateUserId", API_SERVER_ID);
        // 最終更新日時
        condition.put("updateDateTime", SystemTimeUtil.getTimestamp());
        // 削除フラグ(0:未削除)
        condition.put("deletedFlg", OpalDefaultConstants.DELETED_FLG_0);
        // 論理削除日
        condition.put("deletedDate", deletedDate);
        // 実行する。
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("INSERT_MAIL_ADDRESS_CHANGE_TEMP_INFO");
        statement.executeUpdateByMap(condition);
    }

    /**
     * メールアドレス認証メール生成・送信依頼
     *
     * @param mailAddressChangeRctpId
     *            メールアドレス変更受付ID
     * @param requestData
     *            メールアドレス一時変更要求電文
     */
    private void makeMailAddressAuthMail(Long mailAddressChangeRctpId, A113AACRRequestData requestData) {

        // UC000001：認証用ユーティリティ：salt生成
        String mailAddressSalt = MailAddressAuthUtil.getRandomString(OpalDefaultConstants.SALT_PARAM_20);

        // メールアドレス認証有効期限の算出（システム日時 + 有効期間）
        Calendar calendar = MailAddressAuthUtil
                .getExpiDate(Integer.parseInt(SystemRepository.get("expi_date_mail_address_temp")));

        // UC000001：認証用ユーティリティ：メールアドレス認証コード生成
        String mailAddressAuthCode = "";
        try {
            // メールアドレス認証有効期限（yyyyMMddHHmmssSSS）
            String mailAddressAuthDate = new SimpleDateFormat(OpalDefaultConstants.TIME_FORMAT)
                    .format(calendar.getTime());
            mailAddressAuthCode = MailAddressAuthUtil.getMailAddressAuthCode(mailAddressChangeRctpId,
                    mailAddressAuthDate, mailAddressSalt);
        } catch (NoSuchAlgorithmException e) {
            throw new HttpErrorResponse(500, e);
        }

        // 論理削除日の算出
        CM010004Component cm010004Component = new CM010004Component();
        String deletedDate = cm010004Component.getDeletedDateMonthly(
                Integer.parseInt(SystemRepository.get("mail_address_auth_info_retention_period_change")));

        // メールアドレス認証有効期限（yyyy-MM-dd HH:mm:ss.SSS）
        String mailAddressDate = new SimpleDateFormat(OpalDefaultConstants.DATE_TIME_MILLISECOND_FORMAT)
                .format(calendar.getTime());
        // メールアドレス認証情報の登録
        insertMailAddressAuthInfo(mailAddressAuthCode, mailAddressChangeRctpId, mailAddressSalt, mailAddressDate,
                requestData.getAplData().getMailAddress(), deletedDate);

        // メールアドレス変更用URLの生成
        StringBuffer reregistUseUrl = new StringBuffer(
                SystemRepository.get("updateMailAddressUrlParameter.update_mail_address_url"));
        reregistUseUrl.append(mailAddressAuthCode);

        // メールアドレス一時変更要求電文.アプリ会員ID
        Long applicationMemberId = Long.valueOf(requestData.getAplData().getApplicationMemberId());
        // メールアドレス一時変更要求電文.メールアドレス
        String mailAddress = requestData.getAplData().getMailAddress();
        // 差し込み項目
        List<String> variableItemValues = new ArrayList<String>();
        variableItemValues.add(reregistUseUrl.toString());

        CM010001Component cm010001Component = new CM010001Component();
        // メールアドレス認証メールの生成・送信
        cm010001Component.insMailLiteDeliverInfo(applicationMemberId, API_SERVER_ID, mailAddress,
                MailDeliverType.MAIL_DELIVER_TYPE_1, OpalDefaultConstants.MAIL_TEMP_MAIL_ADDRESS_UPDATE_CONFIRM,
                variableItemValues, null);
    }

    /**
     * メールアドレス認証情報の登録
     *
     * @param mailAddressAuthCode
     *            メールアドレス認証コード
     * @param mailAddressChangeRctpId
     *            メールアドレス変更受付ID(採番)
     * @param mailAddressSalt
     *            メールアドレスSALT
     * @param mailAddressDate
     *            メールアドレス認証有効期限
     * @param mailAddress
     *            メールアドレス
     * @param deletedDate
     *            論理削除日
     */
    private void insertMailAddressAuthInfo(String mailAddressAuthCode, Long mailAddressChangeRctpId,
            String mailAddressSalt, String mailAddressDate, String mailAddress, String deletedDate) {

        // メールアドレス認証情報登録の項目内容を設定する。
        Map<String, Object> condition = new HashMap<String, Object>();
        // メールアドレス認証コード
        condition.put("mailAddressAuthCode", mailAddressAuthCode);
        // メールアドレス認証キー
        condition.put("mailAddressAuthKey", mailAddressChangeRctpId);
        // メールアドレス認証SALT
        condition.put("mailAddressAuthSalt", mailAddressSalt);
        // メールアドレス認証有効期限
        condition.put("mailAddressAuthExpiDate", Timestamp.valueOf(mailAddressDate));
        // 処理区分
        condition.put("processDivision", OpalCodeConstants.MailAddressAuthProcessDivision.MAIL_ADDRESS_CHANGE);
        // メールアドレス
        condition.put("mailAddress", mailAddress);
        // 処理済フラグ
        condition.put("processedFlag", OpalDefaultConstants.PROCESSED_FLAG_0);
        // 登録者ID
        condition.put("insertUserId", API_SERVER_ID);
        // 登録日時
        condition.put("insertDateTime", SystemTimeUtil.getTimestamp());
        // 最終更新者
        condition.put("updateUserId", API_SERVER_ID);
        // 最終更新日時
        condition.put("updateDateTime", SystemTimeUtil.getTimestamp());
        // 削除フラグ(0:未削除)
        condition.put("deletedFlg", OpalDefaultConstants.DELETED_FLG_0);
        // 論理削除日
        condition.put("deletedDate", deletedDate);
        // バージョン番号
        condition.put("version", OpalDefaultConstants.VERSION);
        // 実行する。
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("INSERT_MAIL_ADDRESS_AUTH_INFO");
        statement.executeUpdateByMap(condition);
    }

    /**
     * HTTPレスポンスを設定する。
     *
     * @param requestData
     *            メールアドレス一時変更要求電文
     *
     * @param result
     *            メールアドレス一時変更要求電文チェック結果
     *
     * @return HTTPレスポンス
     * @throws IOException
     *             IO異常
     */
    @Override
    protected HttpResponse responseBuilder(A113AACRRequestData requestData, int result) throws IOException {
        // メールアドレス一時変更結果応答電文設定
        A113AACSResponseData responseData = new A113AACSResponseData();
        // 応答電文.処理結果コード
        responseData.setResultCode(super.getResultCode(result));
        if (result == CheckResultConstants.APL_MEM_DATA_ISNULL) {
            // アプリ会員情報TBLに該当アプリ会員IDのデータが存在しない場合
            ResponseErrorData error = new ResponseErrorData();
            error.setId("MA113A0301");
            error.setMessage(MessageUtil.createMessage(MessageLevel.ERROR, "MA113A0301").formatMessage());
            // 応答電文.エラーメッセージ
            responseData.setError(error);
        } else if (result == CheckResultConstants.MAIL_ADDRESS_EXIST) {
            // メールアドレス一時変更要求電文.メールアドレスが存在する場合
            ResponseErrorData error = new ResponseErrorData();
            error.setId("MA113A0302");
            error.setMessage(MessageUtil.createMessage(MessageLevel.ERROR, "MA113A0302").formatMessage());
            // 応答電文.エラーメッセージ
            responseData.setError(error);
        }

        HttpResponse response = super.setHttpResponseData(responseData, result);

        return response;
    }
}
