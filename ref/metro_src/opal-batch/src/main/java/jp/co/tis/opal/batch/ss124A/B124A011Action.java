package jp.co.tis.opal.batch.ss124A;

import java.util.HashMap;
import java.util.Map;

import nablarch.common.io.FileRecordWriterHolder;
import nablarch.core.date.SystemTimeUtil;
import nablarch.core.db.statement.ParameterizedSqlPStatement;
import nablarch.core.db.statement.SqlResultSet;
import nablarch.core.db.statement.SqlRow;
import nablarch.core.util.DateUtil;
import nablarch.core.util.StringUtil;
import nablarch.fw.DataReader;
import nablarch.fw.ExecutionContext;
import nablarch.fw.Result;
import nablarch.fw.Result.Success;
import nablarch.fw.action.BatchAction;
import nablarch.fw.launcher.CommandLine;
import nablarch.fw.reader.DatabaseRecordReader;

import jp.co.tis.opal.common.constants.OpalCodeConstants;
import jp.co.tis.opal.common.constants.OpalDefaultConstants;

/**
 * B124A01:パートナー乗車適用日情報作成のアクションクラス。
 *
 * @author 曹
 * @since 1.0
 */
public class B124A011Action extends BatchAction<SqlRow> {

    /** 入力データ件数(パートナー乗車適用日情報) */
    private int inputDataCount;

    /** 出力データ件数(パートナー乗車適用日情報ファイル) */
    private int outputDataCount;

    /** 取得対象年月 */
    private String rideApplyYearMonth;

    /** パートナー会員サービス管理ID */
    private String partnerMemServiceCtrlId;

    /** ユーザ選択区分 */
    private String userChooseDivision;

    /** パートナー登録者会員管理番号 */
    private String partnerRegistMemCtrlNum;

    /** パートナー登録者会員管理番号枝番 */
    private String partnerRegistMemCtrlBrNum;

    /** パートナー会員管理番号 */
    private String partnerMemCtrlNum;

    /** パートナー会員管理番号枝番 */
    private String partnerMemCtrlBrNum;

    /** 乗車適用日情報 */
    private StringBuilder rideApplyDateinfo;

    /** 乗車適用日件数 */
    private int rideApplyDateCnt;

    /** 処理データ件数 */
    private int currentDataCount;

    /** 乗車適用日登録上限回数 */
    private int upperLimitTimes;

    /** 起動パラメータ: 取得対象年月 */
    private static final String RIDE_APPLY_YEAR_MONTH = "rideApplyYearMonth";

    /** 出力ファイルID：A124A001(パートナー乗車適用日情報ファイル) */
    private static final String FILE_ID = "A124A001";

    /** フォーマット定義ファイルID：A124A001 */
    private static final String FORMAT_ID = "A124A001";

    /**
     * {@inneritDoc}
     * <p/>
     * 事前処理
     */
    @Override
    protected void initialize(CommandLine command, ExecutionContext context) {

        // 定数初期化
        inputDataCount = 0;
        outputDataCount = 0;
        partnerMemServiceCtrlId = null;
        userChooseDivision = null;
        rideApplyDateinfo = new StringBuilder();
        rideApplyDateCnt = 0;
        currentDataCount = 0;

        // 取得対象年月
        rideApplyYearMonth = command.getParamMap().get(RIDE_APPLY_YEAR_MONTH);
        if (StringUtil.isNullOrEmpty(rideApplyYearMonth)) {
            // 起動パラメータが設定しない場合、システム日付の前月を設定。
            rideApplyYearMonth = DateUtil
                    .addMonth(SystemTimeUtil.getDateString(), OpalDefaultConstants.ADD_MONTH_1_MINUS)
                    .substring(OpalDefaultConstants.POSITION_YEAR_MONTH_START,
                            OpalDefaultConstants.POSITION_YEAR_MONTH_END);
        }

        // 出力先のファイルをオープンする。
        FileRecordWriterHolder.open(FILE_ID, FORMAT_ID);

        // ヘッダレコードを出力する。
        writeHeaderRecord();
    }

    /**
     * {@inneritDoc}
     * <p/>
     * パートナー乗車適用日情報取得
     */
    @Override
    public DataReader<SqlRow> createReader(ExecutionContext ctx) {

        // 適用日時を算出
        StringBuilder stringForApplyDateTime = new StringBuilder();
        stringForApplyDateTime.append(rideApplyYearMonth);
        stringForApplyDateTime.append(OpalDefaultConstants.APPLY_DATE_TIME_START_DATE);
        String applyDateTime = DateUtil.addMonth(stringForApplyDateTime.toString(), OpalDefaultConstants.ADD_MONTH_1);

        // パートナー乗車適用日情報取得用のSQL条件を設定
        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("rideApplyYearMonth", rideApplyYearMonth);
        condition.put("applyDateTime", applyDateTime);
        condition.put("admitStatusDivision", OpalCodeConstants.AdmitStatusDivision.ADMIT_STATUS_DIVISION_1);

        // 入力データ件数取得
        inputDataCount = countByParameterizedSql("SELECT_PARTNER_RIDE_APPLY_DATE", condition);
        // 入力データ件数(パートナー乗車適用日情報)をログに出力
        writeLog("MB124A0101", Integer.valueOf(inputDataCount));

        if (inputDataCount > 0) {
            // 乗車適用日登録上限回数取得
            getUpperLimitTimes();
        }

        // パートナー乗車適用日情報取得
        DatabaseRecordReader reader = new DatabaseRecordReader();
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("SELECT_PARTNER_RIDE_APPLY_DATE");
        reader.setStatement(statement, condition);

        return reader;
    }

    /**
     * {@inneritDoc}
     * <p/>
     * パートナー乗車適用日情報ファイル出力
     */
    @Override
    public Result handle(SqlRow inputData, ExecutionContext ctx) {

        // パートナー会員サービス管理ID
        String partnerMemId = inputData.getString("PARTNER_MEM_SERVICE_CTRL_ID");
        // ユーザ選択区分
        String userChooseKbn = inputData.getString("USER_CHOOSE_DIVISION");
        // パートナー登録者会員管理番号
        String partnerRegistCtrlNum = inputData.getString("PARTNER_REGIST_CTRL_NUM");
        // パートナー登録者会員管理番号枝番
        String partnerRegistCtrlBrNum = inputData.getString("PARTNER_REGIST_CTRL_BR_NUM");
        // パートナー会員管理番号
        String partnerUserCtrlNum = inputData.getString("PARTNER_USER_MEM_CTRL_NUM");
        // パートナー会員管理番号枝番
        String partnerUserCtrlBrNum = inputData.getString("PARTNER_USER_MEM_CTRL_BR_NUM");
        // 乗車適用日
        String rideApplyDay = inputData.getString("RIDE_APPLY_DATE").substring(OpalDefaultConstants.POSITION_DATE_START,
                OpalDefaultConstants.POSITION_DATE_END);

        // 処理データ件数が一番目じゃないかつ、会員管理番号が変わる場合、ファイルに出力する
        if (!StringUtil.isNullOrEmpty(partnerMemServiceCtrlId) && !partnerMemServiceCtrlId.equals(partnerMemId)) {
            if (rideApplyDateCnt > 0) {
                // 取得対象年月
                inputData.put("getObjectYearMonth", rideApplyYearMonth);
                // パートナー登録者会員管理番号
                inputData.put("partnerRegistUserCtrlNum", partnerRegistMemCtrlNum);
                // パートナー登録者会員管理番号枝番
                inputData.put("partnerRegistUserCtrlBrNum", partnerRegistMemCtrlBrNum);
                // パートナー会員管理番号
                inputData.put("partnerMemCtrlNum", partnerMemCtrlNum);
                // パートナー会員管理番号枝番
                inputData.put("partnerMemCtrlBrNum", partnerMemCtrlBrNum);
                // 乗車適用日件数
                inputData.put("rideApplyDateCnt", StringUtil.lpad(String.valueOf(rideApplyDateCnt),
                        OpalDefaultConstants.RIDE_APPLY_DATE_CNT_LENGTH, OpalDefaultConstants.CHAR_FOR_PADDING));
                // 乗車適用日情報
                String rideApplyDate = rideApplyDateinfo.toString();
                inputData.put("rideApplyDateInfo", StringUtil.rpad(rideApplyDate,
                        OpalDefaultConstants.RIDE_APPLY_DATE_INFO_LENGTH, OpalDefaultConstants.CHAR_FOR_PADDING));

                // データレコード出力
                writeRecord("data", inputData);

                // 出力データ件数(パートナー乗車適用日情報ファイル)をカウントアップする。
                outputDataCount++;
            }
            // 乗車適用日件数
            rideApplyDateCnt = 0;
            rideApplyDateinfo = new StringBuilder();
            if (OpalCodeConstants.UserChooseDivision.USER_CHOOSE_DIVISION_1.equals(userChooseKbn)
                    || (OpalCodeConstants.UserChooseDivision.USER_CHOOSE_DIVISION_0.equals(userChooseKbn)
                            && rideApplyDateCnt <= upperLimitTimes - 1)) {
                // 乗車適用日件数をカウントアップする。
                rideApplyDateCnt++;
                if (currentDataCount == inputDataCount - 1) {
                    // 取得対象年月
                    inputData.put("getObjectYearMonth", rideApplyYearMonth);
                    // パートナー登録者会員管理番号
                    inputData.put("partnerRegistUserCtrlNum", partnerRegistCtrlNum);
                    // パートナー登録者会員管理番号枝番
                    inputData.put("partnerRegistUserCtrlBrNum", partnerRegistCtrlBrNum);
                    // パートナー会員管理番号
                    inputData.put("partnerMemCtrlNum", partnerUserCtrlNum);
                    // パートナー会員管理番号枝番
                    inputData.put("partnerMemCtrlBrNum", partnerUserCtrlBrNum);
                    // 乗車適用日件数
                    inputData.put("rideApplyDateCnt", StringUtil.lpad(String.valueOf(rideApplyDateCnt),
                            OpalDefaultConstants.RIDE_APPLY_DATE_CNT_LENGTH, OpalDefaultConstants.CHAR_FOR_PADDING));
                    // 乗車適用日情報
                    inputData.put("rideApplyDateInfo", StringUtil.rpad(rideApplyDay,
                            OpalDefaultConstants.RIDE_APPLY_DATE_INFO_LENGTH, OpalDefaultConstants.CHAR_FOR_PADDING));

                    // データレコード出力
                    writeRecord("data", inputData);

                    // 出力データ件数(パートナー乗車適用日情報ファイル)をカウントアップする。
                    outputDataCount++;
                } else {
                    // パートナー会員サービス管理ID
                    partnerMemServiceCtrlId = partnerMemId;
                    // ユーザ選択区分
                    userChooseDivision = userChooseKbn;
                    // パートナー登録者会員管理番号
                    partnerRegistMemCtrlNum = partnerRegistCtrlNum;
                    // パートナー登録者会員管理番号枝番
                    partnerRegistMemCtrlBrNum = partnerRegistCtrlBrNum;
                    // パートナー会員管理番号
                    partnerMemCtrlNum = partnerUserCtrlNum;
                    // パートナー会員管理番号枝番
                    partnerMemCtrlBrNum = partnerUserCtrlBrNum;
                    // 乗車適用日情報
                    rideApplyDateinfo.append(rideApplyDay);
                }
                // 処理データ件数をカウントアップする。
                currentDataCount++;
            }
        } else if (currentDataCount == inputDataCount - 1) {
            // 処理データ件数が最後の件の場合、ファイルに出力する
            if (OpalCodeConstants.UserChooseDivision.USER_CHOOSE_DIVISION_1.equals(userChooseKbn)
                    || (OpalCodeConstants.UserChooseDivision.USER_CHOOSE_DIVISION_0.equals(userChooseKbn)
                            && rideApplyDateCnt <= upperLimitTimes - 1)) {
                // ユーザ選択区分がブランクまたは変わらない場合、乗車適用日を乗車適用日情報に出力
                if (StringUtil.isNullOrEmpty(userChooseDivision) || userChooseKbn.equals(userChooseDivision)) {
                    // 乗車適用日情報
                    rideApplyDateinfo.append(rideApplyDay);
                    // 乗車適用日件数をカウントアップする。
                    rideApplyDateCnt++;
                }
            }
            if (rideApplyDateCnt > 0) {
                // 取得対象年月
                inputData.put("getObjectYearMonth", rideApplyYearMonth);
                // パートナー登録者会員管理番号
                inputData.put("partnerRegistUserCtrlNum", partnerRegistCtrlNum);
                // パートナー登録者会員管理番号枝番
                inputData.put("partnerRegistUserCtrlBrNum", partnerRegistCtrlBrNum);
                // パートナー会員管理番号
                inputData.put("partnerMemCtrlNum", partnerUserCtrlNum);
                // パートナー会員管理番号枝番
                inputData.put("partnerMemCtrlBrNum", partnerUserCtrlBrNum);
                // 乗車適用日件数
                inputData.put("rideApplyDateCnt", StringUtil.lpad(String.valueOf(rideApplyDateCnt),
                        OpalDefaultConstants.RIDE_APPLY_DATE_CNT_LENGTH, OpalDefaultConstants.CHAR_FOR_PADDING));
                // 乗車適用日情報
                String rideApplyDate = rideApplyDateinfo.toString();
                inputData.put("rideApplyDateInfo", StringUtil.rpad(rideApplyDate,
                        OpalDefaultConstants.RIDE_APPLY_DATE_INFO_LENGTH, OpalDefaultConstants.CHAR_FOR_PADDING));

                // データレコード出力
                writeRecord("data", inputData);

                // 出力データ件数(パートナー乗車適用日情報ファイル)をカウントアップする。
                outputDataCount++;
            }
        } else {
            if (OpalCodeConstants.UserChooseDivision.USER_CHOOSE_DIVISION_1.equals(userChooseKbn)
                    || (OpalCodeConstants.UserChooseDivision.USER_CHOOSE_DIVISION_0.equals(userChooseKbn)
                            && rideApplyDateCnt <= upperLimitTimes - 1)) {
                // ユーザ選択区分がブランクまたは変わらない場合、乗車適用日を乗車適用日情報に出力
                if (StringUtil.isNullOrEmpty(userChooseDivision)) {
                    // 乗車適用日情報
                    rideApplyDateinfo.append(rideApplyDay);
                    // 乗車適用日件数をカウントアップする。
                    rideApplyDateCnt++;
                    // パートナー会員サービス管理ID
                    partnerMemServiceCtrlId = partnerMemId;
                    // ユーザ選択区分
                    userChooseDivision = userChooseKbn;
                    // パートナー登録者会員管理番号
                    partnerRegistMemCtrlNum = partnerRegistCtrlNum;
                    // パートナー登録者会員管理番号枝番
                    partnerRegistMemCtrlBrNum = partnerRegistCtrlBrNum;
                    // パートナー会員管理番号
                    partnerMemCtrlNum = partnerUserCtrlNum;
                    // パートナー会員管理番号枝番
                    partnerMemCtrlBrNum = partnerUserCtrlBrNum;
                } else if (userChooseKbn.equals(userChooseDivision)) {
                    // 乗車適用日情報
                    rideApplyDateinfo.append(rideApplyDay);
                    // 乗車適用日件数をカウントアップする。
                    rideApplyDateCnt++;
                }
            }
            // 処理データ件数をカウントアップする。
            currentDataCount++;
        }
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
        }
        FileRecordWriterHolder.close(FILE_ID);

        // 出力データ件数(パートナー乗車適用日情報ファイル)をログに出力する。
        writeLog("MB124A0102", Integer.valueOf(outputDataCount));
    }

    /**
     * 乗車適用日登録上限回数取得
     *
     */
    private void getUpperLimitTimes() {

        // 乗車適用日登録上限回数取得用のSQL条件を設定
        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("rideApplyYearMonth", rideApplyYearMonth);
        condition.put("serviceDivision", OpalCodeConstants.ServiceDivision.SERVICE_DIVISION_2);

        // 乗車適用日登録上限回数取得
        ParameterizedSqlPStatement sqlForUpperLimitTimes = getParameterizedSqlStatement(
                "SELECT_RIDE_UPPER_LIMIT_TIMES");
        SqlResultSet result = sqlForUpperLimitTimes.retrieve(condition);
        if (!result.isEmpty()) {
            upperLimitTimes = result.get(0).getInteger("UPPER_LIMIT_TIMES").intValue();
        }
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
        trailer.put("dataRecordCnt", StringUtil.lpad(String.valueOf(outputDataCount),
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
