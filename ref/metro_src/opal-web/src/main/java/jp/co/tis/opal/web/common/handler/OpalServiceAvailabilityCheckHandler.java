package jp.co.tis.opal.web.common.handler;

import nablarch.common.availability.BasicServiceAvailability;
import nablarch.core.ThreadContext;
import nablarch.core.log.Logger;
import nablarch.core.log.LoggerManager;
import nablarch.core.util.Builder;
import nablarch.fw.ExecutionContext;
import nablarch.fw.Handler;
import nablarch.fw.Result;
import nablarch.fw.results.ServiceUnavailable;

/**
 * OPALサービス提供可否チェックハンドラのクラス。
 *
 * @author 張
 * @since 1.0
 */
public class OpalServiceAvailabilityCheckHandler implements Handler<Object, Object> {
    private BasicServiceAvailability serviceAvailability;

    private static final Logger LOGGER = LoggerManager.get(OpalServiceAvailabilityCheckHandler.class);

    /**
     * OPALサービス提供可否チェックハンドラ
     */
    public OpalServiceAvailabilityCheckHandler() {
    }

    /**
     * OPALサービス提供可否を設定する。
     *
     * @param serviceAvailability
     *            サービス提供可否
     */
    public void setServiceAvailability(BasicServiceAvailability serviceAvailability) {
        this.serviceAvailability = serviceAvailability;
    }

    /**
     * 内部使用リクエストIdを設定する。
     *
     * @param usesInternal
     *            内部使用
     * @return リクエストId
     */
    public OpalServiceAvailabilityCheckHandler setUsesInternalRequestId(boolean usesInternal) {
        return this;
    }

    /**
     * OPALサービス提供可否チェックハンドラ
     *
     * @param inputData
     *            入力データ
     * @param context
     *            実行コンテキスト
     * @return
     */
    @Override
    public Object handle(Object inputData, ExecutionContext context) {
        handleInbound(context);
        return context.handleNext(inputData);
    }

    /**
     * OPALサービス提供可否チェックハンドラ実行
     *
     * @param context
     *            実行コンテキスト
     * @return 実効成功
     */
    public Result handleInbound(ExecutionContext context) {
        String requestId = ThreadContext.getRequestId();

        this.serviceAvailability.initialize();
        if (!(this.serviceAvailability.isAvailable(requestId))) {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.logTrace(Builder.concat(new Object[] { "service unavailable. requestId=[", requestId, "]" }),
                        new Object[0]);
            }

            throw new ServiceUnavailable();
        }

        return new Result.Success();
    }
}
