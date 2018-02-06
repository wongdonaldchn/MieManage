package jp.co.tis.opal.batch.ss151A;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import nablarch.core.dataformat.SimpleDataConvertResult;
import nablarch.core.date.SystemTimeUtil;
import nablarch.core.db.connection.AppDbConnection;
import nablarch.core.db.statement.ParameterizedSqlPStatement;
import nablarch.core.db.statement.SqlRow;
import nablarch.core.db.transaction.SimpleDbTransactionExecutor;
import nablarch.core.db.transaction.SimpleDbTransactionManager;
import nablarch.core.repository.SystemRepository;
import nablarch.core.util.DateUtil;
import nablarch.core.util.FileUtil;
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

import jp.co.tis.opal.batch.common.fw.OpalCurumeruHttpClient;
import jp.co.tis.opal.common.constants.OpalCodeConstants;
import jp.co.tis.opal.common.constants.OpalDefaultConstants;

/**
 * B151A021:メール配信一括指示のアクションクラス。
 *
 * @author 曹
 * @since 1.0
 */
public class B151A021Action extends BatchAction<SqlRow> {

    /** バッチ処理ID */
    private static final String BATCH_PROCESS_ID = "B151A021";

    /** バッチリクエストID */
    private static final String BATCH_REQUEST_ID = "RB151A0210";

    /** Content-Type **/
    private static final String REQ_CONTENT_TYPE = "multipart/form-data; boundary=boundary; charset=UTF-8";

    /** Accept **/
    private static final String RES_ACCEPT = "application/xml; charset=UTF-8";

    /** Charset **/
    private static final String MSG_CHARSET = "UTF-8";

    /** 入力データ件数(メール配信一括指示情報データ件数) */
    private int intputCount;

    /** 出力データ件数(メール一括配信情報)(更新) */
    private int updateCount;
    private int tempUpdateCount;

    /** 処理データ件数 */
    private int currentDataCount;

    /** コミット間隔 */
    private int commitInterval;

    /** タイムアウト例外発生したデータ件数 */
    private int timeOutDataCount;

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
        timeOutDataCount = 0;

        // コミット間隔を取得
        commitInterval = Integer.parseInt(SystemRepository.getString("nablarch.loopHandler.commitInterval"));
    }

    /**
     * {@inneritDoc}
     * <p/>
     * メール一括配信情報取得。
     */
    @Override
    public DataReader<SqlRow> createReader(ExecutionContext ctx) {

        // メール一括配信情報取得用のSQLの条件を設定する
        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("mailDeliverStatus", OpalCodeConstants.MailDeliverStatus.MAIL_DELIVER_STATUS_1);

        // 入力データ件数取得
        intputCount = countByParameterizedSql("SELECT_MAIL_PACK_DELIVER_INFO", condition);
        // 取得データ件数(プッシュ通知情報)をログに出力する。
        writeLog("MB151A0201", Integer.valueOf(intputCount));

        // メール一括配信情報を取得。
        DatabaseRecordReader reader = new DatabaseRecordReader();
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("SELECT_MAIL_PACK_DELIVER_INFO");
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
        // メール一括配信ID
        String mailPackDeliverId = inputData.getString("MAIL_PACK_DELIVER_ID");
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
        byte[] reqMsgBody = null;

        // 要求電文body取得＆生成（メール配信情報ファイルのZIP圧縮）
        try {
            reqMsgBody = getRequestMessageBody(inputData);
        } catch (IOException e) {
            // 業務処理異常終了例外
            throw new TransactionAbnormalEnd(103, "AB151A0204", mailPackDeliverId, e);
        }

        try {
            // HTTP通信情報設定
            OpalCurumeruHttpClient httpClient = (OpalCurumeruHttpClient) SystemRepository
                    .getObject("opalCurumeruHttpClient");
            httpClient.initialize(BATCH_REQUEST_ID);
            httpClient.setAccept(RES_ACCEPT);
            httpClient.setRequestContentType(REQ_CONTENT_TYPE);

            // HTTP通信実行
            httpResult = httpClient.execute(reqMsgBody);
            responseMessage = httpClient.bodyStringToMap(httpClient.getTargetUri(), httpResult);
            if (responseMessage == null) {
                exceptionOccured = true;
                // メール一括配信情報TBLを更新する
                updateMailPackDeliverInfo(mailPackDeliverId, OpalCodeConstants.MailDeliverStatus.MAIL_DELIVER_STATUS_5,
                        deliverServiceMailId, exceptionOccured);
                updateCount++;
                throw new TransactionAbnormalEnd(104, "AB151A0205");
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> resultMap = (Map<String, Object>) responseMessage.getResultMap();
            if (resultMap.containsKey("data.mail_id")) {
                deliverServiceMailId = resultMap.get("data.mail_id").toString();
            }
            code = resultMap.get("code").toString();

        } catch (HttpMessagingTimeoutException e) {
            exceptionOccured = true;
            // メール一括配信情報TBLを更新する
            updateMailPackDeliverInfo(mailPackDeliverId, OpalCodeConstants.MailDeliverStatus.MAIL_DELIVER_STATUS_3,
                    deliverServiceMailId, exceptionOccured);
            // タイムアウト例外発生をログに出力する。
            writeLog("MB151A0203", mailPackDeliverId);
            // タイムアウト例外発生件数をカウントアップする。
            timeOutDataCount++;
        } catch (MessagingException e) {
            exceptionOccured = true;
            // メール一括配信情報TBLを更新する
            updateMailPackDeliverInfo(mailPackDeliverId, OpalCodeConstants.MailDeliverStatus.MAIL_DELIVER_STATUS_4,
                    deliverServiceMailId, exceptionOccured);
            updateCount++;

            // 業務処理異常終了例外
            throw new TransactionAbnormalEnd(101, "AB151A0202", mailPackDeliverId, e);
        }
        // Exception発生しない場合
        if (!exceptionOccured) {
            // API連携結果コードが"10200"（成功）以外の場合、例外処理を行う。
            if (!OpalDefaultConstants.RESPONSE_CODE.equals(code)) {
                exceptionOccured = true;
                // メール一括配信情報TBLを更新する
                updateMailPackDeliverInfo(mailPackDeliverId, OpalCodeConstants.MailDeliverStatus.MAIL_DELIVER_STATUS_5,
                        deliverServiceMailId, exceptionOccured);
                updateCount++;

                // 業務処理異常終了例外
                throw new TransactionAbnormalEnd(102, "AB151A0203", mailPackDeliverId, code,
                        responseMessage.getResultMap().get("status"), responseMessage.getResultMap().get("message"));
            }
            // メール一括配信情報TBLを更新する
            updateMailPackDeliverInfo(mailPackDeliverId, OpalCodeConstants.MailDeliverStatus.MAIL_DELIVER_STATUS_2,
                    deliverServiceMailId, exceptionOccured);
        }
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
        // 更新データ件数(メール一括配信情報)をログに出力する。
        writeLog("MB151A0202", Integer.valueOf(updateCount));
        // タイムアウト異常が発生する場合、異常終了とする。
        if (timeOutDataCount > 0) {
            throw new TransactionAbnormalEnd(100, "AB151A0201", timeOutDataCount);
        }
    }

    /**
     * 要求電文BODYを生成する。
     *
     * @param inputData
     *            処理対象データ
     * @return String 要求電文BODY
     * @throws IOException
     *             ZIP圧縮エラー
     */
    private byte[] getRequestMessageBody(SqlRow inputData) throws IOException {

        Map<String, String> bodyMap = new LinkedHashMap<String, String>();
        // 接続用パスワード
        bodyMap.put("transport_password", SystemRepository.getString("curumeru_tansport_password"));
        // 文字コード
        bodyMap.put("charset", "1");
        // 取得形式
        bodyMap.put("return_format", "xml");
        // Fromのメールアドレス
        bodyMap.put("from_address", inputData.getString("FROM_ADDRESS"));
        // Fromの差出人名
        bodyMap.put("from_name", inputData.getString("FROM_NAME"));
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

        ByteArrayOutputStream requestMessageOst = null;
        byte[] requestMessageBytes;
        try {
            requestMessageOst = new ByteArrayOutputStream();
            Charset charset = Charset.forName(MSG_CHARSET);
            for (String key : bodyMap.keySet()) {
                requestMessageOst.write("--boundary\r\n".getBytes(charset));
                requestMessageOst.write(new StringBuilder("Content-Disposition: form-data; name=\"").append(key)
                        .append("\"\r\n\r\n").toString().getBytes(charset));
                requestMessageOst
                        .write(new StringBuilder(bodyMap.get(key)).append("\r\n").toString().getBytes(charset));
            }

            // CSVファイル
            String deliverFileNmae = inputData.getString("DELIVER_FILE_NAME");
            StringBuilder csvFileName = new StringBuilder();
            csvFileName.append(deliverFileNmae);
            if (!deliverFileNmae.toLowerCase().endsWith(".csv")) {
                csvFileName.append(".csv");
            }
            requestMessageOst.write("--boundary\r\n".getBytes(charset));
            requestMessageOst.write("Content-Disposition: form-data; name=\"csvfile\"; filename=\"".getBytes(charset));
            requestMessageOst.write(new StringBuilder(csvFileName).append(".zip\"\r\n").toString().getBytes(charset));
            requestMessageOst.write("Content-Type: application/octet-stream\r\n\r\n".getBytes(charset));
            requestMessageOst
                    .write(zipCompress(getDeliverFilePath(deliverFileNmae), getDeliverZipPath(deliverFileNmae)));
            requestMessageOst.write("\r\n".getBytes(charset));
            requestMessageOst.write("--boundary\r\n".getBytes(charset));

            requestMessageBytes = requestMessageOst.toByteArray();
            return requestMessageBytes;

        } finally {
            FileUtil.closeQuietly(requestMessageOst);
        }
    }

    /**
     * メール一括配信情報を更新する。
     *
     * @param mailPackDeliverId
     *            メール一括配信ID
     * @param mailDeliverStatus
     *            メール配信状況
     * @param deliverServiceMailId
     *            配信サービスメールID
     * @param exceptionOccured
     *            Exception発生フラグ
     */
    private void updateMailPackDeliverInfo(String mailPackDeliverId, String mailDeliverStatus,
            String deliverServiceMailId, Boolean exceptionOccured) {

        // メール一括配信情報更新用のSQLの条件を設定する
        Map<String, Object> condition = new HashMap<String, Object>();
        // メール一括配信ID
        condition.put("mailPackDeliverId", mailPackDeliverId);
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
                            "jp.co.tis.opal.batch.ss151A.B151A021Action#UPDATE_MAIL_PACK_DELIVER_INFO");
                    return statement.executeUpdateByMap(condition);
                }
            }.doTransaction();
        } else {
            // メール一括配信情報更新
            ParameterizedSqlPStatement statement = getParameterizedSqlStatement("UPDATE_MAIL_PACK_DELIVER_INFO");
            tempUpdateCount += statement.executeUpdateByMap(condition);
        }
    }

    /**
     * 配信ファイルパスを取得する。
     *
     * @param deliverFileNmae
     *            配信ﾌｧｲﾙ名称
     * @return 配信ファイルパス
     */
    private String getDeliverFilePath(String deliverFileNmae) {
        StringBuilder csvFilePath = new StringBuilder();
        csvFilePath.append(
                SystemRepository.getString("nablarch.filePathSetting.basePathSettings.mail").replace("file:", ""));
        csvFilePath.append("/");
        csvFilePath.append(deliverFileNmae);
        return csvFilePath.toString();
    }

    /**
     * 配信ファイル(ZIP)パスを取得する。
     *
     * @param deliverFileNmae
     *            配信ﾌｧｲﾙ名称
     * @return 配信ファイルパス
     */
    private String getDeliverZipPath(String deliverFileNmae) {
        StringBuilder zipFilePath = new StringBuilder();
        zipFilePath.append(
                SystemRepository.getString("nablarch.filePathSetting.basePathSettings.mail").replace("file:", ""));
        zipFilePath.append("/");
        zipFilePath.append(deliverFileNmae);
        zipFilePath.append(".zip");
        return zipFilePath.toString();
    }

    /**
     * ZIP圧縮
     *
     * @param targetFilePath
     *            ZIP化対象ファイルパス
     * @param zipFilePath
     *            ZIPファイルパス
     * @return ZIPファイルの文字列
     * @throws IOException
     *             ZIP圧縮エラー
     */
    private byte[] zipCompress(String targetFilePath, String zipFilePath) throws IOException {

        // メール配信情報ファイルを取得する。
        File sourceFile = new File(targetFilePath);
        File zipFile = new File(zipFilePath);

        FileInputStream fis = null;
        BufferedInputStream bis = null;
        FileOutputStream fos = null;
        ZipOutputStream zos = null;
        byte[] data = new byte[1024];
        try {
            // 圧縮した配信ファイル
            fis = new FileInputStream(sourceFile);
            bis = new BufferedInputStream(fis);
            fos = new FileOutputStream(zipFile);
            zos = new ZipOutputStream(new BufferedOutputStream(fos));

            StringBuilder csvFileName = new StringBuilder();
            csvFileName.append(sourceFile.getName());
            if (!sourceFile.getName().toLowerCase().endsWith(".csv")) {
                csvFileName.append(".csv");
            }
            ZipEntry zipEntry = new ZipEntry(csvFileName.toString());
            zos.putNextEntry(zipEntry);

            while (true) {
                int len = bis.read(data);
                if (len == -1) {
                    break;
                }
                zos.write(data, 0, len);
            }

        } finally {
            FileUtil.closeQuietly(zos);
            FileUtil.closeQuietly(fos);
            FileUtil.closeQuietly(bis);
            FileUtil.closeQuietly(fis);
        }

        byte[] zipFileByte;
        InputStream is = null;
        ByteArrayOutputStream byteStream = null;
        try {
            is = new FileInputStream(zipFile);
            byteStream = new ByteArrayOutputStream();
            int ch = is.read(data);
            while (ch != -1) {
                byteStream.write(data, 0, ch);
                ch = is.read(data);
            }
            zipFileByte = byteStream.toByteArray();

        } finally {
            zipFile.deleteOnExit();
            FileUtil.closeQuietly(is);
            FileUtil.closeQuietly(byteStream);
        }

        return zipFileByte;
    }
}