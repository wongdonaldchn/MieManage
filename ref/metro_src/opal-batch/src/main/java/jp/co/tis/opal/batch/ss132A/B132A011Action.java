package jp.co.tis.opal.batch.ss132A;

import java.util.HashMap;
import java.util.Map;

import nablarch.core.date.SystemTimeUtil;
import nablarch.core.db.statement.ParameterizedSqlPStatement;
import nablarch.core.db.statement.SqlResultSet;
import nablarch.core.db.statement.SqlRow;
import nablarch.core.repository.SystemRepository;
import nablarch.core.util.DateUtil;
import nablarch.core.util.StringUtil;
import nablarch.fw.DataReader;
import nablarch.fw.ExecutionContext;
import nablarch.fw.Result;
import nablarch.fw.Result.Success;
import nablarch.fw.action.BatchAction;
import nablarch.fw.launcher.CommandLine;
import nablarch.fw.reader.DatabaseRecordReader;

import jp.co.tis.opal.common.component.CM010004Component;
import jp.co.tis.opal.common.constants.OpalDefaultConstants;

/**
 * B132A011:マイル集計のアクションクラス。
 *
 * @author 張 成剛
 * @since 1.0
 */
public class B132A011Action extends BatchAction<SqlRow> {

    /** 起動パラメータ.マイル集計年月 */
    private static final String PROCESS_YM = "processYm";

    /** 年月日加算用 */
    private static final int ADD_MONTH_1_MINUS = -1;

    /** バッチ処理ID */
    private static final String BATCH_PROCESS_ID = "B132A011";

    /** マイル集計年月 */
    private String processYm;
    /** 論理削除日(マイル) */
    private String mileDeletedDate;
    /** 処理データ件数 */
    private int currentDataCount;
    /** コミット間隔 */
    private int commitInterval;

    /** 入力データ件数(集計されていないアプリ会員情報) */
    private int mileSummaryCount;
    /** (マイル履歴情報)(取得)件数 */
    private int mileHistoryCount;
    private int tmpMileHistoryCount;
    /** (マイル種別集計情報)(登録)件数 */
    private int insertMileCategorySummaryInfoCount;
    private int tmpInsertMileCategorySummaryInfoCount;
    /** (獲得マイル・合計)件数 */
    private int acquireTotalMailCount;
    private int tmpAcquireTotalMailCount;
    /** (使用マイル・合計)件数 */
    private int useTotalMailCount;
    private int tmpUseTotalMailCount;
    /** (前月末マイル残高)件数 */
    private int lastMonthMileCount;
    private int tmpLastMonthMileCount;
    /** (マイル集計情報)(登録)件数 */
    private int insertMileSummaryInfoCount;
    private int tmpInsertMileSummaryInfoCount;

    /**
     * {@inneritDoc}
     * <p/>
     * 事前処理
     */
    @Override
    protected void initialize(CommandLine command, ExecutionContext context) {

        // 処理ログの初期化
        this.mileSummaryCount = 0;
        this.mileHistoryCount = 0;
        this.tmpMileHistoryCount = 0;
        this.insertMileCategorySummaryInfoCount = 0;
        this.tmpInsertMileCategorySummaryInfoCount = 0;
        this.acquireTotalMailCount = 0;
        this.tmpAcquireTotalMailCount = 0;
        this.useTotalMailCount = 0;
        this.tmpUseTotalMailCount = 0;
        this.lastMonthMileCount = 0;
        this.tmpLastMonthMileCount = 0;
        this.insertMileSummaryInfoCount = 0;
        this.tmpInsertMileSummaryInfoCount = 0;
        this.currentDataCount = 0;

        // マイル集計年月取得
        this.processYm = command.getParamMap().get(PROCESS_YM);
        if (StringUtil.isNullOrEmpty(processYm)) {
            this.processYm = DateUtil.addMonth(SystemTimeUtil.getDateString(), ADD_MONTH_1_MINUS).substring(0, 6);
        }

        // 論理削除日の算出
        CM010004Component cM010004Component = new CM010004Component();
        // データ保持期間：システム設定ファイル(opal.config)から取得する。
        String yearSpan = SystemRepository.getString("mile_control_data_retention_period");
        // 論理削除日(マイル)
        this.mileDeletedDate = cM010004Component.getDeletedDateMileYearly(Integer.valueOf(yearSpan));

        // コミット間隔を取得
        this.commitInterval = Integer.valueOf(SystemRepository.getString("nablarch.loopHandler.commitInterval"));
    }

    /**
     * {@inneritDoc}
     * <p/>
     * マイル集計情報から処理対象のレコードを読み込む。
     */
    @Override
    public DataReader<SqlRow> createReader(ExecutionContext arg0) {

        // アプリ会員情報取得用のSQLの条件を設定する
        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("mileSummaryYearMonth", this.processYm);

        // アプリ会員情報TBL、マイル集計情報TBLから、集計されていないアプリ会員情報を取得する。
        this.mileSummaryCount = countByParameterizedSql("SELECT_APPLICATION_MEM_ID", condition);
        // 入力データ件数をログに出力
        writeLog("MB132A0101", Integer.valueOf(this.mileSummaryCount));

        // アプリ会員情報TBL、マイル集計情報TBLから、処理対象のレコードを読み込む。
        DatabaseRecordReader reader = new DatabaseRecordReader();
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("SELECT_APPLICATION_MEM_ID");
        reader.setStatement(statement, condition);

        return reader;
    }

    /**
     * {@inneritDoc}
     * <p/>
     * 本処理
     */
    @Override
    public Result handle(SqlRow inputData, ExecutionContext ctx) {

        // 処理データ件数をカウントアップする。
        this.currentDataCount++;

        // アプリ会員ID
        Long applicationMemberId = inputData.getLong("APPLICATION_MEMBER_ID");

        // マイル種別集計情報登録
        this.registMileCategorySumInfo(applicationMemberId);

        // マイル集計情報登録
        this.registMileSummaryInfo(applicationMemberId);

        // コミット件数取得
        if (this.currentDataCount == this.mileSummaryCount || this.currentDataCount % this.commitInterval == 0) {
            this.mileHistoryCount = this.tmpMileHistoryCount;
            this.insertMileCategorySummaryInfoCount = this.tmpInsertMileCategorySummaryInfoCount;
            this.acquireTotalMailCount = this.tmpAcquireTotalMailCount;
            this.useTotalMailCount = this.tmpUseTotalMailCount;
            this.lastMonthMileCount = this.tmpLastMonthMileCount;
            this.insertMileSummaryInfoCount = this.tmpInsertMileSummaryInfoCount;
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

        // 取得したデータ件数(マイル履歴情報)をログに出力
        writeLog("MB132A0102", Integer.valueOf(mileHistoryCount));

        // 出力データ件数(マイル種別集計情報)(登録)をログに出力
        writeLog("MB132A0103", Integer.valueOf(insertMileCategorySummaryInfoCount));

        // 取得したデータ件数(獲得マイル・合計)件数をログに出力する。
        writeLog("MB132A0104", Integer.valueOf(acquireTotalMailCount));

        // 取得したデータ件数(使用マイル・合計)件数をログに出力する。
        writeLog("MB132A0104", Integer.valueOf(useTotalMailCount));

        // 取得したデータ件数(前月末マイル残高)件数をログに出力する。
        writeLog("MB132A0105", Integer.valueOf(lastMonthMileCount));

        // 出力データ件数(マイル集計情報)(登録)件数をログに出力する。
        writeLog("MB132A0106", Integer.valueOf(insertMileSummaryInfoCount));
    }

    /**
     * {@inneritDoc} マイル種別集計情報登録
     * <p/>
     *
     * @param applicationMemberId
     *            アプリ会員ID
     */
    private void registMileCategorySumInfo(Long applicationMemberId) {

        StringBuilder processYmCondition = new StringBuilder();
        processYmCondition.append(this.processYm).append("%");

        // マイル種別集計情報取得用SQL条件を設定する。
        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("applicationMemberId", applicationMemberId);
        condition.put("mileSummaryYearMonth", processYmCondition.toString());
        ParameterizedSqlPStatement selectStatement = getParameterizedSqlStatement("SELECT_CATEGORY_TOTAL_MILE");

        // マイル履歴情報取得
        SqlResultSet mileHistoryDatas = selectStatement.retrieve(condition);

        // 取得したデータ(マイル履歴情報)件数をカウントアップする。
        this.tmpMileHistoryCount += mileHistoryDatas.size();

        for (SqlRow row : mileHistoryDatas) {
            // マイル種別集計情報登録用のSQL条件を設定する。
            Map<String, Object> inputData = new HashMap<String, Object>();
            // アプリ会員ID
            inputData.put("applicationMemberId", applicationMemberId);
            // マイル集計年月
            inputData.put("mileSumYearMonth", this.processYm);
            // マイル種別コード
            inputData.put("mileCategoryCode", row.getString("MILE_CATEGORY_CODE"));
            // マイル合計
            inputData.put("mileTotal", row.getLong("MILE_TOTAL"));
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
            inputData.put("deletedDate", this.mileDeletedDate);

            // マイル種別集計情報登録
            ParameterizedSqlPStatement insertStatement = super.getParameterizedSqlStatement(
                    "INSERT_MILE_CATEGORY_SUMMARY_INFO");
            int inputRowCount = insertStatement.executeUpdateByMap(inputData);

            // 出力データ(マイル種別集計情報)(登録)件数をカウントアップする。
            this.tmpInsertMileCategorySummaryInfoCount += inputRowCount;
        }
    }

    /**
     * {@inneritDoc} 獲得マイル・合計取得
     * <p/>
     *
     * @param applicationMemberId
     *            アプリ会員ID
     * @return 獲得マイル・合計
     */
    private Long getAcquireMileTotal(Long applicationMemberId) {

        // 獲得マイル・合計取得用SQL条件を設定する。
        Map<String, Object> condition = new HashMap<String, Object>();
        // アプリ会員ID
        condition.put("applicationMemberId", applicationMemberId);
        // マイル集計年月
        condition.put("mileSummaryYearMonth", this.processYm);
        // マイル種別コード
        condition.put("mileCategoryCode", "A%");

        ParameterizedSqlPStatement selectGetTotleStatement = getParameterizedSqlStatement("SELECT_GET_TOTAL_MILE");
        // 獲得マイル・合計取得
        SqlResultSet acquireMileTotalDatas = selectGetTotleStatement.retrieve(condition);
        Long acquireMileTotal = acquireMileTotalDatas.get(0).getLong("ACQUIRE_MILE_TOTAL");

        return acquireMileTotal;
    }

    /**
     * {@inneritDoc} 前月末マイル残高取得
     * <p/>
     *
     * @param applicationMemberId
     *            アプリ会員ID
     * @return 前月末マイル残高
     */
    private SqlResultSet getLastMonthEndMileBalance(Long applicationMemberId) {

        // 前月末マイル残高取得用SQL条件を設定する。
        Map<String, Object> condition = new HashMap<String, Object>();
        // アプリ会員ID
        condition.put("applicationMemberId", applicationMemberId);
        // マイル集計年月
        condition.put("lastMileSummaryYearMonth", DateUtil.addMonth(this.processYm, ADD_MONTH_1_MINUS));

        ParameterizedSqlPStatement selectMonthTotleStatement = getParameterizedSqlStatement(
                "SELECT_LAST_MONTH_TOTAL_MILE");

        // 前月末マイル残高取得
        SqlResultSet lastMonthTotalMileDatas = selectMonthTotleStatement.retrieve(condition);

        return lastMonthTotalMileDatas;
    }

    /**
     * {@inneritDoc} 使用マイル・合計取得
     * <p/>
     *
     * @param applicationMemberId
     *            アプリ会員ID
     * @return 使用マイル・合計
     */
    private Long getUseMileTotal(Long applicationMemberId) {

        // 使用マイル・合計取得用SQL条件を設定する。
        Map<String, Object> condition = new HashMap<String, Object>();
        // アプリ会員ID
        condition.put("applicationMemberId", applicationMemberId);
        // マイル集計年月
        condition.put("mileSummaryYearMonth", this.processYm);
        // マイル種別コード
        condition.put("mileCategoryCode", "S%");

        ParameterizedSqlPStatement selectUseTotleStatement = getParameterizedSqlStatement("SELECT_USE_TOTAL_MILE");
        // 使用マイル・合計取得
        SqlResultSet useMileTotalDatas = selectUseTotleStatement.retrieve(condition);
        Long useMileTotal = useMileTotalDatas.get(0).getLong("ACQUIRE_MILE_TOTAL");

        return useMileTotal;
    }

    /**
     * {@inneritDoc} マイル集計情報登録
     * <p/>
     *
     * @param applicationMemberId
     *            アプリ会員ID
     */
    private void registMileSummaryInfo(Long applicationMemberId) {

        // 獲得マイル・合計取得
        Long acquireMileTotal = this.getAcquireMileTotal(applicationMemberId);
        if (acquireMileTotal != null) {
            // 取得したデータ(獲得マイル・合計)件数をカウントアップする。
            this.tmpAcquireTotalMailCount++;
        } else {
            acquireMileTotal = 0L;
        }

        // 使用マイル・合計取得
        Long useMileTotal = this.getUseMileTotal(applicationMemberId);
        if (useMileTotal != null) {
            // 取得したデータ(使用マイル・合計)件数をカウントアップする。
            this.tmpUseTotalMailCount++;
        } else {
            useMileTotal = 0L;
        }

        // 取得したデータ( 前月末マイル残高)
        SqlResultSet getLastMonthEndMileBalance = this.getLastMonthEndMileBalance(applicationMemberId);
        // 前月末マイル残高取得
        Long lastMonthEndMileBalance = 0L;
        if (!getLastMonthEndMileBalance.isEmpty()) {
            lastMonthEndMileBalance = getLastMonthEndMileBalance.get(0).getLong("THIS_MONTH_END_MILE_BALANCE");

            // 取得したデータ( 前月末マイル残高)件数をカウントアップする。
            this.tmpLastMonthMileCount++;
        }

        // 当月末マイル残高算出（当月末マイル残高 = 前月末マイル残高 + 獲得マイル・合計 - 使用マイル・合計）
        Long thisMonthEndMileBalance = lastMonthEndMileBalance + acquireMileTotal - useMileTotal;

        // マイル集計情報登録用のSQL条件を設定する。
        Map<String, Object> inputData = new HashMap<String, Object>();
        // アプリ会員ID
        inputData.put("applicationMemberId", applicationMemberId);
        // マイル集計年月
        inputData.put("mileSumYearMonth", this.processYm);
        // 獲得マイル・合計
        inputData.put("acquireMileTotal", acquireMileTotal);
        // 使用マイル・合計
        inputData.put("useMileTotal", useMileTotal);
        // 前月末マイル残高
        inputData.put("lastMonthEndMileBalance", lastMonthEndMileBalance);
        // 当月末マイル残高
        inputData.put("thisMonthEndMileBalance", thisMonthEndMileBalance);
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
        inputData.put("deletedDate", this.mileDeletedDate);

        // マイル集計情報登録
        ParameterizedSqlPStatement insertStatement = super.getParameterizedSqlStatement("INSERT_MILE_SUMMARY_INFO");
        int inputRowCount = insertStatement.executeUpdateByMap(inputData);

        // 出力データ(マイル集計情報)(登録)件数をカウントアップする。
        this.tmpInsertMileSummaryInfoCount += inputRowCount;
    }

}
