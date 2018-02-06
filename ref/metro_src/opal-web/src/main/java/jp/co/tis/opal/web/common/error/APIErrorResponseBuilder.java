package jp.co.tis.opal.web.common.error;

import nablarch.common.dao.NoDataException;
import nablarch.core.ThreadContext;
import nablarch.core.message.ApplicationException;
import nablarch.core.util.StringUtil;
import nablarch.fw.ExecutionContext;
import nablarch.fw.jaxrs.ErrorResponseBuilder;
import nablarch.fw.results.ServiceUnavailable;
import nablarch.fw.web.HttpRequest;
import nablarch.fw.web.HttpResponse;
import nablarch.fw.web.handler.HttpAccessLogFormatter;
import nablarch.fw.web.handler.HttpAccessLogUtil;
import nablarch.fw.web.servlet.ServletExecutionContext;

/**
 * ラーレスポンス生成のクラス。
 *
 * @author 張
 * @since 1.0
 */
public class APIErrorResponseBuilder extends ErrorResponseBuilder {
    /**
     * エラーレスポンスを生成する。
     * <p/>
     * 発生した例外クラスが ApplicationException の場合は{@code 400}、
     * HttpErrorResponse#getResponse() から戻される HttpResponse がクライアントに戻される。
     * 発生した例外クラスが{@link NoDataException}の場合は{@code 404}、
     * 発生した例外クラスが{@link ServiceUnavailable}の場合は{@code 503}を生成する。
     * それ以外のエラーの場合には、上位クラスに処理を委譲する。
     *
     * @param request
     *            {@link HttpRequest}HTTPリクエスト
     * @param context
     *            {@link ExecutionContext}実行文脈
     * @param throwable
     *            発生したエラーの情報
     * @return エラーレスポンス
     */
    @Override
    public HttpResponse build(HttpRequest request, ExecutionContext context, Throwable throwable) {
        final HttpResponse response;
        if (throwable instanceof ApplicationException) {
            // 電文解析・フォーマット変換時エラー
            response = new HttpResponse(400);
        } else if (throwable instanceof NoDataException) {
            // 更新対象しない時エラー
            response = new HttpResponse(404);
        } else if (throwable instanceof ServiceUnavailable) {
            // 閉局エラー
            response = new HttpResponse(503);
        } else {
            // その他の予期しないエラー
            response = super.build(request, context, throwable);
        }

        ServletExecutionContext ctx = (ServletExecutionContext) context;
        HttpAccessLogFormatter.HttpAccessLogContext logContext = HttpAccessLogUtil.getAccessLogContext(request, ctx);
        logContext.setResponse(response);
        Object[] responseOptions = new Object[0];

        // HTTPボディ部のメディアの種類
        ThreadContext.setObject("RESPONSE_CONTENT_TYPE", StringUtil.nullToEmpty(response.getHeader("Content-Type")));
        // 電文の長さ
        ThreadContext.setObject("RESPONSE_CONTENT_LENGTH",
                StringUtil.nullToEmpty(response.getHeader("Content-Length")));

        HttpAccessLogUtil.end(logContext, responseOptions);

        return response;
    }

}
