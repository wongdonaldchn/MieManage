package jp.co.tis.opal.batch.ss118A;

import java.util.LinkedHashMap;
import java.util.Map;

import nablarch.common.io.FileRecordWriterHolder;
import nablarch.core.beans.BeanUtil;
import nablarch.core.dataformat.DataRecord;
import nablarch.core.date.SystemTimeUtil;
import nablarch.core.db.statement.ParameterizedSqlPStatement;
import nablarch.core.message.ApplicationException;
import nablarch.core.message.Message;
import nablarch.core.validation.ee.ValidatorUtil;
import nablarch.fw.ExecutionContext;
import nablarch.fw.Result;
import nablarch.fw.action.FileBatchAction;
import nablarch.fw.launcher.CommandLine;
import nablarch.fw.results.TransactionAbnormalEnd;

import jp.co.tis.opal.common.constants.OpalDefaultConstants;

/**
 * B118A011:主なご利用駅一時情報作成。
 *
 * @author 唐
 * @since 1.0
 */
public class B118A011Action extends FileBatchAction {

    /** ファイル名 */
    private static final String FILE_ID = "A118A001";

    /** フォーマットファイル名(拡張子除く) */
    private static final String FORMAT_ID = "A118A001";

    /** エラーフォーマットファイル名(拡張子除く) */
    private static final String ERROR_FORMAT_ID = "A118A001_ERROR";

    /** バッチ処理ID */
    private static final String BATCH_PROCESS_ID = "B118A011";

    /** バッチエラーファイル名称 */
    private StringBuffer batchErrorFileName = new StringBuffer("A118A001_error_");

    /** 入力データ件数(主なご利用駅情報データレコード) */
    private int inputDataCount;

    /** 出力データ件数(主なご利用駅一時情報)(登録) */
    private int insertMainStationTempInfoCount;

    /** 主なご利用駅情報レコード精査エラー存在確認用 */
    private boolean isExistError;

    /**
     * 入力データファイル名の取得。
     *
     * @return ファイル名
     */
    @Override
    public String getDataFileName() {
        return FILE_ID;
    }

    /**
     * フォーマットファイル名の取得。
     *
     * @return フォーマットファイル名（拡張子除く）
     */
    @Override
    public String getFormatFileName() {
        return FORMAT_ID;
    }

    /**
     * 初期化処理。
     *
     * @param command
     *            コマンドライン引数
     * @param context
     *            実行コンテキスト
     */
    @Override
    protected void initialize(CommandLine command, ExecutionContext context) {

        // 出力データ件数(主なご利用駅一時情報)(登録)
        this.insertMainStationTempInfoCount = 0;
        // 入力データ件数(主なご利用駅情報データレコード)
        this.inputDataCount = 0;
        // バッチエラーファイル名称
        batchErrorFileName.append(SystemTimeUtil.getDateTimeString());
        // バッチエラーファイル生成
        FileRecordWriterHolder.open("error", batchErrorFileName.toString(), ERROR_FORMAT_ID);
    }

    /**
     * データのバリデーションと登録を行う。
     * <p/>
     *
     * @param inputData
     *            一行分のデータ
     * @param ctx
     *            実行コンテキスト
     * @return 結果オブジェクト
     */
    public Result doData(DataRecord inputData, ExecutionContext ctx) {

        // 入力データ件数(主なご利用駅情報データレコード)をカウントアップする
        this.inputDataCount++;
        // inputDataをFormに展開する
        A118A001DataForm dataForm = BeanUtil.createAndCopy(A118A001DataForm.class, inputData);
        try {
            // バリデーションを実行する
            ValidatorUtil.validate(dataForm);
            // データレコードを主なご利用駅一時情報TBLに登録する
            insertMainStationTempInfo(inputData);
            // 出力データ件数(主なご利用駅一時情報)(登録)をカウントアップする
            insertMainStationTempInfoCount++;
        } catch (ApplicationException e) {
            // 精査エラーが存在する場合
            this.isExistError = true;

            for (Message message : e.getMessages()) {
                // キー情報を設定
                Map<String, Object> keyMap = new LinkedHashMap<String, Object>();
                keyMap.put("会員管理番号", inputData.getString("memberControlNumber"));
                keyMap.put("会員管理番号枝番", inputData.getString("memrCtrlBrNum"));
                // バリデーションエラーの内容をロギングする
                writeLog("M000000017", message, keyMap, inputData.getRecordNumber());

                // 行番号
                StringBuffer line = new StringBuffer("line:");
                line.append(inputData.getRecordNumber());
                // 会員管理番号
                StringBuffer memberControlNumber = new StringBuffer("memberControlNumber:");
                memberControlNumber.append(inputData.getString("memberControlNumber"));
                // 会員管理番号枝番
                StringBuffer memrCtrlBrNum = new StringBuffer("memrCtrlBrNum:");
                memrCtrlBrNum.append(inputData.getString("memrCtrlBrNum"));
                // メッセージ
                StringBuffer errorMessage = new StringBuffer("message:");
                errorMessage.append(message.formatMessage());
                // エラーファイル出力処理。
                writeRecord(line.toString(), memberControlNumber.toString(), memrCtrlBrNum.toString(),
                        errorMessage.toString());
            }

        }
        return new Result.Success();
    }

    /**
     * エラーファイル出力処理。
     *
     * @param line
     *            行番号
     * @param memberControlNumber
     *            会員管理番号
     * @param memrCtrlBrNum
     *            会員管理番号枝番
     * @param message
     *            メッセージ
     */
    private void writeRecord(String line, String memberControlNumber, String memrCtrlBrNum, String message) {
        Map<String, Object> errorMap = new LinkedHashMap<String, Object>();
        errorMap.put("line", line);
        errorMap.put("memberControlNumber", memberControlNumber);
        errorMap.put("memrCtrlBrNum", memrCtrlBrNum);
        errorMap.put("message", message);
        FileRecordWriterHolder.write(errorMap, "error", batchErrorFileName.toString());
    }

    /**
     * 終了処理。
     * <p/>
     * 成功件数をロギングする。
     *
     * @param result
     *            結果オブジェクト
     * @param context
     *            実行コンテキスト
     */
    @Override
    protected void terminate(Result result, ExecutionContext context) {

        // エラーファイルを出力する。
        FileRecordWriterHolder.close("error", batchErrorFileName.toString());

        // 入力データ件数(主なご利用駅情報データレコード)
        writeLog("MB118A0101", this.inputDataCount);
        // 出力データ件数(主なご利用駅一時情報)(登録)
        writeLog("MB118A0102", this.insertMainStationTempInfoCount);
        // 主なご利用駅情報レコードで精査エラーが存在する場合は、エラーを出力する。
        if (this.isExistError) {
            throw new TransactionAbnormalEnd(100, "AB118A0101");
        }
    }

    /**
     * 主なご利用駅一時情報登録処理
     *
     * @param inputData
     *            入力データ
     * @return 処理件数
     */
    private int insertMainStationTempInfo(DataRecord inputData) {

        // DBに登録する値を設定する。
        // 処理済フラグ
        inputData.put("processedFlag", OpalDefaultConstants.PROCESSED_FLAG_0);
        // 登録者ID
        inputData.put("insertUserId", BATCH_PROCESS_ID);
        // 登録日時
        inputData.put("insertDateTime", SystemTimeUtil.getTimestamp());
        // 最終更新者ID
        inputData.put("updateUserId", BATCH_PROCESS_ID);
        // 最終更新日時
        inputData.put("updateDateTime", SystemTimeUtil.getTimestamp());
        // 削除フラグ
        inputData.put("deletedFlag", OpalDefaultConstants.DELETED_FLG_0);
        // 論理削除日
        inputData.put("deletedDate", null);

        // データを登録する
        ParameterizedSqlPStatement statement = super.getParameterizedSqlStatement("INSERT_MAIN_USE_STAION_TEMP_INFO");
        return statement.executeUpdateByMap(inputData);
    }

}
