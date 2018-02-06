package jp.co.tis.opal.web.common.RequestData;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import nablarch.core.validation.ee.Required;

/**
 * OPAL制御データ
 *
 * @author 陳
 * @since 1.0
 */
public class RequestOpalControlData implements Serializable {

    /** シリアルバージョンUID */
    private static final long serialVersionUID = 1L;

    /** メッセージID */
    @Required(message = "{M000000001}")
    private String messageId;

    /** 関連メッセージID */
    private String correlationId;

    /** リクエストID */
    @Required(message = "{M000000001}")
    private String requestId;

    /** ユーザID */
    @Required(message = "{M000000001}")
    private String userId;

    /** 再送信フラグ */
    @Required(message = "{M000000001}")
    private String resendFlag;

    /**
     * メッセージIDを取得する。
     *
     * @return メッセージID
     */
    @JsonInclude(Include.NON_NULL)
    public String getMessageId() {
        return messageId;
    }

    /**
     * メッセージIDを設定する。
     *
     * @param messageId
     *            メッセージID
     */
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    /**
     * 関連メッセージIDを取得する。
     *
     * @return 関連メッセージID
     */
    @JsonInclude(Include.NON_NULL)
    public String getCorrelationId() {
        return correlationId;
    }

    /**
     * 関連メッセージIDを設定する。
     *
     * @param correlationId
     *            関連メッセージID
     */
    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    /**
     * リクエストIDを取得する。
     *
     * @return リクエストID
     */
    @JsonInclude(Include.NON_NULL)
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

    /**
     * ユーザIDを取得する。
     *
     * @return ユーザID
     */
    @JsonInclude(Include.NON_NULL)
    public String getUserId() {
        return userId;
    }

    /**
     * ユーザIDを設定する。
     *
     * @param userId
     *            ユーザID
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * 再送信フラグを取得する。
     *
     * @return 再送信フラグ
     */
    @JsonInclude(Include.NON_NULL)
    public String getResendFlag() {
        return resendFlag;
    }

    /**
     * 再送信フラグを設定する。
     *
     * @param resendFlag
     *            再送信フラグ
     */
    public void setResendFlag(String resendFlag) {
        this.resendFlag = resendFlag;
    }

}