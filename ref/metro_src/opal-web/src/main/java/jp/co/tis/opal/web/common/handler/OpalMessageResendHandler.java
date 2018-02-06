package jp.co.tis.opal.web.common.handler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import nablarch.core.ThreadContext;
import nablarch.core.date.SystemTimeUtil;
import nablarch.core.db.connection.DbConnectionContext;
import nablarch.core.db.statement.ResultSetIterator;
import nablarch.core.db.statement.SqlPStatement;
import nablarch.core.db.statement.SqlRow;
import nablarch.core.db.statement.exception.DuplicateStatementException;
import nablarch.core.log.Logger;
import nablarch.core.log.LoggerManager;
import nablarch.core.repository.SystemRepository;
import nablarch.core.util.StringUtil;
import nablarch.fw.ExecutionContext;
import nablarch.fw.Handler;
import nablarch.fw.web.HttpErrorResponse;
import nablarch.fw.web.HttpRequest;
import nablarch.fw.web.HttpResponse;

import jp.co.tis.opal.common.component.CM010004Component;
import jp.co.tis.opal.web.common.RequestData.RequestOpalControlData;
import jp.co.tis.opal.web.common.ResponseData.ResponseOpalControlData;
import jp.co.tis.opal.web.common.handler.ThreadContext.OpalControlDataAttribute;
import jp.co.tis.opal.web.common.rest.OpalJackson2BodyConverter;

/**
 * OPAL再送電文制御ハンドラのクラス。
 *
 * @author 張
 * @since 1.0
 */
public class OpalMessageResendHandler implements Handler<HttpRequest, HttpResponse> {
    /** ロガー */
    protected static final Logger LOGGER = LoggerManager.get(OpalMessageResendHandler.class);
    /** 応答電文データ保持期間(日) */
    private static final String RETENTION_PERIOD = "response_message_data_retention_period";
    /** ロガー */
    public static final String RESPONSE_DATA = "responseData";

    /** 再送APIリスト */
    private String resendAPIList;
    /** リクエストId */
    private String requestId;
    /** テーブル名 */
    private String tableName;
    /** メッセージ */
    private String messageId;
    /** 電文ボディ */
    private String bodyData;
    /** 電文ヘッド */
    private String headData;
    /** スタータスコード */
    private String statusCode;
    /** 論理削除日 */
    private String deletedDate;

    /** 発信済みメッセージの調査 */
    private String findAlreadySentMessageQuery = null;
    /** 新しいのメッセージの登録 */
    private String insertNewSentMessageDml = null;

    /**
     * テーブル名を設定する。
     *
     * @param tableName
     *            テーブル名
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    /**
     * メッセージを設定する。
     *
     * @param messageId
     *            メッセージ
     */
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    /**
     * 電文ボディを設定する。
     *
     * @param bodyData
     *            電文ボディ
     */
    public void setBodyData(String bodyData) {
        this.bodyData = bodyData;
    }

    /**
     * 電文ヘッドを設定する。
     *
     * @param headData
     *            電文ヘッド
     */
    public void setHeadData(String headData) {
        this.headData = headData;
    }

    /**
     * リクエストIdを設定する。
     *
     * @param requestId
     *            リクエストId
     */
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    /**
     * スタータスコードを設定する。
     *
     * @param statusCode
     *            スタータスコード
     */
    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * 論理削除日を設定する。
     *
     * @param deletedDate
     *            論理削除日
     */
    public void setDeletedDate(String deletedDate) {
        this.deletedDate = deletedDate;
    }

    /**
     * 再送APIリストを設定する。
     *
     * @param resendAPIList
     *            再送APIリスト
     */
    public void setResendAPIList(String resendAPIList) {
        this.resendAPIList = resendAPIList;
    }

    /**
     * OPAL再送電文初期化
     */
    public synchronized void initialize() {
        if (this.findAlreadySentMessageQuery != null) {
            return;
        }

        StringBuilder selectSQL = new StringBuilder();
        selectSQL.append("SELECT ");
        selectSQL.append(this.messageId).append(" AS messageId, ");
        selectSQL.append(this.requestId).append(" AS requestId, ");
        selectSQL.append(this.bodyData).append(" AS bodyData, ");
        selectSQL.append(this.headData).append(" AS headData ");
        selectSQL.append("FROM ");
        selectSQL.append(this.tableName);
        selectSQL.append(" WHERE ");
        selectSQL.append(this.messageId).append(" = ? ");

        this.findAlreadySentMessageQuery = selectSQL.toString();

        if (this.insertNewSentMessageDml != null) {
            return;
        }

        StringBuilder insertSQL = new StringBuilder();
        insertSQL.append("INSERT INTO ");
        insertSQL.append(this.tableName);
        insertSQL.append("(");
        insertSQL.append(this.messageId).append(", ");
        insertSQL.append(this.requestId).append(", ");
        insertSQL.append(this.statusCode).append(", ");
        insertSQL.append(this.headData).append(", ");
        insertSQL.append(this.bodyData).append(", ");
        insertSQL.append("INSERT_USER_ID, ");
        insertSQL.append("INSERT_DATE_TIME, ");
        insertSQL.append("UPDATE_USER_ID, ");
        insertSQL.append("UPDATE_DATE_TIME, ");
        insertSQL.append("DELETED_FLG, ");
        insertSQL.append(this.deletedDate);
        insertSQL.append(") VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )");

        this.insertNewSentMessageDml = insertSQL.toString();
    }

    /**
     * 再送信処理制御ハンドラ
     *
     * @param request
     *            HTTPリクエスト
     * @param context
     *            実行コンテキスト
     * @return 結果オブジェクト
     */
    @Override
    public HttpResponse handle(HttpRequest request, ExecutionContext context) {
        Object controlData = ThreadContext.getObject(OpalControlDataAttribute.KEY);
        if (controlData == null) {
            return ((HttpResponse) context.handleNext(request));
        }

        if (this.findAlreadySentMessageQuery == null) {
            this.initialize();
        }

        // 要求電文ヘッダ取得
        RequestOpalControlData reqOpalControlData = new RequestOpalControlData();
        reqOpalControlData = (RequestOpalControlData) controlData;

        // 更新処理が発生する処理のみ有効
        if (!this.checkResendObject(ThreadContext.getRequestId())) {
            HttpResponse response = ((HttpResponse) context.handleNext(request));
            return response;
        }

        // 再送電文が否か（0:初回 1:再送）
        if (!"1".equals(reqOpalControlData.getResendFlag())) {
            // 初回電文
            HttpResponse response = ((HttpResponse) context.handleNext(request));
            // 再送信応答電文の保存
            this.saveReply(response, reqOpalControlData);
            return response;
        }

        // （「再送信フラグ」が「1:再送信」の場合）
        // 応答電文が作成済み
        HttpResponse reply = this.getAlreadySentReply(request, context, reqOpalControlData);
        if (reply != null) {

            OpalJackson2BodyConverter converter = new OpalJackson2BodyConverter();

            String replyBody = reply.getBodyString();

            ResponseOpalControlData replyResponse = new ResponseOpalControlData();
            // メッセージID
            replyResponse.setMessageId(reqOpalControlData.getMessageId());
            // 関連メッセージID
            replyResponse.setCorrelationId(reqOpalControlData.getCorrelationId());
            // リクエストID
            replyResponse.setRequestId(reqOpalControlData.getRequestId());

            String responseOpalControlData = "";
            try {
                responseOpalControlData = converter.writeValue(replyResponse);
            } catch (IOException ex) {
                LOGGER.logError(ex.getMessage(), ex);
                throw new HttpErrorResponse(ex);
            }

            // 応答電文取得
            StringBuilder bodyData = new StringBuilder();
            bodyData.append("{\"opalControlData\":");
            bodyData.append(responseOpalControlData);
            bodyData.append(replyBody.substring(replyBody.indexOf("},") + 1));

            // スレッドコンテキスト変数管理追加（応答電文）⇒ ログ用
            ThreadContext.setObject(RESPONSE_DATA, bodyData.toString());

            InputStream bodyStream = new ByteArrayInputStream(bodyData.toString().getBytes());
            reply.setBodyStream(bodyStream);
            return reply;
        }
        reply = (HttpResponse) context.handleNext(request);
        try {
            // 再送信応答電文の保存
            this.saveReply(reply, reqOpalControlData);
            return reply;
        } catch (DuplicateStatementException e) {
            throw e;
        }
    }

    /**
     * 再送信処理チェック
     *
     * @param requestId
     *            リクエストId
     * @return 結果オブジェクト
     */
    public boolean checkResendObject(String requestId) {
        if (StringUtil.isNullOrEmpty(this.resendAPIList)) {
            return false;
        }

        if (this.resendAPIList.indexOf(requestId) == -1) {
            return false;
        }
        return true;
    }

    /**
     * 再送信応答電文の保存
     *
     * @param response
     *            応答電文
     * @param reqOpalControlData
     *            OPAL制御データ
     */
    public void saveReply(HttpResponse response, RequestOpalControlData reqOpalControlData) {
        String messageId = ("1".equals(reqOpalControlData.getResendFlag())) ? reqOpalControlData.getCorrelationId()
                : reqOpalControlData.getMessageId();

        Map<String, Object> record = new HashMap<String, Object>();
        record.put("MESSAGE_ID", messageId);
        record.put("REQUEST_ID", reqOpalControlData.getRequestId());
        record.put("STATUS_CODE", response.getStatusCode());
        record.put("HEAD_DATA", setHeadData(response).getBytes());
        record.put("BODY_DATA", response.getBodyString().getBytes());
        record.put("INSERT_USER_ID", reqOpalControlData.getRequestId().substring(1, 9));
        record.put("INSERT_DATE_TIME", SystemTimeUtil.getTimestamp());
        record.put("UPDATE_USER_ID", reqOpalControlData.getRequestId().substring(1, 9));
        record.put("UPDATE_DATE_TIME", SystemTimeUtil.getTimestamp());
        record.put("DELETED_FLG", "0");
        record.put("DELETED_DATE", this.getDeletedDate());

        insertNewSentMessage(record);
    }

    /**
     * 論理削除日（日単位）を導出する。
     *
     * @return 論理削除日
     */
    private String getDeletedDate() {
        int daySpan = Integer.valueOf(SystemRepository.get(RETENTION_PERIOD));

        CM010004Component cm010004Component = new CM010004Component();
        String deletedDate = cm010004Component.getDeletedDateDaily(daySpan);

        return deletedDate;
    }

    /**
     * ヘッドデータを設定する。
     *
     * @param response
     *            応答電文
     * @return ヘッドデータ
     */
    private String setHeadData(HttpResponse response) {
        StringBuilder sb = new StringBuilder();

        sb.append("Status-Code#");
        sb.append(response.getStatusCode());
        sb.append("@@@");
        sb.append("Content-Type#");
        sb.append(response.getContentType());
        sb.append("@@@");
        sb.append("reason-phrase#");
        sb.append(HttpResponse.Status.valueOfCode(response.getStatusCode()).toString());
        sb.append("@@@");
        sb.append("Content-Length#");
        sb.append(response.getBodyString().getBytes().length);
        sb.append("@@@");
        sb.append("Content-Path#");
        sb.append(ThreadContext.getObject("CONTENT_PATH").toString());
        sb.append("@@@");

        return sb.toString();
    }

    /**
     * 新発信メッセージの登録
     *
     * @param values
     *            電文
     */
    public void insertNewSentMessage(Map<String, Object> values) {
        SqlPStatement stmt = DbConnectionContext.getConnection().prepareStatement(this.insertNewSentMessageDml);

        stmt.setString(1, values.get("MESSAGE_ID").toString());
        stmt.setString(2, values.get("REQUEST_ID").toString());
        stmt.setString(3, values.get("STATUS_CODE").toString());
        stmt.setBytes(4, (byte[]) values.get("HEAD_DATA"));
        stmt.setBytes(5, (byte[]) values.get("BODY_DATA"));
        stmt.setString(6, values.get("INSERT_USER_ID").toString());
        stmt.setObject(7, values.get("INSERT_DATE_TIME"));
        stmt.setString(8, values.get("UPDATE_USER_ID").toString());
        stmt.setObject(9, values.get("UPDATE_DATE_TIME"));
        stmt.setString(10, values.get("DELETED_FLG").toString());
        stmt.setString(11, values.get("DELETED_DATE").toString());

        stmt.execute();
    }

    /**
     * 発信済みメッセージの取得
     *
     * @param request
     *            HTTPリクエスト
     * @param context
     *            実行コンテキスト
     * @param reqOpalControlData
     *            OPAL制御データ
     * @return 応答電文
     * @throws Exception
     *             異常
     */
    public HttpResponse getAlreadySentReply(HttpRequest request, ExecutionContext context,
            RequestOpalControlData reqOpalControlData) {
        String correlationId = reqOpalControlData.getCorrelationId();
        if (StringUtil.isNullOrEmpty(correlationId)) {
            LOGGER.logInfo("correlationId was not set. correlationId must be set.");
            throw new HttpErrorResponse(400);
        }

        // 発信済みメッセージの調査
        SqlRow record = findAlreadySentMessage(correlationId);
        if (record != null) {
            Map<String, String> recordMap = getDataMap(record);

            // リクエストIDは電文のリクエストIDと一致している必要があります。
            if (!record.getString("requestId").equals(reqOpalControlData.getRequestId())) {
                LOGGER.logInfo("requestId was not existed.");
                throw new HttpErrorResponse(400);
            }

            // 既に応答電文が作成済みの場合、保存した応答電文の送信処理
            HttpResponse response = new HttpResponse();
            response.setStatusCode(Integer.valueOf(recordMap.get("Status-Code")));
            response.setContentType(recordMap.get("Content-Type"));
            response.setHeader("reason-phrase", recordMap.get("reason-phrase"));
            response.setHeader("Content-Length", recordMap.get("Content-Length"));
            ThreadContext.setObject("CONTENT_PATH", recordMap.get("Content-Path"));

            InputStream bodyStream = new ByteArrayInputStream(recordMap.get("body").getBytes());
            response.setBodyStream(bodyStream);

            return response;
        } else {
            return null;
        }
    }

    /**
     * HTTPレスポンスを設定する。
     *
     * @param record
     *            リクエストパラメータ
     * @return HTTPレスポンス
     */
    private Map<String, String> getDataMap(SqlRow record) {
        Map<String, String> head = new HashMap<String, String>();

        // 電文ヘッダ部
        String headData = new String(record.getBytes("headData"));
        for (String value : headData.split("@{3}")) {
            String[] headStr = value.split("#");
            head.put(headStr[0], headStr[1]);
        }

        // 電文データ部
        String bodyData = new String(record.getBytes("bodyData"));
        head.put("body", bodyData);
        return head;
    }

    /**
     * 発信済みメッセージの調査
     *
     * @param messageId
     *            メッセージID
     * @return 結果オブジェクト
     */
    public SqlRow findAlreadySentMessage(String messageId) {
        SqlPStatement stmt = DbConnectionContext.getConnection().prepareStatement(this.findAlreadySentMessageQuery);

        stmt.setString(1, messageId);
        ResultSetIterator results = stmt.executeQuery();
        if (!(results.next())) {
            return null;
        }
        return results.getRow();
    }
}
