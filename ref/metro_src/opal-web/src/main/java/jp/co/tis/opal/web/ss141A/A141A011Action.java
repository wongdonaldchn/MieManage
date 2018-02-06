package jp.co.tis.opal.web.ss141A;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.core.JsonProcessingException;

import nablarch.core.db.statement.ParameterizedSqlPStatement;
import nablarch.core.db.statement.SqlResultSet;
import nablarch.core.db.statement.SqlRow;
import nablarch.core.message.MessageLevel;
import nablarch.core.message.MessageUtil;
import nablarch.fw.web.HttpResponse;

import jp.co.tis.opal.common.constants.OpalCodeConstants;
import jp.co.tis.opal.web.common.ResponseData.ResponseErrorData;
import jp.co.tis.opal.web.common.constants.CheckResultConstants;
import jp.co.tis.opal.web.common.rest.AbstractRestBaseAction;

/**
 * A141A01:郵送情報取得APIのアクションクラス。
 *
 * @author 陳
 * @since 1.0
 */
public class A141A011Action extends AbstractRestBaseAction<A141AAARRequestData> {

    /** レスポンスの郵送情報データ */
    private Map<String, Object> responsePostInfo = new LinkedHashMap<String, Object>();

    /**
     * 郵送情報取得API
     *
     * @param requestData
     *            郵送情報取得要求電文
     * @return HTTPレスポンス
     *
     */
    @Consumes(MediaType.APPLICATION_JSON)
    @Valid
    public HttpResponse getPostInfo(A141AAARRequestData requestData) {

        HttpResponse responseData = super.execute(requestData);
        return responseData;

    }

    /**
     * 処理詳細ロジック
     *
     * @param requestData
     *            郵送情報取得要求電文
     * @return チェックの結果
     */
    @Override
    protected int executeLogic(A141AAARRequestData requestData) {

        // アプリ会員情報取得
        SqlResultSet aplMemInfo = getAplMemInfo(requestData.getAplData());

        // アプリ会員情報が存在しない場合、応答電文を以下の通り設定して、「HTTPアクセスログ出力」を行う。
        if (aplMemInfo.isEmpty()) {
            return CheckResultConstants.APL_MEM_DATA_ISNULL;
        }

        // 郵送情報取得
        SqlResultSet rs = getPostInformation(requestData.getAplData());

        if (!rs.isEmpty()) {
            // 取得された郵送情報を応答電文の郵送情報データに設定する。
            setResponseParams(rs.get(0));
        }

        return CheckResultConstants.CHECK_OK;
    }

    /**
     * HTTPレスポンスを設定する。
     *
     * @param requestData
     *            郵送情報取得要求電文
     *
     * @param result
     *            チェックの結果
     *
     * @return HTTPレスポンス
     * @throws JsonProcessingException
     *             異常
     */
    @Override
    protected HttpResponse responseBuilder(A141AAARRequestData requestData, int result) throws IOException {

        // 郵送情報取得応答電文設定
        A141AAASResponseData responseData = new A141AAASResponseData();

        // 応答電文.処理結果コード
        responseData.setResultCode(getResultCode(result));

        if (result == CheckResultConstants.APL_MEM_DATA_ISNULL) {
            // 応答電文.エラーメッセージ
            ResponseErrorData error = new ResponseErrorData();
            error.setId("MA141A0101");
            error.setMessage(MessageUtil.createMessage(MessageLevel.ERROR, "MA141A0101").formatMessage());
            responseData.setError(error);
        } else if (result == CheckResultConstants.CHECK_OK && !responsePostInfo.isEmpty()) {
            // 応答電文.郵送情報データ
            responseData.setPostInfoData(responsePostInfo);
        }

        HttpResponse response = super.setHttpResponseData(responseData, result);

        return response;

    }

    /**
     * 郵送情報取得
     *
     * @param form
     *            郵送情報取得フォーム
     *
     * @return 郵送情報
     */
    private SqlResultSet getPostInformation(A141AAARBodyForm form) {

        // 郵送情報取得用のSQL条件を設定する。
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("SELECT_POST_INFO");

        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("applicationMemberId", form.getApplicationMemberId());

        // 実行する。
        SqlResultSet result = statement.retrieve(condition);
        return result;
    }

    /**
     * 郵送情報データ設定
     *
     * @param row
     *            郵送情報
     */
    private void setResponseParams(SqlRow row) {
        // 郵便番号
        responsePostInfo.put("postcode", row.getString("POSTCODE"));
        // 住所
        responsePostInfo.put("address", row.getString("ADDRESS"));
        // 氏名
        responsePostInfo.put("name", row.getString("NAME"));
        // 電話番号
        responsePostInfo.put("telephoneNumber", row.getString("TELEPHONE_NUMBER"));
    }

    /**
     * アプリ会員情報取得
     *
     * @param form
     *            郵送情報取得フォーム
     *
     * @return アプリ会員情報
     */
    private SqlResultSet getAplMemInfo(A141AAARBodyForm form) {

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

}
