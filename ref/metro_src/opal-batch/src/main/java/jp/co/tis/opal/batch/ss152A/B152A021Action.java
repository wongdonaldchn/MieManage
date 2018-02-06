package jp.co.tis.opal.batch.ss152A;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import nablarch.common.code.CodeUtil;
import nablarch.core.date.SystemTimeUtil;
import nablarch.core.db.connection.AppDbConnection;
import nablarch.core.db.connection.DbConnectionContext;
import nablarch.core.db.statement.ParameterizedSqlPStatement;
import nablarch.core.db.statement.SqlResultSet;
import nablarch.core.db.statement.SqlRow;
import nablarch.core.db.transaction.SimpleDbTransactionExecutor;
import nablarch.core.db.transaction.SimpleDbTransactionManager;
import nablarch.core.message.ApplicationException;
import nablarch.core.repository.SystemRepository;
import nablarch.core.util.StringUtil;
import nablarch.core.validation.ee.ValidatorUtil;
import nablarch.fw.DataReader;
import nablarch.fw.ExecutionContext;
import nablarch.fw.Result;
import nablarch.fw.Result.Success;
import nablarch.fw.action.BatchAction;
import nablarch.fw.launcher.CommandLine;
import nablarch.fw.messaging.MessageSender;
import nablarch.fw.messaging.MessagingException;
import nablarch.fw.messaging.SyncMessage;
import nablarch.fw.messaging.realtime.http.exception.HttpMessagingTimeoutException;
import nablarch.fw.reader.DatabaseRecordReader;
import nablarch.fw.results.TransactionAbnormalEnd;

import jp.co.tis.opal.common.constants.OpalCodeConstants;
import jp.co.tis.opal.common.constants.OpalDefaultConstants;
import jp.co.tis.opal.common.utility.DateConvertUtil;

/**
 * B152A021:プッシュ通知一括指示のアクションクラス。
 *
 * @author 曹
 * @since 1.0
 */
public class B152A021Action extends BatchAction<SqlRow> {

    /** 入力データ件数(プッシュ通知一括指示処理対象件数) */
    private int intputCountPushNotice;

    /** 出力データ件数(プッシュ通知情報)(更新) */
    private int updateCountPushNotice;
    private int tempUpdateCountPushNotice;

    /** 例外発生する場合プッシュ通知情報更新件数 */
    private int exceptionUpdateCount;

    /** 処理データ件数 */
    private int currentDataCount;

    /** コミット間隔 */
    private int commitInterval;

    /** 処理ID */
    private String opalProcessId;

    /** タイムアウト例外発生したデータ件数 */
    private int timeOutDataCount;

    /** 連続タイムアウト例外回数 */
    private int continueTimeOutDataCount;

    /** バッチ処理ID */
    private static final String BATCH_PROCESS_ID = "B152A021";

    /** 処理ID */
    private static final String OPAL_PROCESS_ID = "opalProcessId";

    /** ステータス */
    private static final String STATUS_OK = "OK";

    /** 処理済フラグ：2(タイムアウト) */
    private static final String PROCESSED_FLAG_2 = "2";

    /** 処理済フラグ：3(HTTP送信エラー) */
    private static final String PROCESSED_FLAG_3 = "3";

    /** 処理済フラグ：4(API連携エラー) */
    private static final String PROCESSED_FLAG_4 = "4";

    /** プッシュ通知送信タイプのコードID：C1500005 */
    private static final String DELIVER_TYPE_CODE_ID = "C1500005";

    /**
     * {@inneritDoc}
     * <p/>
     * 事前処理
     */
    @Override
    protected void initialize(CommandLine command, ExecutionContext context) {

        // 処理ログの初期化
        intputCountPushNotice = 0;
        updateCountPushNotice = 0;
        tempUpdateCountPushNotice = 0;
        timeOutDataCount = 0;
        continueTimeOutDataCount = 0;
        exceptionUpdateCount = 0;

        // コミット間隔を取得
        commitInterval = Integer.parseInt(SystemRepository.getString("nablarch.loopHandler.commitInterval"));

        // 処理ID
        opalProcessId = command.getParamMap().get(OPAL_PROCESS_ID);
    }

    /**
     * {@inneritDoc}
     * <p/>
     * プッシュ通知情報取得。
     */
    @Override
    public DataReader<SqlRow> createReader(ExecutionContext ctx) {

        // プッシュ通知情報取得用のSQLの条件を設定する
        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("deliverDivision", OpalCodeConstants.PushNoticeDivision.PUSH_NOTICE_DIVISION_2);
        condition.put("opalProcessId", opalProcessId);

        // プッシュ通知情報取得
        AppDbConnection connection = DbConnectionContext.getConnection();
        ParameterizedSqlPStatement statement = connection.prepareParameterizedSqlStatementBySqlId(
                "jp.co.tis.opal.batch.ss152A.B152A021Action#SELECT_PUSH_NOTICE_INFORMATION", condition);
        DatabaseRecordReader reader = new DatabaseRecordReader();
        reader.setStatement(statement, condition);

        // 処理対象レコード件数を取得
        SqlResultSet result = statement.retrieve(condition);
        intputCountPushNotice = result.size();
        writeLog("MB152A0201", Integer.valueOf(intputCountPushNotice));

        return reader;
    }

    /**
     * {@inneritDoc}
     * <p/>
     * 本処理
     */
    @Override
    public Result handle(SqlRow inputData, ExecutionContext ctx) {

        // プッシュ通知ID
        String pushNoticeId = inputData.getString("PUSH_NOTICE_ID");

        // 処理ID
        opalProcessId = inputData.getString("OPAL_PROCESS_ID");

        // プッシュ通知送信先情報取得
        SqlResultSet pushNoticeDestInfo = getPushNoticeDestInfo(pushNoticeId);

        // プッシュ通知ID毎の送信先件数をログに出力する。
        writeLog("MB152A0202", pushNoticeId, opalProcessId, pushNoticeDestInfo.size());
        // 遅延時間取得
        int delayTime = Integer.parseInt(SystemRepository.getString("push_notice_instr_delaytime"));
        // 送信日時取得
        String systemDateTimeMillis = SystemTimeUtil.getDateTimeMillisString();
        String deliverTime = DateConvertUtil.addMinute(systemDateTimeMillis, delayTime,
                OpalDefaultConstants.DATE_TIME_FORMAT);
        String deliverDateTime = inputData.getString("DELIVER_DATE_TIME");
        if (deliverDateTime.compareTo(deliverTime) < 0) {
            deliverDateTime = deliverTime;
        }

        // 要求電文生成
        SyncMessage requestMessage = setRequestMessage(inputData, pushNoticeDestInfo, deliverDateTime);
        // 応答電文
        SyncMessage responseMessage = null;
        // ステータス
        String status = null;
        // id
        String id = null;
        // Exception発生フラグ
        Boolean exceptionOccured = false;
        try {
            // 要求電文送信
            responseMessage = MessageSender.sendSync(requestMessage);
            status = responseMessage.getDataRecord().get("status").toString();
            if (responseMessage.getDataRecord().get("result.id") != null) {
                id = responseMessage.getDataRecord().get("result.id").toString();
            }
        } catch (HttpMessagingTimeoutException e) {
            exceptionOccured = true;
            // プッシュ通知情報更新
            updatePushNoticeInformation(pushNoticeId, id, PROCESSED_FLAG_2, deliverDateTime, exceptionOccured);
            // タイムアウト例外発生をログに出力する。
            writeLog("MB152A0205", pushNoticeId);
            // タイムアウト例外発生件数をカウントアップする。
            timeOutDataCount++;
            // 連続タイムアウト例外回数をカウントアップする。
            continueTimeOutDataCount++;
            // 連続タイムアウト例外回数が、タイムアウト上限回数を超える場合、異常終了とする。
            if (continueTimeOutDataCount > Integer
                    .parseInt(SystemRepository.getString("push_notice_instr_timeout_limit_count"))) {

                // 業務処理異常終了例外（TransactionAbnormalEnd）を送出し、処理を終了する。
                throw new TransactionAbnormalEnd(100, "AB152A0201", continueTimeOutDataCount);
            }
        } catch (MessagingException e) {
            exceptionOccured = true;
            // プッシュ通知情報更新
            updatePushNoticeInformation(pushNoticeId, id, PROCESSED_FLAG_3, deliverDateTime, exceptionOccured);

            // 業務処理異常終了例外(TransactionAbnormalEnd)を送出し、処理を終了する。
            throw new TransactionAbnormalEnd(101, "AB152A0202", pushNoticeId, e.getClass());
        }
        // Exception発生しない場合
        if (!exceptionOccured) {

            // API連携例外処理
            if (!STATUS_OK.equals(status)) {
                exceptionOccured = true;
                // プッシュ通知情報更新
                updatePushNoticeInformation(pushNoticeId, id, PROCESSED_FLAG_4, deliverDateTime, exceptionOccured);

                // システムエラーをログに出力し、処理を異常終了する。
                throw new TransactionAbnormalEnd(102, "AB152A0203", pushNoticeId, status,
                        responseMessage.getDataRecord().get("error.code"),
                        responseMessage.getDataRecord().get("error.message"));
            }

            A152AAASBodyForm responseBodyForm = new A152AAASBodyForm();
            responseBodyForm.setStatus(status);
            responseBodyForm.setId(id);

            // 応答電文の項目精査を実施する。
            try {
                // バリデーションを実行する
                ValidatorUtil.validate(responseBodyForm);
            } catch (ApplicationException e) {
                // 精査エラーの場合、システムエラーをログに出力する。
                throw new TransactionAbnormalEnd(103, "AB152A0204");
            }
            // プッシュ通知実行結果のログ出力
            writeLog("MB152A0203", pushNoticeId, opalProcessId, status);
            // プッシュ通知情報更新
            updatePushNoticeInformation(pushNoticeId, id, OpalDefaultConstants.PROCESSED_FLAG_1, deliverDateTime,
                    exceptionOccured);
            // 連続タイムアウト例外回数を「0」に設定する。
            continueTimeOutDataCount = 0;

            // 処理データ件数をカウントアップする。
            currentDataCount++;
            // コミット件数取得
            if (currentDataCount == intputCountPushNotice || currentDataCount % commitInterval == 0) {
                updateCountPushNotice = tempUpdateCountPushNotice;
            }
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
        // 更新データ件数(プッシュ通知情報)をログに出力する。
        writeLog("MB152A0204", Integer.valueOf(updateCountPushNotice + exceptionUpdateCount));
        // タイムアウト異常が発生する場合、異常終了とする。
        if (timeOutDataCount > 0) {
            throw new TransactionAbnormalEnd(100, "AB152A0201", timeOutDataCount);
        }

    }

    /**
     * プッシュ通知送信先情報取得。
     *
     * @param pushNoticeId
     *            プッシュ通知ID
     * @return プッシュ通知送信先情報
     */
    private SqlResultSet getPushNoticeDestInfo(String pushNoticeId) {

        // プッシュ通知送信先情報取得用のSQLの条件を設定する
        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("pushNoticeId", pushNoticeId);

        // プッシュ通知送信先情報取得
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("SELECT_PUSH_NOTICE_DEST_INFO");
        return statement.retrieve(condition);
    }

    /**
     * 要求電文生成。
     *
     * @param inputData
     *            プッシュ通知情報
     * @param pushNoticeDestInfo
     *            プッシュ通知送信先情報
     * @param deliverDateTime
     *            送信日時
     * @return 要求電文
     */
    private SyncMessage setRequestMessage(SqlRow inputData, SqlResultSet pushNoticeDestInfo, String deliverDateTime) {

        // 要求電文生成
        StringBuilder requestMessage = new StringBuilder();
        // 送信タイプ
        requestMessage.append("{\"type\":\"");
        requestMessage.append(CodeUtil.getShortName(DELIVER_TYPE_CODE_ID, inputData.getString("DELIVER_TYPE")));
        requestMessage.append("\",");
        // コンテキストタイプ
        requestMessage.append("\"content_type\":\"text/html\",");
        // 配信対象プラットフォーム
        requestMessage.append("\"platform\":[\"android\",\"iphone\"],");
        // フリーワード
        requestMessage.append("\"popup\":\"");
        requestMessage.append(inputData.getString("FREE_WORD"));
        requestMessage.append("\",");
        // 件名
        requestMessage.append("\"title\":\"");
        requestMessage.append(inputData.getString("SUBJECT"));
        requestMessage.append("\",");
        // 本文
        requestMessage.append("\"content\":\"");
        requestMessage.append(inputData.getString("BODY"));
        requestMessage.append("\",");
        // 配信ユーザーIDデータ
        requestMessage.append("\"target_id\":{");
        // デバイスID
        String device = null;
        // アプリIDリスト
        List<String> applicationIdList = new ArrayList<String>();
        // 配信ユーザーID
        for (int j = 0; j < pushNoticeDestInfo.size(); j++) {
            // デバイスID
            String deviceId = pushNoticeDestInfo.get(j).getString("DEVICE_ID");
            // アプリID
            StringBuilder applicationId = new StringBuilder();
            applicationId.append("\"");
            applicationId.append(pushNoticeDestInfo.get(j).getString("APPLICATION_ID"));
            applicationId.append("\"");
            // 処理データが一番目じゃないかつ、デバイスIDが変わる場合、
            if (!StringUtil.isNullOrEmpty(device) && !deviceId.equals(device)) {
                requestMessage.append(",\"");
                requestMessage.append(device);
                requestMessage.append("\":");
                requestMessage.append(applicationIdList.toString().replace(", ", ","));
                // デバイスID
                device = deviceId;
                // アプリIDリスト
                applicationIdList = new ArrayList<String>();
                applicationIdList.add(applicationId.toString());
                if (j == pushNoticeDestInfo.size() - 1) {
                    requestMessage.append(",\"");
                    requestMessage.append(device);
                    requestMessage.append("\":");
                    requestMessage.append(applicationIdList.toString().replace(", ", ","));
                }
            } else if (j == pushNoticeDestInfo.size() - 1) {
                // 処理データが最後の件の場合、
                // デバイスID
                device = deviceId;
                // アプリIDリスト
                applicationIdList.add(applicationId.toString());
                requestMessage.append(",\"");
                requestMessage.append(device);
                requestMessage.append("\":");
                requestMessage.append(applicationIdList.toString().replace(", ", ","));
            } else {
                // アプリIDリスト
                applicationIdList.add(applicationId.toString());
                if (StringUtil.isNullOrEmpty(device)) {
                    // デバイスID
                    device = deviceId;
                }
            }
        }
        requestMessage.append("},");

        // 送信日時
        requestMessage.append("\"send_time\":\"");
        requestMessage.append(deliverDateTime);
        requestMessage.append("\"}");

        SyncMessage request = new SyncMessage("RB152A0210");

        // プッシュ通知指示要求:HTTPボディ
        Map<String, Object> dataRecord = new LinkedHashMap<String, Object>();
        dataRecord.put("data", requestMessage.toString().replace("{,", "{"));
        request.addDataRecord(dataRecord);

        // プッシュ通知指示要求:HTTPヘッダ
        Map<String, Object> headRecord = new LinkedHashMap<String, Object>();
        headRecord.put("method", "POST");
        headRecord.put("HTTP-version", "HTTP/1.1");
        headRecord.put("Content-Type", "text/plain; charset=UTF-8");
        StringBuffer authorization = new StringBuffer("PopinfoLogin auth=");
        authorization.append(SystemRepository.getString("push_notice_instr_popinfo_auth"));
        headRecord.put("Authorization", authorization.toString());
        headRecord.put("Accept", "application/json; charset=UTF-8");
        headRecord.put("User-Agent", System.getProperty("java.version"));
        request.setHeaderRecord(headRecord);

        return request;
    }

    /**
     * プッシュ通知情報更新
     *
     * @param pushNoticeId
     *            プッシュ通知ID
     * @param pushNoticeDistinguishId
     *            プッシュ通知識別ID
     * @param processedFlag
     *            処理済フラグ
     * @param deliverDateTime
     *            送信日時
     * @param exceptionOccured
     *            Exception発生フラグ
     */
    private void updatePushNoticeInformation(String pushNoticeId, String pushNoticeDistinguishId, String processedFlag,
            String deliverDateTime, Boolean exceptionOccured) {

        // プッシュ通知情報更新用のSQLの条件を設定する
        Map<String, Object> condition = new HashMap<String, Object>();
        // プッシュ通知ID
        condition.put("pushNoticeId", pushNoticeId);
        // プッシュ通知識別ID
        condition.put("pushNoticeDistinguishId", pushNoticeDistinguishId);
        // 処理済フラグ
        condition.put("processedFlag", processedFlag);
        // 送信日時
        condition.put("deliverDateTime", deliverDateTime);
        // 最終更新者ID
        condition.put("updateUserId", BATCH_PROCESS_ID);
        // 最終更新日時
        condition.put("updateDatetime", SystemTimeUtil.getTimestamp());

        if (exceptionOccured) {
            // システムリポジトリからSimpleDbTransactionManagerを取得する
            SimpleDbTransactionManager dbTransactionManager = SystemRepository.get("update-mail-transaction");

            // SimpleDbTransactionManagerをコンストラクタに指定して実行する
            exceptionUpdateCount += new SimpleDbTransactionExecutor<Integer>(dbTransactionManager) {
                @Override
                public Integer execute(AppDbConnection connection) {
                    ParameterizedSqlPStatement statement = connection.prepareParameterizedSqlStatementBySqlId(
                            "jp.co.tis.opal.batch.ss152A.B152A021Action#UPDATE_PUSH_NOTICE_INFORMATION");
                    return statement.executeUpdateByMap(condition);

                }
            }.doTransaction();
        } else {
            // プッシュ通知情報更新
            ParameterizedSqlPStatement statement = getParameterizedSqlStatement("UPDATE_PUSH_NOTICE_INFORMATION");
            tempUpdateCountPushNotice += statement.executeUpdateByMap(condition);
        }
    }
}
