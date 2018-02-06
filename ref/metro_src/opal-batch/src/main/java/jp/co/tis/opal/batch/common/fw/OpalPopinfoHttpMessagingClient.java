package jp.co.tis.opal.batch.common.fw;

import java.util.List;
import java.util.Map;

import nablarch.fw.messaging.realtime.http.client.HttpMessagingClient;
import nablarch.fw.messaging.realtime.http.client.HttpProtocolClient;
import nablarch.fw.messaging.realtime.http.dto.HttpResult;
import nablarch.fw.messaging.realtime.http.streamio.CharHttpStreamReader;
import nablarch.fw.messaging.realtime.http.streamio.HttpInputStreamReader;
import nablarch.fw.messaging.realtime.http.streamio.HttpOutputStreamWriter;

/**
 * HTTPを利用したメッセージング機能の実装。
 *
 * @author 張
 * @since 1.0
 */
public class OpalPopinfoHttpMessagingClient extends HttpMessagingClient {

    /**
     * HTTPリクエストを送出する。
     *
     * @param httpProtocolClient
     *            HTTPリクエストを発行するオブジェクト
     * @param httpMethod
     *            HTTPメソッド
     * @param uri
     *            送信先
     * @param headerInfo
     *            HTTPリクエストのヘッダ情報
     * @param urlParams
     *            URLパラメータ
     * @param charset
     *            文字コード
     * @param bodyText
     *            HTTPリクエストの本文
     * @return 送信結果
     */
    @Override
    protected HttpResult execute(HttpProtocolClient httpProtocolClient,
            HttpProtocolClient.HttpRequestMethodEnum httpMethod, String uri, Map<String, List<String>> headerInfo,
            Map<String, String> urlParams, String charset, String bodyText) {

        HttpOutputStreamWriter writer = createCharHttpStreamWritter(charset, bodyText);

        CharHttpStreamReader charReader = new CharHttpStreamReader();
        charReader.setEncode(charset);

        return httpProtocolClient.execute(httpMethod, uri, headerInfo, urlParams, writer,
                (HttpInputStreamReader) charReader);
    }

}
