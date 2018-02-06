package jp.co.tis.opal.web.common.error;

import javax.persistence.OptimisticLockException;

import nablarch.common.dao.NoDataException;
import nablarch.core.log.Logger;
import nablarch.core.log.LoggerManager;
import nablarch.core.message.ApplicationException;
import nablarch.core.message.Message;
import nablarch.fw.ExecutionContext;
import nablarch.fw.jaxrs.JaxRsErrorLogWriter;
import nablarch.fw.results.ServiceUnavailable;
import nablarch.fw.web.HttpRequest;
import nablarch.fw.web.HttpResponse;

/**
 * HTTPアクセスログ出力クラス。
 *
 * @author 張
 * @since 1.0
 */
public class APIErrorLogWriter extends JaxRsErrorLogWriter {

    /** ロガー */
    protected static final Logger LOGGER = LoggerManager.get(APIErrorLogWriter.class);

    /**
     * HTTPアクセスログを出力する。
     */
    @Override
    public void write(HttpRequest request, HttpResponse response, ExecutionContext context, Throwable throwable) {
        if (!(throwable instanceof NoDataException) && !(throwable instanceof OptimisticLockException)
                && !(throwable instanceof ServiceUnavailable)) {
            super.write(request, response, context, throwable);
        }
    }

    /**
     * アプリケーションログを出力する。
     */
    @Override
    protected void writeApplicationExceptionLog(HttpRequest request, HttpResponse response, ExecutionContext context,
            ApplicationException exception) {
        for (Message message : exception.getMessages()) {
            LOGGER.logInfo(message.formatMessage());
        }
    }
}
