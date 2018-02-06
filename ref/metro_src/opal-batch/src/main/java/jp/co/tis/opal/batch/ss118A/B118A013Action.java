package jp.co.tis.opal.batch.ss118A;

import java.util.HashMap;
import java.util.Map;

import nablarch.core.date.SystemTimeUtil;
import nablarch.core.db.statement.ParameterizedSqlPStatement;
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

import jp.co.tis.opal.common.component.CM010002Component;
import jp.co.tis.opal.common.constants.OpalCodeConstants;
import jp.co.tis.opal.common.constants.OpalDefaultConstants;

/**
 * B118A013:移動情報差分チェック。
 *
 * @author 唐
 * @since 1.0
 */
public class B118A013Action extends BatchAction<SqlRow> {

    /** 入力データ件数 */
    private int inputCount;

    /** 出力データ件数(プッシュ通知情報)(登録) */
    private int insertPushNoticeInfoCount;

    /** 出力データ件数(プッシュ通知送信先情報)(登録) */
    private int insertPushNoticeDestInfoCount;

    /** 出力データ件数(プッシュ通知情報)(登録) */
    private int tempInsertPushNoticeInfoCount;

    /** 出力データ件数(プッシュ通知送信先情報)(登録) */
    private int tempInsertPushNoticeDestInfoCount;

    /** チェック対象年月日 */
    private String checkObjectDate;

    /** バッチ処理ID */
    private static final String BATCH_PROCESS_ID = "B118A013";

    /** 起動パラメータ:チェック対象年月日 */
    private static final String CHECK_OBJECT_DATE = "checkObjectDate";

    /** プッシュ通知情報登録の共通コンポーネント */
    private CM010002Component cm010002Component;

    /** 処理データ件数 */
    private int currentDataCount;

    /** コミット間隔 */
    private int commitInterval;

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
        inputCount = 0;
        insertPushNoticeInfoCount = 0;
        insertPushNoticeDestInfoCount = 0;
        tempInsertPushNoticeInfoCount = 0;
        tempInsertPushNoticeDestInfoCount = 0;
        // チェック対象年月日
        checkObjectDate = command.getParamMap().get(CHECK_OBJECT_DATE);
        if (StringUtil.isNullOrEmpty(checkObjectDate)) {
            checkObjectDate = SystemTimeUtil.getDateString();
        }
        // コミット間隔を取得
        commitInterval = Integer.parseInt(SystemRepository.getString("nablarch.loopHandler.commitInterval"));

        // プッシュ通知可能のデバイスID取得
        sendableDeviceId = SystemRepository.getString("push_notice_sendable_device_id").split(",");

    }

    /**
     * {@inneritDoc}
     * <p/>
     * 移動情報が変更されたアプリ会員ID取得。
     */
    @Override
    public DataReader<SqlRow> createReader(ExecutionContext ctx) {

        // 入力データ件数用のSQLの条件を設定する
        Map<String, Object> condition = new HashMap<String, Object>();
        // 移動情報取込日付
        condition.put("moveInfoRinDate", checkObjectDate);
        // アプリ会員状態コード
        condition.put("applicationMemberStatusCode", OpalCodeConstants.AplMemStatusCode.OP_AUTH_APL_MEM);
        // 処理対象レコード件数を取得
        inputCount = countByParameterizedSql("SELECT_MAIN_USE_STA_TEMP_INFO", condition);
        // 入力データ件数をログに出力
        writeLog("MB118A0107", Integer.valueOf(inputCount));

        // 入力データ件数は0件ではないの場合
        if (inputCount > 0) {
            // 送信日時
            String sysDate = DateUtil.formatDate(SystemTimeUtil.getDateString(), OpalDefaultConstants.DATE_FORMAT);
            StringBuilder deliverTime = new StringBuilder();
            deliverTime.append(sysDate);
            deliverTime.append(OpalDefaultConstants.BLANK);
            deliverTime.append(SystemRepository.getString("deliver_time_push_notice_instr"));

            // 1ファイル当たりの宛先件数上限
            int upperLimitNumber = Integer.valueOf(SystemRepository.getString("upper_limit_push_notice_instr"));

            // プッシュ通知情報登録するための初期設定を行う
            cm010002Component = new CM010002Component(BATCH_PROCESS_ID,
                    OpalCodeConstants.PushNoticeDivision.PUSH_NOTICE_DIVISION_2,
                    OpalCodeConstants.PushNoticeType.PUSH_NOTICE_TYPE_2,
                    OpalDefaultConstants.PUSH_TEMP_MAIN_USE_STA_UPDATE_NOTICE, deliverTime.toString(),
                    upperLimitNumber);

        }

        // 主なご利用駅情報・アプリ会員情報から処理対象のレコードを読み込む。
        DatabaseRecordReader reader = new DatabaseRecordReader();
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("SELECT_MAIN_USE_STA_TEMP_INFO");
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
            // 移動情報が変更されたアプリ会員毎に、移動情報変更のお知らせを送信する
            outputDataCount = cm010002Component.insPushNoticeInformation(inputData.getLong("APPLICATION_MEMBER_ID"),
                    inputData.getString("APPLICATION_ID"), inputData.getString("DEVICE_ID"));
            // 処理対象レコード件数を取得
            tempInsertPushNoticeInfoCount += outputDataCount.get("PUSH_NOTICE_INFORMATION_OUTPUT_CNT");
            tempInsertPushNoticeDestInfoCount += outputDataCount.get("PUSH_NOTICE_DEST_INFO_OUTPUT_CNT");

            currentDataCount++;
            // コミット件数取得
            if (currentDataCount == inputCount || currentDataCount % commitInterval == 0) {
                insertPushNoticeInfoCount = tempInsertPushNoticeInfoCount;
                insertPushNoticeDestInfoCount = tempInsertPushNoticeDestInfoCount;
            }
        } else {
            // デバイスIDがプッシュ通知可能のデバイスID以外の場合、プッシュ通知不可ワーニングメッセージをログに出力する
            Message message = MessageUtil.createMessage(MessageLevel.WARN, "MB118A0110",
                    String.valueOf(inputData.getString("APPLICATION_MEMBER_ID")),
                    String.valueOf(inputData.getString("DEVICE_ID")));
            LOG.logWarn(message.formatMessage());
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
        // 出力データ件数(プッシュ通知情報)(登録)をログに出力
        writeLog("MB118A0108", Integer.valueOf(insertPushNoticeInfoCount));
        // 出力データ件数(プッシュ通知送信先情報)(登録)をログに出力
        writeLog("MB118A0109", Integer.valueOf(insertPushNoticeDestInfoCount));
    }

}
