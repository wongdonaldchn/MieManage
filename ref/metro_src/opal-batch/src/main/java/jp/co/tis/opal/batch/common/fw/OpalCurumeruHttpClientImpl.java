package jp.co.tis.opal.batch.common.fw;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import nablarch.core.dataformat.InvalidDataFormatException;
import nablarch.core.dataformat.SimpleDataConvertResult;
import nablarch.core.dataformat.SimpleDataConvertUtil;
import nablarch.core.log.Logger;
import nablarch.core.log.LoggerManager;
import nablarch.core.repository.SystemRepository;
import nablarch.core.util.FileUtil;
import nablarch.core.util.StringUtil;
import nablarch.core.util.annotation.Published;
import nablarch.fw.messaging.MessagingException;
import nablarch.fw.messaging.ReceivedMessage;
import nablarch.fw.messaging.SendingMessage;
import nablarch.fw.messaging.logging.MessagingLogUtil;
import nablarch.fw.messaging.realtime.http.dto.HttpResult;
import nablarch.fw.messaging.realtime.http.exception.HttpMessagingException;
import nablarch.fw.messaging.realtime.http.exception.HttpMessagingInvalidDataFormatException;
import nablarch.fw.messaging.realtime.http.exception.HttpMessagingTimeoutException;
import nablarch.fw.messaging.realtime.http.streamio.CharHttpStreamReader;

/**
 * Curumeru専用Http接続用クライアント実装クラス。
 *
 * @author A.Mukae
 * @since 1.0
 */
@Published
public class OpalCurumeruHttpClientImpl implements OpalCurumeruHttpClient {

    /** 証跡ログを出力するロガー */
    private static final Logger MESSAGING_LOGGER = LoggerManager.get("MESSAGING");

    /** Charset **/
    private static final String MSG_CHARSET = "UTF-8";

    /** 接続先uri */
    private String targetUri = null;

    /** HTTP通信時のMethod */
    private String httpMethod = "POST";

    /** コネクションタイムアウト(単位:ミリ秒) */
    private int httpConnectTimeout = 0;

    /** 読み取りタイムアウト(単位:ミリ秒) */
    private int httpReadTimeout = 0;

    /** javaVersion */
    private String javaVersion = null;

    /** HTTP通信のAccept */
    private String accept = null;

    /** HTTP通信のContent-Type */
    private String requestContentType = null;

    /** バッチリクエストID */
    private String batchRequestId = null;

    /** 要求電文をログに出力するかどうか */
    private boolean isOutputRequestMessage = true;

    /** 応答電文をログに出力するかどうか */
    private boolean isOutputReceivedMessage = true;

    /*
     * (非 Javadoc)
     *
     * @see
     * jp.co.tis.opal.batch.common.fw.OpalCurumeruHttpClient#initialize(java.
     * lang.String)
     */
    @Override
    public void initialize(String batchRequestId) {
        // 接続先uri取得
        this.targetUri = SystemRepository
                .getString(StringUtil.join(".", Arrays.asList(KEY_NAME, batchRequestId, "uri")));

        // コネクションタイムアウト(単位:ミリ秒)取得
        String httpConnectTimeoutStr = SystemRepository
                .getString(StringUtil.join(".", Arrays.asList(KEY_NAME, batchRequestId, "httpConnectTimeout")));
        if (!StringUtil.isNullOrEmpty(httpConnectTimeoutStr)) {
            this.httpConnectTimeout = Integer.parseInt(httpConnectTimeoutStr);
        }

        // 読み取りタイムアウト(単位:ミリ秒)取得
        String httpReadTimeoutStr = SystemRepository
                .getString(StringUtil.join(".", Arrays.asList(KEY_NAME, batchRequestId, "httpReadTimeout")));
        if (!StringUtil.isNullOrEmpty(httpConnectTimeoutStr)) {
            this.httpReadTimeout = Integer.parseInt(httpReadTimeoutStr);
        }

        // javaバージョン
        this.javaVersion = new StringBuilder("Java/").append(System.getProperty("java.version")).toString();

        // 要求電文をログに出力するかどうか
        String isOutputRequestMessageStr = SystemRepository
                .getString(StringUtil.join(".", Arrays.asList(KEY_NAME, batchRequestId, "isOutputRequestMessage")));
        if (!StringUtil.isNullOrEmpty(isOutputRequestMessageStr)) {
            this.isOutputRequestMessage = Boolean.valueOf(isOutputRequestMessageStr);
        }

        // 応答電文をログに出力するかどうか
        String isOutputReceivedMessageStr = SystemRepository
                .getString(StringUtil.join(".", Arrays.asList(KEY_NAME, batchRequestId, "isOutputReceivedMessage")));
        if (!StringUtil.isNullOrEmpty(isOutputReceivedMessageStr)) {
            this.isOutputReceivedMessage = Boolean.valueOf(isOutputReceivedMessageStr);
        }

        this.batchRequestId = batchRequestId;
    }

    /*
     * (非 Javadoc)
     *
     * @see
     * jp.co.tis.opal.batch.common.fw.OpalCurumeruHttpClient#execute(java.lang.
     * String)
     */
    @Override
    public HttpResult execute(String reqMsgBody) throws MessagingException {
        return execute(reqMsgBody.getBytes(Charset.forName(MSG_CHARSET)));
    }

    @Override
    public HttpResult execute(byte[] reqMsgBody) throws MessagingException {
        // 通信結果
        HttpResult result = new HttpResult();
        // ヘッダ情報
        Map<String, List<String>> headeInfo = null;
        // ステータスコード
        Integer responseCode = null;
        // レスポンス本体
        Object responseObject = null;
        // 応答の読み込みに使用するreader
        CharHttpStreamReader reader = new CharHttpStreamReader();

        HttpURLConnection http = null;
        OutputStream outputStream = null;
        InputStream inputStream = null;

        try {
            URL urlobj = new URL(targetUri);
            http = (HttpURLConnection) urlobj.openConnection();
            http.setRequestMethod(httpMethod);
            http.setDoOutput(true);
            http.setConnectTimeout(httpConnectTimeout);
            http.setReadTimeout(httpReadTimeout);
            // 一般要求プロパティの設定
            http.setRequestProperty("User-Agent", javaVersion);
            if (accept != null) {
                http.setRequestProperty("Accept", accept);
            }
            if (requestContentType != null) {
                http.setRequestProperty("Content-Type", requestContentType);
            }

            // 送信前ログ出力
            if (MESSAGING_LOGGER.isInfoEnabled()) {
                emitRequestLog(http.getRequestProperties(), http.getRequestMethod(), targetUri, reqMsgBody,
                        MSG_CHARSET);
            }

            // POST送信
            outputStream = http.getOutputStream();
            outputStream.write(reqMsgBody);

            // レスポンス取得
            // HTTPステータスコードとヘッダ情報の読み取り
            responseCode = http.getResponseCode();
            headeInfo = http.getHeaderFields();
            reader.setEncode(MSG_CHARSET);
            reader.setHeaderInfo(headeInfo);

            // body部の読み取り
            inputStream = http.getInputStream();
            responseObject = reader.readInputStream(inputStream);

            // レスポンスログ
            if (MESSAGING_LOGGER.isInfoEnabled()) {
                emitResponseLog(headeInfo, responseObject.toString(), MSG_CHARSET);
            }

        } catch (SocketTimeoutException e) {
            throw new HttpMessagingTimeoutException("Time-out occurs.", targetUri, responseCode, e);

        } catch (IOException ie) {
            InputStream es = null;
            try {
                // エラー用ストリームからの読み取りを試す。
                // (何種類かのHTTPステータスコードについては、Body部の読み取り時にこのロジックに到達する)
                if (http != null) {
                    es = http.getErrorStream();
                }
                if (es != null) {
                    // エラー用ストリームから読み取れた場合は、処理を続行する。
                    responseObject = reader.readInputStream(es);
                } else {
                    // getErrorStream()からも読み取れない場合は、例外を送出する。
                    throw new HttpMessagingException(targetUri, responseCode, ie);
                }
            } catch (SocketTimeoutException e) {
                throw new HttpMessagingTimeoutException("Time-out occurs.", targetUri, responseCode, e);

            } catch (IOException e) {
                // getErrorStream()からも読み取れない場合は、例外を送出する。
                throw new HttpMessagingException(targetUri, responseCode, e);

            } finally {
                FileUtil.closeQuietly(es);
            }

        } finally {
            FileUtil.closeQuietly(outputStream);
            FileUtil.closeQuietly(inputStream);
            if (http != null) {
                http.disconnect();
            }
        }

        // 通信結果設定
        result.setResponseCode(responseCode);
        result.setHeaderInfo(headeInfo);
        result.setReadObject(responseObject);
        return result;
    }

    /**
     * メッセージングの証跡ログを出力する。
     *
     * @param requestHeader
     *            要求ヘッダ情報
     * @param method
     *            HTTPメソッド
     * @param uri
     *            接続先URI
     * @param bodyText
     *            変換済みの要求メッセージ本文
     * @param charsetName
     *            変換に使用した文字セット
     */
    private void emitRequestLog(Map<String, List<String>> requestHeader, String method, String uri,
            final byte[] bodyText, String charsetName) {
        // 共通のログ出力フォーマットとするためSendingMessageに変換する
        final Charset charset = Charset.forName(charsetName);
        byte[] outputBodyText;
        if (isOutputRequestMessage) {
            outputBodyText = bodyText;
        } else {
            outputBodyText = "*** to ommit request message ***".getBytes(charset);
        }

        SendingMessage sendingMessage = new SendingMessage() {
            @Override
            public byte[] getBodyBytes() {
                // ボディ部は変換済みテキストのバイト列を返却する
                return outputBodyText;
            }
        };
        sendingMessage.setHeaderMap(requestHeader);
        sendingMessage.setDestination(method + " " + uri);
        String log = MessagingLogUtil.getHttpSentMessageLog(sendingMessage, charset);
        MESSAGING_LOGGER.logInfo(log);
    }

    /**
     * メッセージングの証跡ログを出力する。
     *
     * @param responseHeader
     *            応答ヘッダ情報
     * @param bodyText
     *            変換前の応答メッセージ本文
     * @param charsetName
     *            変換に使用した文字セット
     */
    private void emitResponseLog(Map<String, List<String>> responseHeader, String bodyText, String charsetName) {
        // 共通のログ出力フォーマットとするためReceivedMessageに変換する
        Charset charset = Charset.forName(charsetName);
        byte[] bodyBytes = bodyText.getBytes(charset);

        ReceivedMessage receivedMessage;
        if (isOutputReceivedMessage) {
            receivedMessage = new ReceivedMessage(bodyBytes);
        } else {
            receivedMessage = new ReceivedMessage("*** to ommit recieved message ***".getBytes(charset));
        }

        receivedMessage.setHeaderMap(responseHeader);

        String log = MessagingLogUtil.getHttpReceivedMessageLog(receivedMessage, charset);
        MESSAGING_LOGGER.logInfo(log);
    }

    /*
     * (非 Javadoc)
     *
     * @see
     * jp.co.tis.opal.batch.common.fw.OpalCurumeruHttpClient#bodyStringToMap(
     * String uri, String requestId, HttpResult httpResult)
     */
    @Override
    public SimpleDataConvertResult bodyStringToMap(String uri, HttpResult httpResult)
            throws HttpMessagingInvalidDataFormatException {
        SimpleDataConvertResult ret = null;
        String data = (String) httpResult.getReadObject();
        try {
            if (!StringUtil.isNullOrEmpty(data)) {
                // 電文フォーマット変換対象のデータが存在していれば、変換を行う。
                String formatName = new StringBuilder(batchRequestId).append("_RECEIVE").toString();
                ret = SimpleDataConvertUtil.parseData(formatName, data);
            }
        } catch (InvalidDataFormatException e) {
            String message = "Invalid receive message format. requestId=[" + batchRequestId + "].";
            throw new HttpMessagingInvalidDataFormatException(message, uri, httpResult.getResponseCode(),
                    httpResult.getHeaderInfo(), data, e);
        }
        return ret;
    }

    /*
     * (非 Javadoc)
     *
     * @see
     * jp.co.tis.opal.batch.common.fw.OpalCurumeruHttpClient#setAccept(java.lang
     * .String)
     */
    @Override
    public void setAccept(String accept) {
        this.accept = accept;
    }

    /*
     * (非 Javadoc)
     *
     * @see jp.co.tis.opal.batch.common.fw.OpalCurumeruHttpClient#
     * setRequestContentType(java.lang.String)
     */
    @Override
    public void setRequestContentType(String requestContentType) {
        this.requestContentType = requestContentType;
    }

    /*
     * (非 Javadoc)
     *
     * @see jp.co.tis.opal.batch.common.fw.OpalCurumeruHttpClient#getTargetUri()
     */
    @Override
    public String getTargetUri() {
        return targetUri;
    }
}
