package jp.co.tis.opal.web.ss123A;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import nablarch.core.db.statement.ParameterizedSqlPStatement;
import nablarch.core.db.statement.SqlResultSet;
import nablarch.core.db.statement.SqlRow;
import nablarch.core.message.MessageLevel;
import nablarch.core.message.MessageUtil;
import nablarch.fw.web.HttpResponse;

import jp.co.tis.opal.common.constants.OpalCodeConstants;
import jp.co.tis.opal.common.constants.OpalDefaultConstants;
import jp.co.tis.opal.web.common.ResponseData.ResponseErrorData;
import jp.co.tis.opal.web.common.constants.CheckResultConstants;
import jp.co.tis.opal.web.common.rest.AbstractRestBaseAction;

/**
 * A123A02:パートナー会員サービス情報取得APIのアクションクラス。
 *
 * @author 陳
 * @since 1.0
 */
public class A123A021Action extends AbstractRestBaseAction<A123AABRRequestData> {

    /** レスポンスのパートナー会員サービス情報データ */
    private Map<String, Object> partnerData = new LinkedHashMap<String, Object>();

    /**
     * パートナー会員サービス情報取得API
     *
     * @param requestData
     *            パートナー会員サービス情報取得要求電文
     * @return HTTPレスポンス
     * @throws Exception
     *             異常
     */
    @Consumes(MediaType.APPLICATION_JSON)
    @Valid
    public HttpResponse getPartnerMemServiceInfo(A123AABRRequestData requestData) {

        HttpResponse responseData = super.execute(requestData);
        return responseData;

    }

    /**
     * 処理詳細ロジック
     *
     * @param requestData
     *            パートナー会員サービス情報取得要求電文
     * @return チェックの結果
     */
    @Override
    protected int executeLogic(A123AABRRequestData requestData) {

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

        // パートナー会員サービス情報データ取得
        SqlResultSet partnerMemServiceInfo = getPartnerMemServiceInfo(requestData.getAplData(), aplMemInfo.get(0));

        // データが存在しない場合、応答電文を以下の通り設定して、「HTTPアクセスログ出力」を行う。
        if (partnerMemServiceInfo.isEmpty()) {
            return CheckResultConstants.PARTNER_MEM_SERVICE_INFO_ISNULL;
        }

        // 取得したパートナー会員サービス情報を応答電文のパートナー会員サービス情報データに設定する。
        setResponseParams(partnerMemServiceInfo.get(0));

        return CheckResultConstants.CHECK_OK;
    }

    /**
     * HTTPレスポンスを設定する。
     *
     * @param requestData
     *            パートナー会員サービス情報取得要求電文
     *
     * @param result
     *            チェックの結果
     *
     * @return HTTPレスポンス
     * @throws IOException
     *             異常
     */
    @Override
    protected HttpResponse responseBuilder(A123AABRRequestData requestData, int result) throws IOException {

        // パートナー会員サービス情報データ応答電文設定
        A123AABSResponseData responseData = new A123AABSResponseData();

        // 応答電文.処理結果コード
        responseData.setResultCode(getResultCode(result));
        if (result == CheckResultConstants.APL_MEM_DATA_ISNULL) {
            // 応答電文.エラーメッセージ
            ResponseErrorData error = new ResponseErrorData();
            error.setId("MA123A0201");
            error.setMessage(MessageUtil.createMessage(MessageLevel.ERROR, "MA123A0201").formatMessage());
            responseData.setError(error);
        } else if (result == CheckResultConstants.APPLICATION_MEMBER_STATUS_CODE_NOT_A) {
            // 応答電文.エラーメッセージ
            ResponseErrorData error = new ResponseErrorData();
            error.setId("MA123A0202");
            error.setMessage(MessageUtil.createMessage(MessageLevel.ERROR, "MA123A0202").formatMessage());
            responseData.setError(error);
        } else if (result == CheckResultConstants.PARTNER_MEM_SERVICE_INFO_ISNULL) {
            // 応答電文.エラーメッセージ
            ResponseErrorData error = new ResponseErrorData();
            error.setId("MA123A0203");
            error.setMessage(MessageUtil.createMessage(MessageLevel.ERROR, "MA123A0203").formatMessage());
            responseData.setError(error);
        } else if (result == CheckResultConstants.CHECK_OK) {
            responseData.setPartnerData(partnerData);
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
    private SqlResultSet getAplMemInfo(A123AABRBodyForm form) {

        // アプリ会員情報取得用のSQL条件を設定する。
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("SELECT_APL_MEM_INFO");

        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("applicationMemberId", form.getApplicationMemberId());
        condition.put("opAuthAplMem", OpalCodeConstants.AplMemStatusCode.OP_AUTH_APL_MEM);
        condition.put("notOpMem", OpalCodeConstants.AplMemStatusCode.NOT_OP_MEM);

        // 実行する。
        SqlResultSet result = statement.retrieve(condition);
        return result;
    }

    /**
     * パートナー会員サービス情報データ取得
     *
     * @param form
     *            パートナー会員サービス情報取得フォーム
     * @param aplMemInfo
     *            取得したアプリ会員情報
     *
     * @return パートナー会員サービス情報
     */
    private SqlResultSet getPartnerMemServiceInfo(A123AABRBodyForm form, SqlRow aplMemInfo) {

        // パートナー会員サービス情報取得用のSQL条件を設定する。
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("SELECT_PARTNER_MEM_SERVICE_INFO");

        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("applicationMemberId", form.getApplicationMemberId());
        condition.put("osakaPitapaNumber", aplMemInfo.getString("OSAKA_PITAPA_NUMBER"));
        condition.put("memberControlNumber", aplMemInfo.getString("MEMBER_CONTROL_NUMBER"));
        condition.put("memCtrlNumBrNum", aplMemInfo.getString("MEM_CTRL_NUM_BR_NUM"));
        condition.put("admitStatusDivision", OpalCodeConstants.AdmitStatusDivision.ADMIT_STATUS_DIVISION_1);
        condition.put("applicationMemberStatusCode", OpalCodeConstants.AplMemStatusCode.OP_AUTH_APL_MEM);
        // 実行する。
        SqlResultSet result = statement.retrieve(condition);
        return result;
    }

    /**
     * 取得したパートナー会員サービス情報を応答電文のパートナー会員サービス情報データに設定する
     *
     * @param row
     *            パートナー会員サービス情報
     */
    private void setResponseParams(SqlRow row) {
        // パートナー会員サービス管理ID
        partnerData.put("partnerMemServiceCtrlId", row.getString("PARTNER_MEM_SERVICE_CTRL_ID"));
        // パートナー登録者アプリ会員ID
        partnerData.put("partnerRegistUserAplMemId", row.getString("REGIST_APPLICATION_MEMBER_ID"));
        // パートナー登録者OP番号
        partnerData.put("partnerRegistUserOPNum", row.getString("REGIST_OSAKA_PITAPA_NUMBER"));
        // パートナーアプリ会員ID
        partnerData.put("partnerApplicationMemberId", row.getString("PARTNER_APPLICATION_MEMBER_ID"));
        // パートナーOP番号
        partnerData.put("partnerOsakaPitapaNumber", row.getString("PARTNER_OSAKA_PITAPA_NUMBER"));
        // 適用開始日時
        SimpleDateFormat sdf = new SimpleDateFormat(OpalDefaultConstants.YEAR_MONTH_DAY_HOUR_MIN_SEC_FORMAT);
        String applyStartDateTime = sdf.format(row.getTimestamp("APPLY_START_DATE_TIME"));
        partnerData.put("applyStartDateTime",  applyStartDateTime);
    }

}
