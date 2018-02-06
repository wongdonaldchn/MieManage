package jp.co.tis.opal.web.ss134A;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import nablarch.common.dao.UniversalDao;
import nablarch.core.db.statement.ParameterizedSqlPStatement;
import nablarch.core.message.MessageLevel;
import nablarch.core.message.MessageUtil;
import nablarch.fw.web.HttpResponse;

import jp.co.tis.opal.common.component.CM010005Component;
import jp.co.tis.opal.common.constants.OpalCodeConstants;
import jp.co.tis.opal.common.entity.MileBalanceInfoEntity;
import jp.co.tis.opal.web.common.ResponseData.ResponseErrorData;
import jp.co.tis.opal.web.common.constants.CheckResultConstants;
import jp.co.tis.opal.web.common.rest.AbstractRestBaseAction;

/**
 * A134A01:マイル加算APIのアクションクラス。
 *
 * @author 曹
 * @since 1.0
 */
public class A134A011Action extends AbstractRestBaseAction<A134AAARRequestData> {

    /** API処理ID */
    private static final String API_PROCESS_ID = "A134A011";

    /**
     * マイル加算API
     *
     * @param requestData
     *            マイル加算要求電文
     * @return マイル加算結果応答電文
     */
    @Consumes(MediaType.APPLICATION_JSON)
    @Valid
    public HttpResponse addMile(A134AAARRequestData requestData) {

        HttpResponse responseData = super.execute(requestData);
        return responseData;

    }

    /**
     * 処理詳細
     *
     * @param requestData
     *            マイル加算要求電文
     * @return チェック結果
     */
    @Override
    protected int executeLogic(A134AAARRequestData requestData) {

        // アプリ会員存在チェック
        Boolean aplMemDataIsNull = chekAplMemDataIsNull(
                Long.valueOf(requestData.getAplData().getApplicationMemberId()));
        if (aplMemDataIsNull) {
            // アプリ会員情報が存在しないと判断
            return CheckResultConstants.APL_MEM_DATA_ISNULL;
        }

        // マイル残高情報排他制御（アプリ会員単位）
        Map<String, Object> mileBalanceInfoExclusive = new HashMap<String, Object>();
        mileBalanceInfoExclusive.put("applicationMemberId", requestData.getAplData().getApplicationMemberId());
        UniversalDao.findAllBySqlFile(MileBalanceInfoEntity.class, "SELECT_MILE_BALANCE_INFO",
                mileBalanceInfoExclusive);

        // マイル加算処理
        CM010005Component cm010005Component = new CM010005Component();
        cm010005Component.addMile(Long.valueOf(requestData.getAplData().getApplicationMemberId()),
                requestData.getAplData().getMileAddSubRcptNo(), requestData.getAplData().getMileCategoryCode(),
                Long.valueOf(requestData.getAplData().getAddMileAmount()), API_PROCESS_ID, null);

        return CheckResultConstants.CHECK_OK;
    }

    /**
     * マイル加算結果応答電文を設定する。
     *
     * @param requestData
     *            マイル加算要求電文
     *
     * @param result
     *            マイル加算要求電文チェック結果
     *
     * @return マイル加算結果応答電文
     * @throws IOException
     *             IO異常
     */
    @Override
    protected HttpResponse responseBuilder(A134AAARRequestData requestData, int result) throws IOException {

        // マイル加算結果応答電文設定
        A134AAASResponseData responseData = new A134AAASResponseData();
        // 応答電文.処理結果コード
        responseData.setResultCode(getResultCode(result));
        if (result == CheckResultConstants.APL_MEM_DATA_ISNULL) {
            // 応答電文.エラーメッセージ
            ResponseErrorData error = new ResponseErrorData();
            error.setId("MA134A0101");
            error.setMessage(MessageUtil.createMessage(MessageLevel.ERROR, "MA134A0101").formatMessage());
            responseData.setError(error);
        }

        HttpResponse response = super.setHttpResponseData(responseData, result);

        return response;
    }

    /**
     * アプリ会員存在チェック。
     *
     * @param applicationMemberId
     *            アプリ会員ID
     *
     * @return aplMemInfoIsNull アプリ会員存在チェック結果
     */
    private Boolean chekAplMemDataIsNull(Long applicationMemberId) {
        Boolean aplMemDataIsNull = false;
        // アプリ会員情報取得用のSQLの条件を設定する
        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("applicationMemberId", applicationMemberId);
        condition.put("statusCodeA", OpalCodeConstants.AplMemStatusCode.OP_AUTH_APL_MEM);
        condition.put("statusCodeD", OpalCodeConstants.AplMemStatusCode.NOT_OP_MEM);

        // アプリ会員情報取得
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("SELECT_APL_MEM_INFO");
        if (statement.retrieve(condition).isEmpty()) {
            // 取得できない場合、チェック結果を「true」に設定
            aplMemDataIsNull = true;
        }

        return aplMemDataIsNull;
    }

}
