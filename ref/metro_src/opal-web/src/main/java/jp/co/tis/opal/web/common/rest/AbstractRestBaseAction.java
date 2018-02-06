package jp.co.tis.opal.web.common.rest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import nablarch.core.ThreadContext;
import nablarch.core.db.connection.DbConnectionContext;
import nablarch.core.db.connection.TransactionManagerConnection;
import nablarch.core.db.support.DbAccessSupport;
import nablarch.core.log.Logger;
import nablarch.core.log.LoggerManager;
import nablarch.core.util.StringUtil;
import nablarch.fw.web.HttpErrorResponse;
import nablarch.fw.web.HttpResponse;

import jp.co.tis.opal.web.common.RequestData.RequestOpalControlData;
import jp.co.tis.opal.web.common.ResponseData.ResponseOpalControlData;
import jp.co.tis.opal.web.common.constants.CheckResultConstants;
import jp.co.tis.opal.web.common.handler.ThreadContext.OpalControlDataAttribute;

/**
 * REST処理のセポートクラス。
 *
 * @author 張
 * @since 1.0
 */
public abstract class AbstractRestBaseAction<T> extends DbAccessSupport {

    /** ロガー */
    protected static final Logger LOGGER = LoggerManager.get(AbstractRestBaseAction.class);

    /** ロガー */
    public static final String RESPONSE_DATA = "responseData";

    /** 要求電文（OPAL制御データ） */
    private RequestOpalControlData reqOpalControlData = null;
    /** 応答電文（OPAL制御データ） */
    private ResponseOpalControlData respOpalControlData = null;
    /** トランザクションパタンリスト */
    private List<Integer> rollbackNoProcessParttonList;

    /**
     * REST処理のセポートクラス。
     *
     * @param requestData
     *            応答電文対象
     *
     * @return HTTPレスポンス
     */
    protected HttpResponse execute(T requestData) {

        this.setRollbackNoProcessParttonList(CheckResultConstants.CHECK_OK);

        // 処理詳細ロジック
        int result = executeLogic(requestData);
        if (this.isRollbackTransaction(result)) {
            this.rollbackTransaction();
        }

        // 応答電文を送信する。
        HttpResponse response = null;
        try {
            response = responseBuilder(requestData, result);
        } catch (IOException ex) {
            throw new HttpErrorResponse(response, ex);
        }

        return response;
    }

    /**
     * トランザクションパタンリストを設定する。
     *
     * @param partton
     *            実行結果パタン
     */
    protected void setRollbackNoProcessParttonList(int partton) {

        if (this.rollbackNoProcessParttonList == null) {
            this.rollbackNoProcessParttonList = new ArrayList<Integer>();
        }
        this.rollbackNoProcessParttonList.add(partton);
    }

    /**
     * トランザクション処理可否
     *
     * @param result
     *            実行結果
     *
     * @return トランザクション処理可否
     */
    private Boolean isRollbackTransaction(int result) {

        if (this.rollbackNoProcessParttonList.indexOf(result) >= 0) {
            return false;
        }
        return true;
    }

    /**
     * トランザクション処理が異常終了した場合に実行される。
     *
     */
    public void rollbackTransaction() {
        TransactionManagerConnection connection = DbConnectionContext.getTransactionManagerConnection("transaction");
        connection.rollback();
    }

    /**
     * REST処理のセポートクラスの実行
     *
     * @param requestData
     *            応答電文対象
     *
     * @return HTTPレスポンス
     */
    protected abstract int executeLogic(T requestData);

    /**
     * REST処理のセポートクラス論理チェック。
     *
     * @param requestData
     *            応答電文対象
     *
     * @return HTTPレスポンス
     */
    protected boolean executeCheckLogic(T requestData) {
        return true;
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
    protected abstract HttpResponse responseBuilder(T requestData, int result) throws IOException;

    /**
     *
     *
     * @param <F>
     *            応答電文対象
     * @param responseOpalData
     *            応答電文対象
     * @param result
     *            要求電文
     *
     * @return 実行の結果
     * @throws IOException
     *             IO異常
     */
    protected <F> HttpResponse setHttpResponseData(F responseOpalData, int result) throws IOException {

        // 応答電文（OPAL制御データ）初期化
        this.initRespOpalControlData();

        OpalJackson2BodyConverter converter = new OpalJackson2BodyConverter();
        String jsonOpalData = converter.writeValue(responseOpalData);

        StringBuilder sbResponseData = new StringBuilder(jsonOpalData);
        String jsonOpalControlData = converter.writeValue(this.respOpalControlData);

        sbResponseData.insert(1, ",");
        sbResponseData.insert(1, jsonOpalControlData);
        sbResponseData.insert(1, "\"opalControlData\":");

        // スレッドコンテキスト変数管理追加（応答電文）⇒ ログ用
        ThreadContext.setObject(RESPONSE_DATA, sbResponseData);

        InputStream bodyStream = new ByteArrayInputStream(sbResponseData.toString().getBytes());

        HttpResponse response = new HttpResponse();
        response.setStatusCode(HttpResponse.Status.OK.getStatusCode());
        response.setContentType("application/json; charset=UTF-8");
        response.setHeader("reason-phrase", getReasonPhrase(result));
        response.setHeader("Content-Length", String.valueOf(sbResponseData.toString().getBytes().length));

        response.setBodyStream(bodyStream);

        return response;
    }

    /**
     * 応答電文（OPAL制御データ）初期化
     */
    private void initRespOpalControlData() {

        // 要求電文ヘッダ取得
        this.reqOpalControlData = (RequestOpalControlData) ThreadContext.getObject(OpalControlDataAttribute.KEY);

        // 応答電文（OPAL制御データ）設定
        this.respOpalControlData = new ResponseOpalControlData();

        // メッセージID←要求電文と同じ値を設定
        if (!StringUtil.isNullOrEmpty(this.reqOpalControlData.getMessageId())) {
            this.respOpalControlData.setMessageId(this.reqOpalControlData.getMessageId());
        }
        // 関連メッセージID←要求電文と同じ値を設定
        if (!StringUtil.isNullOrEmpty(this.reqOpalControlData.getCorrelationId())) {
            this.respOpalControlData.setCorrelationId(this.reqOpalControlData.getCorrelationId());
        }
        // リクエストID←要求電文と同じ値を設定
        this.respOpalControlData.setRequestId(this.reqOpalControlData.getRequestId());
    }

    /**
     * HTTPレスポンスを設定する。
     *
     * @param result
     *            アプリ会員情報更新要求電文
     *
     * @return HTTP StatusCode
     */
    protected String getReasonPhrase(int result) {
        // 処理正常の場合、
        String reasonPhrase = String.valueOf(HttpResponse.Status.OK);
        return reasonPhrase;
    }

    /**
     * 処理結果コードを設定する。
     *
     * @param result
     *            要求電文
     *
     * @return 処理結果コード（1:異常終了 0:正常終了）
     */
    protected String getResultCode(int result) {
        String resultCode = "";
        if (result == CheckResultConstants.CHECK_OK) {
            // 処理正常の場合、
            resultCode = "0";
        } else {
            // 以外の場合、
            resultCode = "1";
        }
        return resultCode;
    }

}
