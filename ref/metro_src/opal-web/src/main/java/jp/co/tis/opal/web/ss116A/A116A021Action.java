package jp.co.tis.opal.web.ss116A;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import nablarch.common.dao.EntityList;
import nablarch.common.dao.UniversalDao;
import nablarch.core.date.SystemTimeUtil;
import nablarch.core.db.statement.ParameterizedSqlPStatement;
import nablarch.core.db.statement.SqlResultSet;
import nablarch.core.db.statement.SqlRow;
import nablarch.core.message.MessageLevel;
import nablarch.core.message.MessageUtil;
import nablarch.fw.web.HttpResponse;

import jp.co.tis.opal.common.component.CM010001Component;
import jp.co.tis.opal.common.constants.OpalCodeConstants;
import jp.co.tis.opal.common.constants.OpalCodeConstants.MailDeliverType;
import jp.co.tis.opal.common.constants.OpalDefaultConstants;
import jp.co.tis.opal.common.entity.MailAddressAuthInfoEntity;
import jp.co.tis.opal.web.common.ResponseData.ResponseErrorData;
import jp.co.tis.opal.web.common.constants.CheckResultConstants;
import jp.co.tis.opal.web.common.rest.AbstractRestBaseAction;

/**
 * A116A02:ログインID・パスワード再登録APIのアクションクラス。
 *
 * @author 唐
 * @since 1.0
 */
public class A116A021Action extends AbstractRestBaseAction<A116AABRRequestData> {

    /** API */
    private static final String API_SERVER_ID = "A116A021";

    /**
     * ログインID・パスワード再登録API
     *
     * @param requestData
     *            ログインID・パスワード再登録要求電文
     * @return HTTPレスポンス
     */
    @Consumes(MediaType.APPLICATION_JSON)
    @Valid
    public HttpResponse reregistLoginIdPassword(A116AABRRequestData requestData) {
        HttpResponse responseData = super.execute(requestData);
        return responseData;
    }

    /**
     * ログインID・パスワード再登録
     *
     * @param requestData
     *            ログインID・パスワード再登録要求電文
     * @return 処理結果
     */
    @Override
    protected int executeLogic(A116AABRRequestData requestData) {

        // ログインID・パスワード再登録認証情報取得
        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("mailAddressAuthCode", requestData.getAplData().getLoginIdPasswordReregistAuthCode());
        condition.put("processDivision", OpalCodeConstants.MailAddressAuthProcessDivision.LOGIN_ID_PASSWORD_REREGIST);
        EntityList<MailAddressAuthInfoEntity> mailAddressAuthInfo = UniversalDao
                .findAllBySqlFile(MailAddressAuthInfoEntity.class, "SELECT_MAIL_ADDRESS_AUTH_INFO_LOGIN", condition);

        // 該当メールアドレス認証情報が存在しない場合
        if (mailAddressAuthInfo.isEmpty()) {
            return CheckResultConstants.MAIL_ADDRESS_AUTH_NULL;
        }

        // 認証済チェック
        if (OpalDefaultConstants.PROCESSED_FLAG_1.equals(mailAddressAuthInfo.get(0).getProcessedFlag())) {
            return CheckResultConstants.PROCESSED_COMPLETED;
        }

        // メールアドレス認証有効期限確認
        if (SystemTimeUtil.getTimestamp().compareTo(mailAddressAuthInfo.get(0).getMailAddressAuthExpiDate()) > 0) {
            return CheckResultConstants.MAIL_ADDRESS_AUTH_DATE_ERROR;
        }

        // ログイン情報再登録一時情報取得
        SqlResultSet loginTempInfo = getLoginTempInfo(mailAddressAuthInfo.get(0));

        // データが存在しない場合
        if (loginTempInfo.isEmpty()) {
            return CheckResultConstants.APL_MEM_DATA_ISNULL;
        }

        // アプリ会員情報更新（ログインID・パスワード再登録）
        int updateAplMemInfoCount = updateAplMemInfo(loginTempInfo.get(0));

        // 更新件数は0件の場合
        if (updateAplMemInfoCount == 0) {
            return CheckResultConstants.APL_MEM_DATA_ISNULL;
        }

        // ログイン情報再登録一時情報更新
        updateLoginTempInfo(mailAddressAuthInfo.get(0));

        // メールアドレス認証情報更新
        updateMailAddressAuthInfo(mailAddressAuthInfo.get(0));

        // 配信可否チェック
        if (!OpalCodeConstants.MailDeliverStatusDivision.MAIL_DELIVER_STATUS_DIVISION_0
                .equals(loginTempInfo.get(0).getString("MAIL_DELIVER_STATUS_DIVISION"))) {
            String message = MessageUtil.createMessage(MessageLevel.WARN, "MA116A0205").formatMessage();
            LOGGER.logWarn(message);

        } else {
            // ログインID・パスワード再登録完了のお知らせメール送信
            insMailLiteDeliverInfo(loginTempInfo.get(0).getLong("APPLICATION_MEMBER_ID"),
                    loginTempInfo.get(0).getString("MAIL_ADDRESS"));
        }
        return CheckResultConstants.CHECK_OK;
    }

    /**
     * ログインID・パスワード再登録完了のお知らせメール送信
     *
     * @param applicationMemberId
     *            アプリ会員ID
     * @param mailAddress
     *            メールアドレス
     */
    private void insMailLiteDeliverInfo(Long applicationMemberId, String mailAddress) {

        // 差し込み項目（空配列）
        List<String> variableItemValues = new ArrayList<String>();

        CM010001Component cm010001Component = new CM010001Component();
        // ログインID・パスワード再登録完了のお知らせメール送信
        cm010001Component.insMailLiteDeliverInfo(applicationMemberId, API_SERVER_ID, mailAddress,
                MailDeliverType.MAIL_DELIVER_TYPE_1, OpalDefaultConstants.MAIL_TEMP_LOGIN_REREGISTED_NOTICE,
                variableItemValues, null);
    }

    /**
     * ログイン情報再登録一時情報取得
     *
     * @param mailAddressAuthInfo
     *            メールアドレス認証情報
     *
     * @return ログイン情報再登録一時情報
     */
    private SqlResultSet getLoginTempInfo(MailAddressAuthInfoEntity mailAddressAuthInfo) {
        Map<String, Object> condition = new HashMap<String, Object>();
        // ログイン情報再登録一時情報TBL.ログイン情報再登録受付ID = 取得されたメールアドレス認証キー
        condition.put("loginInfoReregistRcptId", mailAddressAuthInfo.getMailAddressAuthKey());
        // アプリ会員状態コード.OP認証済みのアプリ会員
        condition.put("statusA", OpalCodeConstants.AplMemStatusCode.OP_AUTH_APL_MEM);
        // アプリ会員状態コード.OP非会員
        condition.put("statusD", OpalCodeConstants.AplMemStatusCode.NOT_OP_MEM);

        // 実行する。
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("SELECT_LOGIN_INFO_REREGIST_TEMP_INFO");
        SqlResultSet result = statement.retrieve(condition);
        return result;
    }

    /**
     * アプリ会員情報更新（ログインID・パスワード再登録）
     *
     * @param loginTempInfo
     *            ログイン情報再登録一時情報
     * @return 更新件数
     */
    private int updateAplMemInfo(SqlRow loginTempInfo) {

        Map<String, Object> condition = new HashMap<String, Object>();
        // ログインID
        condition.put("loginId", loginTempInfo.getString("LOGIN_ID"));
        // パスワード
        condition.put("password", loginTempInfo.getString("PASSWORD"));
        // パスワードSALT
        condition.put("passwordSalt", loginTempInfo.getString("PASSWORD_SALT"));
        // ストレッチング回数
        condition.put("stretchingTimes", loginTempInfo.getString("STRETCHING_TIMES"));
        // 最終更新者ID
        condition.put("updateUserId", API_SERVER_ID);
        // 最終更新日時
        condition.put("updateDateTime", SystemTimeUtil.getTimestamp());
        // アプリ会員ID
        condition.put("applicationMemberId", loginTempInfo.getLong("APPLICATION_MEMBER_ID"));
        // アプリ会員状態コード.OP認証済みのアプリ会員
        condition.put("statusA", OpalCodeConstants.AplMemStatusCode.OP_AUTH_APL_MEM);
        // アプリ会員状態コード.OP非会員
        condition.put("statusD", OpalCodeConstants.AplMemStatusCode.NOT_OP_MEM);
        // 削除フラグ
        condition.put("deletedFlg", OpalDefaultConstants.DELETED_FLG_0);
        // 実行する。
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("UPDATE_APL_MEM_INFO");
        return statement.executeUpdateByMap(condition);

    }

    /**
     * ログイン情報再登録一時情報更新
     *
     * @param mailAddressAuthInfo
     *            メールアドレス認証情報
     */
    private void updateLoginTempInfo(MailAddressAuthInfoEntity mailAddressAuthInfo) {
        Map<String, Object> condition = new HashMap<String, Object>();
        // ログイン情報再登録一時情報TBL.ログイン情報再登録受付ID = 取得したメールアドレス認証キー
        condition.put("loginInfoReregistRcptId", mailAddressAuthInfo.getMailAddressAuthKey());
        // 処理済フラグ
        condition.put("processedFlag", OpalDefaultConstants.PROCESSED_FLAG_1);
        // 最終更新者ID
        condition.put("updateUserId", API_SERVER_ID);
        // 最終更新日時
        condition.put("updateDateTime", SystemTimeUtil.getTimestamp());
        // 実行する。
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("UPDATE_LOGIN_INFO_REREGIST_TEMP_INFO");
        statement.executeUpdateByMap(condition);

    }

    /**
     * メールアドレス認証情報更新
     *
     * @param mailAddressAuthInfo
     *            メールアドレス認証情報
     */
    private void updateMailAddressAuthInfo(MailAddressAuthInfoEntity mailAddressAuthInfo) {
        // 処理済フラグ
        mailAddressAuthInfo.setProcessedFlag(OpalDefaultConstants.PROCESSED_FLAG_1);
        // 最終更新者ID
        mailAddressAuthInfo.setUpdateUserId(API_SERVER_ID);
        // 最終更新日時
        mailAddressAuthInfo.setUpdateDateTime(SystemTimeUtil.getTimestamp());
        // メールアドレス認証情報更新
        UniversalDao.update(mailAddressAuthInfo);
    }

    /**
     * HTTPレスポンスを設定する。
     *
     * @param requestData
     *            ログインID・パスワード再登録要求電文
     *
     * @param result
     *            チェックの結果
     *
     * @return HTTPレスポンス
     * @throws IOException
     *             異常
     */
    @Override
    protected HttpResponse responseBuilder(A116AABRRequestData requestData, int result) throws IOException {

        // 応答電文設定
        A116AABSResponseData responseData = new A116AABSResponseData();

        // 応答電文.処理結果コード
        responseData.setResultCode(getResultCode(result));
        // メールアドレス認証情報が存在しない場合
        if (result == CheckResultConstants.MAIL_ADDRESS_AUTH_NULL) {
            // 応答電文.エラーメッセージ
            ResponseErrorData error = new ResponseErrorData();
            error.setId("MA116A0201");
            error.setMessage(MessageUtil.createMessage(MessageLevel.ERROR, "MA116A0201").formatMessage());
            responseData.setError(error);
        } else if (result == CheckResultConstants.PROCESSED_COMPLETED) {
            // 認証済の場合
            ResponseErrorData error = new ResponseErrorData();
            error.setId("MA116A0202");
            error.setMessage(MessageUtil.createMessage(MessageLevel.ERROR, "MA116A0202").formatMessage());
            responseData.setError(error);
        } else if (result == CheckResultConstants.MAIL_ADDRESS_AUTH_DATE_ERROR) {
            // システム日時がメールアドレス認証有効期限を超える場合
            ResponseErrorData error = new ResponseErrorData();
            error.setId("MA116A0203");
            error.setMessage(MessageUtil.createMessage(MessageLevel.ERROR, "MA116A0203").formatMessage());
            responseData.setError(error);
        } else if (result == CheckResultConstants.APL_MEM_DATA_ISNULL) {
            // アプリ会員情報存在しないエラー
            ResponseErrorData error = new ResponseErrorData();
            error.setId("MA116A0204");
            error.setMessage(MessageUtil.createMessage(MessageLevel.ERROR, "MA116A0204").formatMessage());
            responseData.setError(error);
        }
        HttpResponse response = super.setHttpResponseData(responseData, result);
        return response;
    }
}
