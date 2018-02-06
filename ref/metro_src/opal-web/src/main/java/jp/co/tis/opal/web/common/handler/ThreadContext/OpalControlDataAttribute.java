package jp.co.tis.opal.web.common.handler.ThreadContext;

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import nablarch.common.handler.threadcontext.ThreadContextAttribute;
import nablarch.core.ThreadContext;
import nablarch.core.log.Logger;
import nablarch.core.log.LoggerManager;
import nablarch.core.util.StringUtil;
import nablarch.fw.ExecutionContext;
import nablarch.fw.Request;
import nablarch.fw.web.HttpErrorResponse;
import nablarch.fw.web.servlet.ServletExecutionContext;

import jp.co.tis.opal.web.common.RequestData.RequestOpalControlData;
import jp.co.tis.opal.web.common.RequestData.RequestParentOpalControlData;
import jp.co.tis.opal.web.common.rest.BodyReaderHttpServletRequestWarpper;
import jp.co.tis.opal.web.common.rest.HttpHelper;
import jp.co.tis.opal.web.common.rest.OpalJackson2BodyConverter;

import net.unit8.http.router.Options;
import net.unit8.http.router.Routes;

/**
 * スレッドコンテキスト変数管理ハンドラ（要求電文共通部取得）
 *
 * @author 張
 * @since 1.0
 */
public class OpalControlDataAttribute implements ThreadContextAttribute<Request<?>> {
    /** ロガー */
    protected static final Logger LOGGER = LoggerManager.get(OpalControlDataAttribute.class);
    /** キー */
    public static final String KEY = "REQUEST_OPAL_CONTROL_DATA";
    /** 要求電文 */
    private String bodyData;

    /**
     * キーを取得
     *
     * @return キー
     */
    public String getKey() {
        return OpalControlDataAttribute.KEY;
    }

    /**
     * 要求電文共通部を取得
     *
     * @param req
     *            リクエスト
     * @param ctx
     *            実行コンテキスト
     * @return 要求電文共通部
     */
    public Object getValue(Request<?> req, ExecutionContext ctx) {
        if (StringUtil.isNullOrEmpty(req.getRequestPath())) {
            return null;
        }

        ServletRequest requestWarpper;
        try {

            HttpServletRequest request = ((ServletExecutionContext) ctx).getServletRequest();
            requestWarpper = new BodyReaderHttpServletRequestWarpper(request);
            this.bodyData = HttpHelper.getBodyString(requestWarpper);

            String path = ((ServletExecutionContext) ctx).getHttpRequest().getRequestPath();
            Options options = Routes.recognizePath(path, request.getMethod());

            StringBuilder contentPath = new StringBuilder();
            contentPath.append("/");
            contentPath.append(options.getString("controller"));
            contentPath.append("/");
            contentPath.append(options.getString("action"));
            contentPath.append("/");
            contentPath.append(options.getString("id"));

            ThreadContext.setObject("CONTENT_PATH", contentPath);

        } catch (IOException ex) {
            throw new HttpErrorResponse(500, ex);
        }

        RequestOpalControlData data = null;
        if (!StringUtil.isNullOrEmpty(this.bodyData)) {
            try {
                OpalJackson2BodyConverter converter = new OpalJackson2BodyConverter();

                // 要求電文共通部を取得
                RequestParentOpalControlData parent = (RequestParentOpalControlData) converter
                        .convertToObject(this.bodyData, RequestParentOpalControlData.class);

                data = parent.getOpalControlData();

                // その値がnullでなければスレッドコンテキストに設定する。
                if (data != null) {
                    // ユーザIDを設定する
                    ThreadContext.setUserId(data.getUserId());
                }
            } catch (IOException ex) {
                LOGGER.logError(ex.getMessage(), ex);
                throw new HttpErrorResponse(ex);
            }
        }

        return data;
    }
}
