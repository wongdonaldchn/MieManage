package jp.co.tis.opal.batch.ss118A;

import java.util.HashMap;
import java.util.Map;

import nablarch.core.date.SystemTimeUtil;
import nablarch.core.db.statement.ParameterizedSqlPStatement;
import nablarch.core.db.statement.SqlPStatement;
import nablarch.core.db.statement.SqlResultSet;
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

/**
 * B118A012:主なご利用駅情報反映。
 *
 * @author 唐
 * @since 1.0
 */
public class B118A012Action extends BatchAction<SqlRow> {

    /** 入力データ件数(主なご利用駅一時情報) */
    private int inputMainTempStationCount;

    /** 出力データ件数(主なご利用駅情報)(更新) */
    private int updateMainStationCount;

    /** 出力データ件数(主なご利用駅情報)(登録) */
    private int insertMainStationCount;

    /** 出力データ件数(主なご利用駅一時情報)(更新) */
    private int updateMainTempStationCount;

    /** 出力データ件数(主なご利用駅情報)(更新) */
    private int tempUpdateMainStationCount;

    /** 出力データ件数(主なご利用駅情報)(登録) */
    private int tempInsertMainStationCount;

    /** 出力データ件数(主なご利用駅一時情報)(更新) */
    private int tempUpdateMainTempStationCount;

    /** 処理データ件数 */
    private int currentDataCount;

    /** コミット間隔 */
    private int commitInterval;

    /** バッチ処理ID */
    private static final String BATCH_PROCESS_ID = "B118A012";

    /**
     * {@inneritDoc}
     * <p/>
     * 事前処理
     */
    @Override
    protected void initialize(CommandLine command, ExecutionContext context) {

        // 処理ログの初期化
        inputMainTempStationCount = 0;
        tempUpdateMainStationCount = 0;
        tempInsertMainStationCount = 0;
        tempUpdateMainTempStationCount = 0;

        // コミット間隔を取得
        commitInterval = Integer.parseInt(SystemRepository.getString("nablarch.loopHandler.commitInterval"));
    }

    /**
     * {@inneritDoc}
     * <p/>
     * 主なご利用駅一時情報から処理対象のレコードを読み込む。
     */
    @Override
    public DataReader<SqlRow> createReader(ExecutionContext ctx) {

        // 処理対象レコード件数を取得
        inputMainTempStationCount = countByStatementSql("SELECT_MAIN_USE_STAION_TEMP_INFO");
        // 入力データ件数をログに出力
        writeLog("MB118A0103", Integer.valueOf(inputMainTempStationCount));

        // 主なご利用駅一時情報から処理対象のレコードを読み込む。
        DatabaseRecordReader reader = new DatabaseRecordReader();
        SqlPStatement statement = getSqlPStatement("SELECT_MAIN_USE_STAION_TEMP_INFO");
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

        // 主なご利用駅情報の取得
        SqlResultSet mainUseStaInfo = getMainUseStaInfo(memberControlNumber, memCtrlNumBrNum);
        if (mainUseStaInfo.isEmpty()) {
            // データが存在しない場合、主なご利用駅一時情報を主なご利用駅情報TBLに登録する。
            insertMainUseStaInfo(inputData);
        } else {
            // データが存在する場合、主なご利用駅一時情報を主なご利用駅情報TBLに更新する。
            updateMainUseStaInfo(inputData, mainUseStaInfo.get(0));
        }

        // 主なご利用駅一時情報TBLに処理済フラグを更新する。
        updateMainUseStaTempInfo(memberControlNumber, memCtrlNumBrNum);

        currentDataCount++;
        // コミット件数取得
        if (currentDataCount == inputMainTempStationCount || currentDataCount % commitInterval == 0) {
            updateMainStationCount = tempUpdateMainStationCount;
            insertMainStationCount = tempInsertMainStationCount;
            updateMainTempStationCount = tempUpdateMainTempStationCount;
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
        // 出力データ件数(主なご利用駅情報)(更新)をログに出力
        writeLog("MB118A0104", Integer.valueOf(updateMainStationCount));
        // 出力データ件数(主なご利用駅情報)(登録)をログに出力
        writeLog("MB118A0105", Integer.valueOf(insertMainStationCount));
        // 出力データ件数(主なご利用駅一時情報)(更新)をログに出力
        writeLog("MB118A0106", Integer.valueOf(updateMainTempStationCount));
    }

    /**
     * 主なご利用駅情報取得
     *
     * @param memberControlNumber
     *            会員管理番号
     * @param memCtrlNumBrNum
     *            会員管理番号枝番
     *
     * @return 処理結果
     */
    private SqlResultSet getMainUseStaInfo(String memberControlNumber, String memCtrlNumBrNum) {
        // 主なご利用駅情報取得用のSQLの条件を設定する
        Map<String, Object> condition = new HashMap<String, Object>();
        // 会員管理番号
        condition.put("memberControlNumber", memberControlNumber);
        // 会員管理番号枝番
        condition.put("memCtrlNumBrNum", memCtrlNumBrNum);
        // 実行する。
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("SELECT_MAIN_USE_STAION_INFO");
        SqlResultSet result = statement.retrieve(condition);
        return result;
    }

    /**
     * {@inneritDoc} 主なご利用駅情報登録。
     * <p/>
     *
     * @param inputData
     *            主なご利用駅一時情報
     */
    private void insertMainUseStaInfo(SqlRow inputData) {
        // 主なご利用駅情報登録用のSQL条件を設定する。
        Map<String, Object> condition = new HashMap<String, Object>();
        // 会員管理番号
        condition.put("memberControlNumber", inputData.getString("MEMBER_CONTROL_NUMBER"));
        // 会員管理番号枝番
        condition.put("memCtrlNumBrNum", inputData.getString("MEM_CTRL_NUM_BR_NUM"));
        // 前回登録駅1
        condition.put("lastTimeRegistStation1", null);
        // 前回登録駅2
        condition.put("lastTimeRegistStation2", null);
        // 前回登録駅3
        condition.put("lastTimeRegistStation3", null);
        // 前回登録駅4
        condition.put("lastTimeRegistStation4", null);
        // 前回登録駅5
        condition.put("lastTimeRegistStation5", null);
        // 今回登録駅1
        condition.put("thisTimeRegistStation1", inputData.getString("REGIST_STATION_1"));
        // 今回登録駅2
        condition.put("thisTimeRegistStation2", inputData.getString("REGIST_STATION_2"));
        // 今回登録駅3
        condition.put("thisTimeRegistStation3", inputData.getString("REGIST_STATION_3"));
        // 今回登録駅4
        condition.put("thisTimeRegistStation4", inputData.getString("REGIST_STATION_4"));
        // 今回登録駅5
        condition.put("thisTimeRegistStation5", inputData.getString("REGIST_STATION_5"));
        // 移動情報取込日付
        condition.put("moveInfoRinDate", SystemTimeUtil.getDateString());
        // 登録者ID
        condition.put("insertUserId", BATCH_PROCESS_ID);
        // 登録日時
        condition.put("insertDateTime", SystemTimeUtil.getTimestamp());
        // 最終更新者ID
        condition.put("updateUserId", BATCH_PROCESS_ID);
        // 最終更新日時
        condition.put("updateDateTime", SystemTimeUtil.getTimestamp());
        // 削除フラグ
        condition.put("deletedFlag", OpalDefaultConstants.DELETED_FLG_0);
        // 論理削除日
        condition.put("deletedDate", null);
        // バージョン番号
        condition.put("version", OpalDefaultConstants.VERSION);

        // 主なご利用駅情報を登録する。
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("INSERT_MAIN_USE_STAION_INFO");
        tempInsertMainStationCount += statement.executeUpdateByMap(condition);
    }

    /**
     * {@inneritDoc} 主なご利用駅情報更新。
     * <p/>
     *
     * @param inputData
     *            主なご利用駅一時情報
     * @param mainUseStaInfo
     *            主なご利用駅情報
     */
    private void updateMainUseStaInfo(SqlRow inputData, SqlRow mainUseStaInfo) {

        // 主なご利用駅情報更新用のSQL条件を設定する。
        Map<String, Object> condition = new HashMap<String, Object>();
        // 前回登録駅1
        condition.put("lastTimeRegistStation1", mainUseStaInfo.getString("THIS_TIME_REGIST_STATION_1"));
        // 前回登録駅2
        condition.put("lastTimeRegistStation2", mainUseStaInfo.getString("THIS_TIME_REGIST_STATION_2"));
        // 前回登録駅3
        condition.put("lastTimeRegistStation3", mainUseStaInfo.getString("THIS_TIME_REGIST_STATION_3"));
        // 前回登録駅4
        condition.put("lastTimeRegistStation4", mainUseStaInfo.getString("THIS_TIME_REGIST_STATION_4"));
        // 前回登録駅5
        condition.put("lastTimeRegistStation5", mainUseStaInfo.getString("THIS_TIME_REGIST_STATION_5"));
        // 今回登録駅1
        condition.put("thisTimeRegistStation1", inputData.getString("REGIST_STATION_1"));
        // 今回登録駅2
        condition.put("thisTimeRegistStation2", inputData.getString("REGIST_STATION_2"));
        // 今回登録駅3
        condition.put("thisTimeRegistStation3", inputData.getString("REGIST_STATION_3"));
        // 今回登録駅4
        condition.put("thisTimeRegistStation4", inputData.getString("REGIST_STATION_4"));
        // 今回登録駅5
        condition.put("thisTimeRegistStation5", inputData.getString("REGIST_STATION_5"));
        // 移動情報取込日付
        condition.put("moveInfoRinDate", SystemTimeUtil.getDateString());
        // 最終更新者ID
        condition.put("updateUserId", BATCH_PROCESS_ID);
        // 最終更新日時
        condition.put("updateDateTime", SystemTimeUtil.getTimestamp());
        // 会員管理番号
        condition.put("memberControlNumber", inputData.getString("MEMBER_CONTROL_NUMBER"));
        // 会員管理番号枝番
        condition.put("memCtrlNumBrNum", inputData.getString("MEM_CTRL_NUM_BR_NUM"));
        // 削除フラグ
        condition.put("deletedFlg", OpalDefaultConstants.DELETED_FLG_0);

        // 主なご利用駅情報を更新する。
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("UPDATE_MAIN_USE_STAION_INFO");
        // 出力データ件数(主なご利用駅情報)(更新)をカウントアップする。
        tempUpdateMainStationCount += statement.executeUpdateByMap(condition);
    }

    /**
     * {@inneritDoc} 主なご利用駅一時情報更新。
     * <p/>
     *
     * @param memberControlNumber
     *            会員管理番号
     * @param memCtrlNumBrNum
     *            会員管理番号枝番
     */
    private void updateMainUseStaTempInfo(String memberControlNumber, String memCtrlNumBrNum) {

        // 主なご利用駅一時情報更新用のSQL条件を設定する。
        Map<String, Object> condition = new HashMap<String, Object>();
        // 会員管理番号
        condition.put("memberControlNumber", memberControlNumber);
        // 会員管理番号枝番
        condition.put("memCtrlNumBrNum", memCtrlNumBrNum);
        // 処理済フラグ
        condition.put("processedFlag", OpalDefaultConstants.PROCESSED_FLAG_1);
        // 最終更新者ID
        condition.put("updateUserId", BATCH_PROCESS_ID);
        // 最終更新日時
        condition.put("updateDateTime", SystemTimeUtil.getTimestamp());

        // 主なご利用駅一時情報を更新する。
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("UPDATE_MAIN_USE_STAION_TEMP_INFO");
        // 出力データ件数(主なご利用駅一時情報)(更新)をカウントアップする。
        tempUpdateMainTempStationCount += statement.executeUpdateByMap(condition);
    }
}
