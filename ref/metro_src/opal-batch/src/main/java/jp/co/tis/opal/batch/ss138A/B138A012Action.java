package jp.co.tis.opal.batch.ss138A;

import java.util.HashMap;
import java.util.Map;

import nablarch.core.date.SystemTimeUtil;
import nablarch.core.db.statement.ParameterizedSqlPStatement;
import nablarch.core.db.statement.SqlPStatement;
import nablarch.core.db.statement.SqlRow;
import nablarch.core.repository.SystemRepository;
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
 * B138A012:PiTaPa利用実績情報登録
 *
 * @author 唐
 * @since 1.0
 */
public class B138A012Action extends BatchAction<SqlRow> {

    /** 入力データ件数(PiTaPa利用実績一時情報) */
    private int inputPitapaUseResTempInfoCount;

    /** 出力データ件数(PiTaPa利用実績情報)(登録) */
    private int insertPitapaUseResInfoCount;

    /** 出力データ件数(PiTaPa利用実績一時情報)(更新) */
    private int updatePitapaUseResTempInfoCount;

    /** 出力データ件数(PiTaPa利用実績情報)(登録) */
    private int tempInsertPitapaUseResInfoCount;

    /** 出力データ件数(PiTaPa利用実績一時情報)(更新) */
    private int tempUpdatePitapaUseResTempInfoCount;

    /** バッチ処理ID */
    private static final String BATCH_PROCESS_ID = "B138A012";

    /** 処理データ件数 */
    private int currentDataCount;

    /** コミット間隔 */
    private int commitInterval;

    /** 論理削除日(PiTaPa利用実績情報) */
    private String pitapaDeletedDate;

    /**
     * {@inneritDoc}
     * <p/>
     * 事前処理
     */
    @Override
    protected void initialize(CommandLine command, ExecutionContext context) {

        // 処理ログの初期化
        inputPitapaUseResTempInfoCount = 0;
        insertPitapaUseResInfoCount = 0;
        updatePitapaUseResTempInfoCount = 0;
        tempInsertPitapaUseResInfoCount = 0;
        tempUpdatePitapaUseResTempInfoCount = 0;

        // 論理削除日の算出
        CM010004Component cM010004Component = new CM010004Component();
        // 論理削除日(PiTaPa利用実績情報)
        String monthSpan = SystemRepository.getString("pitapa_use_results_data_retention_period");
        pitapaDeletedDate = cM010004Component.getDeletedDateMonthly(Integer.parseInt(monthSpan));

        // コミット間隔を取得
        commitInterval = Integer.parseInt(SystemRepository.getString("nablarch.loopHandler.commitInterval"));
    }

    /**
     * {@inneritDoc}
     * <p/>
     * PiTaPa利用実績一時情報TBLから処理対象のレコードを取得。
     */
    @Override
    public DataReader<SqlRow> createReader(ExecutionContext ctx) {

        // 処理対象レコード件数を取得
        inputPitapaUseResTempInfoCount = countByStatementSql("SELECT_PITAPA_USE_RES_TEMP_INFO");
        // 入力データ件数をログに出力
        writeLog("MB138A0107", Integer.valueOf(inputPitapaUseResTempInfoCount));

        // PiTaPa利用実績一時情報TBLから処理対象のレコードを読み込む。
        DatabaseRecordReader reader = new DatabaseRecordReader();
        SqlPStatement statement = getSqlPStatement("SELECT_PITAPA_USE_RES_TEMP_INFO");
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

        // 会員管理番号
        String memberControlNumber = inputData.getString("MEMBER_CONTROL_NUMBER");
        // 会員管理番号枝番
        String memCtrlNumBrNum = inputData.getString("MEM_CTRL_NUM_BR_NUM");
        // PiTaPaご利用年月
        String pitapaUseYearMonth = inputData.getString("PITAPA_USE_YEAR_MONTH");

        // PiTaPa利用実績情報登録
        insertPitapaUseResInfo(inputData);

        // PiTaPa利用実績一時情報更新
        updatePitapaUseResTempInfo(memberControlNumber, memCtrlNumBrNum, pitapaUseYearMonth);

        currentDataCount++;
        // コミット件数取得
        if (currentDataCount == inputPitapaUseResTempInfoCount || currentDataCount % commitInterval == 0) {
            insertPitapaUseResInfoCount = tempInsertPitapaUseResInfoCount;
            updatePitapaUseResTempInfoCount = tempUpdatePitapaUseResTempInfoCount;
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
        // 出力データ件数(PiTaPa利用実績情報)(登録)をログに出力
        writeLog("MB138A0108", Integer.valueOf(insertPitapaUseResInfoCount));
        // 出力データ件数(PiTaPa利用実績一時情報)(更新)をログに出力
        writeLog("MB138A0109", Integer.valueOf(updatePitapaUseResTempInfoCount));
    }

    /**
     * PiTaPa利用実績情報登録
     * <p/>
     *
     * @param inputData
     *            PiTaPa利用実績一時情報
     *
     */
    private void insertPitapaUseResInfo(SqlRow inputData) {

        // PiTaPa利用実績情報登録用のSQL条件を設定する。
        Map<String, Object> condition = new HashMap<String, Object>();
        // 会員管理番号
        condition.put("memControlNum", inputData.getString("MEMBER_CONTROL_NUMBER"));
        // 会員管理番号枝番
        condition.put("memControlBrNum", inputData.getString("MEM_CTRL_NUM_BR_NUM"));
        // PiTaPaご利用年月
        condition.put("pitapaUseYearMonth", inputData.getString("PITAPA_USE_YEAR_MONTH"));
        // プランコード
        condition.put("planCode", inputData.getString("PLAN_CODE"));
        // 会員単位支払合計
        condition.put("memberUnitPayTotal", inputData.getString("MEMBER_UNIT_PAY_TOTAL"));
        // 会員単位支払合計の合計
        condition.put("memberUnitPayTotalTotal", inputData.getString("MEMBER_UNIT_PAY_TOTAL_TOTAL"));
        // 明細書発送手数料
        condition.put("detailBookPostCharge", inputData.getString("DETAIL_BOOK_POST_CHARGE"));
        // ショップdeポイント割引
        condition.put("shopDePointDiscount", inputData.getString("SHOP_DE_POINT_DISCOUNT"));
        // 口座単位支払合計
        condition.put("accountUnitPayTotal", inputData.getString("ACCOUNT_UNIT_PAY_TOTAL"));
        // 登録駅ご利用 適用金額
        condition.put("registStaUseApplyMoney", inputData.getString("REGIST_STA_USE_APPLY_MONEY"));
        // 登録駅ご利用 割引後金額
        condition.put("registStaUseDisMoney", inputData.getString("REGIST_STA_USE_DIS_MONEY"));
        // 登録駅外ご利用 適用金額
        condition.put("notRegistStaUseApplyMoney", inputData.getString("NOT_REGIST_STA_USE_APPLY_MONEY"));
        // 登録駅外ご利用 割引後金額
        condition.put("notRegistStaUseDisMoney", inputData.getString("NOT_REGIST_STA_USE_DIS_MONEY"));
        // 非登録型ご利用 適用金額
        condition.put("notRegistUseApplyMoney", inputData.getString("NOT_REGIST_USE_APPLY_MONEY"));
        // 非登録型ご利用 割引後金額
        condition.put("notRegistUseDisMoney", inputData.getString("NOT_REGIST_USE_DIS_MONEY"));
        // その他鉄道バスご利用
        condition.put("otherRailwayBusUse", inputData.getString("OTHER_RAILWAY_BUS_USE"));
        // PiTaPaショッピング
        condition.put("pitapaShopping", inputData.getString("PITAPA_SHOPPING"));
        // 登録者ID
        condition.put("insertUserId", BATCH_PROCESS_ID);
        // 登録日時
        condition.put("insertDateTime", SystemTimeUtil.getTimestamp());
        // 最終更新者ID
        condition.put("updateUserId", BATCH_PROCESS_ID);
        // 最終更新日時
        condition.put("updateDateTime", SystemTimeUtil.getTimestamp());
        // 削除フラグ
        condition.put("deletedFlg", OpalDefaultConstants.DELETED_FLG_0);
        // 論理削除日
        condition.put("deletedDate", pitapaDeletedDate);

        // PiTaPa利用実績情報を登録する。
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("INSERT_PITAPA_USE_RES_INFO");
        tempInsertPitapaUseResInfoCount += statement.executeUpdateByMap(condition);
    }

    /**
     * PiTaPa利用実績一時情報更新
     * <p/>
     *
     * @param memberControlNumber
     *            会員管理番号
     * @param memCtrlNumBrNum
     *            会員管理番号枝番
     * @param pitapaUseYearMonth
     *            PiTaPaご利用年月
     */
    private void updatePitapaUseResTempInfo(String memberControlNumber, String memCtrlNumBrNum,
            String pitapaUseYearMonth) {

        // PiTaPa利用実績一時情報更新用のSQL条件を設定する。
        Map<String, Object> condition = new HashMap<String, Object>();
        // 会員管理番号
        condition.put("memControlNum", memberControlNumber);
        // 会員管理番号枝番
        condition.put("memControlBrNum", memCtrlNumBrNum);
        // PiTaPaご利用年月
        condition.put("pitapaUseYearMonth", pitapaUseYearMonth);
        // 最終更新者ID
        condition.put("updateUserId", BATCH_PROCESS_ID);
        // 最終更新日時
        condition.put("updateDateTime", SystemTimeUtil.getTimestamp());
        // 処理済フラグ
        condition.put("processedFlag", OpalDefaultConstants.PROCESSED_FLAG_1);

        // PiTaPa利用実績一時情報を更新する。
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("UPDATE_PITAPA_USE_RES_TEMP_INFO");
        tempUpdatePitapaUseResTempInfoCount += statement.executeUpdateByMap(condition);
    }
}
