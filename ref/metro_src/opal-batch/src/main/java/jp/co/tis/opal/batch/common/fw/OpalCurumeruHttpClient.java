package jp.co.tis.opal.batch.common.fw;

import nablarch.core.dataformat.SimpleDataConvertResult;
import nablarch.core.util.annotation.Published;
import nablarch.fw.messaging.MessagingException;
import nablarch.fw.messaging.realtime.http.dto.HttpResult;
import nablarch.fw.messaging.realtime.http.exception.HttpMessagingInvalidDataFormatException;

/**
 * Curumeru専用 HTTP接続用クライアントインタフェースクラス。
 *
 * @author A.Mukae
 * @since 1.0
 */
@Published
public interface OpalCurumeruHttpClient {

    /** 設定ファイルに設定する本実装クラス用のキー名の接頭辞。 */
    public final static String KEY_NAME = "opalCurumeruHttpClient";

    /**
     * 初期化処理を行う。
     *
     * @param batchRequestId
     *            バッチリクエストID
     */
    public void initialize(String batchRequestId);

    /**
     * HTTP通信を実行する。
     *
     * @param reqMsgBody
     *            リクエストBODY
     * @return HttpResult 実行結果
     * @throws MessagingException
     *             MessagingException
     */
    public HttpResult execute(String reqMsgBody) throws MessagingException;

    /**
     * HTTP通信を実行する。
     *
     * @param reqMsgBody
     *            リクエストBODY
     * @return HttpResult 実行結果
     * @throws MessagingException
     *             MessagingException
     */
    public HttpResult execute(byte[] reqMsgBody) throws MessagingException;

    /**
     * 返信のボディ部分を解析し、応答電文に設定するデータを生成する。
     *
     * @param uri
     *            接続先
     * @param httpResult
     *            送信結果
     * @return 解析後のSimpleDataConvertResult
     * @throws HttpMessagingInvalidDataFormatException
     *             電文フォーマット変換に失敗した場合に送出される。
     */
    public SimpleDataConvertResult bodyStringToMap(String uri, HttpResult httpResult)
            throws HttpMessagingInvalidDataFormatException;

    /**
     * Acceptを設定する。
     *
     * @param accept
     *            Accept
     */
    public void setAccept(String accept);

    /**
     * HTTP通信のContent-Typeを設定する。
     *
     * @param requestContentType
     *            Content-Type
     */
    public void setRequestContentType(String requestContentType);

    /**
     * 接続先uriを取得する。
     *
     * @return URL
     */
    public String getTargetUri();

}