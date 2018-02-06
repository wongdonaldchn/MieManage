package jp.co.tis.opal.web.common.rest;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * リクエストをラッピングしたリクエストオブジェクトを構築する
 *
 * @author 張
 * @since 1.0
 */
public class BodyReaderHttpServletRequestWarpper extends HttpServletRequestWrapper {
    /** ボディ */
    private final byte[] body;

    /**
     * リクエストをラッピングしたリクエストオブジェクトを構築する
     *
     * @param request
     *            リクエスト
     * @throws IOException
     *             異常
     */
    public BodyReaderHttpServletRequestWarpper(HttpServletRequest request) throws IOException {
        super(request);
        body = HttpHelper.getBodyString(request).getBytes(Charset.forName("UTF-8"));
    }

    /**
     * リクエストのメッセージボディを文字データとして取得する
     *
     * @throws IOException
     *             異常
     */
    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(this.getInputStream()));
    }

    /**
     * リクエストのメッセージボディに含まれているバイナリデータを読み込むためのストリームを取得する
     *
     * @throws IOException
     *             異常
     */
    @Override
    public ServletInputStream getInputStream() throws IOException {
        final ByteArrayInputStream bais = new ByteArrayInputStream(body);
        return new ServletInputStream() {
            @Override
            public int read() throws IOException {
                return bais.read();
            }
        };
    }
}
