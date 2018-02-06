package jp.co.tis.opal.batch.common.exception;

import nablarch.core.log.basic.LogLevel;
import nablarch.fw.results.InternalError;

/**
 * 送信した電文に対する応答電文をタイムアウト時間内に受信することができなかった場合に送出される例外。
 *
 * @author 張
 * @since 1.0
 */
public class HttpMessagingTimeoutRetryunableException extends InternalError {

    static final long serialVersionUID = 1L;

    private final int exitCode;

    /**
     * 終了コードとメッセージ（障害コードとオプション）を元に例外を構築する。
     *
     * @param exitCode
     *            終了コード(プロセスを終了(System.exit(int))する際に設定する値)
     * @param failureCode
     *            障害コード
     * @param messageOptions
     *            障害コードからメッセージを取得する際に使用するオプション情報
     */
    public HttpMessagingTimeoutRetryunableException(int exitCode, String failureCode, Object[] messageOptions) {
        super(LogLevel.FATAL, failureCode, messageOptions);
        this.exitCode = exitCode;
    }

    /**
     * 終了コードとメッセージ（障害コードとオプション）、元例外Throwableを元に例外を構築する。
     *
     * @param exitCode
     *            終了コード(プロセスを終了(System.exit(int))する際に設定する値)
     * @param error
     *            元例外
     * @param failureCode
     *            障害コード
     * @param messageOptions
     *            障害コードからメッセージを取得する際に使用するオプション情報
     */
    public HttpMessagingTimeoutRetryunableException(int exitCode, Throwable error, String failureCode,
            Object[] messageOptions) {
        super(LogLevel.FATAL, error, failureCode, messageOptions);
        this.exitCode = exitCode;
    }

    /**
     * ステータスコードを返す。
     *
     * @return インスタンス生成時に指定された終了コードを返却する。
     */
    public int getStatusCode() {
        return this.exitCode;
    }

}
