package jp.co.tis.opal.batch.ss151A;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import nablarch.core.dataformat.SimpleDataConvertResult;
import nablarch.core.date.SystemTimeUtil;
import nablarch.core.db.connection.AppDbConnection;
import nablarch.core.db.statement.ParameterizedSqlPStatement;
import nablarch.core.db.statement.SqlRow;
import nablarch.core.db.transaction.SimpleDbTransactionExecutor;
import nablarch.core.db.transaction.SimpleDbTransactionManager;
import nablarch.core.repository.SystemRepository;
import nablarch.core.util.DateUtil;
import nablarch.core.util.StringUtil;
import nablarch.fw.DataReader;
import nablarch.fw.ExecutionContext;
import nablarch.fw.Result;
import nablarch.fw.Result.Success;
import nablarch.fw.action.BatchAction;
import nablarch.fw.launcher.CommandLine;
import nablarch.fw.messaging.MessagingException;
import nablarch.fw.messaging.realtime.http.dto.HttpResult;
import nablarch.fw.messaging.realtime.http.exception.HttpMessagingTimeoutException;
import nablarch.fw.reader.DatabaseRecordReader;
import nablarch.fw.results.TransactionAbnormalEnd;

import jp.co.tis.opal.batch.common.exception.HttpMessagingTimeoutRetryunableException;
import jp.co.tis.opal.batch.common.fw.OpalCurumeruHttpClient;
import jp.co.tis.opal.common.constants.OpalCodeConstants;
import jp.co.tis.opal.common.constants.OpalDefaultConstants;

/**
 * B151A011:メール配信個別指示のアクションクラス。
 *
 * @author 曹
 * @since 1.0
 */
public class B151A011Action extends BatchAction<SqlRow> {

    /** バッチ処理ID */
    private static final String BATCH_PROCESS_ID = "B151A011";

    /** バッチリクエストID */
    private static final String BATCH_REQUEST_ID = "RB151A0110";

    /** Content-Type **/
    private static final String REQ_CONTENT_TYPE = "multipart/form-data; boundary=boundary; charset=UTF-8";

    /** Accept **/
    private static final String RES_ACCEPT = "application/xml; charset=UTF-8";

    /** 入力データ件数(メール配信個別指示処理対象件数) */
    private int intputCount;

    /** 出力データ件数(メール個別配信情報)(更新) */
    private int updateCount;
    private int tempUpdateCount;

    /** 一度の取得件数 */
    private int getCount;

    /** 処理データ件数 */
    private int currentDataCount;

    /** コミット間隔 */
    private int commitInterval;

    /**
     * {@inneritDoc}
     * <p/>
     * 事前処理
     */
    @Override
    protected void initialize(CommandLine command, ExecutionContext context) {

        // 処理ログの初期化
        intputCount = 0;
        updateCount = 0;
        tempUpdateCount = 0;

        // 取得件数
        getCount = Integer.parseInt(SystemRepository.getString("lite_mail_inv_count"));

        // コミット間隔を取得
        commitInterval = Integer.parseInt(SystemRepository.getString("nablarch.loopHandler.commitInterval"));
    }

    /**
     * {@inneritDoc}
     * <p/>
     * メール個別配信情報取得。
     */
    @Override
    public DataReader<SqlRow> createReader(ExecutionContext ctx) {

        // メール個別配信情報取得用のSQLの条件を設定する
        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("mailDeliverStatus", OpalCodeConstants.MailDeliverStatus.MAIL_DELIVER_STATUS_1);
        condition.put("getCount", getCount);

        // 入力データ件数取得
        intputCount = countByParameterizedSql("SELECT_MAIL_LITE_DELIVER_INFO", condition);
        // 取得データ件数(メール個別配信情報)をログに出力する。
        writeLog("MB151A0101", Integer.valueOf(intputCount));

        // メール個別配信情報を取得。
        DatabaseRecordReader reader = new DatabaseRecordReader();
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("SELECT_MAIL_LITE_DELIVER_INFO");
        reader.setStatement(statement, condition);

        return reader;
    }

    /**
     * {@inneritDoc}
     * <p/>
     * 本処理
     */
    @Override
    public Result handle(SqlRow inputData, ExecutionContext ctx) {
        // メール個別配信ID
        String mailLiteDeliverId = inputData.getString("MAIL_LITE_DELIVER_ID");
        // WebAPI実行結果
        HttpResult httpResult = null;
        // 応答電文
        SimpleDataConvertResult responseMessage = null;
        // 配信サービスメールID
        String deliverServiceMailId = null;
        // Exception発生フラグ
        boolean exceptionOccured = false;
        // API連携結果コード
        String code = null;

        try {
            // HTTP通信情報設定
            OpalCurumeruHttpClient httpClient = (OpalCurumeruHttpClient) SystemRepository
                    .getObject("opalCurumeruHttpClient");
            httpClient.initialize(BATCH_REQUEST_ID);
            httpClient.setAccept(RES_ACCEPT);
            httpClient.setRequestContentType(REQ_CONTENT_TYPE);

            // 要求電文body取得
            String reqMsgBody = getRequestMessageBody(inputData);

            // HTTP通信実行
            httpResult = httpClient.execute(reqMsgBody);
            responseMessage = httpClient.bodyStringToMap(httpClient.getTargetUri(), httpResult);
            if (responseMessage == null) {
                exceptionOccured = true;
                // メール配信個別情報更新
                updateMailLiteDeliverInfo(mailLiteDeliverId, OpalCodeConstants.MailDeliverStatus.MAIL_DELIVER_STATUS_5,
                        deliverServiceMailId, exceptionOccured);
                updateCount++;
                throw new TransactionAbnormalEnd(103, "AB151A0104");
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> resultMap = (Map<String, Object>) responseMessage.getResultMap();
            if (resultMap.containsKey("data.lite_mail_id")) {
                deliverServiceMailId = resultMap.get("data.lite_mail_id").toString();
            }
            code = resultMap.get("code").toString();

        } catch (HttpMessagingTimeoutException e) {
            exceptionOccured = true;
            // メール配信個別情報更新
            updateMailLiteDeliverInfo(mailLiteDeliverId, OpalCodeConstants.MailDeliverStatus.MAIL_DELIVER_STATUS_3,
                    deliverServiceMailId, exceptionOccured);

            updateCount++;
            throw new HttpMessagingTimeoutRetryunableException(100, "AB151A0101", new Object[] { mailLiteDeliverId });

        } catch (MessagingException e) {
            exceptionOccured = true;
            // メール配信個別情報更新
            updateMailLiteDeliverInfo(mailLiteDeliverId, OpalCodeConstants.MailDeliverStatus.MAIL_DELIVER_STATUS_4,
                    deliverServiceMailId, exceptionOccured);

            updateCount++;
            throw new TransactionAbnormalEnd(101, "AB151A0102", mailLiteDeliverId, e.getClass());

        }
        // API連携結果コードが"10200"（成功）以外の場合、例外処理を行う。
        if (!OpalDefaultConstants.RESPONSE_CODE.equals(code)) {
            exceptionOccured = true;
            // メール配信個別情報更新
            updateMailLiteDeliverInfo(mailLiteDeliverId, OpalCodeConstants.MailDeliverStatus.MAIL_DELIVER_STATUS_5,
                    deliverServiceMailId, exceptionOccured);

            updateCount++;
            throw new TransactionAbnormalEnd(102, "AB151A0103", mailLiteDeliverId, code,
                    responseMessage.getResultMap().get("status"), responseMessage.getResultMap().get("message"));
        }

        // メール配信個別情報更新
        updateMailLiteDeliverInfo(mailLiteDeliverId, OpalCodeConstants.MailDeliverStatus.MAIL_DELIVER_STATUS_2,
                deliverServiceMailId, exceptionOccured);
        // 処理データ件数をカウントアップする。
        currentDataCount++;
        // コミット件数取得
        if (currentDataCount == intputCount || currentDataCount % commitInterval == 0) {
            updateCount = tempUpdateCount;
        }

        return new Success();
    }

    /**
     * {@inneritDoc}
     * <p/>
     * 事後処理
     */
    @Override
    protected void terminate(Result result, ExecutionContext context) {
        // 更新データ件数(メール個別配信情報)をログに出力する。
        writeLog("MB151A0102", Integer.valueOf(updateCount));
    }

    /**
     * 要求電文BODYを生成する。
     *
     * @param inputData
     *            処理対象データ
     * @return String 要求電文BODY
     */
    private String getRequestMessageBody(SqlRow inputData) {

        Map<String, String> bodyMap = new LinkedHashMap<String, String>();

        // 接続用パスワード
        bodyMap.put("transport_password", SystemRepository.getString("curumeru_tansport_password"));
        // 文字コード
        bodyMap.put("charset", "1");
        // 取得形式
        bodyMap.put("return_format", "xml");
        // 宛先（to）
        bodyMap.put("to_address", inputData.getString("MAIL_ADDRESS"));
        // Fromのメールアドレス
        bodyMap.put("from_address", inputData.getString("FROM_ADDRESS"));
        // Fromの差出人名
        bodyMap.put("from_name", inputData.getString("FROM_NAME"));
        // 顧客情報
        String replacementData = getReplacementData(inputData);
        if (replacementData != null) {
            bodyMap.put("replacement_data", replacementData);
        }
        // 件名
        bodyMap.put("subject", inputData.getString("SUBJECT"));
        // 本文（テキストパート）
        bodyMap.put("text_part", inputData.getString("BODY"));
        // 予約種別
        String deliverType = inputData.getString("DELIVER_TYPE");
        bodyMap.put("schedule_type", deliverType);
        // 配信日時
        if (OpalCodeConstants.MailDeliverType.MAIL_DELIVER_TYPE_2.equals(deliverType)) {
            bodyMap.put("schedule_date",
                    DateUtil.formatDate(inputData.getDate("DELIVER_DATE"), OpalDefaultConstants.DELIVER_DATE_FORMAT));
        }
        // レポートオプション
        bodyMap.put("report_option", "0");

        StringBuilder requestMessage = new StringBuilder();
        for (String key : bodyMap.keySet()) {
            requestMessage.append("--boundary\r\n");
            requestMessage.append("Content-Disposition: form-data; name=\"").append(key).append("\"\r\n\r\n");
            requestMessage.append(bodyMap.get(key)).append("\r\n");
        }
        requestMessage.append("--boundary\r\n");

        return requestMessage.toString();
    }

    /**
     * 差し込み文字（顧客情報）を生成する。
     *
     * @param inputData
     *            処理対象データ
     * @return 差し込み文字（顧客情報）
     */
    private String getReplacementData(SqlRow inputData) {

        String replacementData = null;
        StringBuilder itemName = new StringBuilder();
        StringBuilder itemValue = new StringBuilder();

        for (int i = 1; i <= 10; i++) {
            String itemNameTmp = inputData.getString("VARIABLE_ITEM_NAME_" + i);
            if (StringUtil.isNullOrEmpty(itemNameTmp)) {
                break;
            }
            if (i > 1) {
                itemName.append(",");
                itemValue.append(",");
            }
            // 項目目設定
            itemName.append("\"").append(itemNameTmp).append("\"");
            // 項目値設定
            itemValue.append("\"").append(inputData.getString("VARIABLE_ITEM_VALUE_" + i)).append("\"");
        }

        if (itemName.length() > 0) {
            replacementData = itemName.toString() + "\r\n" + itemValue.toString();
        }
        return replacementData;
    }

    /**
     * メール個別配信情報更新を行う。
     *
     * @param mailLiteDeliverId
     *            メール個別配信ID
     * @param mailDeliverStatus
     *            メール配信状況
     * @param deliverServiceMailId
     *            配信サービスメールID
     * @param exceptionOccured
     *            Exception発生フラグ
     */
    private void updateMailLiteDeliverInfo(String mailLiteDeliverId, String mailDeliverStatus,
            String deliverServiceMailId, Boolean exceptionOccured) {

        // メール個別配信情報更新用のSQLの条件を設定する
        Map<String, Object> condition = new HashMap<String, Object>();
        // メール個別配信ID
        condition.put("mailLiteDeliverId", mailLiteDeliverId);
        // メール配信状況
        condition.put("mailDeliverStatus", mailDeliverStatus);
        // 配信サービスメールID
        condition.put("deliverServiceMailId", deliverServiceMailId);
        // 最終更新者ID
        condition.put("updateUserId", BATCH_PROCESS_ID);
        // 最終更新日時
        condition.put("updateDatetime", SystemTimeUtil.getTimestamp());

        if (exceptionOccured) {
            // システムリポジトリからSimpleDbTransactionManagerを取得する
            SimpleDbTransactionManager dbTransactionManager = SystemRepository.get("update-mail-transaction");

            // SimpleDbTransactionManagerをコンストラクタに指定して実行する
            tempUpdateCount += new SimpleDbTransactionExecutor<Integer>(dbTransactionManager) {
                @Override
                public Integer execute(AppDbConnection connection) {
                    ParameterizedSqlPStatement statement = connection.prepareParameterizedSqlStatementBySqlId(
                            "jp.co.tis.opal.batch.ss151A.B151A011Action#UPDATE_MAIL_LITE_DELIVER_INFO");
                    return statement.executeUpdateByMap(condition);

                }
            }.doTransaction();
        } else {
            ParameterizedSqlPStatement statement = getParameterizedSqlStatement("UPDATE_MAIL_LITE_DELIVER_INFO");
            tempUpdateCount += statement.executeUpdateByMap(condition);
        }
    }
}
