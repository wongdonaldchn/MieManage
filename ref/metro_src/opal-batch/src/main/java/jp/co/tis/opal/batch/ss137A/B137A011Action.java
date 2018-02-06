package jp.co.tis.opal.batch.ss137A;

import java.util.HashMap;
import java.util.Map;

import nablarch.common.dao.UniversalDao;
import nablarch.common.io.FileRecordWriterHolder;
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

import jp.co.tis.opal.common.component.CM010001Component;
import jp.co.tis.opal.common.component.CM010002Component;
import jp.co.tis.opal.common.component.CM010005Component;
import jp.co.tis.opal.common.constants.OpalCodeConstants;
import jp.co.tis.opal.common.constants.OpalDefaultConstants;
import jp.co.tis.opal.common.entity.MileAdjustInstrInfoEntity;
import jp.co.tis.opal.common.entity.MileBalanceInfoEntity;
import jp.co.tis.opal.common.utility.DateConvertUtil;

/**
 * B137A011:マイル調整のアクションクラス。
 *
 * @author 曹
 * @since 1.0
 */
public class B137A011Action extends BatchAction<SqlRow> {

    /** 入力データ件数(マイル調整指示情報) */
    private int intputCountMileAdjust;

    /** 出力データ件数(マイル残高情報)(登録) */
    private int insertCountMileBalance;
    private int tempInsertCountMileBalance;

    /** 出力データ件数(マイル残高情報)(更新) */
    private int updateCountMileBalance;
    private int tempUpdateCountMileBalance;

    /** 出力データ件数(マイル履歴情報)(登録) */
    private int insertCountMileHistory;
    private int tempInsertCountMileHistory;

    /** 出力データ件数(マイル調整指示情報)(更新) */
    private int updateCountMileAdjust;
    private int tempUpdateCountMileAdjust;

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

    /** 処理対象年月日 */
    private String processYmd;

    /** アプリ会員ID:加算 */
    private Long aplMemIdAdd;

    /** アプリ会員ID:減算 */
    private Long aplMemIdSub;

    /** プッシュ通知情報登録の共通コンポーネント:加算 */
    private CM010002Component cm010002ComponentAdd;

    /** プッシュ通知情報登録の共通コンポーネント:減算 */
    private CM010002Component cm010002ComponentSub;

    /** バッチ処理ID */
    private static final String BATCH_PROCESS_ID = "B137A011";

    /** 起動パラメータ:処理対象年月日 */
    private static final String PROCESS_YMD = "processYmd";

    /** 年月日加算用 */
    private static final int ADD_DATE_1_MINUS = -1;

    /** 出力ファイルID：A151A001(メール配信情報ファイル) */
    private static final String FILE_ID = "A151A001";

    /** フォーマット定義ファイルID：A151A001 */
    private static final String FORMAT_ID = "A151A001";

    /** メール配信情報ファイルの出力先ディレクトリ */
    private static final String OUTPUT_PATH = "mail";

    /** メール配信一括指示_マイル調整_出力上限件数 */
    private static final String OUTPUT_MAX_COUNT = "mail_del_lump_instr_mile_adjust_max_count";

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
        intputCountMileAdjust = 0;
        insertCountMileBalance = 0;
        tempInsertCountMileBalance = 0;
        updateCountMileBalance = 0;
        tempUpdateCountMileBalance = 0;
        insertCountMileHistory = 0;
        tempInsertCountMileHistory = 0;
        updateCountMileAdjust = 0;
        tempUpdateCountMileAdjust = 0;
        currentDataCount = 0;
        outputDataCount = 0;
        aplMemIdAdd = null;
        aplMemIdSub = null;
        pushNoticeIfoOutputCnt = 0;
        tempPushNoticeIfoOutputCnt = 0;
        pushNoticeDestInfoOutputCnt = 0;
        tempPushNoticeDestInfoOutputCnt = 0;
        mailPackDeliverInfoOutputCnt = 0;
        tempMailPackDeliverInfoOutputCnt = 0;
        deliverFileOutputCnt = 0;
        tempDeliverFileOutputCnt = 0;

        // 処理対象年月日取得
        processYmd = command.getParamMap().get(PROCESS_YMD);
        if (StringUtil.isNullOrEmpty(processYmd)) {
            processYmd = DateUtil.addDay(SystemTimeUtil.getDateString(), ADD_DATE_1_MINUS);
        }

        // プッシュ通知可能のデバイスID取得
        sendableDeviceId = SystemRepository.getString("push_notice_sendable_device_id").split(",");
    }

    /**
     * {@inneritDoc}
     * <p/>
     * マイル調整指示情報から処理対象のレコードを読み込む。
     */
    @Override
    public DataReader<SqlRow> createReader(ExecutionContext ctx) {

        // 入力データ件数用のSQLの条件を設定する
        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("mileAdjustInstrDate", processYmd);
        condition.put("mileAdjustStatusDivi", OpalCodeConstants.MileAdjustStatusDivi.MILE_ADJUST_STATUS_DIVI_0);

        // 処理対象レコード件数を取得
        intputCountMileAdjust = countByParameterizedSql("SELECT_MILE_ADJUST_INSTR_INFO", condition);
        // 入力データ件数をログに出力
        writeLog("MB137A0102", Integer.valueOf(intputCountMileAdjust));

        // 処理対象レコードが0件以外の場合
        if (intputCountMileAdjust > 0) {

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

            // プッシュ通知情報登録するための初期設定を行う(加算)
            cm010002ComponentAdd = new CM010002Component(BATCH_PROCESS_ID,
                    OpalCodeConstants.PushNoticeDivision.PUSH_NOTICE_DIVISION_2,
                    OpalCodeConstants.PushNoticeType.PUSH_NOTICE_TYPE_2,
                    OpalDefaultConstants.PUSH_TEMP_MILE_ADJUST_ADD_NOTICE, deliverTime.toString(), upperLimitNumber);

            // プッシュ通知情報登録するための初期設定を行う(減算)
            cm010002ComponentSub = new CM010002Component(BATCH_PROCESS_ID,
                    OpalCodeConstants.PushNoticeDivision.PUSH_NOTICE_DIVISION_2,
                    OpalCodeConstants.PushNoticeType.PUSH_NOTICE_TYPE_2,
                    OpalDefaultConstants.PUSH_TEMP_MILE_ADJUST_SUB_NOTICE, deliverTime.toString(), upperLimitNumber);

        }

        // マイル調整指示情報から処理対象のレコードを読み込む。
        DatabaseRecordReader reader = new DatabaseRecordReader();
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("SELECT_MILE_ADJUST_INSTR_INFO");
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
        // マイル調整指示ID
        Long mileAdjustInstrId = inputData.getLong("MILE_ADJUST_INSTR_ID");
        // アプリ会員ID
        Long applicationMemberId = inputData.getLong("APPLICATION_MEMBER_ID");
        // アプリID
        String applicationId = inputData.getString("APPLICATION_ID");
        // デバイスID
        String deviceId = inputData.getString("DEVICE_ID");
        // アプリ会員状態コード
        String aplMemStatusCode = inputData.getString("APPLICATION_MEMBER_STATUS_CODE");
        // マイル種別コード
        String mileCategoryCode = inputData.getString("MILE_CATEGORY_CODE");
        // 調整マイル数
        Long adjustMileAmount = inputData.getLong("ADJUST_MILE_AMOUNT");
        // メール配信状態区分
        String mailDeliverStatusDivision = inputData.getString("MAIL_DELIVER_STATUS_DIVISION");
        // メールアドレス
        String mailAddress = inputData.getString("MAIL_ADDRESS");
        Map<String, Object> mailAddressMap = new HashMap<String, Object>();
        mailAddressMap.put("mailAddress", mailAddress);

        // マイル残高情報排他制御（アプリ会員単位）
        Map<String, Object> mileBalanceInfoExclusive = new HashMap<String, Object>();
        mileBalanceInfoExclusive.put("applicationMemberId", applicationMemberId);
        UniversalDao.findAllBySqlFile(MileBalanceInfoEntity.class, "SELECT_MILE_BALANCE_INFO",
                mileBalanceInfoExclusive);
        // マイル調整指示情報排他制御
        Map<String, Object> mileAdjustInstrInfoExclusive = new HashMap<String, Object>();
        mileAdjustInstrInfoExclusive.put("mileAdjustInstrId", mileAdjustInstrId);
        MileAdjustInstrInfoEntity mileAdjustInstrInfo = UniversalDao.findBySqlFile(MileAdjustInstrInfoEntity.class,
                "SELECT_MILE_ADJUST_INSTR_INFO", mileAdjustInstrInfoExclusive);

        // CM010005：マイル計算共通コンポーネントを呼び出す。
        CM010005Component cm010005Component = new CM010005Component();
        if (OpalCodeConstants.MileCategoryCode.MILE_ADJUST_ADD.equals(mileCategoryCode)) {
            // マイル種別コードが"A06"(マイル調整:加算)の場合、マイル加算を行う。
            String result = cm010005Component.addMile(applicationMemberId, null, mileCategoryCode, adjustMileAmount,
                    BATCH_PROCESS_ID, null);

            if (OpalDefaultConstants.MILE_BALANCE_UPDATE_FLG_0.equals(result)) {
                // 出力データ件数(マイル残高情報)(登録)をカウントアップする。
                tempInsertCountMileBalance++;
            } else if (OpalDefaultConstants.MILE_BALANCE_UPDATE_FLG_1.equals(result)) {
                // 出力データ件数(マイル残高情報)(更新)をカウントアップする。
                tempUpdateCountMileBalance++;
            }
            // アプリ会員状態コードが"A"（OP認証済）、または、"D"（未OP認証）の場合、プッシュ通知情報登録
            if (OpalCodeConstants.AplMemStatusCode.OP_AUTH_APL_MEM.equals(aplMemStatusCode)
                    || OpalCodeConstants.AplMemStatusCode.NOT_OP_MEM.equals(aplMemStatusCode)) {
                // アプリ会員IDが変わる場合、プッシュ通知情報登録
                if (aplMemIdAdd == null || !aplMemIdAdd.equals(applicationMemberId)) {
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
                        // プッシュ通知情報登録
                        Map<String, Integer> resultAdd = cm010002ComponentAdd
                                .insPushNoticeInformation(applicationMemberId, applicationId, deviceId);
                        // 出力データ件数(プッシュ通知情報)(登録)をカウントアップする。
                        tempPushNoticeIfoOutputCnt += resultAdd.get("PUSH_NOTICE_INFORMATION_OUTPUT_CNT");
                        // 出力データ件数(プッシュ通知送信先情報)(登録)をカウントアップする。
                        tempPushNoticeDestInfoOutputCnt += resultAdd.get("PUSH_NOTICE_DEST_INFO_OUTPUT_CNT");
                    } else {
                        // デバイスIDがプッシュ通知可能のデバイスID以外の場合、プッシュ通知不可ワーニングメッセージをログに出力する
                        Message message = MessageUtil.createMessage(MessageLevel.WARN, "MB136A0115",
                                String.valueOf(applicationMemberId), String.valueOf(deviceId));
                        LOG.logWarn(message.formatMessage());
                    }
                }
            }
        } else if (OpalCodeConstants.MileCategoryCode.MILE_ADJUST_SUB.equals(mileCategoryCode)) {
            // マイル残高情報TBLから、マイル残高合計を取得する。
            Long sumMileBalance = getSumMileBalance(applicationMemberId);
            // マイル残高合計チェック
            if (sumMileBalance.longValue() < adjustMileAmount.longValue()) {
                // マイル残高不足のメッセージをログに出力
                writeLog("MB137A0101", String.valueOf(applicationMemberId));

                return new Success();
            } else {
                // マイル種別コードが"S02"(マイル調整:減算)の場合、マイル減算を行う。
                Map<String, String> result = cm010005Component.subMile(applicationMemberId, null, mileCategoryCode,
                        adjustMileAmount, BATCH_PROCESS_ID);
                // 減算したマイル残高情報出力件数で、出力データ件数(マイル残高情報)(更新)をカウントアップする。
                tempUpdateCountMileBalance += Integer.parseInt(result.get("mileBalanceOutputCnt"));

                // アプリ会員状態コードが"A"（OP認証済）、または、"D"（未OP認証）の場合、プッシュ通知情報登録
                if (OpalCodeConstants.AplMemStatusCode.OP_AUTH_APL_MEM.equals(aplMemStatusCode)
                        || OpalCodeConstants.AplMemStatusCode.NOT_OP_MEM.equals(aplMemStatusCode)) {
                    // アプリ会員IDが変わる場合、プッシュ通知情報登録
                    if (aplMemIdSub == null || !aplMemIdSub.equals(applicationMemberId)) {
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
                            Map<String, Integer> resultSub = cm010002ComponentSub
                                    .insPushNoticeInformation(applicationMemberId, applicationId, deviceId);
                            aplMemIdSub = applicationMemberId;
                            // 出力データ件数(プッシュ通知情報)(登録)をカウントアップする。
                            tempPushNoticeIfoOutputCnt += resultSub.get("PUSH_NOTICE_INFORMATION_OUTPUT_CNT");
                            // 出力データ件数(プッシュ通知送信先情報)(登録)をカウントアップする。
                            tempPushNoticeDestInfoOutputCnt += resultSub.get("PUSH_NOTICE_DEST_INFO_OUTPUT_CNT");
                        } else {
                            // デバイスIDがプッシュ通知可能のデバイスID以外の場合、プッシュ通知不可ワーニングメッセージをログに出力する
                            Message message = MessageUtil.createMessage(MessageLevel.WARN, "MB136A0115",
                                    String.valueOf(applicationMemberId), String.valueOf(deviceId));
                            LOG.logWarn(message.formatMessage());
                        }
                    }
                }
            }
        }

        // 出力データ件数(マイル履歴情報)(登録)をカウントアップする。
        tempInsertCountMileHistory++;

        // マイル調整状況区分
        mileAdjustInstrInfo.setMileAdjustStatusDivi(OpalCodeConstants.MileAdjustStatusDivi.MILE_ADJUST_STATUS_DIVI_1);
        // 最終更新者ID
        mileAdjustInstrInfo.setUpdateUserId(BATCH_PROCESS_ID);
        // 最終更新日時
        mileAdjustInstrInfo.setUpdateDateTime(SystemTimeUtil.getTimestamp());
        // マイル調整指示情報更新
        UniversalDao.update(mileAdjustInstrInfo);
        // 出力データ件数(マイル調整指示情報)(更新)をカウントアップする。
        tempUpdateCountMileAdjust++;

        // マイル種別コードが"A06"(マイル調整:加算)の場合、
        if (OpalCodeConstants.MileCategoryCode.MILE_ADJUST_ADD.equals(mileCategoryCode)) {

            // アプリ会員状態コードが"A"（OP認証済）、または、"D"（未OP認証）の場合、
            if (OpalCodeConstants.AplMemStatusCode.OP_AUTH_APL_MEM.equals(aplMemStatusCode)
                    || OpalCodeConstants.AplMemStatusCode.NOT_OP_MEM.equals(aplMemStatusCode)) {
                // アプリ会員IDが変わる場合、メール配信情報ファイル（CSV）を出力する
                if (aplMemIdAdd == null || !aplMemIdAdd.equals(applicationMemberId)) {

                    // 配信可否チェック
                    // "1"(配信停止)の場合
                    if (OpalCodeConstants.MailDeliverStatusDivision.MAIL_DELIVER_STATUS_DIVISION_1
                            .equals(mailDeliverStatusDivision)) {
                        // 送信不可ワーニングとして、ワーニングメッセージをログに出力する。
                        Message message = MessageUtil.createMessage(MessageLevel.WARN, "MB137A0111",
                                String.valueOf(applicationMemberId));
                        LOG.logWarn(message.formatMessage());

                    } else {
                        // 本処理が初回の場合、メール配信情報ファイル（CSV）を生成する。
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
                            // メール配信情報ファイルを生成した場合、マイル加算調整のメール一括配信情報を登録する。
                            insMailPackDeliverInfo();

                            // 出力データ件数(メール配信情報ファイル)をカウントアップする。
                            outputDataCount++;
                            tempDeliverFileOutputCnt++;

                        } else if (outputDataCount < Integer.parseInt(SystemRepository.getString(OUTPUT_MAX_COUNT))
                                - 1) {
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
                    aplMemIdAdd = applicationMemberId;
                }
            }
        }
        // 入力データが最後件の場合
        if (currentDataCount == intputCountMileAdjust && outputDataCount > 0) {
            // メール配信情報ファイルを出力する。
            FileRecordWriterHolder.close(OUTPUT_PATH, deliverFileName);
        }

        // コミット件数取得
        if (currentDataCount == intputCountMileAdjust || currentDataCount % commitInterval == 0) {
            insertCountMileBalance = tempInsertCountMileBalance;
            updateCountMileBalance = tempUpdateCountMileBalance;
            insertCountMileHistory = tempInsertCountMileHistory;
            updateCountMileAdjust = tempUpdateCountMileAdjust;
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

        // 出力データ件数(マイル残高情報)(登録)をログに出力
        writeLog("MB137A0104", Integer.valueOf(insertCountMileBalance));
        // 出力データ件数(マイル残高情報)(更新)をログに出力
        writeLog("MB137A0105", Integer.valueOf(updateCountMileBalance));
        // 出力データ件数(マイル履歴情報)(登録)をログに出力
        writeLog("MB137A0106", Integer.valueOf(insertCountMileHistory));
        // 出力データ件数(マイル調整指示情報)(更新)をログに出力
        writeLog("MB137A0103", Integer.valueOf(updateCountMileAdjust));
        // 出力データ件数(プッシュ通知情報)(登録)をログに出力する。
        writeLog("MB137A0107", Integer.valueOf(pushNoticeIfoOutputCnt));
        // 出力データ件数(プッシュ通知送信先情報)(登録)をログに出力する。
        writeLog("MB137A0108", Integer.valueOf(pushNoticeDestInfoOutputCnt));
        // 出力データ件数(メール一括配信情報)(登録)をログに出力する。
        writeLog("MB137A0109", Integer.valueOf(mailPackDeliverInfoOutputCnt));
        // 出力データ件数(メール配信情報ファイル)をログに出力する。
        writeLog("MB137A0110", Integer.valueOf(deliverFileOutputCnt));
    }

    /**
     * {@inneritDoc} マイル残高情報TBLから、マイル残高合計を取得する。
     * <p/>
     *
     * @param applicationMemberId
     *            アプリ会員ID
     * @return sumMileBalance マイル残高合計
     */
    private Long getSumMileBalance(Long applicationMemberId) {

        StringBuilder stringForFromMonth = new StringBuilder();
        // システム日付の月日を取得
        String sysDate = SystemTimeUtil.getDateString().substring(4, 8);
        if (sysDate.compareTo(OpalDefaultConstants.MILE_INVALID_DATE) >= 0) {
            // システム日付が4/1以降の場合、開始年月＝今年の3月
            stringForFromMonth.append(DateConvertUtil.getSysYear());
            stringForFromMonth.append(OpalDefaultConstants.MILE_INVALID_FROM_MONTH);

        } else {
            // システム日付が3/31以前の場合、開始年月＝昨年の3月
            stringForFromMonth.append(Integer.parseInt(DateConvertUtil.getSysYear()) - 1);
            stringForFromMonth.append(OpalDefaultConstants.MILE_INVALID_FROM_MONTH);
        }
        // 開始年月を取得
        String objectYearMonth = stringForFromMonth.toString();

        // マイル残高合計取得用SQL条件を設定する。
        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("applicationMemberId", applicationMemberId);
        condition.put("objectYearMonth", objectYearMonth);
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("SELECT_SUM_MILE_BALANCE");

        // マイル残高合計を取得する。
        SqlRow mileBalanceData = statement.retrieve(condition).get(0);

        // マイル残高合計がnullの場合、「0」に設定する。
        if (StringUtil.isNullOrEmpty(mileBalanceData.getString("SUM_MILE_BALANCE"))) {
            return OpalDefaultConstants.MILE_BALANCE_ZERO;
        }

        return mileBalanceData.getLong("SUM_MILE_BALANCE");
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
     * マイル加算調整のメール一括配信情報登録
     */
    private void insMailPackDeliverInfo() {

        // システム日付
        String sysDate = DateUtil.formatDate(SystemTimeUtil.getDateString(), OpalDefaultConstants.DATE_FORMAT);

        // 配信日時
        StringBuilder deliverDate = new StringBuilder();
        deliverDate.append(sysDate);
        deliverDate.append(OpalDefaultConstants.BLANK);
        deliverDate.append(SystemRepository.getString("mail_deliver_instr_deliver_time"));

        // マイル加算調整のメール一括配信指示を登録する。
        CM010001Component cm010001Component = new CM010001Component();
        cm010001Component.insMailPackDeliverInfo(BATCH_PROCESS_ID,
                OpalCodeConstants.MailDeliverType.MAIL_DELIVER_TYPE_2,
                OpalDefaultConstants.MAIL_TEMP_MILE_ADJUST_ADD_NOTICE,
                deliverFileName.substring(OpalDefaultConstants.MAIL_FILE_START, OpalDefaultConstants.MAIL_FILE_END),
                DateConvertUtil.stringToDate(deliverDate.toString(), OpalDefaultConstants.DATE_TIME_FORMAT));

    }
}
