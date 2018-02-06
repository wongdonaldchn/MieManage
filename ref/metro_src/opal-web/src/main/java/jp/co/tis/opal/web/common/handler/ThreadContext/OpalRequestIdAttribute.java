package jp.co.tis.opal.web.common.handler.ThreadContext;

import nablarch.common.handler.threadcontext.RequestIdAttribute;
import nablarch.core.log.Logger;
import nablarch.core.log.LoggerManager;
import nablarch.core.util.StringUtil;
import nablarch.fw.ExecutionContext;
import nablarch.fw.Request;
import nablarch.fw.web.HttpErrorResponse;
import nablarch.fw.web.HttpRequest;
import nablarch.fw.web.servlet.ServletExecutionContext;

import net.unit8.http.router.Options;
import net.unit8.http.router.Routes;
import net.unit8.http.router.RoutingException;

/**
 * スレッドコンテキスト変数管理ハンドラ（要求電文共通部取得）
 *
 * @author 張
 * @since 1.0
 */
public class OpalRequestIdAttribute extends RequestIdAttribute {

    /** ロガー */
    protected static final Logger LOGGER = LoggerManager.get(OpalRequestIdAttribute.class);

    @Override
    public Object getValue(Request<?> req, ExecutionContext ctx) {
        assert (req != null);
        if (StringUtil.isNullOrEmpty(req.getRequestPath())) {
            return null;
        }

        try {
            HttpRequest request = ((ServletExecutionContext) ctx).getHttpRequest();

            Options options = Routes.recognizePath(request.getRequestPath(), request.getMethod());
            String requestId = options.getString("id");

            return requestId;
        } catch (RoutingException e) {
            LOGGER.logError(e.getMessage(), e);
            throw new HttpErrorResponse(404, e);
        }
    }
}
