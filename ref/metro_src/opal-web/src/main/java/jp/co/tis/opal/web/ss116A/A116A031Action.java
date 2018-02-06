package jp.co.tis.opal.web.ss116A;

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
 * A116A03:ログインID・パスワード一時登録APIのアクションクラス。
 *
 * @author 唐
 * @since 1.0
 */
public class A116A031Action extends AbstractRestBaseAction<A116AACRRequestData> {

    /** API */
    private static final String API_SERVER_ID = "A116A031";

    /** ストレッチング回数_ログインID・パスワード再登録 */
    private static final String STRETCHING_TIMES = "stretching_times_reregist";

    /** メールアドレス認証情報データ保持期間_ログインID・パスワード再登録 */
    private static final String MAIL_ADDRESS_AUTH_INFO = "mail_address_auth_info_retention_period_reregist";

    /** 有効期間_ログインID・パスワード一時登録 */
    private static final String EXPI_DATE_LOGIN_TEMP = "expi_date_login_temp";

    /** ログインID・パスワード再登録用URL */
    private static final String REREGIST_USE_URL = "reregistUseUrlParameter.reregist_use_url";

    /**
     * ログインID・パスワード一時登録API
     *
     * @param requestData
     *            ログインID・パスワード一時登録要求電文
     * @return HTTPレスポンス
     */
    @Consumes(MediaType.APPLICATION_JSON)
    @Valid
    public HttpResponse registLoginIdPasswordTemp(A116AACRRequestData requestData) {
        HttpResponse responseData = super.execute(requestData);
        return responseData;
    }

    /**
     * ログインID・パスワード一時登録
     *
     * @param requestData
     *            ログインID・パスワード一時登録要求電文
     * @return 処理結果
     */
    @Override
    protected int executeLogic(A116AACRRequestData requestData) {
        // アプリ会員情報取得
        SqlResultSet aplMemInfo = getAplMemInfo(Long.valueOf(requestData.getAplData().getApplicationMemberId()));
        // アプリ会員情報存在しない場合
        if (aplMemInfo.isEmpty()) {
            return CheckResultConstants.APL_MEM_DATA_ISNULL;
        }

        // ログインID存在チェック
        Boolean loginIdIsExist = checkLoginIdIsExist(Long.valueOf(requestData.getAplData().getApplicationMemberId()),
                requestData.getAplData().getLoginId());
        // ログインID存在の場合
        if (loginIdIsExist) {
            return CheckResultConstants.LOGIN_ID_EXIST;
        }

        // 配信可否チェック
        if (!OpalCodeConstants.MailDeliverStatusDivision.MAIL_DELIVER_STATUS_DIVISION_0
                .equals(aplMemInfo.get(0).getString("MAIL_DELIVER_STATUS_DIVISION"))) {
            return CheckResultConstants.MAIL_DELIVER_STATUS_ISNOT_ZERO;
        }

        // ログイン情報再登録受付IDを採番する。(採番対象ID：1103)
        Long loginReregistId = IdGeneratorUtil.generateLoginInfoReregistRcptId();

        // 再登録用パスワードハッシュ化処理を行い、ログイン情報再登録一時情報を登録する
        insertLoginTempInfoByPassword(loginReregistId, requestData);

        // メールアドレス認証メール生成・送信依頼
        makeMailAddressAuthMail(loginReregistId, requestData, aplMemInfo.get(0).getString("MAIL_ADDRESS"));

        return CheckResultConstants.CHECK_OK;
    }

    /**
     * アプリ会員情報取得
     *
     * @param applicationMemberId
     *            アプリ会員ID
     * @return アプリ会員情報
     */
    private SqlResultSet getAplMemInfo(Long applicationMemberId) {
        Map<String, Object> condition = new HashMap<String, Object>();
        // アプリ会員情報TBL.アプリ会員ID = ログインID・パスワード一時登録要求電文.アプリ会員ID
        condition.put("applicationMemberId", applicationMemberId);
        // アプリ会員状態コード.OP認証済みのアプリ会員
        condition.put("statusA", OpalCodeConstants.AplMemStatusCode.OP_AUTH_APL_MEM);
        // アプリ会員状態コード.OP非会員
        condition.put("statusD", OpalCodeConstants.AplMemStatusCode.NOT_OP_MEM);
        // 実行する。
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("SELECT_APL_MEM_INFO");
        SqlResultSet result = statement.retrieve(condition);
        return result;
    }

    /**
     * ログインID存在チェック
     *
     * @param applicationMemberId
     *            アプリ会員ID
     * @param loginId
     *            ログインID
     * @return 処理結果（存在する：true 存在しない：false）
     */
    private Boolean checkLoginIdIsExist(Long applicationMemberId, String loginId) {
        // アプリ会員情報TBLに存在しないことをチェックする
        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("applicationMemberId", applicationMemberId);
        condition.put("loginId", loginId);
        // 実行する。
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("SELECT_APL_MEM_INFO_LOGIN_ID");
        SqlResultSet result = statement.retrieve(condition);
        // ログインIDがアプリ会員情報TBLに存在する場合
        if (!result.isEmpty()) {
            return true;
        }

        // アプリ会員一時情報TBLに存在しないことをチェックする
        Map<String, Object> conditionTemp = new HashMap<String, Object>();
        // ログインID
        conditionTemp.put("loginId", loginId);
        // システム日時
        conditionTemp.put("sysDateTime", SystemTimeUtil.getTimestamp());
        // 処理区分
        conditionTemp.put("procseeDivision", OpalCodeConstants.MailAddressAuthProcessDivision.APL_MEM_REGIST);
        // 実行する。
        ParameterizedSqlPStatement statementTemp = getParameterizedSqlStatement("SELECT_APL_MEM_TEMP_INFO");
        SqlResultSet resultTemp = statementTemp.retrieve(conditionTemp);
        // ログインIDがアプリ会員一時情報TBLに存在する場合
        if (!resultTemp.isEmpty()) {
            return true;
        }

        // 処理区分
        conditionTemp.replace("procseeDivision",
                OpalCodeConstants.MailAddressAuthProcessDivision.LOGIN_ID_PASSWORD_REREGIST);
        // ログイン情報再登録一時情報TBLに存在しないことをチェックする
        // 実行する。
        ParameterizedSqlPStatement statementLogin = getParameterizedSqlStatement(
                "SELECT_LOGIN_INFO_REREGIST_TEMP_INFO");
        SqlResultSet resultLogin = statementLogin.retrieve(conditionTemp);
        // ログインIDがログイン情報再登録一時情報TBLに存在する場合
        if (!resultLogin.isEmpty()) {
            return true;
        }

        return false;
    }

    /**
     * 再登録用パスワードハッシュ化処理を行い、ログイン情報再登録一時情報の登録処理
     *
     * @param loginReregistId
     *            ログイン情報再登録受付ID(採番)
     *
     * @param requestData
     *            ログインID・パスワード一時登録要求電文
     */
    private void insertLoginTempInfoByPassword(Long loginReregistId, A116AACRRequestData requestData) {

        // 論理削除日の算出
        CM010004Component cm010004Component = new CM010004Component();
        String deletedDate = cm010004Component
                .getDeletedDateMonthly(Integer.parseInt(SystemRepository.get(MAIL_ADDRESS_AUTH_INFO)));

        // ストレッチング回数取得
        int stretchingTimes = Integer.parseInt(SystemRepository.get(STRETCHING_TIMES));

        // パスワードSALT取得
        String passwordSalt = MailAddressAuthUtil.getRandomString(OpalDefaultConstants.SALT_PARAM_20);

        // パスワードハッシュ化（不可逆）
        String passwordAuthCode;
        try {
            passwordAuthCode = MailAddressAuthUtil.getPasswordAuthCode(requestData.getAplData().getPassword(),
                    passwordSalt, stretchingTimes);
        } catch (NoSuchAlgorithmException e) {
            throw new HttpErrorResponse(500, e);
        }
        // ログイン情報再登録一時情報登録
        insertLoginTempInfo(loginReregistId, requestData, stretchingTimes, passwordSalt, passwordAuthCode, deletedDate);

    }

    /**
     * ログイン情報再登録一時情報登録処理
     *
     * @param loginReregistId
     *            ログイン情報再登録受付ID(採番)
     * @param requestData
     *            ログインID・パスワード一時登録要求電文
     * @param stretchingTimes
     *            ストレッチング回数
     * @param passwordSalt
     *            パスワードSALT
     * @param passwordAuthCode
     *            ハッシュ化されたパスワード
     * @param deletedDate
     *            論理削除日
     */
    private void insertLoginTempInfo(Long loginReregistId, A116AACRRequestData requestData, int stretchingTimes,
            String passwordSalt, String passwordAuthCode, String deletedDate) {

        // 新しいログイン情報をログイン情報再登録一時情報TBLに登録する。
        // ログイン情報再登録一時情報登録の項目内容を設定する。
        Map<String, Object> condition = new HashMap<String, Object>();
        // ログイン情報再登録受付ID
        condition.put("loginInfoReregistRcptId", loginReregistId);
        // アプリ会員ID
        condition.put("applicationMemberId", requestData.getAplData().getApplicationMemberId());
        // ログインID
        condition.put("loginId", requestData.getAplData().getLoginId());
        // パスワード
        condition.put("password", passwordAuthCode);
        // パスワードSALT
        condition.put("passwordSalt", passwordSalt);
        // ストレッチング回数
        condition.put("stretchingTimes", stretchingTimes);
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
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("INSERT_LOGIN_INFO_REREGIST_TEMP_INFO");
        statement.executeUpdateByMap(condition);
    }

    /**
     * メールアドレス認証メール生成・送信依頼
     *
     * @param loginReregistId
     *            ログイン情報再登録受付ID(採番)
     * @param requestData
     *            ログインID・パスワード一時登録要求電文
     * @param mailAddress
     *            メールアドレス
     */
    private void makeMailAddressAuthMail(Long loginReregistId, A116AACRRequestData requestData, String mailAddress) {

        // メールアドレス認証コードの生成
        // 認証用ユーティリティ：salt生成
        String mailAddressSalt = MailAddressAuthUtil.getRandomString(OpalDefaultConstants.SALT_PARAM_20);

        // メールアドレス認証有効期限の算出（システム日時 + 有効期間）
        Calendar calendar = MailAddressAuthUtil
                .getExpiDate(Integer.parseInt(SystemRepository.get(EXPI_DATE_LOGIN_TEMP)));

        // 認証用ユーティリティ：メールアドレス認証コード生成
        String mailAuthCode = "";
        try {
            // メールアドレス認証有効期限（yyyyMMddHHmmssSSS）
            String mailAddressAuthDate = new SimpleDateFormat(OpalDefaultConstants.TIME_FORMAT)
                    .format(calendar.getTime());
            mailAuthCode = MailAddressAuthUtil.getMailAddressAuthCode(loginReregistId, mailAddressAuthDate,
                    mailAddressSalt);
        } catch (NoSuchAlgorithmException e) {
            throw new HttpErrorResponse(500, e);
        }

        // 論理削除日の算出
        CM010004Component cm010004Component = new CM010004Component();
        String deletedDate = cm010004Component
                .getDeletedDateMonthly(Integer.parseInt(SystemRepository.get(MAIL_ADDRESS_AUTH_INFO)));

        // メールアドレス認証有効期限（yyyy-MM-dd HH:mm:ss.SSS）
        String mailAddressDate = new SimpleDateFormat(OpalDefaultConstants.DATE_TIME_MILLISECOND_FORMAT)
                .format(calendar.getTime());
        // メールアドレス認証情報の登録
        insertMailAddressAuthInfo(mailAuthCode, loginReregistId, mailAddressSalt, mailAddressDate, mailAddress,
                deletedDate);

        // ログインID・パスワード再登録用URLの生成
        StringBuffer reregistUseUrl = new StringBuffer(SystemRepository.get(REREGIST_USE_URL));
        // URL＋メールアドレス認証コード
        reregistUseUrl.append(mailAuthCode);

        // メールアドレス認証メールの生成・送信
        insMailLiteDeliverInfo(requestData, reregistUseUrl, mailAddress);
    }

    /**
     * メールアドレス認証情報の登録
     *
     * @param mailAuthCode
     *            メールアドレス認証コード
     * @param loginReregistId
     *            ログイン情報再登録受付ID(採番)
     * @param mailAddressSalt
     *            メールアドレスSALT
     * @param mailAddressDate
     *            メールアドレス認証有効期限
     * @param mailAddress
     *            メールアドレス
     * @param deletedDate
     *            論理削除日
     */
    private void insertMailAddressAuthInfo(String mailAuthCode, Long loginReregistId, String mailAddressSalt,
            String mailAddressDate, String mailAddress, String deletedDate) {

        // メールアドレス認証情報登録の項目内容を設定する。
        Map<String, Object> condition = new HashMap<String, Object>();
        // メールアドレス認証コード
        condition.put("mailAddressAuthCode", mailAuthCode);
        // メールアドレス認証キー
        condition.put("mailAddressAuthKey", loginReregistId);
        // メールアドレス認証SALT
        condition.put("mailAddressAuthSalt", mailAddressSalt);
        // メールアドレス認証有効期限
        condition.put("mailAddressAuthExpiDate", Timestamp.valueOf(mailAddressDate));
        // 処理区分
        condition.put("processDivision", OpalCodeConstants.MailAddressAuthProcessDivision.LOGIN_ID_PASSWORD_REREGIST);
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
     * メールアドレス認証メールの生成・送信
     *
     * @param requestData
     *            ログインID・パスワード一時登録要求電文
     * @param reregistUseUrl
     *            ログインID・パスワード再登録用URL
     * @param mailAddress
     *            メールアドレス
     */
    private void insMailLiteDeliverInfo(A116AACRRequestData requestData, StringBuffer reregistUseUrl,
            String mailAddress) {

        // ログインID・パスワード一時登録要求電文.アプリ会員ID
        Long applicationMemberId = Long.valueOf(requestData.getAplData().getApplicationMemberId());

        // 差し込み項目（空配列）
        List<String> variableItemValues = new ArrayList<String>();
        variableItemValues.add(reregistUseUrl.toString());

        CM010001Component cm010001Component = new CM010001Component();
        // メールアドレス認証メールの生成・送信
        cm010001Component.insMailLiteDeliverInfo(applicationMemberId, API_SERVER_ID, mailAddress,
                MailDeliverType.MAIL_DELIVER_TYPE_1, OpalDefaultConstants.MAIL_TEMP_LOGIN_REREGIST_CONFIRM,
                variableItemValues, null);
    }

    /**
     * HTTPレスポンスを設定する。
     *
     * @param requestData
     *            ログインID・パスワード一時登録要求電文
     *
     * @param result
     *            チェックの結果
     *
     * @return HTTPレスポンス
     * @throws IOException
     *             IO異常
     */
    @Override
    protected HttpResponse responseBuilder(A116AACRRequestData requestData, int result) throws IOException {

        // 応答電文設定
        A116AACSResponseData responseData = new A116AACSResponseData();

        // 応答電文.処理結果コード
        responseData.setResultCode(getResultCode(result));
        // 無効なアプリ会員エラー
        if (result == CheckResultConstants.APL_MEM_DATA_ISNULL) {
            // 応答電文.エラーメッセージ
            ResponseErrorData error = new ResponseErrorData();
            error.setId("MA116A0301");
            error.setMessage(MessageUtil.createMessage(MessageLevel.ERROR, "MA116A0301").formatMessage());
            responseData.setError(error);
        } else if (result == CheckResultConstants.LOGIN_ID_EXIST) {
            // ログインID重複エラー
            ResponseErrorData error = new ResponseErrorData();
            error.setId("MA116A0302");
            error.setMessage(MessageUtil.createMessage(MessageLevel.ERROR, "MA116A0302").formatMessage());
            responseData.setError(error);
        } else if (result == CheckResultConstants.MAIL_DELIVER_STATUS_ISNOT_ZERO) {
            // メール配信不可
            ResponseErrorData error = new ResponseErrorData();
            error.setId("MA116A0303");
            error.setMessage(MessageUtil.createMessage(MessageLevel.ERROR, "MA116A0303").formatMessage());
            responseData.setError(error);
        }
        HttpResponse response = super.setHttpResponseData(responseData, result);
        return response;
    }
}
