package jp.co.tis.opal.web.common.log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import nablarch.core.log.LogItem;
import nablarch.core.util.StringUtil;
import nablarch.fw.web.handler.HttpAccessLogFormatter;

/**
 * ログの出力項目(プレースホルダ)を追加用のアクションクラス。
 *
 * @author 張
 * @since 1.0
 */
public class OpalLogFormatter extends HttpAccessLogFormatter {
    /**
     * カスタムのLogFormatterを指定のクラス。
     */
    public OpalLogFormatter() {
    }

    /*
     * フォーマット対象のログ出力項目を取得するメソッドをオーバーライドする。
     */
    @Override
    protected Map<String, LogItem<HttpAccessLogContext>> getLogItems(Map<String, String> props) {

        // 起動プロセスのプレースホルダを上書きで設定する。
        Map<String, LogItem<HttpAccessLogContext>> logItems = super.getLogItems(props);

        // マスク対象のパラメータ名又は変数名の正規表現を取得
        Pattern[] maskingPatterns = this.getMaskingPatterns(props);
        // マスクに使用する文字を取得
        char maskingChar = getMaskingChar(props);
        // hiddenパラメータ復号後の出力が有効か否か
        boolean parametersOutputEnabled = Boolean
                .valueOf(getProp(props, "httpAccessLogFormatter.parametersOutputEnabled", Boolean.TRUE.toString()))
                .booleanValue();

        // 要求電文用
        logItems.put("$requestData$", new OpalLogItem(OpalLogItem.PROCESS_REQUEST_PATTERN, maskingPatterns, maskingChar,
                parametersOutputEnabled));

        // 応答電文用
        logItems.put("$responseData$", new OpalLogItem(OpalLogItem.PROCESS_RESPONSE_PATTERN, maskingPatterns,
                maskingChar, parametersOutputEnabled));

        // コンテンツパス
        logItems.put("$content_path$", new OpalLogItem(OpalLogItem.CONTENT_PATH_PATTERN));

        // HTTPボディ部のメディアの種類
        logItems.put("$request_content_type$", new OpalLogItem(OpalLogItem.REQUEST_CONTENT_TYPE_PATTERN));
        // 電文の長さ
        logItems.put("$request_content_length$", new OpalLogItem(OpalLogItem.REQUEST_CONTENT_LENGTH_PATTERN));
        // クライアントが受信可能なContent-Type
        logItems.put("$request_accept$", new OpalLogItem(OpalLogItem.REQUEST_ACCEPT_PATTERN));

        // HTTPボディ部のメディアの種類
        logItems.put("$response_content_type$", new OpalLogItem(OpalLogItem.RESPONSE_CONTENT_TYPE_PATTERN));
        // 電文の長さ
        logItems.put("$response_content_length$", new OpalLogItem(OpalLogItem.RESPONSE_CONTENT_LENGTH_PATTERN));

        return logItems;
    }

    @Override
    protected Pattern[] getMaskingPatterns(Map<String, String> props) {
        String patterns = (String) props.get("httpAccessLogFormatter.maskingPatterns");
        if (patterns == null) {
            return new Pattern[0];
        }
        String[] splitPatterns = Pattern.compile(",").split(patterns);
        List<Pattern> maskingPatterns = new ArrayList<Pattern>();
        for (String regex : splitPatterns) {
            StringBuilder sbReg = new StringBuilder();
            if (StringUtil.isNullOrEmpty(regex.trim())) {
                continue;
            }
            sbReg.append(regex.trim()).append("(.*?)(,|})");
            maskingPatterns.add(Pattern.compile(sbReg.toString(), 2));
        }

        return ((Pattern[]) maskingPatterns.toArray(new Pattern[maskingPatterns.size()]));
    }
}
