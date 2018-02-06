package jp.co.tis.opal.web.ss113A;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import nablarch.core.date.SystemTimeUtil;
import nablarch.core.db.statement.ParameterizedSqlPStatement;
import nablarch.core.message.MessageLevel;
import nablarch.core.message.MessageUtil;
import nablarch.fw.web.HttpResponse;

import jp.co.tis.opal.common.constants.OpalCodeConstants;
import jp.co.tis.opal.common.constants.OpalDefaultConstants;
import jp.co.tis.opal.web.common.ResponseData.ResponseErrorData;
import jp.co.tis.opal.web.common.constants.CheckResultConstants;
import jp.co.tis.opal.web.common.rest.AbstractRestBaseAction;

/**
 * {@link A113A021Action} アプリ会員情報更新APIのアクションクラス。
 *
 * @author 張
 * @since 1.0
 */
public class A113A021Action extends AbstractRestBaseAction<A113AABRRequestData> {

    /** API */
    private static final String API_SERVER_ID = "A113A021";

    /**
     * 更新処理。
     * <p>
     * 応答にJSONを使用する。
     * </p>
     *
     * @param requestData
     *            HTTPリクエスト
     * @return HTTPレスポンス
     */
    @Consumes(MediaType.APPLICATION_JSON)
    @Valid
    public HttpResponse updateAppMemInfo(A113AABRRequestData requestData) {
        HttpResponse responseData = super.execute(requestData);
        return responseData;
    }

    /**
     * アプリ会員情報TBL更新
     *
     * @param data
     *            アプリ会員情報更新要求電文
     * @return 更新件数
     */
    private int updateApplicationMemberInfo(A113AABRRequestData data) {

        // アプリ会員情報更新用のSQL条件を設定する。
        Map<String, Object> condition = new HashMap<String, Object>();
        // アプリ会員ID
        condition.put("applicationMemberId", Long.valueOf(data.getAplMemInfo().getApplicationMemberId()));
        // 生年月日
        condition.put("birthdate", data.getAplMemInfo().getBirthdate());
        // 性別コード
        condition.put("sexCode", data.getAplMemInfo().getSexCode());
        // レコメンド利用承諾可否フラグ
        condition.put("recommendUseAcceptFlag", data.getAplMemInfo().getRecommendUseAcceptFlag());
        // アンケート1
        condition.put("enquete1", data.getAplMemInfo().getEnquete1());
        // アンケート2
        condition.put("enquete2", data.getAplMemInfo().getEnquete2());
        // アンケート3
        condition.put("enquete3", data.getAplMemInfo().getEnquete3());
        // アンケート4
        condition.put("enquete4", data.getAplMemInfo().getEnquete4());
        // アンケート5
        condition.put("enquete5", data.getAplMemInfo().getEnquete5());
        // アンケート6
        condition.put("enquete6", data.getAplMemInfo().getEnquete6());
        // アンケート7
        condition.put("enquete7", data.getAplMemInfo().getEnquete7());
        // アンケート8
        condition.put("enquete8", data.getAplMemInfo().getEnquete8());
        // アンケート9
        condition.put("enquete9", data.getAplMemInfo().getEnquete9());
        // アンケート10
        condition.put("enquete10", data.getAplMemInfo().getEnquete10());
        // 主なご利用駅1
        condition.put("mainUseStation1", data.getAplMemInfo().getMainUseStation1());
        // 主なご利用駅2
        condition.put("mainUseStation2", data.getAplMemInfo().getMainUseStation2());
        // 主なご利用駅3
        condition.put("mainUseStation3", data.getAplMemInfo().getMainUseStation3());
        // 主なご利用駅4
        condition.put("mainUseStation4", data.getAplMemInfo().getMainUseStation4());
        // 主なご利用駅5
        condition.put("mainUseStation5", data.getAplMemInfo().getMainUseStation5());
        // 休日1
        condition.put("dayOff1", data.getAplMemInfo().getDayOff1());
        // 休日2
        condition.put("dayOff2", data.getAplMemInfo().getDayOff2());
        // 削除フラグ(0:未削除)
        condition.put("deletedFlg", OpalDefaultConstants.DELETED_FLG_0);
        // アプリ会員状態コード.OP認証済みのアプリ会員
        condition.put("statusA", OpalCodeConstants.AplMemStatusCode.OP_AUTH_APL_MEM);
        // アプリ会員状態コード.OP非会員
        condition.put("statusD", OpalCodeConstants.AplMemStatusCode.NOT_OP_MEM);
        // 最終更新者＝"A113A021"
        condition.put("updateUserId", API_SERVER_ID);
        // 最終更新日時
        condition.put("updateDateTime", SystemTimeUtil.getTimestamp());

        // アプリ会員情報更新
        ParameterizedSqlPStatement updateStatement = super.getParameterizedSqlStatement("UPDATE_APL_MEM_INFO");
        int updateRowCount = updateStatement.executeUpdateByMap(condition);
        return updateRowCount;
    }

    /**
     * 処理詳細ロジック実行
     *
     * @param requestData
     *            アプリ会員情報更新要求電文
     * @return 処理結果
     */
    @Override
    protected int executeLogic(A113AABRRequestData requestData) {

        // アプリ会員情報更新要求電文.アプリ会員IDをキーに、アプリ会員情報TBLを更新する。
        Integer updateRowCount = this.updateApplicationMemberInfo(requestData);
        // 更新件数は0件の場合、
        if (updateRowCount == 0) {
            return CheckResultConstants.APL_MEM_DATA_ISNULL;
        }

        return CheckResultConstants.CHECK_OK;
    }

    /**
     * HTTPレスポンスを設定する。
     *
     * @param requestData
     *            アプリ会員情報更新要求電文
     *
     * @param result
     *            アプリ会員情報更新要求電文
     *
     * @return HTTPレスポンス
     * @throws IOException
     *             IO異常
     */
    @Override
    protected HttpResponse responseBuilder(A113AABRRequestData requestData, int result) throws IOException {
        // アプリ会員情報更新結果応答電文設定
        A113AABSResponseData responseData = new A113AABSResponseData();
        // 更新結果応答電文.処理結果コード
        responseData.setResultCode(super.getResultCode(result));
        if (result == CheckResultConstants.APL_MEM_DATA_ISNULL) {
            // アプリ会員情報TBLに該当アプリ会員IDのデータが存在しない場合、
            ResponseErrorData error = new ResponseErrorData();
            error.setId("MA113A0202");
            error.setMessage(MessageUtil.createMessage(MessageLevel.ERROR, "MA113A0202").formatMessage());
            // 応答電文.エラーメッセージ
            responseData.setError(error);
        }

        HttpResponse response = super.setHttpResponseData(responseData, result);

        return response;
    }
}
