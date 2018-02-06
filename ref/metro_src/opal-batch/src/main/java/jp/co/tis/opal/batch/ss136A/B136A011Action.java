package jp.co.tis.opal.batch.ss136A;

import java.util.HashMap;
import java.util.Map;

import nablarch.core.date.SystemTimeUtil;
import nablarch.core.db.statement.ParameterizedSqlPStatement;
import nablarch.core.db.statement.SqlRow;
import nablarch.core.repository.SystemRepository;
import nablarch.fw.DataReader;
import nablarch.fw.ExecutionContext;
import nablarch.fw.Result;
import nablarch.fw.Result.Success;
import nablarch.fw.action.BatchAction;
import nablarch.fw.launcher.CommandLine;
import nablarch.fw.reader.DatabaseRecordReader;

import jp.co.tis.opal.common.constants.OpalDefaultConstants;
import jp.co.tis.opal.common.utility.DateConvertUtil;

/**
 * B136A011:マイル失効対象抽出のアクションクラス。
 *
 * @author 曹
 * @since 1.0
 */
public class B136A011Action extends BatchAction<SqlRow> {

    /** マイル失効処理対象件数 */
    private int mileInvalid;

    /** 出力データ件数(マイル失効対象一時情報) */
    private int insertMileInvalidTemp;
    private int tempInsertMileInvalidTemp;

    /** 処理データ件数 */
    private int currentDataCount;

    /** コミット間隔 */
    private int commitInterval;

    /** バッチ処理ID */
    private static final String BATCH_PROCESS_ID = "B136A011";

    /**
     * {@inneritDoc}
     * <p/>
     * 事前処理
     */
    @Override
    protected void initialize(CommandLine command, ExecutionContext context) {

        // 処理ログの初期化
        mileInvalid = 0;
        tempInsertMileInvalidTemp = 0;
        insertMileInvalidTemp = 0;
        currentDataCount = 0;

        // コミット間隔を取得
        commitInterval = Integer.parseInt(SystemRepository.getString("nablarch.loopHandler.commitInterval"));
    }

    /**
     * {@inneritDoc}
     * <p/>
     * マイル残高情報、アプリ会員情報から処理対象のレコードを読み込む。
     */
    @Override
    public DataReader<SqlRow> createReader(ExecutionContext ctx) {

        // マイル失効対象の終了年月を取得する。
        StringBuilder stringForToMonth = new StringBuilder();
        stringForToMonth.append(DateConvertUtil.getSysYear());
        stringForToMonth.append(OpalDefaultConstants.MILE_INVALID_TO_MONTH);
        String mileInvalidToMonth = stringForToMonth.toString();

        // マイル失効対象の開始年月を取得する。
        StringBuilder stringForFromMonth = new StringBuilder();
        stringForFromMonth.append(Integer.parseInt(DateConvertUtil.getSysYear()) - 1);
        stringForFromMonth.append(OpalDefaultConstants.MILE_INVALID_FROM_MONTH);
        String mileInvalidFromMonth = stringForFromMonth.toString();

        // 入力データ件数用のSQLの条件を設定する
        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("mileInvalidFromMonth", mileInvalidFromMonth);
        condition.put("mileInvalidToMonth", mileInvalidToMonth);

        // 処理対象レコード件数を取得
        mileInvalid = countByParameterizedSql("SELECT_MILE_INV_OBJ_INFO", condition);
        // 入力データ件数をログに出力
        writeLog("MB136A0101", Integer.valueOf(mileInvalid));

        // マイル残高情報、アプリ会員情報から処理対象のレコードを読み込む。
        DatabaseRecordReader reader = new DatabaseRecordReader();
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("SELECT_MILE_INV_OBJ_INFO");
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
        currentDataCount++;
        // 入力データ取得
        // アプリ会員ID
        Long applicationMemberId = inputData.getLong("APPLICATION_MEMBER_ID");
        // マイル残高合計
        Long sumMileBalance = inputData.getLong("SUM_MILE_BALANCE");
        // 会員管理番号
        String memberControlNumber = inputData.getString("MEMBER_CONTROL_NUMBER");
        // 会員管理番号枝番
        String memCtrlNumBrNum = inputData.getString("MEM_CTRL_NUM_BR_NUM");
        // OP番号
        String osakaPitapaNumber = inputData.getString("OSAKA_PITAPA_NUMBER");

        // マイル失効対象一時情報登録
        insertMileInvObjTempInfo(applicationMemberId, memberControlNumber, memCtrlNumBrNum, osakaPitapaNumber,
                sumMileBalance);
        // コミット件数取得
        if (currentDataCount == mileInvalid || currentDataCount % commitInterval == 0) {
            insertMileInvalidTemp = tempInsertMileInvalidTemp;
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

        // 出力データ件数(マイル失効対象一時情報)(登録)をログに出力
        writeLog("MB136A0102", Integer.valueOf(insertMileInvalidTemp));
    }

    /**
     * {@inneritDoc} マイル失効対象一時情報登録登録
     * <p/>
     *
     * @param applicationMemberId
     *            アプリ会員ID
     * @param memberControlNumber
     *            会員管理番号
     * @param memCtrlNumBrNum
     *            会員管理番号枝番
     * @param osakaPitapaNumber
     *            OP番号
     * @param sumMileBalance
     *            失効対象マイル数
     */
    private void insertMileInvObjTempInfo(Long applicationMemberId, String memberControlNumber, String memCtrlNumBrNum,
            String osakaPitapaNumber, Long sumMileBalance) {

        // マイル失効対象一時情報登録用のSQL条件を設定する。
        Map<String, Object> condition = new HashMap<String, Object>();
        // アプリ会員ID
        condition.put("applicationMemberId", applicationMemberId);
        // 会員管理番号
        condition.put("memberControlNumber", memberControlNumber);
        // 会員管理番号枝番
        condition.put("memCtrlNumBrNum", memCtrlNumBrNum);
        // OP番号
        condition.put("osakaPitapaNumber", osakaPitapaNumber);
        // 失効対象マイル数
        condition.put("sumMileBalance", sumMileBalance);
        // 処理済フラグ
        condition.put("processedFlag", OpalDefaultConstants.PROCESSED_FLAG_0);
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
        condition.put("deletedDate", null);

        // マイル失効対象一時情報登録
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("INSERT_MILE_INV_OBJ_TEMP_INFO");
        tempInsertMileInvalidTemp += statement.executeUpdateByMap(condition);
    }

}
