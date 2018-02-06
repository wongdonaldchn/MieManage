package jp.co.tis.opal.web.common.ResponseData;

import java.io.Serializable;

/**
 * エラー情報
 *
 * @author 陳
 * @since 1.0
 */
public class ResponseErrorData implements Serializable {

    /** シリアルバージョンUID */
    private static final long serialVersionUID = 1L;

    /** エラーメッセージID */
    private String id;

    /** エラーメッセージ */
    private String message;

    /**
     * エラーメッセージIDを取得する。
     *
     * @return エラーメッセージID
     */
    public String getId() {
        return id;
    }

    /**
     * エラーメッセージIDを設定する。
     *
     * @param id
     *            エラーメッセージID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * エラーメッセージを取得する。
     *
     * @return エラーメッセージ
     */
    public String getMessage() {
        return message;
    }

    /**
     * エラーメッセージを設定する。
     *
     * @param message
     *            エラーメッセージ
     */
    public void setMessage(String message) {
        this.message = message;
    }
}