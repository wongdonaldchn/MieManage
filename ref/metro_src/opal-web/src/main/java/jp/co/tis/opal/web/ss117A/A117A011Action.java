package jp.co.tis.opal.web.ss117A;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import nablarch.core.db.statement.ParameterizedSqlPStatement;
import nablarch.core.db.statement.SqlResultSet;
import nablarch.core.message.MessageLevel;
import nablarch.core.message.MessageUtil;
import nablarch.fw.web.HttpResponse;

import jp.co.tis.opal.common.constants.OpalCodeConstants;
import jp.co.tis.opal.web.common.ResponseData.ResponseErrorData;
import jp.co.tis.opal.web.common.constants.CheckResultConstants;
import jp.co.tis.opal.web.common.rest.AbstractRestBaseAction;

/**
 * A117A01:アプリ会員本人確認APIのアクションクラス。
 *
 * @author 唐
 * @since 1.0
 */
public class A117A011Action extends AbstractRestBaseAction<A117AAARRequestData> {

    /** レスポンスのアプリ会員情報データ */
    private Map<String, Object> responseAplMemInfo = new LinkedHashMap<String, Object>();

    /**
     * アプリ会員本人確認API
     *
     * @param requestData
     *            アプリ会員本人確認要求電文
     * @return HTTPレスポンス
     * @throws Exception
     *             異常
     */
    @Valid
    @Consumes(MediaType.APPLICATION_JSON)
    public HttpResponse confirmAplMemInfo(A117AAARRequestData requestData) {
        HttpResponse responseData = super.execute(requestData);
        return responseData;
    }

    /**
     * 処理詳細ロジック
     *
     * @param requestData
     *            アプリ会員本人確認要求電文
     * @return チェックの結果
     */
    @Override
    protected int executeLogic(A117AAARRequestData requestData) {
        // アプリ会員情報取得
        SqlResultSet aplMemInfo = getAplMemInfo(requestData.getAplData());

        // アプリ会員情報が存在しない場合、応答電文を以下の通り設定して、「HTTPアクセスログ出力」を行う。
        if (aplMemInfo.isEmpty()) {
            return CheckResultConstants.APL_MEM_DATA_ISNULL;
        }

        // アプリ会員情報データ設定
        setResponseParams(aplMemInfo);

        return CheckResultConstants.CHECK_OK;

    }

    /**
     * HTTPレスポンスを設定する。
     *
     * @param requestData
     *            アプリ会員本人確認要求電文
     *
     * @param result
     *            チェックの結果
     *
     * @return HTTPレスポンス
     * @throws IOException
     *             異常
     */
    @Override
    protected HttpResponse responseBuilder(A117AAARRequestData requestData, int result) throws IOException {

        // アプリ会員本人確認結果応答電文設定
        A117AAASResponseData responseData = new A117AAASResponseData();
        // 応答電文.処理結果コード
        responseData.setResultCode(getResultCode(result));
        // アプリ会員未存在エラー
        if (result == CheckResultConstants.APL_MEM_DATA_ISNULL) {
            // 応答電文.エラーメッセージ
            ResponseErrorData error = new ResponseErrorData();
            error.setId("MA117A0101");
            error.setMessage(MessageUtil.createMessage(MessageLevel.ERROR, "MA117A0101").formatMessage());
            responseData.setError(error);
        } else if (result == CheckResultConstants.CHECK_OK) {
            responseData.setAplMemInfo(responseAplMemInfo);
        }
        HttpResponse response = super.setHttpResponseData(responseData, result);
        return response;

    }

    /**
     * アプリ会員情報データ取得
     *
     * @param form
     *            アプリ会員本人確認APIの検索フォーム
     *
     * @return アプリ会員情報
     */
    private SqlResultSet getAplMemInfo(A117AAARBodyForm form) {

        // アプリ会員情報取得用のSQL条件を設定する。
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("SELECT_APL_MEM_INFO");
        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("birthdate", form.getBirthdate());
        condition.put("sexCode", form.getSexCode());
        condition.put("mailAddress", form.getMailAddress());
        condition.put("notOpMem", OpalCodeConstants.AplMemStatusCode.NOT_OP_MEM);
        condition.put("opAuthAplMem", OpalCodeConstants.AplMemStatusCode.OP_AUTH_APL_MEM);
        // 実行する。
        SqlResultSet result = statement.retrieve(condition);
        return result;
    }

    /**
     * アプリ会員情報データ設定
     *
     * @param aplMemInfo
     *            アプリ会員情報
     */
    private void setResponseParams(SqlResultSet aplMemInfo) {
        // アプリ会員ID
        responseAplMemInfo.put("applicationMemberId", aplMemInfo.get(0).getString("APPLICATION_MEMBER_ID"));
        // OP番号
        responseAplMemInfo.put("osakaPitapaNumber", aplMemInfo.get(0).getString("OSAKA_PITAPA_NUMBER"));
    }
}
