package jp.co.tis.opal.web.ss123A;

import java.io.IOException;
import java.util.HashMap;
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
import nablarch.fw.web.HttpResponse;

import jp.co.tis.opal.common.component.CM010004Component;
import jp.co.tis.opal.common.constants.OpalCodeConstants;
import jp.co.tis.opal.web.common.ResponseData.ResponseErrorData;
import jp.co.tis.opal.web.common.constants.CheckResultConstants;
import jp.co.tis.opal.web.common.rest.AbstractRestBaseAction;

/**
 * A123A03:パートナー会員サービス更新APIのアクションクラス。
 *
 * @author 陳
 * @since 1.0
 */
public class A123A031Action extends AbstractRestBaseAction<A123AACRRequestData> {

    /** API */
    private static final String API_SERVER_ID = "A123A031";

    /** 退会後パートナー会員サービス情報データ保持期間(月) */
    private static final String RETENTION_PERIOD = "after_withdraw_partner_info_retention_period";

    /**
     * パートナー会員サービス更新API
     *
     * @param requestData
     *            パートナー会員サービス更新要求電文
     * @return HTTPレスポンス
     * @throws Exception
     *             異常
     */
    @Consumes(MediaType.APPLICATION_JSON)
    @Valid
    public HttpResponse updatePartnerMemServiceInfo(A123AACRRequestData requestData) {

        HttpResponse responseData = super.execute(requestData);
        return responseData;

    }

    /**
     * 処理詳細ロジック
     *
     * @param requestData
     *            パートナー会員サービス更新要求電文
     * @return チェックの結果
     */
    @Override
    protected int executeLogic(A123AACRRequestData requestData) {

        // アプリ会員情報取得
        SqlResultSet aplMemInfo = getAplMemInfo(requestData.getAplData());

        // アプリ会員情報が存在しない場合、応答電文を以下の通り設定して、「HTTPアクセスログ出力」を行う。
        if (aplMemInfo.isEmpty()) {
            return CheckResultConstants.APL_MEM_DATA_ISNULL;
        }

        // パートナー会員サービス利用可チェック
        if (OpalCodeConstants.AplMemStatusCode.NOT_OP_MEM
                .equals(aplMemInfo.get(0).getString("APPLICATION_MEMBER_STATUS_CODE"))) {
            return CheckResultConstants.APPLICATION_MEMBER_STATUS_CODE_NOT_A;
        }

        // パートナー会員サービス情報取得
        SqlResultSet partnerMemServiceInfo = getPartnerMemServiceInfo(
                requestData.getAplData().getPartnerMemServiceCtrlId(),
                aplMemInfo.get(0).getString("MEMBER_CONTROL_NUMBER"),
                aplMemInfo.get(0).getString("MEM_CTRL_NUM_BR_NUM"));

        // データが存在しない場合、応答電文を以下の通り設定して、「HTTPアクセスログ出力」を行う。
        if (partnerMemServiceInfo.isEmpty()) {
            return CheckResultConstants.PARTNER_MEM_SERVICE_INFO_ISNULL;
        }

        // パートナー会員サービス情報更新
        // 論理削除日を導出する。
        int monthSpan = Integer.valueOf(SystemRepository.get(RETENTION_PERIOD));

        CM010004Component cm010004Component = new CM010004Component();
        String deletedDate = cm010004Component.getDeletedDateMonthly(monthSpan);

        // パートナー会員サービス情報更新
        updatePartnerMemServiceInfo(requestData.getAplData().getPartnerMemServiceCtrlId(), deletedDate);

        return CheckResultConstants.CHECK_OK;
    }

    /**
     * HTTPレスポンスを設定する。
     *
     * @param requestData
     *            パートナー会員サービス更新要求電文
     *
     * @param result
     *            チェックの結果
     *
     * @return HTTPレスポンス
     * @throws IOException
     *             異常
     */
    @Override
    protected HttpResponse responseBuilder(A123AACRRequestData requestData, int result) throws IOException {

        // パートナー会員サービス更新要求電文設定
        A123AABSResponseData responseData = new A123AABSResponseData();

        // 応答電文.処理結果コード
        responseData.setResultCode(getResultCode(result));
        if (result == CheckResultConstants.APL_MEM_DATA_ISNULL) {
            // 応答電文.エラーメッセージ
            ResponseErrorData error = new ResponseErrorData();
            error.setId("MA123A0301");
            error.setMessage(MessageUtil.createMessage(MessageLevel.ERROR, "MA123A0301").formatMessage());
            responseData.setError(error);
        } else if (result == CheckResultConstants.APPLICATION_MEMBER_STATUS_CODE_NOT_A) {
            // 応答電文.エラーメッセージ
            ResponseErrorData error = new ResponseErrorData();
            error.setId("MA123A0302");
            error.setMessage(MessageUtil.createMessage(MessageLevel.ERROR, "MA123A0302").formatMessage());
            responseData.setError(error);
        } else if (result == CheckResultConstants.PARTNER_MEM_SERVICE_INFO_ISNULL) {
            // 応答電文.エラーメッセージ
            ResponseErrorData error = new ResponseErrorData();
            error.setId("MA123A0303");
            error.setMessage(MessageUtil.createMessage(MessageLevel.ERROR, "MA123A0303").formatMessage());
            responseData.setError(error);
        }

        HttpResponse response = super.setHttpResponseData(responseData, result);

        return response;

    }

    /**
     * アプリ会員情報取得
     *
     * @param form
     *            パートナー会員サービス情報取得フォーム
     *
     * @return アプリ会員情報
     */
    private SqlResultSet getAplMemInfo(A123AACRBodyForm form) {

        // アプリ会員情報取得用のSQL条件を設定する。
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("SELECT_APPLICATION_MEMBER_STATUS_CODE");

        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("applicationMemberId", form.getApplicationMemberId());
        condition.put("opAuthAplMem", OpalCodeConstants.AplMemStatusCode.OP_AUTH_APL_MEM);
        condition.put("notOpMem", OpalCodeConstants.AplMemStatusCode.NOT_OP_MEM);
        // 実行する。
        SqlResultSet result = statement.retrieve(condition);
        return result;
    }

    /**
     * パートナー会員サービス情報取得
     *
     * @param partnerMemServiceCtrlId
     *            パートナー会員サービス管理ID
     * @param memberControlNumber
     *            会員管理番号
     * @param memCtrlNumBrNum
     *            会員管理番号枝番
     *
     * @return パートナー会員サービス情報
     */
    private SqlResultSet getPartnerMemServiceInfo(String partnerMemServiceCtrlId, String memberControlNumber,
            String memCtrlNumBrNum) {

        // パートナー会員サービス情報取得用のSQL条件を設定する。
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("SELECT_PARTNER_MEM_SERVICE_INFO");

        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("partnerMemServiceCtrlId", partnerMemServiceCtrlId);
        condition.put("memberControlNumber", memberControlNumber);
        condition.put("memCtrlNumBrNum", memCtrlNumBrNum);
        condition.put("admitStatusDivision", OpalCodeConstants.AdmitStatusDivision.ADMIT_STATUS_DIVISION_1);

        // 実行する。
        SqlResultSet result = statement.retrieve(condition);
        return result;
    }

    /**
     * パートナー会員サービス情報更新
     *
     * @param partnerMemServiceCtrlId
     *            パートナー会員サービス管理ID
     * @param deletedDate
     *            論理削除日
     */
    private void updatePartnerMemServiceInfo(String partnerMemServiceCtrlId, String deletedDate) {

        // パートナー会員サービス情報更新用のSQL条件を設定する。
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("UPDATE_PARTNER_MEM_SERVICE_INFO");

        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("admitStatusDivision", OpalCodeConstants.AdmitStatusDivision.ADMIT_STATUS_DIVISION_2);
        condition.put("applyEndDateTime", SystemTimeUtil.getDateTimeString());
        condition.put("updateUserId", API_SERVER_ID);
        condition.put("updateDateTime", SystemTimeUtil.getTimestamp());
        condition.put("deletedDate", deletedDate);
        condition.put("partnerMemServiceCtrlId", partnerMemServiceCtrlId);

        // 実行する。
        statement.executeUpdateByMap(condition);
    }

}
