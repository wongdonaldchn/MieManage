package jp.co.tis.opal.batch.common.exception;

import nablarch.fw.handler.retry.Retryable;
import nablarch.fw.results.InternalError;

/**
 * 送信した電文に対する応答電文をタイムアウト時間内に受信することができなかった場合に送出される例外。
 *
 * @author 張
 * @since 1.0
 */
public class HttpMessagingTimeoutRetryableException extends InternalError implements Retryable {

    static final long serialVersionUID = 1L;

    /**
     * ハンドラの内部処理で発生した問題により、処理が継続できないことを 示す例外。
     *
     * @param cause
     *            例外
     */
    public HttpMessagingTimeoutRetryableException(Throwable cause) {
        super(cause);
    }

}
