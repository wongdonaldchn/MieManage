package jp.co.tis.opal.web.ss113A;

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
 * {@link A113A041Action} メールアドレス変更APIのアクションクラス。
 *
 * @author 唐
 * @since 1.0
 */
public class A113A041Action extends AbstractRestBaseAction<A113AADRRequestData> {

    /** API */
    private static final String API_SERVER_ID = "A113A041";

    /**
     * メールアドレス変更処理。
     * <p>
     * 応答にJSONを使用する。
     * </p>
     *
     * @param requestData
     *            メールアドレス変更要求電文
     * @return HTTPレスポンス
     */
    @Consumes(MediaType.APPLICATION_JSON)
    @Valid
    public HttpResponse changeMailAddress(A113AADRRequestData requestData) {
        HttpResponse responseData = super.execute(requestData);
        return responseData;
    }

    /**
     * 処理詳細ロジック実行
     *
     * @param requestData
     *            メールアドレス変更要求電文
     * @return 処理結果
     */
    @Override
    protected int executeLogic(A113AADRRequestData requestData) {
        // メールアドレス認証情報取得
        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("mailAddressAuthCode", requestData.getAplData().getMailAddressChangeAuthCode());
        condition.put("processDivision", OpalCodeConstants.MailAddressAuthProcessDivision.MAIL_ADDRESS_CHANGE);
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

        // メールアドレス変更一時情報取得
        SqlResultSet mailAddressChangeTempInfo = getMailAddressChangeTempInfo(
                mailAddressAuthInfo.get(0).getMailAddressAuthKey());

        // データが存在しない場合
        if (mailAddressChangeTempInfo.isEmpty()) {
            return CheckResultConstants.APL_MEM_DATA_ISNULL;
        }

        // アプリ会員情報更新（メールアドレス変更）
        int updateAplMemCount = updateAplMemInfo(mailAddressChangeTempInfo.get(0));
        // 更新件数は0件の場合
        if (updateAplMemCount == 0) {
            return CheckResultConstants.APL_MEM_DATA_ISNULL;
        }

        // メールアドレス変更一時情報更新
        updateMailAddressChangeTempInfo(mailAddressAuthInfo.get(0));

        // メールアドレス認証情報更新
        updateMailAddressAuthInfo(mailAddressAuthInfo.get(0));

        // メールアドレス変更完了のお知らせメール送信
        deliverMail(mailAddressChangeTempInfo.get(0).getLong("APPLICATION_MEMBER_ID"),
                mailAddressChangeTempInfo.get(0).getString("MAIL_ADDRESS"));

        return CheckResultConstants.CHECK_OK;
    }

    /**
     * メールアドレス変更完了のお知らせメール送信
     *
     * @param applicationMemberId
     *            アプリ会員ID
     * @param mailAddress
     *            メールアドレス
     */
    private void deliverMail(Long applicationMemberId, String mailAddress) {

        // 差し込み項目（空配列）
        List<String> variableItemValues = new ArrayList<String>();

        CM010001Component cm010001Component = new CM010001Component();
        // アプリ会員のメールアドレス宛に、メールアドレス変更完了旨のメールを送信する。
        cm010001Component.insMailLiteDeliverInfo(applicationMemberId, API_SERVER_ID, mailAddress,
                MailDeliverType.MAIL_DELIVER_TYPE_1, OpalDefaultConstants.MAIL_TEMP_MAIL_ADDRESS_UPDATED_NOTICE,
                variableItemValues, null);
    }

    /**
     * メールアドレス変更一時情報取得
     *
     * @param mailAddressAuthKey
     *            メールアドレス認証キー
     *
     * @return メールアドレス変更一時情報
     */
    private SqlResultSet getMailAddressChangeTempInfo(Long mailAddressAuthKey) {
        // メールアドレス変更一時情報取得用のSQLの条件を設定する
        Map<String, Object> condition = new HashMap<String, Object>();
        // メールアドレス変更一時情報TBL.メールアドレス変更受付ID = メールアドレス認証キー
        condition.put("mailAddressAuthKey", mailAddressAuthKey);
        // アプリ会員状態コード(A：OP認証済みのアプリ会員)
        condition.put("statusA", OpalCodeConstants.AplMemStatusCode.OP_AUTH_APL_MEM);
        // アプリ会員状態コード(D：OP非会員)
        condition.put("statusD", OpalCodeConstants.AplMemStatusCode.NOT_OP_MEM);
        // 実行する。
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("SELECT_MAIL_ADDRESS_CHANGE_TEMP_INFO");
        SqlResultSet result = statement.retrieve(condition);
        return result;
    }

    /**
     * アプリ会員情報更新（メールアドレス変更）
     *
     * @param mailAddressChangeTempInfo
     *            メールアドレス変更一時情報
     * @return 実行件数
     */
    private int updateAplMemInfo(SqlRow mailAddressChangeTempInfo) {
        // アプリ会員情報更新の項目内容を設定する。
        Map<String, Object> condition = new HashMap<String, Object>();
        // メールアドレス
        condition.put("mailAddress", mailAddressChangeTempInfo.getString("MAIL_ADDRESS"));
        // メール配信状態区分
        condition.put("mailDeliverStatusDivision",
                OpalCodeConstants.MailDeliverStatusDivision.MAIL_DELIVER_STATUS_DIVISION_0);
        // アプリ会員状態コード.OP認証済みのアプリ会員
        condition.put("statusA", OpalCodeConstants.AplMemStatusCode.OP_AUTH_APL_MEM);
        // アプリ会員状態コード.OP非会員
        condition.put("statusD", OpalCodeConstants.AplMemStatusCode.NOT_OP_MEM);
        // 最終更新者
        condition.put("updateUserId", API_SERVER_ID);
        // 最終更新日時
        condition.put("updateDateTime", SystemTimeUtil.getTimestamp());
        // アプリ会員ID
        condition.put("applicationMemberId", mailAddressChangeTempInfo.getLong("APPLICATION_MEMBER_ID"));
        // 削除フラグ(0:未削除)
        condition.put("deletedFlg", OpalDefaultConstants.DELETED_FLG_0);

        // 実行する。
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("UPDATE_APL_MEM_INFO");
        return statement.executeUpdateByMap(condition);
    }

    /**
     * メールアドレス変更一時情報更新
     *
     * @param mailAddressAuthInfo
     *            メールアドレス認証情報
     */
    private void updateMailAddressChangeTempInfo(MailAddressAuthInfoEntity mailAddressAuthInfo) {
        // メールアドレス変更一時情報更新の項目内容を設定する。
        Map<String, Object> condition = new HashMap<String, Object>();
        // 処理済フラグ
        condition.put("processedFlag", OpalDefaultConstants.PROCESSED_FLAG_1);
        // 最終更新者
        condition.put("updateUserId", API_SERVER_ID);
        // 最終更新日時
        condition.put("updateDateTime", SystemTimeUtil.getTimestamp());
        // メールアドレス認証キー
        condition.put("mailAddressAuthKey", mailAddressAuthInfo.getMailAddressAuthKey());
        // 実行する。
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("UPDATE_MAIL_ADDRESS_CHANGE_TEMP_INFO");
        statement.executeUpdateByMap(condition);
    }

    /**
     * メールアドレス認証情報更新
     *
     * @param mailAddressAuthInfo
     *            メールアドレス認証情報
     */
    private void updateMailAddressAuthInfo(MailAddressAuthInfoEntity mailAddressAuthInfo) {
        // 処理済フラグ("1"(処理済))
        mailAddressAuthInfo.setProcessedFlag(OpalDefaultConstants.PROCESSED_FLAG_1);
        // 最終更新日時
        mailAddressAuthInfo.setUpdateDateTime(SystemTimeUtil.getTimestamp());
        // 最終更新者ID
        mailAddressAuthInfo.setUpdateUserId(API_SERVER_ID);
        // メールアドレス認証情報更新
        UniversalDao.update(mailAddressAuthInfo);
    }

    /**
     * HTTPレスポンスを設定する。
     *
     * @param requestData
     *            メールアドレス変更要求電文
     *
     * @param result
     *            メールアドレス変更要求電文チェック結果
     *
     * @return HTTPレスポンス
     * @throws IOException
     *             IO異常
     */
    @Override
    protected HttpResponse responseBuilder(A113AADRRequestData requestData, int result) throws IOException {
        // メールアドレス変更結果応答電文設定
        A113AADSResponseData responseData = new A113AADSResponseData();
        // 応答電文.処理結果コード
        responseData.setResultCode(super.getResultCode(result));
        if (result == CheckResultConstants.MAIL_ADDRESS_AUTH_NULL) {
            // メールアドレス認証情報が存在しない場合
            ResponseErrorData error = new ResponseErrorData();
            error.setId("MA113A0401");
            error.setMessage(MessageUtil.createMessage(MessageLevel.ERROR, "MA113A0401").formatMessage());
            // 応答電文.エラーメッセージ
            responseData.setError(error);
        } else if (result == CheckResultConstants.PROCESSED_COMPLETED) {
            // メールアドレス認証情報.処理済フラグが"1"(処理済)の場合
            ResponseErrorData error = new ResponseErrorData();
            error.setId("MA113A0402");
            error.setMessage(MessageUtil.createMessage(MessageLevel.ERROR, "MA113A0402").formatMessage());
            // 応答電文.エラーメッセージ
            responseData.setError(error);
        } else if (result == CheckResultConstants.MAIL_ADDRESS_AUTH_DATE_ERROR) {
            // システム日時がメールアドレス認証情報.メールアドレス認証有効期限を超える場合
            ResponseErrorData error = new ResponseErrorData();
            error.setId("MA113A0403");
            error.setMessage(MessageUtil.createMessage(MessageLevel.ERROR, "MA113A0403").formatMessage());
            // 応答電文.エラーメッセージ
            responseData.setError(error);
        } else if (result == CheckResultConstants.APL_MEM_DATA_ISNULL) {
            // アプリ会員情報更新（メールアドレス変更）更新件数は0件の場合
            ResponseErrorData error = new ResponseErrorData();
            error.setId("MA113A0404");
            error.setMessage(MessageUtil.createMessage(MessageLevel.ERROR, "MA113A0404").formatMessage());
            // 応答電文.エラーメッセージ
            responseData.setError(error);
        }

        HttpResponse response = super.setHttpResponseData(responseData, result);

        return response;
    }
}
