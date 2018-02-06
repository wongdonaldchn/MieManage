package jp.co.tis.opal.batch.ss135A;

import java.util.LinkedHashMap;
import java.util.Map;

import nablarch.common.io.FileRecordWriterHolder;
import nablarch.core.beans.BeanUtil;
import nablarch.core.dataformat.DataRecord;
import nablarch.core.date.SystemTimeUtil;
import nablarch.core.db.statement.ParameterizedSqlPStatement;
import nablarch.core.message.ApplicationException;
import nablarch.core.message.Message;
import nablarch.core.message.MessageLevel;
import nablarch.core.message.MessageUtil;
import nablarch.core.util.StringUtil;
import nablarch.core.validation.ee.ValidatorUtil;
import nablarch.fw.ExecutionContext;
import nablarch.fw.Result;
import nablarch.fw.action.FileBatchAction;
import nablarch.fw.launcher.CommandLine;
import nablarch.fw.reader.ValidatableFileDataReader;
import nablarch.fw.results.TransactionAbnormalEnd;

import jp.co.tis.opal.common.constants.OpalDefaultConstants;

/**
 * 乗車マイル情報取込。
 *
 * @author 唐
 * @since 1.0
 */
public class B135A011Action extends FileBatchAction {

    /** ファイル名 */
    private static final String FILE_ID = "A135A001";

    /** フォーマットファイル名(拡張子除く) */
    private static final String FORMAT_ID = "A135A001";

    /** エラーフォーマットファイル名(拡張子除く) */
    private static final String ERROR_FORMAT_ID = "A135A001_ERROR";

    /** バッチ処理ID */
    private static final String BATCH_PROCESS_ID = "B135A011";

    /** バッチエラーファイル名称 */
    private StringBuffer batchErrorFileName = new StringBuffer("A135A001_error_");

    /** 入力データ件数(乗車マイル情報レコード) */
    private int inputDataCount = 0;

    /** 出力データ件数(乗車マイル取込一時情報)(登録) */
    private int insertRideMileTempInfoCount;

    /** レコード存在確認用 */
    private boolean isExistRecord;

    /** 乗車マイルレコード精査エラー存在確認用 */
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

        // 件数の初期値を設定する
        this.insertRideMileTempInfoCount = 0;

        // バッチエラーファイル名称
        batchErrorFileName.append(SystemTimeUtil.getDateTimeString());
        // バッチエラーファイル生成
        FileRecordWriterHolder.open("error", batchErrorFileName.toString(), ERROR_FORMAT_ID);
    }

    /**
     * ファイルレイアウトをバリデーションする。
     *
     * @return バリデータアクション
     */
    @Override
    public ValidatableFileDataReader.FileValidatorAction getValidatorAction() {
        return new FileLayoutValidatorAction(FILE_ID);
    }

    /**
     * ヘッダレコードの処理。
     *
     * @param inputData
     *            一行分のデータ
     * @param ctx
     *            実行コンテキスト
     * @return 結果オブジェクト
     */
    public Result doHeader(DataRecord inputData, ExecutionContext ctx) {
        this.isExistRecord = true;
        return new Result.Success();
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

        this.isExistRecord = true;

        // inputDataをFormに展開する
        A135A001DataForm dataForm = BeanUtil.createAndCopy(A135A001DataForm.class, inputData);

        try {
            // バリデーションを実行する
            ValidatorUtil.validate(dataForm);

            // データレコードをOP会員一時情報TBLに登録する
            insertRideMileTempInfo(inputData);

        } catch (ApplicationException e) {
            // 精査エラーが存在する場合
            this.isExistError = true;

            for (Message message : e.getMessages()) {
                // キー情報を設定
                Map<String, Object> keyMap = new LinkedHashMap<String, Object>();
                keyMap.put("会員管理番号", inputData.getString("memCtrlNum"));
                keyMap.put("会員管理番号枝番", inputData.getString("memCtrlNumBrNum"));
                keyMap.put("対象年月", inputData.getString("objectYearMonth"));
                keyMap.put("マイル種別コード", inputData.getString("mileCategoryCode"));

                // バリデーションエラーの内容をロギングする
                writeLog("M000000017", message, keyMap, inputData.getRecordNumber());

                // 行番号
                StringBuffer line = new StringBuffer("line:");
                line.append(inputData.getRecordNumber());
                // 会員管理番号
                StringBuffer memberControlNumber = new StringBuffer("memberControlNumber:");
                memberControlNumber.append(inputData.getString("memCtrlNum"));
                // 会員管理番号枝番
                StringBuffer memrCtrlBrNum = new StringBuffer("memrCtrlBrNum:");
                memrCtrlBrNum.append(inputData.getString("memCtrlNumBrNum"));
                // 対象年月
                StringBuffer objectYearMonth = new StringBuffer("objectYearMonth:");
                objectYearMonth.append(inputData.getString("objectYearMonth"));
                // マイル種別コード
                StringBuffer mileCategoryCode = new StringBuffer("mileCategoryCode:");
                mileCategoryCode.append(inputData.getString("mileCategoryCode"));
                // メッセージ
                StringBuffer errorMessage = new StringBuffer("message:");
                errorMessage.append(message.formatMessage());

                // エラーファイル出力処理。
                writeRecord(line.toString(), memberControlNumber.toString(), memrCtrlBrNum.toString(),
                        objectYearMonth.toString(), mileCategoryCode.toString(), errorMessage.toString());
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
     * @param objectYearMonth
     *            対象年月
     * @param mileCategoryCode
     *            マイル種別コード
     * @param message
     *            メッセージ
     */
    private void writeRecord(String line, String memberControlNumber, String memrCtrlBrNum, String objectYearMonth,
            String mileCategoryCode, String message) {
        Map<String, Object> errorMap = new LinkedHashMap<String, Object>();
        errorMap.put("line", line);
        errorMap.put("memberControlNumber", memberControlNumber);
        errorMap.put("memrCtrlBrNum", memrCtrlBrNum);
        errorMap.put("objectYearMonth", objectYearMonth);
        errorMap.put("mileCategoryCode", mileCategoryCode);
        errorMap.put("message", message);
        FileRecordWriterHolder.write(errorMap, "error", batchErrorFileName.toString());
    }

    /**
     * トレーラレコードの処理。
     *
     * @param inputData
     *            一行分のデータ
     * @param ctx
     *            実行コンテキスト
     * @return 結果オブジェクト
     */
    public Result doTrailer(DataRecord inputData, ExecutionContext ctx) {
        this.isExistRecord = true;
        return new Result.Success();
    }

    /**
     * エンドレコードの処理。
     *
     * @param inputData
     *            入力データ
     * @param ctx
     *            実行コンテキスト
     * @return 結果オブジェクト
     */
    public Result doEnd(DataRecord inputData, ExecutionContext ctx) {
        this.isExistRecord = true;
        return new Result.Success();
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

        // ファイルが空ファイルの場合は、エラーを出力する。
        if (result.isSuccess()) {
            if (!this.isExistRecord) {
                writeLog("AB135A0102");
                throw new TransactionAbnormalEnd(101, "AB135A0102");
            }
        }

        // 出力データ件数(乗車マイル取込一時情報)(登録)
        writeLog("MB135A0106", this.insertRideMileTempInfoCount);

        // 乗車マイル情報レコードで精査エラーが存在する場合は、エラーを出力する。
        if (this.isExistError) {
            throw new TransactionAbnormalEnd(100, "AB135A0101");
        }
    }

    /**
     * 乗車マイル取込一時情報登録
     *
     * @param inputData
     *            入力データ
     */
    private void insertRideMileTempInfo(DataRecord inputData) {
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
        inputData.put("deletedFlg", OpalDefaultConstants.DELETED_FLG_0);
        // 論理削除日
        inputData.put("deletedDate", null);

        // データを登録する
        ParameterizedSqlPStatement statement = super.getParameterizedSqlStatement("INSERT_RIDE_MILE_RIN_TEMP_INFO");
        this.insertRideMileTempInfoCount += statement.executeUpdateByMap(inputData);
    }

    /**
     * ファイルの内容をバリデーションするクラス。
     *
     * @author 唐
     * @since 1.0
     */
    public class FileLayoutValidatorAction implements ValidatableFileDataReader.FileValidatorAction {

        /** ヘッダーレコード */
        private static final String HEADER_RECORD = "1";

        /** データレコード */
        private static final String DATA_RECORD = "2";

        /** トレーラレコード */
        private static final String TRAILER_RECORD = "8";

        /** エンドレコード */
        private static final String END_RECORD = "9";

        /** ヘッダーレコード */
        private static final String HEADER_RECORD_MSG = "ヘッダレコード";

        /** データレコード */
        private static final String DATA_RECORD_MSG = "乗車マイル情報レコード";

        /** トレーラレコード */
        private static final String TRAILER_RECORD_MSG = "トレーラレコード";

        /** エンドレコード */
        private static final String END_RECORD_MSG = "エンドレコード";

        /** ファイルID */
        private static final String FILE_ID_MSG = "ファイルID";

        /** データレコード数 */
        private static final String DATA_RECORD_CNT_MSG = "データレコード数";

        /** 前レコード区分 */
        private String preRecordKbn;

        /** ファイルID */
        private String fileId;

        /**
         * コンストラクタ
         *
         * @param fileId
         *            ファイルID
         */
        public FileLayoutValidatorAction(String fileId) {
            this.fileId = fileId;
        }

        /**
         * ヘッダーレコードのバリデーション。
         * <p/>
         * ヘッダーレコードは、1レコード目であること。
         *
         * @param inputData
         *            入力データ
         * @param ctx
         *            実行コンテキスト
         * @return 結果オブジェクト
         */
        public Result doHeader(DataRecord inputData, ExecutionContext ctx) {

            // ヘッダーレコードの全項目精査を実施する
            checkHeader(inputData);
            // レコード区分を設定
            this.preRecordKbn = HEADER_RECORD;

            return new Result.Success();
        }

        /**
         * データレコードのバリデーション。
         * <p/>
         * 前レコードのレコード区分は、ヘッダーレコードまたはデータレコードであること。
         *
         * @param inputData
         *            入力データ
         * @param ctx
         *            実行コンテキスト
         * @return 結果オブジェクト
         */
        public Result doData(DataRecord inputData, ExecutionContext ctx) {

            // 入力データ件数(乗車マイル情報レコード)をカウントアップする。
            B135A011Action.this.inputDataCount++;

            // データレコードの全項目精査を実施する
            checkData(inputData);
            // レコード区分を設定
            this.preRecordKbn = DATA_RECORD;

            return new Result.Success();
        }

        /**
         * トレーラレコードのバリデーション。
         * <p>
         * 前レコードのレコード区分は、ヘッダレコードまたはデータレコードであること。
         *
         * @param inputData
         *            入力データ
         * @param ctx
         *            実行コンテキスト
         * @return 結果オブジェクト
         */
        public Result doTrailer(DataRecord inputData, ExecutionContext ctx) {

            // トレーラレコードの全項目精査を実施する
            checkTrailer(inputData);
            // レコード区分を設定
            this.preRecordKbn = TRAILER_RECORD;

            return new Result.Success();
        }

        /**
         * エンドレコードのバリデーションを行う。
         * <p>
         * 前レコードのレコード区分は、トレーラレコードであること。
         *
         * @param inputData
         *            入力データ
         * @param ctx
         *            実行コンテキスト
         * @return 結果オブジェクト
         */
        public Result doEnd(DataRecord inputData, ExecutionContext ctx) {

            // エンドレコードの全項目精査を実施する
            checkEnd(inputData);
            // レコード区分を設定
            this.preRecordKbn = END_RECORD;

            return new Result.Success();
        }

        /**
         * バリデーション終了時のコールバックメソッド。
         * <p>
         * 最終レコードがエンドレコードであること。
         *
         * @param ctx
         *            実行コンテキスト
         */
        @Override
        public void onFileEnd(ExecutionContext ctx) {

            if (!END_RECORD.equals(this.preRecordKbn)) {
                // 最終レコードがエンドレコードで無い場合
                writeLog("MB135A0104", END_RECORD_MSG);
                throw new TransactionAbnormalEnd(100, "AB135A0101");
            }

            // 入力データ件数(乗車マイル情報レコード)
            writeLog("MB135A0105", B135A011Action.this.inputDataCount);
        }

        /**
         * ヘッダーレコードの項目精査
         *
         * @param inputData
         *            入力データ
         */
        private void checkHeader(DataRecord inputData) {

            if (this.preRecordKbn != null) {
                // 前レコードの値がnull以外の場合は、1レコード目以外のためエラーとする。
                writeLog("MB135A0101");
                throw new TransactionAbnormalEnd(100, "AB135A0101");
            }

            // inputDataをFormに展開する
            A135A001HeaderForm headerForm = BeanUtil.createAndCopy(A135A001HeaderForm.class, inputData);

            // バリデーションを実行する。
            try {
                ValidatorUtil.validate(headerForm);

            } catch (ApplicationException e) {
                for (Message message : e.getMessages()) {

                    // バリデーションエラーの内容をロギングする
                    writeLog("M000000018", message, inputData.getRecordNumber());
                }
                throw new TransactionAbnormalEnd(100, "AB135A0101");
            }

            // ファイルIDをチェック
            if (!this.fileId.equals(inputData.getString("fileId"))) {
                // ファイルIDのその他精査はエラーとする
                writeLog("MB135A0102", HEADER_RECORD_MSG, FILE_ID_MSG);
                throw new TransactionAbnormalEnd(100, "AB135A0101");
            }
        }

        /**
         * データレコードの項目精査
         *
         * @param inputData
         *            入力データ
         */
        private void checkData(DataRecord inputData) {

            if (!HEADER_RECORD.equals(this.preRecordKbn) && !DATA_RECORD.equals(this.preRecordKbn)) {
                // 前レコードがヘッダレコード、データレコードで無い場合
                writeLog("MB135A0103", DATA_RECORD_MSG);
                throw new TransactionAbnormalEnd(100, "AB135A0101");
            }
        }

        /**
         * トレーラレコードの項目精査
         *
         * @param inputData
         *            入力データ
         */
        private void checkTrailer(DataRecord inputData) {

            if (!HEADER_RECORD.equals(this.preRecordKbn) && !DATA_RECORD.equals(this.preRecordKbn)) {
                // 前レコードがヘッダーレコード、データレコードで無い場合
                writeLog("MB135A0103", TRAILER_RECORD_MSG);
                throw new TransactionAbnormalEnd(100, "AB135A0101");
            }

            // データレコード数の必須チェック
            String dataRecordCnt = inputData.getString("dataRecordCnt");
            if (StringUtil.isNullOrEmpty(dataRecordCnt)) {
                Message message = MessageUtil.createMessage(MessageLevel.INFO, "M000000001");
                StringBuffer sb = new StringBuffer();
                sb.append('[').append(DATA_RECORD_CNT_MSG).append(']').append(message.formatMessage());
                writeLog("M000000018", sb.toString(), inputData.getRecordNumber());
                throw new TransactionAbnormalEnd(100, "AB135A0101");
            }

            // レコード件数と一致すること
            if (B135A011Action.this.inputDataCount != Integer.parseInt(dataRecordCnt)) {
                // レコード件数と一致しない場合
                writeLog("MB135A0102", TRAILER_RECORD_MSG, DATA_RECORD_CNT_MSG);
                throw new TransactionAbnormalEnd(100, "AB135A0101");
            }
        }

        /**
         * エンドレコードの項目精査
         *
         * @param inputData
         *            入力データ
         */
        private void checkEnd(DataRecord inputData) {

            if (!TRAILER_RECORD.equals(this.preRecordKbn)) {
                // 前レコードがトレーラレコードで無い場合
                writeLog("MB135A0104", END_RECORD_MSG);
                throw new TransactionAbnormalEnd(100, "AB135A0101");
            }
        }
    }
}
