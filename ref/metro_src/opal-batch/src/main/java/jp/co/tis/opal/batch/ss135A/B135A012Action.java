package jp.co.tis.opal.batch.ss135A;

import java.util.HashMap;
import java.util.Map;

import nablarch.common.dao.UniversalDao;
import nablarch.core.date.SystemTimeUtil;
import nablarch.core.db.statement.ParameterizedSqlPStatement;
import nablarch.core.db.statement.SqlPStatement;
import nablarch.core.db.statement.SqlRow;
import nablarch.core.log.Logger;
import nablarch.core.log.LoggerManager;
import nablarch.core.message.Message;
import nablarch.core.message.MessageLevel;
import nablarch.core.message.MessageUtil;
import nablarch.core.repository.SystemRepository;
import nablarch.core.util.DateUtil;
import nablarch.core.util.StringUtil;
import nablarch.fw.DataReader;
import nablarch.fw.ExecutionContext;
import nablarch.fw.Result;
import nablarch.fw.Result.Success;
import nablarch.fw.action.BatchAction;
import nablarch.fw.action.BatchActionBase;
import nablarch.fw.launcher.CommandLine;
import nablarch.fw.reader.DatabaseRecordReader;
import nablarch.fw.results.TransactionAbnormalEnd;

import jp.co.tis.opal.common.component.CM010002Component;
import jp.co.tis.opal.common.component.CM010005Component;
import jp.co.tis.opal.common.constants.OpalCodeConstants;
import jp.co.tis.opal.common.constants.OpalDefaultConstants;
import jp.co.tis.opal.common.entity.MileBalanceInfoEntity;

/**
 * B135A012:乗車マイル情報加算
 *
 * @author 唐
 * @since 1.0
 */
public class B135A012Action extends BatchAction<SqlRow> {

    /** 入力データ件数(乗車マイル取込一時情報) */
    private int inputMileTempCount;

    /** 出力データ件数(マイル残高情報)(登録) */
    private int insertMileCount;

    /** 出力データ件数(マイル残高情報)(更新) */
    private int updateMileCount;

    /** 出力データ件数(マイル履歴情報)(登録) */
    private int insertMileHistoryCount;

    /** 出力データ件数(アプリ会員情報)(更新) */
    private int updateAplMemCount;

    /** 出力データ件数(乗車マイル取込一時情報)(更新) */
    private int updateMileTempCount;

    /** 出力データ件数(マイル残高情報)(登録) */
    private int tempInsertMileCount;

    /** 出力データ件数(マイル残高情報)(更新) */
    private int tempUpdateMileCount;

    /** 出力データ件数(マイル履歴情報)(登録) */
    private int tempInsertMileHistoryCount;

    /** 出力データ件数(アプリ会員情報)(更新) */
    private int tempUpdateAplMemCount;

    /** 出力データ件数(乗車マイル取込一時情報)(更新) */
    private int tempUpdateMileTempCount;

    /** 出力データ件数(プッシュ通知情報)(登録) */
    private int insertPushNoticeInfoCount;

    /** 出力データ件数(プッシュ通知送信先情報)(登録) */
    private int insertPushNoticeDestInfoCount;

    /** 出力データ件数(プッシュ通知情報)(登録) */
    private int tempInsertPushNoticeInfoCount;

    /** 出力データ件数(プッシュ通知送信先情報)(登録) */
    private int tempInsertPushNoticeDestInfoCount;

    /** バッチ処理ID */
    private static final String BATCH_PROCESS_ID = "B135A012";

    /** 処理データ件数 */
    private int currentDataCount;

    /** コミット間隔 */
    private int commitInterval;

    /** プッシュ通知情報登録の共通コンポーネント */
    private CM010002Component cm010002Component;

    /** プッシュ通知指示_送信可能デバイスID */
    private String[] sendableDeviceId;

    /** ロガー */
    private static final Logger LOG = LoggerManager.get(BatchActionBase.class);

    /**
     * {@inneritDoc}
     * <p/>
     * 事前処理
     */
    @Override
    protected void initialize(CommandLine command, ExecutionContext context) {

        // 処理ログの初期化
        inputMileTempCount = 0;
        tempInsertMileCount = 0;
        tempUpdateMileCount = 0;
        tempInsertMileHistoryCount = 0;
        tempUpdateAplMemCount = 0;
        tempUpdateMileTempCount = 0;
        tempInsertPushNoticeInfoCount = 0;
        tempInsertPushNoticeDestInfoCount = 0;

        // コミット間隔を取得
        commitInterval = Integer.parseInt(SystemRepository.getString("nablarch.loopHandler.commitInterval"));

        // プッシュ通知可能のデバイスID取得
        sendableDeviceId = SystemRepository.getString("push_notice_sendable_device_id").split(",");
    }

    /**
     * {@inneritDoc}
     * <p/>
     * 乗車マイル取込一時情報TBLとアプリ会員情報TBLから処理対象のレコードを取得。
     */
    @Override
    public DataReader<SqlRow> createReader(ExecutionContext ctx) {

        // 処理対象レコード件数を取得
        inputMileTempCount = countByStatementSql("SELECT_RIDE_MILE_RIN_TEMP_INFO");
        // 入力データ件数をログに出力
        writeLog("MB135A0107", Integer.valueOf(inputMileTempCount));

        // 入力データ件数は0件ではないの場合
        if (inputMileTempCount > 0) {
            // 送信日時
            String sysDate = DateUtil.formatDate(SystemTimeUtil.getDateString(), OpalDefaultConstants.DATE_FORMAT);
            StringBuilder deliverTime = new StringBuilder();
            deliverTime.append(sysDate);
            deliverTime.append(OpalDefaultConstants.BLANK);
            deliverTime.append(SystemRepository.getString("deliver_time_push_notice_instr"));

            // 1ファイル当たりの宛先件数上限
            int upperLimitNumber = Integer.parseInt(SystemRepository.getString("upper_limit_push_notice_instr"));

            // プッシュ通知情報登録するための初期設定を行
            cm010002Component = new CM010002Component(BATCH_PROCESS_ID,
                    OpalCodeConstants.PushNoticeDivision.PUSH_NOTICE_DIVISION_2,
                    OpalCodeConstants.PushNoticeType.PUSH_NOTICE_TYPE_2,
                    OpalDefaultConstants.PUSH_TEMP_RIDE_MILE_ACQUIRE_NOTICE, deliverTime.toString(), upperLimitNumber);

        }

        // 乗車マイル取込一時情報TBLとアプリ会員情報TBLから処理対象のレコードを読み込む。
        DatabaseRecordReader reader = new DatabaseRecordReader();
        SqlPStatement statement = getSqlPStatement("SELECT_RIDE_MILE_RIN_TEMP_INFO");
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
        // 対象年月
        String objectYearMonth = inputData.getString("OBJECT_YEAR_MONTH");
        // 乗車マイル数
        Long rideMileAmount = inputData.getLong("RIDE_MILE_AMOUNT");
        // マイル種別コード
        String mileCategoryCode = inputData.getString("MILE_CATEGORY_CODE");
        // アプリ会員ID
        Long applicationMemberId = inputData.getLong("APPLICATION_MEMBER_ID");
        // OP退会フラグ
        String opWithdrawFlag = inputData.getString("OSAKA_PITAPA_WITHDRAW_FLAG");

        // 乗車マイル情報取込可否チェック
        if (StringUtil.isNullOrEmpty(inputData.getString("APPLICATION_MEMBER_ID"))) {
            throw new TransactionAbnormalEnd(101, "AB135A0104", memberControlNumber, memCtrlNumBrNum);
        }

        // OP退会チェック
        if (OpalCodeConstants.OPWithdrawFlag.OP_WITHDRAW_FLAG_1.equals(opWithdrawFlag)) {
            throw new TransactionAbnormalEnd(100, "AB135A0103", applicationMemberId);
        }

        // アプリ会員状態コード = "C"(乗車マイルなしOP会員)の場合
        if (OpalCodeConstants.AplMemStatusCode.WITHOUT_RIDE_MILE_OP_MEM
                .equals(inputData.getString("APPLICATION_MEMBER_STATUS_CODE"))) {
            // アプリ会員状態更新
            updateAplMemInfo(applicationMemberId);
        }

        // マイル残高情報排他制御（アプリ会員単位）
        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("applicationMemberId", applicationMemberId);
        UniversalDao.findAllBySqlFile(MileBalanceInfoEntity.class, "SELECT_MILE_BALANCE_INFO", condition);

        // CM010005：マイル計算共通コンポーネントを呼び出す。
        CM010005Component cm010005Component = new CM010005Component();
        // 乗車マイル加算。
        String result = cm010005Component.addMile(applicationMemberId, null, mileCategoryCode, rideMileAmount,
                BATCH_PROCESS_ID, objectYearMonth);

        if (OpalDefaultConstants.MILE_BALANCE_UPDATE_FLG_0.equals(result)) {
            // 出力データ件数(マイル残高情報)(登録)をカウントアップする。
            tempInsertMileCount++;
        } else if (OpalDefaultConstants.MILE_BALANCE_UPDATE_FLG_1.equals(result)) {
            // 出力データ件数(マイル残高情報)(更新)をカウントアップする。
            tempUpdateMileCount++;
        }

        // 出力データ件数(マイル履歴情報)(登録)をカウントアップする。
        tempInsertMileHistoryCount++;

        // アプリ会員状態コードが"A"（OP認証済のアプリ会員）の場合
        if (OpalCodeConstants.AplMemStatusCode.OP_AUTH_APL_MEM
                .equals(inputData.getString("APPLICATION_MEMBER_STATUS_CODE"))
                && OpalCodeConstants.MileCategoryCode.BASIC_RIDE_MILE.equals(mileCategoryCode)) {
            // デバイスIDチェック
            boolean sendableDeviceFlag = false;
            for (int i = 0; i < sendableDeviceId.length; i++) {
                if (sendableDeviceId[i].equals(inputData.getString("DEVICE_ID"))) {
                    sendableDeviceFlag = true;
                    break;
                }
            }
            // デバイスIDがプッシュ通知可能のデバイスIDの場合
            if (sendableDeviceFlag) {
                Map<String, Integer> outputDataCount = new HashMap<String, Integer>();
                // プッシュ通知情報登録。
                outputDataCount = cm010002Component.insPushNoticeInformation(applicationMemberId,
                        inputData.getString("APPLICATION_ID"), inputData.getString("DEVICE_ID"));
                // 処理対象レコード件数を取得
                tempInsertPushNoticeInfoCount += outputDataCount.get("PUSH_NOTICE_INFORMATION_OUTPUT_CNT");
                tempInsertPushNoticeDestInfoCount += outputDataCount.get("PUSH_NOTICE_DEST_INFO_OUTPUT_CNT");
            } else {
                // デバイスIDがプッシュ通知可能のデバイスID以外の場合、プッシュ通知不可ワーニングメッセージをログに出力する
                Message message = MessageUtil.createMessage(MessageLevel.WARN, "MB135A0115",
                        String.valueOf(applicationMemberId), String.valueOf(inputData.getString("DEVICE_ID")));
                LOG.logWarn(message.formatMessage());
            }
        }

        // 乗車マイル取込一時情報更新。
        updateRideMileRinTempInfo(memberControlNumber, memCtrlNumBrNum, objectYearMonth, rideMileAmount,
                mileCategoryCode);

        currentDataCount++;
        // コミット件数取得
        if (currentDataCount == inputMileTempCount || currentDataCount % commitInterval == 0) {
            insertMileCount = tempInsertMileCount;
            updateMileCount = tempUpdateMileCount;
            insertMileHistoryCount = tempInsertMileHistoryCount;
            updateAplMemCount = tempUpdateAplMemCount;
            updateMileTempCount = tempUpdateMileTempCount;
            insertPushNoticeInfoCount = tempInsertPushNoticeInfoCount;
            insertPushNoticeDestInfoCount = tempInsertPushNoticeDestInfoCount;
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
        // 出力データ件数(マイル残高情報)(登録)をログに出力
        writeLog("MB135A0110", Integer.valueOf(insertMileCount));
        // 出力データ件数(マイル残高情報)(更新)をログに出力
        writeLog("MB135A0111", Integer.valueOf(updateMileCount));
        // 出力データ件数(マイル履歴情報)(登録)をログに出力
        writeLog("MB135A0112", Integer.valueOf(insertMileHistoryCount));
        // 出力データ件数(アプリ会員情報)(更新)をログに出力
        writeLog("MB135A0108", Integer.valueOf(updateAplMemCount));
        // 出力データ件数(乗車マイル取込一時情報)(更新)をログに出力
        writeLog("MB135A0109", Integer.valueOf(updateMileTempCount));
        // 出力データ件数(プッシュ通知情報)(登録)をログに出力
        writeLog("MB135A0113", Integer.valueOf(insertPushNoticeInfoCount));
        // 出力データ件数(プッシュ通知送信先情報)(登録)をログに出力
        writeLog("MB135A0114", Integer.valueOf(insertPushNoticeDestInfoCount));
    }

    /**
     * アプリ会員情報更新
     * <p/>
     *
     * @param applicationMemberId
     *            アプリ会員ID
     *
     */
    private void updateAplMemInfo(Long applicationMemberId) {
        // アプリ会員情報更新用のSQL条件を設定する。
        Map<String, Object> condition = new HashMap<String, Object>();
        // アプリ会員状態コード
        condition.put("statusB", OpalCodeConstants.AplMemStatusCode.HAVE_RIDE_MILE_OP_MEM);
        // 最終更新者ID
        condition.put("updateUserId", BATCH_PROCESS_ID);
        // 最終更新日時
        condition.put("updateDateTime", SystemTimeUtil.getTimestamp());
        // アプリ会員ID
        condition.put("applicationMemberId", applicationMemberId);
        // 削除フラグ
        condition.put("deletedFlg", OpalDefaultConstants.DELETED_FLG_0);

        // アプリ会員情報を更新する。
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("UPDATE_APL_MEM_INFO");
        // 出力データ件数(アプリ会員情報)(更新)をカウントアップする。
        tempUpdateAplMemCount += statement.executeUpdateByMap(condition);
    }

    /**
     * 乗車マイル取込一時情報更新
     * <p/>
     *
     * @param memberControlNumber
     *            会員管理番号
     * @param memCtrlNumBrNum
     *            会員管理番号枝番
     * @param objectYearMonth
     *            対象年月
     * @param rideMileAmount
     *            乗車マイル数
     * @param mileCategoryCode
     *            マイル種別コード
     */
    private void updateRideMileRinTempInfo(String memberControlNumber, String memCtrlNumBrNum, String objectYearMonth,
            Long rideMileAmount, String mileCategoryCode) {

        // 乗車マイル取込一時情報更新用のSQL条件を設定する。
        Map<String, Object> condition = new HashMap<String, Object>();
        // 処理済フラグ
        condition.put("processedFlag", OpalDefaultConstants.PROCESSED_FLAG_1);
        // 最終更新者ID
        condition.put("updateUserId", BATCH_PROCESS_ID);
        // 最終更新日時
        condition.put("updateDateTime", SystemTimeUtil.getTimestamp());
        // 会員管理番号
        condition.put("memberControlNumber", memberControlNumber);
        // 会員管理番号枝番
        condition.put("memCtrlNumBrNum", memCtrlNumBrNum);
        // 対象年月
        condition.put("objectYearMonth", objectYearMonth);
        // 乗車マイル数
        condition.put("rideMileAmount", rideMileAmount);
        // マイル種別コード
        condition.put("mileCategoryCode", mileCategoryCode);

        // 乗車マイル取込一時情報を更新する。
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("UPDATE_RIDE_MILE_RIN_TEMP_INFO");
        tempUpdateMileTempCount += statement.executeUpdateByMap(condition);
    }
}
