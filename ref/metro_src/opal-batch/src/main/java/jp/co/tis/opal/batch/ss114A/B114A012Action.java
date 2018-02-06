package jp.co.tis.opal.batch.ss114A;

import java.util.HashMap;
import java.util.Map;

import nablarch.common.dao.EntityList;
import nablarch.common.dao.UniversalDao;
import nablarch.core.date.SystemTimeUtil;
import nablarch.core.db.statement.ParameterizedSqlPStatement;
import nablarch.core.db.statement.SqlPStatement;
import nablarch.core.db.statement.SqlRow;
import nablarch.core.log.app.FailureLogUtil;
import nablarch.core.repository.SystemRepository;
import nablarch.fw.DataReader;
import nablarch.fw.ExecutionContext;
import nablarch.fw.Result;
import nablarch.fw.Result.Success;
import nablarch.fw.action.BatchAction;
import nablarch.fw.launcher.CommandLine;
import nablarch.fw.reader.DatabaseRecordReader;
import nablarch.fw.results.TransactionAbnormalEnd;

import jp.co.tis.opal.common.component.CM010004Component;
import jp.co.tis.opal.common.constants.OpalCodeConstants;
import jp.co.tis.opal.common.constants.OpalDefaultConstants;
import jp.co.tis.opal.common.entity.AplMemInfoEntity;
import jp.co.tis.opal.common.entity.OpMemInfoEntity;
import jp.co.tis.opal.common.entity.PartnerMemServiceInfoEntity;
import jp.co.tis.opal.common.utility.IdGeneratorUtil;

/**
 * B114A012:OP会員情報更新アクションクラス。
 *
 * @author 趙
 * @since 1.0
 */
public class B114A012Action extends BatchAction<SqlRow> {

    /** 入力データ件数 */
    private int inputDataCount;

    /** 出力データ件数(OP会員情報)(登録) */
    private int insertOpMemInfoCount;

    /** 出力データ件数(アプリ会員情報)(登録) */
    private int insertAplMemInfoCount;

    /** 出力データ件数(OP会員情報)(更新) */
    private int updateOpMemInfoCount;

    /** 出力データ件数(アプリ会員情報)(更新) */
    private int updateAplMemInfoCount;

    /** 出力データ件数(パートナー会員サービス情報)(更新) */
    private int updatePartnerMemServiceInfoCount;

    /** 出力データ件数(主なご利用駅情報)(更新) */
    private int updateMainUseStaInfoCount;

    /** 出力データ件数(OP会員一時情報)(更新) */
    private int updateOpMemTempInfoCount;

    /** 出力データ件数(OP会員情報)(登録) */
    private int tempInsertOpMemInfoCount;

    /** 出力データ件数(アプリ会員情報)(登録) */
    private int tempinsertAplMemInfoCount;

    /** 出力データ件数(OP会員情報)(更新) */
    private int tempUpdateOpMemInfoCount;

    /** 出力データ件数(アプリ会員情報)(更新) */
    private int tempUpdateAplMemInfoCount;

    /** 出力データ件数(パートナー会員サービス情報)(更新) */
    private int tempUpdatePartnerMemServiceInfoCount;

    /** 出力データ件数(主なご利用駅情報)(更新) */
    private int tempUpdateMainUseStaInfoCount;

    /** 出力データ件数(OP会員一時情報)(更新) */
    private int tempUpdateOpMemTempInfoCount;

    /** バッチ処理ID */
    private static final String BATCH_PROCESS_ID = "B114A012";

    /** 処理データ件数 */
    private int currentDataCount;

    /** コミット間隔 */
    private int commitInterval;

    /**
     * {@inneritDoc}
     * <p/>
     * 事前処理
     */
    @Override
    protected void initialize(CommandLine command, ExecutionContext context) {

        // 処理ログの初期化
        inputDataCount = 0;
        tempInsertOpMemInfoCount = 0;
        tempinsertAplMemInfoCount = 0;
        tempUpdateOpMemInfoCount = 0;
        tempUpdateAplMemInfoCount = 0;
        tempUpdatePartnerMemServiceInfoCount = 0;
        tempUpdateMainUseStaInfoCount = 0;
        tempUpdateOpMemTempInfoCount = 0;

        // コミット間隔を取得
        commitInterval = Integer.valueOf(SystemRepository.getString("nablarch.loopHandler.commitInterval"));
    }

    /**
     * {@inneritDoc}
     * <p/>
     * OP会員一時情報から処理対象のレコードを読み込む。
     */
    @Override
    public DataReader<SqlRow> createReader(ExecutionContext ctx) {

        // 処理対象レコード件数を取得
        inputDataCount = countByStatementSql("SELECT_OP_MEM_TEMP_INFO");
        // 入力データ件数をログに出力
        writeLog("MB114A0107", Integer.valueOf(inputDataCount));

        // OP会員一時情報から処理対象のレコードを読み込む。
        DatabaseRecordReader reader = new DatabaseRecordReader();
        SqlPStatement statement = getSqlPStatement("SELECT_OP_MEM_TEMP_INFO");
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

        // データ連携区分
        String dataRelateDivision = inputData.getString("DATA_RELATE_DIVISION");
        // 会員管理番号
        String memberControlNumber = inputData.getString("MEMBER_CONTROL_NUMBER");
        // 会員管理番号枝番
        String memCtrlNumBrNum = inputData.getString("MEM_CTRL_NUM_BR_NUM");

        // OP会員番号存在フラグ
        boolean opExistFlag = true;

        // 取得したデータ連携区分が「1：新規追加」の場合、
        if (OpalCodeConstants.DataRelateDivision.NEW_ADD.equals(dataRelateDivision)) {
            // OP会員情報登録
            insertOpMemInfo(inputData);

            // アプリ会員情報登録
            insertAplMemInfo(inputData);

        } else if (OpalCodeConstants.DataRelateDivision.PROP_MODIFY.equals(dataRelateDivision)) {
            // 取得したデータ連携区分が「2：属性変更」の場合、
            // OP会員属性変更
            opExistFlag = modifyOpMemProp(inputData);
            if (!opExistFlag) {
                return new Success();
            }

        } else if (OpalCodeConstants.DataRelateDivision.WITHDRAW.equals(dataRelateDivision)) {
            // 取得したデータ連携区分が「3：退会（解約）」の場合、
            // OP会員退会
            opExistFlag = withdrawFromOpMem(memberControlNumber, memCtrlNumBrNum);
            if (!opExistFlag) {
                return new Success();
            }
        }

        // OP会員一時情報更新
        updateOpMemTempInfo(memberControlNumber, memCtrlNumBrNum);

        currentDataCount++;
        // コミット件数取得
        if (currentDataCount == inputDataCount || currentDataCount % commitInterval == 0) {
            insertOpMemInfoCount = tempInsertOpMemInfoCount;
            insertAplMemInfoCount = tempinsertAplMemInfoCount;
            updateOpMemInfoCount = tempUpdateOpMemInfoCount;
            updateAplMemInfoCount = tempUpdateAplMemInfoCount;
            updatePartnerMemServiceInfoCount = tempUpdatePartnerMemServiceInfoCount;
            updateMainUseStaInfoCount = tempUpdateMainUseStaInfoCount;
            updateOpMemTempInfoCount = tempUpdateOpMemTempInfoCount;
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

        // 出力データ件数(OP会員情報)(登録)をログに出力
        writeLog("MB114A0108", Integer.valueOf(insertOpMemInfoCount));
        // 出力データ件数(アプリ会員情報)(登録)をログに出力
        writeLog("MB114A0109", Integer.valueOf(insertAplMemInfoCount));
        // 出力データ件数(OP会員情報)(更新)をログに出力
        writeLog("MB114A0110", Integer.valueOf(updateOpMemInfoCount));
        // 出力データ件数(アプリ会員情報)(更新)をログに出力
        writeLog("MB114A0111", Integer.valueOf(updateAplMemInfoCount));
        // 出力データ件数(パートナー会員サービス情報)(更新)をログに出力
        writeLog("MB114A0113", Integer.valueOf(updatePartnerMemServiceInfoCount));
        // 出力データ件数(主なご利用駅情報)(更新)をログに出力
        writeLog("MB114A0116", Integer.valueOf(updateMainUseStaInfoCount));
        // 出力データ件数(OP会員一時情報)(更新)をログに出力
        writeLog("MB114A0114", Integer.valueOf(updateOpMemTempInfoCount));
    }

    /**
     * OP会員情報登録処理
     * <p/>
     * 取得したOP会員一時情報をOP会員情報TBLに登録する。
     *
     * @param inputData
     *            入力データ
     */
    private void insertOpMemInfo(SqlRow inputData) {

        // DBに登録する値を設定する。
        Map<String, Object> condition = new HashMap<String, Object>();

        // 会員管理番号
        condition.put("memberControlNumber", inputData.getString("MEMBER_CONTROL_NUMBER"));
        // 会員管理番号枝番
        condition.put("memCtrlNumBrNum", inputData.getString("MEM_CTRL_NUM_BR_NUM"));
        // OP番号
        condition.put("osakaPitapaNumber", inputData.getString("OSAKA_PITAPA_NUMBER"));
        // PiTaPa有効期限
        condition.put("pitapaExpirationDate", inputData.getString("PITAPA_EXPIRATION_DATE"));
        // カード種類
        condition.put("cardType", inputData.getString("CARD_TYPE"));
        // 生年月日
        condition.put("birthdate", inputData.getString("BIRTHDATE"));
        // 性別コード
        condition.put("sexCode", inputData.getString("SEX_CODE"));
        // 自宅電話番号
        condition.put("telephoneNumber", inputData.getString("TELEPHONE_NUMBER"));
        // 携帯電話番号
        condition.put("cellphoneNumber", inputData.getString("CELLPHONE_NUMBER"));
        // 郵便番号
        condition.put("postcode", inputData.getString("POSTCODE"));
        // サービス種別
        condition.put("serviceCategory", inputData.getString("SERVICE_CATEGORY"));
        // 登録駅1
        condition.put("registStation1", inputData.getString("REGIST_STATION_1"));
        // 登録駅2
        condition.put("registStation2", inputData.getString("REGIST_STATION_2"));
        // 続柄コード
        condition.put("relationshipCode", inputData.getString("RELATIONSHIP_CODE"));
        // OP退会フラグ
        condition.put("osakaPitapaWithdrawFlag", OpalDefaultConstants.OSAKA_PITAPA_WITHDRAW_FLAG_0);
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
        // バージョン番号
        condition.put("version", OpalDefaultConstants.VERSION);

        // データを登録する
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("INSERT_OP_MEM_INFO");
        tempInsertOpMemInfoCount += statement.executeUpdateByMap(condition);
    }

    /**
     * アプリ会員情報登録処理
     * <p/>
     * 取得したOP会員一時情報をアプリ会員情報TBLに登録する。
     *
     * @param inputData
     *            入力データ
     */
    private void insertAplMemInfo(SqlRow inputData) {

        // DBに登録する値を設定する。
        Map<String, Object> condition = new HashMap<String, Object>();

        // アプリ会員ID
        condition.put("applicationMemberId", IdGeneratorUtil.generateApplicationMemberId());
        // 会員管理番号
        condition.put("memberControlNumber", inputData.getString("MEMBER_CONTROL_NUMBER"));
        // 会員管理番号枝番
        condition.put("memCtrlNumBrNum", inputData.getString("MEM_CTRL_NUM_BR_NUM"));
        // OP番号
        condition.put("osakaPitapaNumber", inputData.getString("OSAKA_PITAPA_NUMBER"));
        // アプリ会員状態コード
        condition.put("applicationMemberStatusCode", OpalCodeConstants.AplMemStatusCode.WITHOUT_RIDE_MILE_OP_MEM);
        // OP認証フラグ
        condition.put("osakaPitapaAuthenticateFlag", OpalCodeConstants.OPAuthenticateFlag.OP_AUTH_FLAG_0);
        // OP退会フラグ
        condition.put("osakaPitapaWithdrawFlag", OpalDefaultConstants.OSAKA_PITAPA_WITHDRAW_FLAG_0);
        // OP認証回数
        condition.put("opAuthTimes", OpalDefaultConstants.OP_AUTH_TIMES_0);
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
        // バージョン番号
        condition.put("version", OpalDefaultConstants.VERSION);

        // データを登録する
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("INSERT_APL_MEM_INFO");
        tempinsertAplMemInfoCount += statement.executeUpdateByMap(condition);
    }

    /**
     * OP会員属性変更。
     * <p/>
     * OP会員一時情報をOP会員情報TBLに更新する。<br>
     * OP会員一時情報をアプリ会員情報TBLに更新する。
     *
     * @param inputData
     *            入力データ
     * @return true：OP会員番号存在、false：OP会員番号存在しない
     */
    private boolean modifyOpMemProp(SqlRow inputData) {

        // 会員管理番号
        String memberControlNumber = inputData.getString("MEMBER_CONTROL_NUMBER");
        // 会員管理番号枝番
        String memCtrlNumBrNum = inputData.getString("MEM_CTRL_NUM_BR_NUM");

        // OP会員情報排他
        Map<String, Object> exclusiveMap = new HashMap<String, Object>();
        exclusiveMap.put("memberControlNumber", memberControlNumber);
        exclusiveMap.put("memCtrlNumBrNum", memCtrlNumBrNum);
        EntityList<OpMemInfoEntity> opMemInfoList = UniversalDao.findAllBySqlFile(OpMemInfoEntity.class,
                "SELECT_OP_MEM_INFO", exclusiveMap);

        // OP会員情報TBLに該当会員管理番号、会員管理番号枝番のデータが存在しない場合、
        if (opMemInfoList.isEmpty()) {
            FailureLogUtil.logError(inputData, "AB114A0107", inputData.getString("DATA_RELATE_DIVISION"),
                    memberControlNumber, memCtrlNumBrNum);
            return false;
        }

        // アプリ会員情報排他
        EntityList<AplMemInfoEntity> aplMemInfoList = UniversalDao.findAllBySqlFile(AplMemInfoEntity.class,
                "SELECT_APL_MEM_INFO_BY_OP_AND_APL", exclusiveMap);

        // アプリ会員情報TBLに該当会員管理番号、会員管理番号枝番のデータが存在しない場合、
        if (aplMemInfoList.isEmpty()) {
            throw new TransactionAbnormalEnd(100, "AB114A0104", memberControlNumber, memCtrlNumBrNum);
        }

        // OP会員情報更新
        updateOpMemInfo(opMemInfoList.get(0), inputData);

        // OP番号が変わる場合、アプリ会員情報をエンティティに設定
        for (AplMemInfoEntity aplMemInfo : aplMemInfoList) {
            String osakaPitapaNumber = inputData.getString("OSAKA_PITAPA_NUMBER");
            if (!osakaPitapaNumber.equals(aplMemInfo.getOsakaPitapaNumber())) {
                // OP番号
                aplMemInfo.setOsakaPitapaNumber(osakaPitapaNumber);
                // 最終更新者ID
                aplMemInfo.setUpdateUserId(BATCH_PROCESS_ID);
                // 最終更新日時
                aplMemInfo.setUpdateDateTime(SystemTimeUtil.getTimestamp());

                // アプリ会員情報更新
                UniversalDao.update(aplMemInfo);

                // 出力データ件数(アプリ会員情報)(更新)
                tempUpdateAplMemInfoCount++;
            }
        }
        return true;
    }

    /**
     * OP会員退会。
     * <p/>
     * OP会員情報を退会（解約）する。<br>
     * OP会員取込み時に作成したアプリ会員情報を退会（解約）する。<br>
     * OP会員が認証済の場合、アプリ会員登録時に作成したアプリ会員情報のOP認証解除を行う。
     *
     * @param memberControlNumber
     *            会員管理番号
     * @param memCtrlNumBrNum
     *            会員管理番号枝番
     * @return true：OP会員番号存在、false：OP会員番号存在しない
     */
    private boolean withdrawFromOpMem(String memberControlNumber, String memCtrlNumBrNum) {

        // OP会員情報排他
        Map<String, Object> exclusiveMap = new HashMap<String, Object>();
        exclusiveMap.put("memberControlNumber", memberControlNumber);
        exclusiveMap.put("memCtrlNumBrNum", memCtrlNumBrNum);
        EntityList<OpMemInfoEntity> opMemInfoList = UniversalDao.findAllBySqlFile(OpMemInfoEntity.class,
                "SELECT_OP_MEM_INFO", exclusiveMap);

        // OP会員情報TBLに該当会員管理番号、会員管理番号枝番のデータが存在しない場合、
        if (opMemInfoList.isEmpty()) {
            return false;
        }

        // アプリ会員情報排他(OP会員取込み時に作成したアプリ会員)
        EntityList<AplMemInfoEntity> aplMemInfoOfOpList = UniversalDao.findAllBySqlFile(AplMemInfoEntity.class,
                "SELECT_APL_MEM_INFO_FOR_OP_WITHDRAW", exclusiveMap);

        // アプリ会員情報TBLに該当会員管理番号、会員管理番号枝番のデータが存在しない場合、
        if (aplMemInfoOfOpList.isEmpty()) {
            throw new TransactionAbnormalEnd(100, "AB114A0106", memberControlNumber, memCtrlNumBrNum);
        }

        // アプリ会員情報(OP会員取込み時に作成したアプリ会員情報)
        AplMemInfoEntity aplMemInfoOfOp = aplMemInfoOfOpList.get(0);
        AplMemInfoEntity aplMemInfoOfApl = null;
        // 取得したOP認証フラグが「1：OP認証済」の場合、アプリ会員情報排他(アプリ会員登録時に作成したアプリ会員)
        if (OpalCodeConstants.OPAuthenticateFlag.OP_AUTH_FLAG_1
                .equals(aplMemInfoOfOp.getOsakaPitapaAuthenticateFlag())) {

            exclusiveMap.put("aplMemberStatusCode", OpalCodeConstants.AplMemStatusCode.OP_AUTH_APL_MEM);
            aplMemInfoOfApl = UniversalDao.findBySqlFile(AplMemInfoEntity.class, "SELECT_APL_MEM_INFO_FOR_APL_WITHDRAW",
                    exclusiveMap);
        }

        // パートナー会員サービス情報排他
        EntityList<PartnerMemServiceInfoEntity> partnerMemServiceInfoList = UniversalDao.findAllBySqlFile(
                PartnerMemServiceInfoEntity.class, "SELECT_PARTNER_MEM_SERVICE_INFO_FOR_DELETE", exclusiveMap);

        // OP会員情報退会（解約）
        withdrawOpMemInfo(opMemInfoList.get(0));

        // アプリ会員情報退会（解約）
        withdrawAplMemInfoOfOp(aplMemInfoOfOp);

        // OP認証解除
        if (aplMemInfoOfApl != null) {
            withdrawAplMemInfoOfApl(aplMemInfoOfApl);
        }

        // パートナー会員サービス情報論理削除
        deletePartnerMemServiceInfo(partnerMemServiceInfoList);

        // 主なご利用駅情報論理削除処理
        deleteMainUseStaInfo(memberControlNumber, memCtrlNumBrNum);
        return true;
    }

    /**
     * OP会員情報更新。
     *
     * @param opMemInfo
     *            OpMemInfoエンティティ
     * @param inputData
     *            入力データ
     */
    private void updateOpMemInfo(OpMemInfoEntity opMemInfo, SqlRow inputData) {

        // OP番号
        opMemInfo.setOsakaPitapaNumber(inputData.getString("OSAKA_PITAPA_NUMBER"));
        // PiTaPa有効期限の値が変更されていた場合
        if (!opMemInfo.getPitapaExpirationDate().equals(inputData.getString("PITAPA_EXPIRATION_DATE"))) {
            // 更新前PiTaPa有効期限
            opMemInfo.setOldPitapaExpirationDate(opMemInfo.getPitapaExpirationDate());
            // PiTaPa有効期限
            opMemInfo.setPitapaExpirationDate(inputData.getString("PITAPA_EXPIRATION_DATE"));
        }
        // カード種類
        opMemInfo.setCardType(inputData.getString("CARD_TYPE"));
        // 生年月日
        opMemInfo.setBirthdate(inputData.getString("BIRTHDATE"));
        // 性別コード
        opMemInfo.setSexCode(inputData.getString("SEX_CODE"));
        // 自宅電話番号
        opMemInfo.setTelephoneNumber(inputData.getString("TELEPHONE_NUMBER"));
        // 携帯電話番号
        opMemInfo.setCellphoneNumber(inputData.getString("CELLPHONE_NUMBER"));
        // 郵便番号
        opMemInfo.setPostcode(inputData.getString("POSTCODE"));
        // サービス種別
        opMemInfo.setServiceCategory(inputData.getString("SERVICE_CATEGORY"));
        // 登録駅1
        opMemInfo.setRegistStation1(inputData.getString("REGIST_STATION_1"));
        // 登録駅2
        opMemInfo.setRegistStation2(inputData.getString("REGIST_STATION_2"));
        // 続柄コード
        opMemInfo.setRelationshipCode(inputData.getString("RELATIONSHIP_CODE"));
        // 最終更新者ID
        opMemInfo.setUpdateUserId(BATCH_PROCESS_ID);
        // 最終更新日時
        opMemInfo.setUpdateDateTime(SystemTimeUtil.getTimestamp());

        // OP会員情報更新
        UniversalDao.update(opMemInfo);

        // 出力データ件数(OP会員情報)(更新)をカウントアップする。
        tempUpdateOpMemInfoCount++;
    }

    /**
     * OP会員情報退会（解約）。
     *
     * @param opMemInfo
     *            OpMemInfoエンティティ
     */
    private void withdrawOpMemInfo(OpMemInfoEntity opMemInfo) {

        // 論理削除日の算出
        CM010004Component cM010004Component = new CM010004Component();
        // 論理削除日
        String monthSpan = SystemRepository.getString("after_withdraw_member_info_retention_period");
        String deletedDate = cM010004Component.getDeletedDateMonthly(Integer.valueOf(monthSpan));

        // OP退会フラグ
        opMemInfo.setOsakaPitapaWithdrawFlag(OpalDefaultConstants.OSAKA_PITAPA_WITHDRAW_FLAG_1);
        // 削除フラグ
        opMemInfo.setDeletedFlg(OpalDefaultConstants.DELETED_FLG_1);
        // 論理削除日
        opMemInfo.setDeletedDate(deletedDate);
        // 最終更新者ID
        opMemInfo.setUpdateUserId(BATCH_PROCESS_ID);
        // 最終更新日時
        opMemInfo.setUpdateDateTime(SystemTimeUtil.getTimestamp());

        // OP会員情報更新(OP会員情報退会（解約）)
        UniversalDao.update(opMemInfo);

        // 出力データ件数(OP会員情報)(更新)をカウントアップする。
        tempUpdateOpMemInfoCount++;
    }

    /**
     * アプリ会員情報(OP会員取込み時に作成したアプリ会員情報)退会（解約）。
     *
     * @param aplMemInfo
     *            AplMemInfoエンティティ
     */
    private void withdrawAplMemInfoOfOp(AplMemInfoEntity aplMemInfo) {

        // OP認証フラグ
        aplMemInfo.setOsakaPitapaAuthenticateFlag(OpalCodeConstants.OPAuthenticateFlag.OP_AUTH_FLAG_0);
        // OP退会フラグ
        aplMemInfo.setOsakaPitapaWithdrawFlag(OpalDefaultConstants.OSAKA_PITAPA_WITHDRAW_FLAG_1);
        // 最終更新者ID
        aplMemInfo.setUpdateUserId(BATCH_PROCESS_ID);
        // 最終更新日時
        aplMemInfo.setUpdateDateTime(SystemTimeUtil.getTimestamp());

        // アプリ会員情報更新(OP会員取込み時に作成したアプリ会員情報退会（解約）)
        UniversalDao.update(aplMemInfo);

        // 出力データ件数(アプリ会員情報)(更新)をカウントアップする。
        tempUpdateAplMemInfoCount++;
    }

    /**
     * OP認証解除。
     *
     * @param aplMemInfo
     *            AplMemInfoエンティティ
     */
    private void withdrawAplMemInfoOfApl(AplMemInfoEntity aplMemInfo) {

        // 会員管理番号
        aplMemInfo.setMemberControlNumber(null);
        // 会員管理番号枝番
        aplMemInfo.setMemCtrlNumBrNum(null);
        // OP番号
        aplMemInfo.setOsakaPitapaNumber(null);
        // アプリ会員状態コード
        aplMemInfo.setApplicationMemberStatusCode(OpalCodeConstants.AplMemStatusCode.NOT_OP_MEM);
        // 最終更新者ID
        aplMemInfo.setUpdateUserId(BATCH_PROCESS_ID);
        // 最終更新日時
        aplMemInfo.setUpdateDateTime(SystemTimeUtil.getTimestamp());

        // アプリ会員情報更新
        UniversalDao.update(aplMemInfo);

        // 出力データ件数(アプリ会員情報)(更新)をカウントアップする。
        tempUpdateAplMemInfoCount++;
    }

    /**
     * パートナー会員サービス情報論理削除処理
     *
     * @param partnerMemServiceInfoList
     *            PartnerMemServiceInfoエンティティリスト
     */
    private void deletePartnerMemServiceInfo(EntityList<PartnerMemServiceInfoEntity> partnerMemServiceInfoList) {

        if (!partnerMemServiceInfoList.isEmpty()) {

            // 論理削除日の算出
            CM010004Component cM010004Component = new CM010004Component();
            // 論理削除日
            String monthSpan = SystemRepository.getString("after_withdraw_partner_info_retention_period");
            String deletedDate = cM010004Component.getDeletedDateMonthly(Integer.valueOf(monthSpan));

            for (PartnerMemServiceInfoEntity partnerMemServiceInfo : partnerMemServiceInfoList) {
                // 削除フラグ
                partnerMemServiceInfo.setDeletedFlg(OpalDefaultConstants.DELETED_FLG_1);
                // 最終更新者ID
                partnerMemServiceInfo.setUpdateUserId(BATCH_PROCESS_ID);
                // 最終更新日時
                partnerMemServiceInfo.setUpdateDateTime(SystemTimeUtil.getTimestamp());
                // 論理削除日
                partnerMemServiceInfo.setDeletedDate(deletedDate);

                // 出力データ件数(パートナー会員サービス情報)(更新)
                tempUpdatePartnerMemServiceInfoCount++;
            }

            // パートナー会員サービス情報を論理削除する。
            UniversalDao.batchUpdate(partnerMemServiceInfoList);
        }
    }

    /**
     * 主なご利用駅情報論理削除処理
     *
     * @param memberControlNumber
     *            会員管理番号
     * @param memCtrlNumBrNum
     *            会員管理番号枝番
     */
    private void deleteMainUseStaInfo(String memberControlNumber, String memCtrlNumBrNum) {

        // 論理削除日の算出
        CM010004Component cM010004Component = new CM010004Component();
        // 論理削除日
        String monthSpan = SystemRepository.getString("after_withdraw_main_use_sta_info_retention_period");
        String deletedDate = cM010004Component.getDeletedDateMonthly(Integer.valueOf(monthSpan));

        // DBに更新する値を設定する。
        Map<String, Object> condition = new HashMap<String, Object>();

        // 会員管理番号
        condition.put("memberControlNumber", memberControlNumber);
        // 会員管理番号枝番
        condition.put("memCtrlNumBrNum", memCtrlNumBrNum);
        // 削除フラグ
        condition.put("deletedFlag0", OpalDefaultConstants.DELETED_FLG_0);
        // 削除フラグ(1:削除済)
        condition.put("deletedFlag1", OpalDefaultConstants.DELETED_FLG_1);
        // 最終更新者ID
        condition.put("updateUserId", BATCH_PROCESS_ID);
        // 最終更新日時
        condition.put("updateDateTime", SystemTimeUtil.getTimestamp());
        // 論理削除日
        condition.put("deletedDate", deletedDate);

        // 主なご利用駅情報TBLを論理削除する。
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("UPDATE_MAIN_USE_STA_INFO");
        tempUpdateMainUseStaInfoCount += statement.executeUpdateByMap(condition);
    }

    /**
     * OP会員一時情報更新処理
     * <p/>
     * OP会員一時情報TBLに処理済フラグを更新する。
     *
     * @param memberControlNumber
     *            会員管理番号
     * @param memCtrlNumBrNum
     *            会員管理番号枝番
     */
    private void updateOpMemTempInfo(String memberControlNumber, String memCtrlNumBrNum) {

        // DBに更新する値を設定する。
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

        // OP会員一時情報TBLに処理済フラグを更新する
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("UPDATE_OP_MEM_TEMP_INFO");
        tempUpdateOpMemTempInfoCount += statement.executeUpdateByMap(condition);
    }
}
