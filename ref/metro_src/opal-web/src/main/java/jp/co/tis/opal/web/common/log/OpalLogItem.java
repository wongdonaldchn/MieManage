package jp.co.tis.opal.web.common.log;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletRequest;

import nablarch.core.ThreadContext;
import nablarch.core.log.LogItem;
import nablarch.core.log.Logger;
import nablarch.core.log.LoggerManager;
import nablarch.core.util.StringUtil;
import nablarch.fw.web.HttpErrorResponse;
import nablarch.fw.web.handler.HttpAccessLogFormatter;
import nablarch.fw.web.handler.HttpAccessLogFormatter.HttpAccessLogContext;

import jp.co.tis.opal.web.common.rest.BodyReaderHttpServletRequestWarpper;
import jp.co.tis.opal.web.common.rest.HttpHelper;

/**
 * ログの出力項目(プレースホルダ)を追加用のアクションクラス。
 *
 * @author 張
 * @since 1.0
 */
public class OpalLogItem implements LogItem<HttpAccessLogContext> {

    /** ロガー */
    protected static final Logger LOGGER = LoggerManager.get(OpalLogItem.class);

    /** 要求電文 */
    public static final String PROCESS_REQUEST_PATTERN = "request";
    /** 応答電文 */
    public static final String PROCESS_RESPONSE_PATTERN = "response";
    /** コンテンツパス */
    public static final String CONTENT_PATH_PATTERN = "contentPath";

    /** HTTPボディ部のメディアの種類 */
    public static final String REQUEST_CONTENT_TYPE_PATTERN = "request_content_type";
    /** リクエスト電文の長さ */
    public static final String REQUEST_CONTENT_LENGTH_PATTERN = "request_content_length";
    /** クライアントが受信可能なContent-Type */
    public static final String REQUEST_ACCEPT_PATTERN = "request_accept";

    /** HTTPボディ部のメディアの種類 */
    public static final String RESPONSE_CONTENT_TYPE_PATTERN = "response_content_type";
    /** リクエスト電文の長さ */
    public static final String RESPONSE_CONTENT_LENGTH_PATTERN = "response_content_length";

    /** 処理パタン（要求電文と応答電文） */
    private String processPattern;

    /** マスク対象のパラメータ名又は変数名の正規表現 */
    private Pattern[] maskingPatterns;

    /** マスクに使用する文字 */
    private String maskingString;

    /** hiddenパラメータ復号後の出力が有効か否か */
    private boolean parametersOutputEnabled;

    /**
     * ログの出力項目
     *
     * @param processPattern
     *            処理パタン
     */
    public OpalLogItem(String processPattern) {
        this.processPattern = processPattern;
    }

    /**
     * ログの出力項目
     *
     * @param processPattern
     *            処理パタン
     * @param maskingPatterns
     *            マスク対象のパラメータ名又は変数名を正規表現
     * @param maskingChar
     *            マスクに使用する文字
     * @param parametersOutputEnabled
     *            hiddenパラメータ復号後の出力が有効か否か
     */
    public OpalLogItem(String processPattern, Pattern[] maskingPatterns, char maskingChar,
            boolean parametersOutputEnabled) {
        this.processPattern = processPattern;
        this.maskingPatterns = maskingPatterns;
        this.maskingString = StringUtil.lpad("", 5, maskingChar);
        this.parametersOutputEnabled = parametersOutputEnabled;
    }

    @Override
    public String get(HttpAccessLogFormatter.HttpAccessLogContext context) {
        if (StringUtil.isNullOrEmpty(processPattern)) {
            return "";
        }

        String accLog = "";
        if (PROCESS_REQUEST_PATTERN.equals(processPattern)) {
            ServletRequest requestWarpper;
            try {
                requestWarpper = new BodyReaderHttpServletRequestWarpper(context.getServletRequest());
                // 要求電文
                String requestBody = HttpHelper.getBodyString(requestWarpper);
                accLog = this.maskingMapValueEditor(requestBody);
            } catch (IOException e) {
                throw new HttpErrorResponse(500, e);
            }
        } else if (PROCESS_RESPONSE_PATTERN.equals(processPattern)) {
            // 応答電文
            String responseBody = String.valueOf(ThreadContext.getObject("responseData"));
            accLog = this.maskingMapValueEditor(responseBody);
        } else if (CONTENT_PATH_PATTERN.equals(processPattern)) {
            // コンテンツパス
            if (ThreadContext.getObject("CONTENT_PATH") == null) {
                accLog = "";
            } else {
                accLog = ThreadContext.getObject("CONTENT_PATH").toString();
            }
        } else if (REQUEST_CONTENT_TYPE_PATTERN.equals(processPattern)) {
            // HTTPボディ部のメディアの種類
            accLog = context.getServletRequest().getHeader("Content-Type");
        } else if (REQUEST_CONTENT_LENGTH_PATTERN.equals(processPattern)) {
            // 電文の長さ
            accLog = context.getServletRequest().getHeader("Content-Length");
        } else if (REQUEST_ACCEPT_PATTERN.equals(processPattern)) {
            // クライアントが受信可能なContent-Type
            accLog = context.getServletRequest().getHeader("Accept");
        } else if (RESPONSE_CONTENT_TYPE_PATTERN.equals(processPattern)) {
            // HTTPボディ部のメディアの種類
            accLog = ThreadContext.getObject("RESPONSE_CONTENT_TYPE").toString();
        } else if (RESPONSE_CONTENT_LENGTH_PATTERN.equals(processPattern)) {
            // 電文の長さ
            accLog = ThreadContext.getObject("RESPONSE_CONTENT_LENGTH").toString();
        }
        return accLog;
    }

    /**
     * パラメータ復号後のログ出力処理。
     *
     * @param accLog
     *            HTTPアクセスログ
     * @return 復号後のログ
     */
    private String maskingMapValueEditor(String accLog) {

        if (!this.parametersOutputEnabled) {
            return accLog;
        }

        if (StringUtil.isNullOrEmpty(accLog)) {
            return accLog;
        }

        if (this.maskingPatterns.length == 0) {
            return accLog;
        }

        for (Pattern pattern : this.maskingPatterns) {
            Matcher matcher = pattern.matcher(accLog);
            if (matcher.find()) {
                String finder = matcher.group(0);
                String[] finders = finder.split(":");
                StringBuilder processData = new StringBuilder();
                processData.append(finders[0]).append(":\"");
                if ("}".equals(finders[1].substring(finders[1].length() - 1))) {
                    processData.append(this.maskingString).append("\"}");
                } else {
                    processData.append(this.maskingString).append("\",");
                }
                accLog = matcher.replaceFirst(processData.toString());
            }
        }
        return accLog;
    }

}
