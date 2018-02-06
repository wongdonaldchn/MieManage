package jp.co.tis.opal.batch.ss136A;

import java.util.HashMap;
import java.util.Map;

import nablarch.common.io.FileRecordWriterHolder;
import nablarch.core.date.SystemTimeUtil;
import nablarch.core.db.statement.SqlPStatement;
import nablarch.core.db.statement.SqlRow;
import nablarch.core.util.StringUtil;
import nablarch.fw.DataReader;
import nablarch.fw.ExecutionContext;
import nablarch.fw.Result;
import nablarch.fw.Result.Success;
import nablarch.fw.action.BatchAction;
import nablarch.fw.launcher.CommandLine;
import nablarch.fw.reader.DatabaseRecordReader;

import jp.co.tis.opal.common.constants.OpalDefaultConstants;

/**
 * B136A013:マイル移行情報ファイル作成のアクションクラス。
 *
 * @author 曹
 * @since 1.0
 */
public class B136A013Action extends BatchAction<SqlRow> {

    /** 入力データ件数(マイル移行一時情報) */
    private int inputDataMileInvalidTemp;

    /** 出力データ件数(マイル移行情報ファイル) */
    private int outputMileInvalidFile;

    /** 出力ファイルID：A136A001(マイル移行情報ファイル) */
    private static final String FILE_ID = "A136A001";

    /** フォーマット定義ファイルID：A136A001 */
    private static final String FORMAT_ID = "A136A001";

    /**
     * {@inneritDoc}
     * <p/>
     * 事前処理
     */
    @Override
    protected void initialize(CommandLine command, ExecutionContext context) {

        // 処理ログの初期化
        inputDataMileInvalidTemp = 0;
        outputMileInvalidFile = 0;

        // 出力先のファイルをオープンする。
        FileRecordWriterHolder.open(FILE_ID, FORMAT_ID);

        // ヘッダレコードを出力する。
        writeHeaderRecord();
    }

    /**
     * {@inneritDoc}
     * <p/>
     * マイル移行一時情報から処理対象のレコードを読み込む。
     */
    @Override
    public DataReader<SqlRow> createReader(ExecutionContext ctx) {

        // マイル移行一時情報から処理対象のレコードを読み込む。
        inputDataMileInvalidTemp = countByStatementSql("SELECT_MILE_TRANS_TEMP_INFO");
        // 入力データ件数をログに出力
        writeLog("MB136A0108", Integer.valueOf(inputDataMileInvalidTemp));

        // マイル移行一時情報から処理対象のレコードを読み込む。
        DatabaseRecordReader reader = new DatabaseRecordReader();
        SqlPStatement statement = getSqlPStatement("SELECT_MILE_TRANS_TEMP_INFO");
        reader.setStatement(statement);

        return reader;
    }

    /**
     * {@inneritDoc}
     * <p/>
     * 本処理
     */
    @Override
    public Result handle(SqlRow inputData, ExecutionContext ctx) {

        // フォーマット編集
        formatData(inputData);

        // データレコード出力
        writeRecord("data", inputData);

        // 出力データ件数(マイル移行情報ファイル)をカウントアップする。
        outputMileInvalidFile++;

        return new Success();
    }

    /**
     * {@inneritDoc}
     * <p/>
     * 事後処理
     */
    @Override
    protected void terminate(Result result, ExecutionContext context) {

        if (result.isSuccess()) {
            // トレーラレコードを出力する。
            writeTrailerRecord();

            // エンドレコードを出力する。
            writeEndRecord();

            FileRecordWriterHolder.close(FILE_ID);
        }

        // 出力データ件数(マイル移行情報ファイル)をログに出力する。
        writeLog("MB136A0109", Integer.valueOf(outputMileInvalidFile));
    }

    /**
     * データレコードのフォーマット
     *
     * @param inputData
     *            マイル移行情報レコード
     */
    private void formatData(SqlRow inputData) {

        // 会員管理番号
        inputData.put("memCtrlNum", inputData.getString("MEMBER_CONTROL_NUMBER"));
        // 会員管理番号枝番
        inputData.put("memCtrlNumBrNum", inputData.getString("MEM_CTRL_NUM_BR_NUM"));
        // OP番号
        inputData.put("osakaPitapaNumber", inputData.getString("OSAKA_PITAPA_NUMBER"));
        // 移行マイル数
        inputData.put("transitionMileAmount", inputData.getLong("TRANSITION_MILE_AMOUNT"));
        // マイル移行区分
        inputData.put("transitionDivision", inputData.getString("MILE_TRANSITION_DIVISION"));
    }

    /**
     * ヘッダレコード出力
     *
     */
    private void writeHeaderRecord() {

        Map<String, String> header = new HashMap<String, String>();
        // 処理日付
        header.put("dataDate", SystemTimeUtil.getDateString());
        // 処理時刻
        header.put("dataTime", SystemTimeUtil.getDateTimeMillisString()
                .substring(OpalDefaultConstants.POSITION_TIME_START, OpalDefaultConstants.POSITION_TIME_END));

        // ヘッダレコード出力
        writeRecord("header", header);
    }

    /**
     * トレーラレコード出力
     *
     */
    private void writeTrailerRecord() {

        Map<String, Object> trailer = new HashMap<String, Object>();
        // データレコード数
        trailer.put("dataRecordCnt", StringUtil.lpad(String.valueOf(outputMileInvalidFile),
                OpalDefaultConstants.PAD_RECORD_LENGTH, OpalDefaultConstants.CHAR_FOR_PADDING));

        // トレーラレコード出力
        writeRecord("trailer", trailer);
    }

    /**
     * エンドレコード出力
     *
     */
    private void writeEndRecord() {

        // エンドレコード出力
        writeRecord("end", new HashMap<String, Object>(0));
    }

    /**
     * ファイル出力処理。 指定されたMapを1レコードとしてファイル出力を行う。
     * <p/>
     *
     * @param recordType
     *            レコードタイプを表す文字列
     * @param record
     *            1レコードの情報を格納したMap
     */
    private void writeRecord(String recordType, Map<String, ?> record) {

        FileRecordWriterHolder.write(recordType, record, FILE_ID);
    }

}
