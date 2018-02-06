package jp.co.tis.opal.batch.ss153A;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nablarch.common.io.FileRecordWriterHolder;
import nablarch.core.dataformat.InvalidDataFormatException;
import nablarch.core.log.Logger;
import nablarch.core.log.LoggerManager;
import nablarch.core.message.Message;
import nablarch.core.message.MessageLevel;
import nablarch.core.message.MessageUtil;
import nablarch.core.repository.SystemRepository;
import nablarch.core.util.StringUtil;
import nablarch.fw.ExecutionContext;
import nablarch.fw.Result;
import nablarch.fw.Result.Success;
import nablarch.fw.action.NoInputDataBatchAction;
import nablarch.fw.messaging.MessagingException;
import nablarch.fw.messaging.realtime.http.dto.HttpResult;
import nablarch.fw.messaging.realtime.http.exception.HttpMessagingInvalidDataFormatException;
import nablarch.fw.messaging.realtime.http.exception.HttpMessagingTimeoutException;
import nablarch.fw.results.TransactionAbnormalEnd;

import jp.co.tis.opal.batch.common.exception.HttpMessagingTimeoutRetryableException;
import jp.co.tis.opal.batch.common.fw.OpalCurumeruHttpClient;
import jp.co.tis.opal.batch.common.utility.OpalSimpleDataConvertResult;
import jp.co.tis.opal.batch.common.utility.OpalSimpleDataConvertUtil;

/**
 * B153A011:エラーメールアドレス情報取得のアクションクラス。
 *
 * @author 陳
 * @since 1.0
 */
public class B153A011Action extends NoInputDataBatchAction {

    /** ロガー */
    private static final Logger LOG = LoggerManager.get(B153A011Action.class);

    /** バッチリクエストID */
    private static final String BATCH_REQUEST_ID = "RB153A0110";

    /** 出力ファイルID：A153A001(エラーメールアドレス情報ファイル) */
    private static final String FILE_ID = "A153A001";

    /** フォーマット定義ファイルID：A153A001 */
    private static final String FORMAT_ID = "A153A001";

    /** Content-Type **/
    private static final String REQ_CONTENT_TYPE = "multipart/form-data; boundary=boundary; charset=UTF-8";

    /** Accept **/
    private static final String RES_ACCEPT = "application/csv; charset=UTF-8";

    /** エラーメールアドレス情報ファイルの出力先ディレクトリ */
    private static final String OUTPUT_PATH = "mailIn";

    /** 応答電文フォーマット（正常時） */
    private static final String RESPONSE_FORMAT_NORMAL = "RB153A0110_NORMAL_RECEIVE";

    /** 応答電文フォーマット（エラー時） */
    private static final String RESPONSE_ERROR_ABNORMAL = "RB153A0110_ERROR_RECEIVE";

    /** 応答電文ヘッダー（正常時） */
    private static final String RESPONSE_HEADER = "メールアドレス,最終更新日,永続的,一時的,原因不明,合計";

    /** 応答電文ヘッダー（異常時） */
    private static final String RESPONSE_ERROR_HEADER = "CODE,STATUS,MESSAGE";

    /**
     * {@inneritDoc}
     * <p/>
     * 本処理
     */
    @Override
    public Result handle(ExecutionContext ctx) {

        // WebAPI実行結果
        HttpResult httpResult = null;

        // 応答電文
        OpalSimpleDataConvertResult responseMessage = null;

        try {
            // HTTP通信情報設定
            OpalCurumeruHttpClient httpClient = (OpalCurumeruHttpClient) SystemRepository
                    .getObject("opalCurumeruHttpClient");
            httpClient.initialize(BATCH_REQUEST_ID);
            httpClient.setAccept(RES_ACCEPT);
            httpClient.setRequestContentType(REQ_CONTENT_TYPE);

            // 要求電文body取得
            String reqMsgBody = getRequestMessageBody();

            // HTTP通信実行
            httpResult = httpClient.execute(reqMsgBody);
            if (StringUtil.isNullOrEmpty(httpResult.getReadObject().toString())) {
                throw new TransactionAbnormalEnd(103, "AB153A0104");
            }
            responseMessage = bodyStringToListMap(httpClient.getTargetUri(), httpResult);

        } catch (HttpMessagingTimeoutException e) {
            // タイムアウト例外発生をログに出力する。
            Message message = MessageUtil.createMessage(MessageLevel.WARN, "MB153A0101");
            LOG.logWarn(message.formatMessage());
            // リトライ可能タイムアウト例外（HttpMessagingTimeoutRetryableException)を送出し、処理を終了する。
            throw new HttpMessagingTimeoutRetryableException(e);
        } catch (MessagingException e) {
            // 業務処理異常終了例外(TransactionAbnormalEnd)を送出し、処理を終了する。
            throw new TransactionAbnormalEnd(100, "AB153A0101", e);
        }

        // API連携結果毎処理
        List<Map<String, ?>> resListMap = responseMessage.getResultListMap();
        if (resListMap.get(0).get("header_mail_address") != null) {
            // エラーメールアドレス情報応答電文の1行目がヘッダレコードの場合
            // エラーメールアドレス情報応答電文のHTTPボディ全件をエラーメールアドレス情報ファイルに出力する。
            FileRecordWriterHolder.open(OUTPUT_PATH, FILE_ID, FORMAT_ID);
            writeHeaderRecord(resListMap.get(0));
            writeDataRecord(resListMap);
            FileRecordWriterHolder.close(OUTPUT_PATH, FILE_ID);

        } else {
            // エラーメールアドレス情報応答電文の1行目がエラーヘッダレコードの場合
            // エラーデータレコードからAPI連携結果コード・API連携結果ステータス・API連携結果メッセージを取得する。
            String code = resListMap.get(1).get("code").toString();
            String status = resListMap.get(1).get("status").toString();
            String message = resListMap.get(1).get("message").toString();

            // 業務処理異常終了例外(TransactionAbnormalEnd)を送出し、処理を終了する。
            throw new TransactionAbnormalEnd(101, "AB153A0102", code, status, message);
        }

        return new Success();
    }

    /**
     * 要求電文生成。
     *
     * @return 要求電文
     */
    private String getRequestMessageBody() {

        StringBuilder requestMessage = new StringBuilder();

        // 接続用パスワード
        requestMessage.append("--boundary\r\n");
        requestMessage.append("Content-Disposition: form-data; name=\"transport_password\"\r\n\r\n");
        requestMessage.append(SystemRepository.getString("curumeru_tansport_password"));
        requestMessage.append("\r\n");
        // 文字コード
        requestMessage.append("--boundary\r\n");
        requestMessage.append("Content-Disposition: form-data; name=\"charset\"\r\n\r\n");
        requestMessage.append("1");
        requestMessage.append("\r\n");
        // 取得形式
        requestMessage.append("--boundary\r\n");
        requestMessage.append("Content-Disposition: form-data; name=\"return_format\"\r\n\r\n");
        requestMessage.append("csv");
        requestMessage.append("\r\n");
        requestMessage.append("--boundary");
        requestMessage.append("\r\n");

        return requestMessage.toString();
    }

    /**
     * ヘッダレコード出力
     *
     * @param recordMap
     *            エラーメールアドレス情報応答電文
     */
    private void writeHeaderRecord(Map<String, ?> recordMap) {

        Map<String, String> header = new HashMap<String, String>();
        // メールアドレス
        header.put("header_mail_address", recordMap.get("header_mail_address").toString());
        // メール配信日時
        header.put("header_last_update_date", recordMap.get("header_last_update_date").toString());
        // メール配信エラー回数(永続的)
        header.put("header_permanent_error", recordMap.get("header_permanent_error").toString());
        // メール配信エラー回数(一時的)
        header.put("header_temporary_error", recordMap.get("header_temporary_error").toString());
        // メール配信エラー回数(原因不明)
        header.put("header_unknown_error", recordMap.get("header_unknown_error").toString());
        // メール配信エラー回数(合計)
        header.put("header_summary", recordMap.get("header_summary").toString());

        // ヘッダレコード出力
        writeRecord("Header", header);
    }

    /**
     * データレコード出力
     *
     * @param records
     *            エラーメールアドレス情報応答電文
     */
    private void writeDataRecord(List<Map<String, ?>> records) {

        if (records.size() > 1) {

            for (int i = 1; i < records.size(); i++) {
                Map<String, String> data = new HashMap<String, String>();
                // メールアドレス
                data.put("mail_address", records.get(i).get("mail_address").toString());
                // メール配信日時
                data.put("last_update_date", records.get(i).get("last_update_date").toString());
                // メール配信エラー回数(永続的)
                data.put("permanent_error", records.get(i).get("permanent_error").toString());
                // メール配信エラー回数(一時的)
                data.put("temporary_error", records.get(i).get("temporary_error").toString());
                // メール配信エラー回数(原因不明)
                data.put("unknown_error", records.get(i).get("unknown_error").toString());
                // メール配信エラー回数(合計)
                data.put("summary", records.get(i).get("summary").toString());

                // ヘッダレコード出力
                writeRecord("Data", data);
            }
        }
    }

    /**
     * ファイル出力処理。 指定されたMapを1レコードとしてファイル出力を行う。
     * <p/>
     *
     * @param recordType
     *            レコードタイプを表す文字列
     * @param record
     *            1レコードの情報を格納したMap
     */
    private void writeRecord(String recordType, Map<String, ?> record) {

        FileRecordWriterHolder.write(recordType, record, OUTPUT_PATH, FILE_ID);
    }

    /**
     * 返信のボディ部分を解析し、応答電文に設定するデータを生成する。
     *
     * @param uri
     *            接続先
     * @param httpResult
     *            送信結果
     * @return 解析後のOpalSimpleDataConvertResult
     * @throws HttpMessagingInvalidDataFormatException
     *             電文フォーマット変換に失敗した場合に送出される。
     */
    public OpalSimpleDataConvertResult bodyStringToListMap(String uri, HttpResult httpResult)
            throws HttpMessagingInvalidDataFormatException {
        OpalSimpleDataConvertResult ret = null;
        String data = (String) httpResult.getReadObject();
        try {
            // 電文フォーマット変換対象のデータが存在している場合
            String[] records = data.split("\r\n");
            String firstRecord = records[0];
            if (firstRecord.equals(RESPONSE_HEADER)) {
                // 正常時
                ret = OpalSimpleDataConvertUtil.parseData(RESPONSE_FORMAT_NORMAL, data);

            } else if (firstRecord.equals(RESPONSE_ERROR_HEADER)) {
                if (records.length < 2) {
                    throw new MessagingException("Invalid receive message format.");
                }
                // 業務エラー時
                ret = OpalSimpleDataConvertUtil.parseData(RESPONSE_ERROR_ABNORMAL, data);

            } else {
                throw new MessagingException("Invalid receive message header format.");
            }
        } catch (InvalidDataFormatException e) {
            throw new HttpMessagingInvalidDataFormatException("Invalid receive message format.", uri,
                    httpResult.getResponseCode(), httpResult.getHeaderInfo(), data, e);
        }
        return ret;
    }
}