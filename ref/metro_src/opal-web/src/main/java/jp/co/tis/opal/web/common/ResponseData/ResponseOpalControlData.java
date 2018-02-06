package jp.co.tis.opal.web.common.ResponseData;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * OPAL制御データ
 *
 * @author 陳
 * @since 1.0
 */
public class ResponseOpalControlData implements Serializable {

    /** シリアルバージョンUID */
    private static final long serialVersionUID = 1L;

    /** メッセージID */
    private String messageId;

    /** 関連メッセージID */
    private String correlationId;

    /** リクエストID */
    private String requestId;

    /**
     * メッセージIDを取得する。
     *
     * @return メッセージID
     */
    public String getMessageId() {
        return messageId;
    }

    /**
     * メッセージIDを設定する。
     *
     * @param messageId
     *            メッセージID
     */
    @JsonInclude(Include.NON_NULL)
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    /**
     * 関連メッセージIDを取得する。
     *
     * @return 関連メッセージID
     */
    public String getCorrelationId() {
        return correlationId;
    }

    /**
     * 関連メッセージIDを設定する。
     *
     * @param correlationId
     *            関連メッセージID
     */
    @JsonInclude(Include.NON_NULL)
    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    /**
     * リクエストIDを取得する。
     *
     * @return リクエストID
     */
    public String getRequestId() {
        return requestId;
    }

    /**
     * リクエストIDを設定する。
     *
     * @param requestId
     *            リクエストID
     */
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}