package jp.co.tis.opal.batch.ss136A;

import java.util.HashMap;
import java.util.Map;

import nablarch.common.dao.UniversalDao;
import nablarch.common.io.FileRecordWriterHolder;
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

import jp.co.tis.opal.common.component.CM010001Component;
import jp.co.tis.opal.common.component.CM010002Component;
import jp.co.tis.opal.common.component.CM010004Component;
import jp.co.tis.opal.common.constants.OpalCodeConstants;
import jp.co.tis.opal.common.constants.OpalDefaultConstants;
import jp.co.tis.opal.common.entity.MileBalanceInfoEntity;
import jp.co.tis.opal.common.utility.DateConvertUtil;
import jp.co.tis.opal.common.utility.IdGeneratorUtil;

/**
 * B136A012:マイル失効のアクションクラス。
 *
 * @author 曹
 * @since 1.0
 */
public class B136A012Action extends BatchAction<SqlRow> {

    /** 入力データ件数 */
    private int intputData;

    /** 出力データ件数(マイル残高情報) */
    private int updateMileBalance;
    private int tempUpdateMileBalance;

    /** 出力データ件数(マイル履歴) */
    private int insertMileHistory;
    private int tempInsertMileHistory;

    /** 出力データ件数(マイル移行一時情報) */
    private int insertMileTransTemp;
    private int tempInsertMileTransTemp;

    /** 出力データ件数(マイル失効対象一時情報) */
    private int updateMileInvalidTemp;
    private int tempUpdateMileInvalidTemp;

    /** 出力データ件数(プッシュ通知情報) */
    private int pushNoticeIfoOutputCnt;
    private int tempPushNoticeIfoOutputCnt;

    /** 出力データ件数(プッシュ通知送信先情報) */
    private int pushNoticeDestInfoOutputCnt;
    private int tempPushNoticeDestInfoOutputCnt;

    /** 出力データ件数(メール一括配信情報) */
    private int mailPackDeliverInfoOutputCnt;
    private int tempMailPackDeliverInfoOutputCnt;

    /** 出力データ件数(メール配信情報ファイル) */
    private int deliverFileOutputCnt;
    private int tempDeliverFileOutputCnt;

    /** 処理データ件数 */
    private int currentDataCount;

    /** コミット間隔 */
    private int commitInterval;

    /** メール配信情報ファイルの出力件数 */
    private int outputDataCount;

    /** メール配信情報ファイル名 */
    private String deliverFileName;

    /** 論理削除日 */
    private String deletedDate;

    /** プッシュ通知情報登録の共通コンポーネント */
    private CM010002Component cm010002Component;

    /** バッチ処理ID */
    private static final String BATCH_PROCESS_ID = "B136A012";

    /** 出力ファイルID：A151A001(メール配信情報ファイル) */
    private static final String FILE_ID = "A151A001";

    /** フォーマット定義ファイルID：A151A001 */
    private static final String FORMAT_ID = "A151A001";

    /** メール配信情報ファイルの出力先ディレクトリ */
    private static final String OUTPUT_PATH = "mail";

    /** メール配信一括指示_マイル移行_出力上限件数 */
    private static final String OUTPUT_MAX_COUNT = "mail_del_lump_instr_mile_trans_max_count";

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
        intputData = 0;
        updateMileBalance = 0;
        tempUpdateMileBalance = 0;
        insertMileHistory = 0;
        tempInsertMileHistory = 0;
        insertMileTransTemp = 0;
        tempInsertMileTransTemp = 0;
        updateMileInvalidTemp = 0;
        tempUpdateMileInvalidTemp = 0;
        currentDataCount = 0;
        outputDataCount = 0;
        pushNoticeIfoOutputCnt = 0;
        tempPushNoticeIfoOutputCnt = 0;
        pushNoticeDestInfoOutputCnt = 0;
        tempPushNoticeDestInfoOutputCnt = 0;
        mailPackDeliverInfoOutputCnt = 0;
        tempMailPackDeliverInfoOutputCnt = 0;
        deliverFileOutputCnt = 0;
        tempDeliverFileOutputCnt = 0;

        // プッシュ通知可能のデバイスID取得
        sendableDeviceId = SystemRepository.getString("push_notice_sendable_device_id").split(",");
    }

    /**
     * {@inneritDoc}
     * <p/>
     * マイル失効対象一時情報から処理対象のレコードを読み込む。
     */
    @Override
    public DataReader<SqlRow> createReader(ExecutionContext ctx) {

        // 処理対象レコード件数を取得
        intputData = countByStatementSql("SELECT_MILE_INV_OBJ_TEMP_INFO");
        // 入力データ件数をログに出力
        writeLog("MB136A0103", Integer.valueOf(intputData));

        // 処理対象レコードが0件以外の場合
        if (intputData > 0) {
            // 論理削除日の算出
            CM010004Component cm010004Component = new CM010004Component();
            deletedDate = cm010004Component.getDeletedDateMileYearly(
                    Integer.parseInt(SystemRepository.getString("mile_control_data_retention_period")));

            // コミット間隔を取得
            commitInterval = Integer.parseInt(SystemRepository.getString("nablarch.loopHandler.commitInterval"));

            // システム日付
            String sysDate = DateUtil.formatDate(SystemTimeUtil.getDateString(), OpalDefaultConstants.DATE_FORMAT);

            // 送信日時
            StringBuilder deliverTime = new StringBuilder();
            deliverTime.append(sysDate);
            deliverTime.append(OpalDefaultConstants.BLANK);
            deliverTime.append(SystemRepository.getString("deliver_time_push_notice_instr"));

            // 1ファイル当たりの宛先件数上限
            int upperLimitNumber = Integer.parseInt(SystemRepository.getString("upper_limit_push_notice_instr"));

            // プッシュ通知情報登録するための初期設定を行う
            cm010002Component = new CM010002Component(BATCH_PROCESS_ID,
                    OpalCodeConstants.PushNoticeDivision.PUSH_NOTICE_DIVISION_2,
                    OpalCodeConstants.PushNoticeType.PUSH_NOTICE_TYPE_2,
                    OpalDefaultConstants.PUSH_TEMP_MILE_TRANS_NOTICE, deliverTime.toString(), upperLimitNumber);

        }

        // マイル失効対象一時情報から処理対象のレコードを読み込む。
        DatabaseRecordReader reader = new DatabaseRecordReader();
        SqlPStatement statement = getSqlPStatement("SELECT_MILE_INV_OBJ_TEMP_INFO");
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
        // 処理データ件数をカウントアップする。
        currentDataCount++;
        // 入力データ取得
        // アプリ会員ID
        Long applicationMemberId = inputData.getLong("APPLICATION_MEMBER_ID");
        // アプリID
        String applicationId = inputData.getString("APPLICATION_ID");
        // デバイスID
        String deviceId = inputData.getString("DEVICE_ID");
        // アプリ会員状態コード
        String aplMemStatusCode = inputData.getString("APPLICATION_MEMBER_STATUS_CODE");
        // 失効対象マイル数
        Long invalidObjectMileAmount = inputData.getLong("INVALID_OBJECT_MILE_AMOUNT");
        // OP番号
        String osakaPitapaNumber = inputData.getString("OSAKA_PITAPA_NUMBER");
        // 会員管理番号
        String memberControlNumber = inputData.getString("MEMBER_CONTROL_NUMBER");
        // 会員管理番号枝番
        String memCtrlNumBrNum = inputData.getString("MEM_CTRL_NUM_BR_NUM");
        // メール配信状態区分
        String mailDeliverStatusDivision = inputData.getString("MAIL_DELIVER_STATUS_DIVISION");
        // メールアドレス
        String mailAddress = inputData.getString("MAIL_ADDRESS");
        Map<String, Object> mailAddressMap = new HashMap<String, Object>();
        mailAddressMap.put("mailAddress", mailAddress);

        // マイル残高情報排他制御（アプリ会員単位）
        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("applicationMemberId", applicationMemberId);
        UniversalDao.findAllBySqlFile(MileBalanceInfoEntity.class, "SELECT_MILE_BALANCE_INFO", condition);

        // マイル残高情報論理削除
        updateMileBalanceInfo(applicationMemberId);

        String mileCategoryCode = null;
        // 失効対象マイル数 > 0の場合、マイル履歴を登録する。
        if (invalidObjectMileAmount.longValue() > 0) {
            // マイル種別コードを設定する。
            if (StringUtil.isNullOrEmpty(osakaPitapaNumber)) {
                mileCategoryCode = OpalCodeConstants.MileCategoryCode.MILE_INV;
            } else if (!StringUtil.isNullOrEmpty(osakaPitapaNumber)) {
                mileCategoryCode = OpalCodeConstants.MileCategoryCode.OP_POINT_AUTO_TRANS;
            }
            insertMileHistoryInfo(applicationMemberId, invalidObjectMileAmount, mileCategoryCode);
        }

        // OP番号がNULL以外かつ、失効対象マイル数 > 0の場合、マイル移行一時情報を登録する。
        if (!StringUtil.isNullOrEmpty(osakaPitapaNumber) && invalidObjectMileAmount.longValue() > 0) {
            insertMileTransTempInfo(memberControlNumber, memCtrlNumBrNum, osakaPitapaNumber, invalidObjectMileAmount);
        }

        // アプリ会員状態コードが"A"かつ、マイル種別コードが「S04：OSAKA PiTaPaポイント自動移行」の場合、
        // プッシュ通知指示を登録。
        if (OpalCodeConstants.AplMemStatusCode.OP_AUTH_APL_MEM.equals(aplMemStatusCode)
                && OpalCodeConstants.MileCategoryCode.OP_POINT_AUTO_TRANS.equals(mileCategoryCode)) {
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
                Map<String, Integer> result = cm010002Component.insPushNoticeInformation(applicationMemberId,
                        applicationId, deviceId);
                // 出力データ件数(プッシュ通知情報)(登録)をカウントアップする。
                tempPushNoticeIfoOutputCnt += result.get("PUSH_NOTICE_INFORMATION_OUTPUT_CNT");
                // 出力データ件数(プッシュ通知送信先情報)(登録)をカウントアップする。
                tempPushNoticeDestInfoOutputCnt += result.get("PUSH_NOTICE_DEST_INFO_OUTPUT_CNT");
            } else {
                // デバイスIDがプッシュ通知可能のデバイスID以外の場合、プッシュ通知不可ワーニングメッセージをログに出力する
                Message message = MessageUtil.createMessage(MessageLevel.WARN, "MB136A0115",
                        String.valueOf(applicationMemberId), String.valueOf(deviceId));
                LOG.logWarn(message.formatMessage());
            }
        }

        // マイル失効対象一時情報更新
        updateMileInvObjTempInfo(applicationMemberId);

        // アプリ会員状態コードが"A"かつ、マイル種別コードが「S04：OSAKA PiTaPaポイント自動移行」の場合、
        // メール配信情報ファイル（CSV）を出力する。
        if (OpalCodeConstants.AplMemStatusCode.OP_AUTH_APL_MEM.equals(aplMemStatusCode)
                && OpalCodeConstants.MileCategoryCode.OP_POINT_AUTO_TRANS.equals(mileCategoryCode)) {

            // 配信可否チェック
            // "1"(配信停止)の場合
            if (OpalCodeConstants.MailDeliverStatusDivision.MAIL_DELIVER_STATUS_DIVISION_1
                    .equals(mailDeliverStatusDivision)) {
                // 送信不可ワーニングとして、ワーニングメッセージをログに出力する。
                Message message = MessageUtil.createMessage(MessageLevel.WARN, "MB136A0114",
                        String.valueOf(applicationMemberId));
                LOG.logWarn(message.formatMessage());

            } else {
                // 本処理が初回の場合、または前回のループ処理でメール配信情報ファイルへの出力件数が出力上限件数に達した場合、
                // メール配信情報ファイル（CSV）を生成する。
                if (outputDataCount == 0) {
                    // メール配信情報ファイル名称は、ファイルID_処理ID_システム時刻(yyyyMMddhhmmssSSS)とする。
                    StringBuffer fileName = new StringBuffer(FILE_ID);
                    fileName.append(OpalDefaultConstants.UNDER_MINUS_MARK_MILE_USE);
                    fileName.append(BATCH_PROCESS_ID);
                    fileName.append(OpalDefaultConstants.UNDER_MINUS_MARK_MILE_USE);
                    fileName.append(SystemTimeUtil.getDateTimeMillisString());
                    deliverFileName = fileName.toString();
                    // 出力データ件数(メール一括配信情報)(登録)をカウントアップする。
                    tempMailPackDeliverInfoOutputCnt++;

                    // メール配信情報ファイル生成
                    FileRecordWriterHolder.open(OUTPUT_PATH, deliverFileName, FORMAT_ID);
                    // ヘッダレコード出力
                    writeHeaderRecord(deliverFileName);
                    // メール配信情報レコード出力
                    writeRecord("Data", mailAddressMap, deliverFileName);
                    // メール配信情報ファイルを生成した場合、マイル移行のメール一括配信情報を登録する。
                    insMailPackDeliverInfo();

                    // 出力データ件数(メール配信情報ファイル)をカウントアップする。
                    outputDataCount++;
                    tempDeliverFileOutputCnt++;

                } else if (outputDataCount < Integer.parseInt(SystemRepository.getString(OUTPUT_MAX_COUNT)) - 1) {
                    // メール配信情報レコード出力
                    writeRecord("Data", mailAddressMap, deliverFileName);
                    // 出力データ件数(メール配信情報ファイル)をカウントアップする。
                    outputDataCount++;
                    tempDeliverFileOutputCnt++;
                } else {
                    // メール配信情報ファイルへの出力件数が出力上限件数に達した場合、メール配信情報ファイル（CSV）を生成する。
                    // メール配信情報レコード出力
                    writeRecord("Data", mailAddressMap, deliverFileName);
                    // 出力データ件数(メール配信情報ファイル)を初期化する。
                    outputDataCount = 0;
                    tempDeliverFileOutputCnt++;
                    // メール配信情報ファイルを出力する。
                    FileRecordWriterHolder.close(OUTPUT_PATH, deliverFileName);
                }
            }
        }

        // 入力データが最後件の場合
        if (currentDataCount == intputData && outputDataCount > 0) {
            // メール配信情報ファイルを出力する。
            FileRecordWriterHolder.close(OUTPUT_PATH, deliverFileName);
        }

        // コミット件数取得
        if (currentDataCount == intputData || currentDataCount % commitInterval == 0) {
            updateMileBalance = tempUpdateMileBalance;
            insertMileHistory = tempInsertMileHistory;
            insertMileTransTemp = tempInsertMileTransTemp;
            updateMileInvalidTemp = tempUpdateMileInvalidTemp;
            pushNoticeIfoOutputCnt = tempPushNoticeIfoOutputCnt;
            pushNoticeDestInfoOutputCnt = tempPushNoticeDestInfoOutputCnt;
            mailPackDeliverInfoOutputCnt = tempMailPackDeliverInfoOutputCnt;
            deliverFileOutputCnt = tempDeliverFileOutputCnt;
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

        // 出力データ件数(マイル残高情報)(更新)をログに出力
        writeLog("MB136A0104", Integer.valueOf(updateMileBalance));

        // 出力データ件数(マイル履歴)(登録)をログに出力
        writeLog("MB136A0105", Integer.valueOf(insertMileHistory));

        // 出力データ件数(マイル移行一時情報)(登録)をログに出力
        writeLog("MB136A0106", Integer.valueOf(insertMileTransTemp));

        // 出力データ件数(マイル失効対象一時情報)(更新)をログに出力
        writeLog("MB136A0107", Integer.valueOf(updateMileInvalidTemp));

        // 出力データ件数(プッシュ通知情報)(登録)をログに出力する。
        writeLog("MB136A0110", Integer.valueOf(pushNoticeIfoOutputCnt));

        // 出力データ件数(プッシュ通知送信先情報)(登録)をログに出力する。
        writeLog("MB136A0111", Integer.valueOf(pushNoticeDestInfoOutputCnt));

        // 出力データ件数(メール一括配信情報)(登録)をログに出力する。
        writeLog("MB136A0112", Integer.valueOf(mailPackDeliverInfoOutputCnt));

        // 出力データ件数(メール配信情報ファイル)をログに出力する。
        writeLog("MB136A0113", Integer.valueOf(deliverFileOutputCnt));
    }

    /**
     * {@inneritDoc} マイル残高情報論理削除
     * <p/>
     *
     * @param applicationMemberId
     *            アプリ会員ID
     */
    private void updateMileBalanceInfo(Long applicationMemberId) {

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

        // マイル残高情報論理削除用のSQL条件を設定する。
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("UPDATE_MILE_BALANCE_DEL");
        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("deletedFlg", OpalDefaultConstants.DELETED_FLG_1);
        condition.put("updateUserId", BATCH_PROCESS_ID);
        condition.put("updateDateTime", SystemTimeUtil.getTimestamp());
        condition.put("applicationMemberId", applicationMemberId);
        condition.put("mileInvalidFromMonth", mileInvalidFromMonth);
        condition.put("mileInvalidToMonth", mileInvalidToMonth);

        // マイル残高情報を論理削除する。
        tempUpdateMileBalance += statement.executeUpdateByMap(condition);
    }

    /**
     * {@inneritDoc} マイル履歴登録
     * <p/>
     *
     * @param applicationMemberId
     *            アプリ会員ID
     * @param invalidObjectMileAmount
     *            失効対象マイル数
     * @param mileCategoryCode
     *            マイル種別コード
     */
    private void insertMileHistoryInfo(Long applicationMemberId, Long invalidObjectMileAmount,
            String mileCategoryCode) {

        // マイル履歴IDを採番する。
        Long mileHistoryId = IdGeneratorUtil.generateMileHistoryId();

        // マイル履歴登録用のSQL条件を設定する。
        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("mileHistoryId", mileHistoryId);
        condition.put("applicationMemberId", applicationMemberId);
        condition.put("mileAddSubRcptNum", null);
        condition.put("mileCategoryCode", mileCategoryCode);
        condition.put("invalidObjectMileAmount", invalidObjectMileAmount);
        condition.put("mileHistoryRegistDate", SystemTimeUtil.getDateString());
        condition.put("insertUserId", BATCH_PROCESS_ID);
        condition.put("insertDateTime", SystemTimeUtil.getTimestamp());
        condition.put("updateUserId", BATCH_PROCESS_ID);
        condition.put("updateDateTime", SystemTimeUtil.getTimestamp());
        condition.put("deletedFlg", OpalDefaultConstants.DELETED_FLG_0);
        condition.put("deletedDate", deletedDate);

        // マイル履歴を登録する。
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("INSERT_MILE_HISTORY_INFORMATION");
        tempInsertMileHistory += statement.executeUpdateByMap(condition);
    }

    /**
     * {@inneritDoc} マイル移行一時情報登録
     * <p/>
     *
     * @param memberControlNumber
     *            会員管理番号
     * @param memCtrlNumBrNum
     *            会員管理番号枝番
     * @param osakaPitapaNumber
     *            OP番号
     * @param invalidObjectMileAmount
     *            失効対象マイル数
     */
    private void insertMileTransTempInfo(String memberControlNumber, String memCtrlNumBrNum, String osakaPitapaNumber,
            Long invalidObjectMileAmount) {

        // マイル移行一時情報登録用のSQL条件を設定する。
        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("memberControlNumber", memberControlNumber);
        condition.put("memCtrlNumBrNum", memCtrlNumBrNum);
        condition.put("osakaPitapaNumber", osakaPitapaNumber);
        condition.put("invalidObjectMileAmount", invalidObjectMileAmount);
        condition.put("mileTransitionDivision", OpalCodeConstants.MileTransiTionDivision.MILE_TRANSITION_DIVISION_1);
        condition.put("insertUserId", BATCH_PROCESS_ID);
        condition.put("insertDateTime", SystemTimeUtil.getTimestamp());
        condition.put("updateUserId", BATCH_PROCESS_ID);
        condition.put("updateDateTime", SystemTimeUtil.getTimestamp());
        condition.put("deletedFlg", OpalDefaultConstants.DELETED_FLG_0);
        condition.put("deletedDate", null);

        // マイル移行一時情報を登録する。
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("INSERT_MILE_TRANS_TEMP_INFO");
        tempInsertMileTransTemp += statement.executeUpdateByMap(condition);
    }

    /**
     * {@inneritDoc} マイル失効対象一時情報更新
     * <p/>
     *
     * @param applicationMemberId
     *            アプリ会員ID
     */
    private void updateMileInvObjTempInfo(Long applicationMemberId) {

        // マイル失効対象一時情報更新用のSQL条件を設定する。
        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("processedFlag", OpalDefaultConstants.PROCESSED_FLAG_1);
        condition.put("updateUserId", BATCH_PROCESS_ID);
        condition.put("updateDateTime", SystemTimeUtil.getTimestamp());
        condition.put("applicationMemberId", applicationMemberId);

        // マイル失効対象一時情報を更新する。
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("UPDATE_MILE_INV_OBJ_DEL");
        tempUpdateMileInvalidTemp += statement.executeUpdateByMap(condition);
    }

    /**
     * ヘッダレコード出力
     *
     * @param fileId
     *            ファイルID
     */
    private void writeHeaderRecord(String fileId) {

        Map<String, String> header = new HashMap<String, String>();
        // メールアドレス
        header.put("mailAddressHead", "メールアドレス");

        // ヘッダレコード出力
        writeRecord("Header", header, fileId);
    }

    /**
     * ファイル出力処理。 指定されたMapを1レコードとしてファイル出力を行う。
     *
     * @param recordType
     *            レコードタイプを表す文字列
     * @param record
     *            1レコードの情報を格納したMap
     * @param fileId
     *            ファイルID
     */
    private void writeRecord(String recordType, Map<String, ?> record, String fileId) {

        FileRecordWriterHolder.write(recordType, record, OUTPUT_PATH, fileId);
    }

    /**
     * マイル移行のメール一括配信情報登録
     */
    private void insMailPackDeliverInfo() {

        // システム日付
        String sysDate = DateUtil.formatDate(SystemTimeUtil.getDateString(), OpalDefaultConstants.DATE_FORMAT);

        // 配信日時
        StringBuilder deliverDate = new StringBuilder();
        deliverDate.append(sysDate);
        deliverDate.append(OpalDefaultConstants.BLANK);
        deliverDate.append(SystemRepository.getString("mail_deliver_instr_deliver_time"));

        // マイル移行のメール一括配信指示を登録する。
        CM010001Component cm010001Component = new CM010001Component();
        cm010001Component.insMailPackDeliverInfo(BATCH_PROCESS_ID,
                OpalCodeConstants.MailDeliverType.MAIL_DELIVER_TYPE_2, OpalDefaultConstants.MAIL_TEMP_MILE_TRANS_NOTICE,
                deliverFileName.substring(OpalDefaultConstants.MAIL_FILE_START, OpalDefaultConstants.MAIL_FILE_END),
                DateConvertUtil.stringToDate(deliverDate.toString(), OpalDefaultConstants.DATE_TIME_FORMAT));

    }

}
