package jp.co.tis.opal.batch.common.utility;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nablarch.core.dataformat.DataRecordFormatter;
import nablarch.core.dataformat.DataRecordFormatterSupport;
import nablarch.core.dataformat.FormatterFactory;
import nablarch.core.dataformat.InvalidDataFormatException;
import nablarch.core.util.FilePathSetting;

/**
 * 各種データとMapの相互変換を行うユーティリティクラス。
 *
 * @author TIS
 * @since 1.0
 */
public final class OpalSimpleDataConvertUtil {

    /**
     * 隠蔽コンストラクタ
     */
    private OpalSimpleDataConvertUtil() {
    }

    /**
     * 構造化データの文字列からMapを生成する。 変換前の構造化データ形式はフォーマット定義ファイルにて指定される。
     *
     * @param formatName
     *            フォーマット定義ファイル
     * @param data
     *            変換対象データ
     * @return 変換結果
     * @throws InvalidDataFormatException
     *             入力データが不正な場合。
     */
    public static OpalSimpleDataConvertResult parseData(String formatName, String data)
            throws InvalidDataFormatException {
        // フォーマッタ取得
        DataRecordFormatter formatter = getFormatter(formatName);

        // データを解析し、返却
        Charset charset = getCharset(formatter);
        try {
            return parseData(formatName, new ByteArrayInputStream(data.getBytes(charset)));
        } catch (IOException wontHappen) {
            // データフォーマッターに渡す入力ストリームとして、ヒープ上のバイト列を使用して
            // レコードの読み取りに際してI/Oエラーは発生しえない。
            // (このことはJDKのJavadocに仕様として記載されている。)
            throw new RuntimeException(wontHappen);
        }
    }

    /**
     * 構造化データのストリームからMapを生成する。 変換前の構造化データ形式はフォーマット定義ファイルにて指定される。
     *
     * @param formatName
     *            フォーマット定義ファイル
     * @param in
     *            変換対象データ読み込み用ストリーム
     * @return 変換結果
     * @throws InvalidDataFormatException
     *             入力データが不正な場合。
     * @throws IOException
     *             読み込みに伴うIO処理で問題が発生した場合。
     */
    public static OpalSimpleDataConvertResult parseData(String formatName, InputStream in)
            throws InvalidDataFormatException, IOException {
        // フォーマッタ取得
        DataRecordFormatter formatter = getFormatter(formatName);

        // データを解析
        formatter.setInputStream(in);
        formatter.initialize();

        List<Map<String, ?>> resultListMap = new ArrayList<Map<String, ?>>();

        while (formatter.hasNext()) {
            resultListMap.add(formatter.readRecord());
        }
        formatter.close();

        return createResult(formatter).setResultListMap(resultListMap);
    }

    /**
     * フォーマット名に対応したフォーマッタを取得する。
     *
     * @param formatName
     *            フォーマット名
     * @return フォーマッタ
     */
    private static DataRecordFormatter getFormatter(String formatName) {
        // フォーマットファイルを論理パスから取得
        File formatFile = FilePathSetting.getInstance().getFileWithoutCreate("format", formatName);

        // フォーマッタを生成・初期化
        DataRecordFormatter formatter = FormatterFactory.getInstance().createFormatter(formatFile);

        formatter.initialize();

        return formatter;
    }

    /**
     * 変換結果オブジェクトを生成する。
     *
     * @param formatter
     *            フォーマッタ
     * @return 変換結果
     */
    private static OpalSimpleDataConvertResult createResult(DataRecordFormatter formatter) {
        OpalSimpleDataConvertResult result = new OpalSimpleDataConvertResult();

        // フォーマッタの各種設定値を取得
        if (formatter instanceof DataRecordFormatterSupport) {
            DataRecordFormatterSupport drfs = ((DataRecordFormatterSupport) formatter);
            result.setCharset(drfs.getDefaultEncoding());
            result.setDataType(drfs.getFileType());
            result.setMimeType(drfs.getMimeType());
        }

        return result;
    }

    /**
     * フォーマッタに定義されている文字セットを取得する。 取得できない場合はプラットフォームのデフォルト文字セットを取得する。
     *
     * @param formatter
     *            フォーマッタ
     * @return 文字セット
     */
    private static Charset getCharset(DataRecordFormatter formatter) {
        Charset charset = Charset.defaultCharset();
        if (formatter instanceof DataRecordFormatterSupport) {
            charset = ((DataRecordFormatterSupport) formatter).getDefaultEncoding();
        }
        return charset;
    }

}
