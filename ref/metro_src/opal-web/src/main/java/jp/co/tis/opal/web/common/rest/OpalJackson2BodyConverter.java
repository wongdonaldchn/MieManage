package jp.co.tis.opal.web.common.rest;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import com.fasterxml.jackson.databind.ObjectMapper;

import nablarch.integration.jaxrs.jackson.JacksonBodyConverterSupport;

/**
 * JSON変換用のクラス。
 *
 * @author 張
 * @since 1.0
 */
public class OpalJackson2BodyConverter extends JacksonBodyConverterSupport {

    private final ObjectMapper objectMapper;

    /**
     * JSON変換用のクラス。
     *
     */
    public OpalJackson2BodyConverter() {
        this.objectMapper = new ObjectMapper();
    }

    /**
     * JSON -> Object
     *
     * @param content
     *            HTTPリクエスト
     * @param valueType
     *            タイプ
     * @return 要求電文
     * @throws IOException
     *             異常
     */
    public Object convertToObject(String content, Class<?> valueType) throws IOException {
        StringReader reader = new StringReader(content);
        // リクエストパラメータをBeanに変換
        return readValue(reader, valueType);
    }

    /**
     * Object -> JSON
     *
     * @param <T>
     *            応答電文対象
     * @param data
     *            応答電文対象
     *
     * @return 実行の結果
     * @throws IOException
     *             異常
     */
    public <T> String writeValue(T data) throws IOException {
        return this.writeValueAsString((T) data);
    }

    @Override
    protected Object readValue(Reader src, Class<?> valueType) throws IOException {
        return this.objectMapper.readValue(src, valueType);
    }

    @Override
    protected String writeValueAsString(Object value) throws IOException {
        return this.objectMapper.writeValueAsString(value);
    }

}
