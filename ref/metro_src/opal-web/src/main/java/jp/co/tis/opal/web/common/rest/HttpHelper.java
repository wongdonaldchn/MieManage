package jp.co.tis.opal.web.common.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import javax.servlet.ServletRequest;

/**
 * リクエストをラッピング用のメソッド
 *
 * @author 張
 * @since 1.0
 */
public class HttpHelper {

    /**
     * リクエストを取得して、ボディの内容を取得します
     *
     * @param request
     *            電文
     * @return 電文
     * @throws IOException
     *             異常
     */
    public static String getBodyString(ServletRequest request) throws IOException {
        StringBuilder sb = new StringBuilder();

        InputStream inputstream = request.getInputStream();
        if (inputstream != null) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputstream, Charset.forName("UTF-8")));
            String line = "";
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            if (inputstream != null) {
                inputstream.close();
            }
            if (reader != null) {
                reader.close();
            }
        }

        return sb.toString();
    }

}
