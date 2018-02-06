package jp.co.tis.opal.web.common.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nablarch.common.dao.EntityList;
import nablarch.common.dao.UniversalDao;
import nablarch.core.date.SystemTimeUtil;
import nablarch.core.db.statement.ParameterizedSqlPStatement;
import nablarch.core.db.statement.SqlResultSet;
import nablarch.core.db.statement.SqlRow;
import nablarch.core.db.support.DbAccessSupport;
import nablarch.core.repository.SystemRepository;

import jp.co.tis.opal.common.component.CM010001Component;
import jp.co.tis.opal.common.component.CM010004Component;
import jp.co.tis.opal.common.component.CM010005Component;
import jp.co.tis.opal.common.constants.OpalCodeConstants;
import jp.co.tis.opal.common.constants.OpalCodeConstants.MailDeliverType;
import jp.co.tis.opal.common.constants.OpalDefaultConstants;
import jp.co.tis.opal.common.entity.AplMemInfoEntity;
import jp.co.tis.opal.common.entity.MileBalanceInfoEntity;
import jp.co.tis.opal.common.utility.DateConvertUtil;
import jp.co.tis.opal.common.utility.IdGeneratorUtil;

/**
 * CM111001:OP認証の共通コンポーネント
 *
 * @author 趙
 * @since 1.0
 */
public class CM111001Component extends DbAccessSupport {

    /** 既に認証されているアプリ会員のアプリ会員ID */
    private Long oldAplMemberId;

    /**
     * {@inneritDoc} 該当アプリ会員とOP会員のOP認証を行う。
     * <p/>
     *
     * @param applicationMemberId
     *            アプリ会員ID
     * @param osakaPitapaNumber
     *            OP番号
     * @param mileAddSubRcptNum
     *            マイル加算減算受付番号
     * @param bonusMileAmount
     *            ボーナスマイル数
     * @param processId
     *            処理ID
     * @return 処理結果Map ("OP認証処理結果 true：正常、false：存在しないOP会員" "OP認証解除配信可否
     *         true：配信可、false：配信不可" "OP認証成功配信可否 true：配信可、false：配信不可")
     */
    public Map<String, Boolean> setOPAuth(Long applicationMemberId, String osakaPitapaNumber, String mileAddSubRcptNum,
            Long bonusMileAmount, String processId) {

        // 処理結果Map
        Map<String, Boolean> result = new HashMap<String, Boolean>();

        // 1.アプリ会員情報とマイル残高情報の取得
        // パラメータ.OP番号に紐づくアプリ会員情報取得
        Map<String, Object> exclusiveMap = new HashMap<String, Object>();
        exclusiveMap.put("osakaPitapaNumber", osakaPitapaNumber);

        EntityList<AplMemInfoEntity> aplMemInfoList = UniversalDao.findAllBySqlFile(AplMemInfoEntity.class,
                "SELECT_APL_MEM_INFO_BY_OP", exclusiveMap);

        // データが取得されなかった場合は処理結果Map.OP認証処理結果に「false」を設定する。
        if (aplMemInfoList.isEmpty()) {
            result.put("OP_AUTH_RESULT", false);
            return result;
        }

        // OP認証フラグ
        String opAuthFlag = aplMemInfoList.get(0).getOsakaPitapaAuthenticateFlag();

        // アプリ会員IDに紐づくマイル残高情報のロックを取得
        exclusiveMap = new HashMap<String, Object>();
        if (OpalCodeConstants.OPAuthenticateFlag.OP_AUTH_FLAG_1.equals(opAuthFlag)) {
            exclusiveMap.put("applicationMemberId", aplMemInfoList.get(1).getApplicationMemberId());
        } else if (OpalCodeConstants.OPAuthenticateFlag.OP_AUTH_FLAG_0.equals(opAuthFlag)) {
            exclusiveMap.put("applicationMemberId", aplMemInfoList.get(0).getApplicationMemberId());
        }
        UniversalDao.findAllBySqlFile(MileBalanceInfoEntity.class, "SELECT_MILE_BALANCE_INFO", exclusiveMap);

        // 2.OP認証解除
        Boolean opAuthReleaseMailDeliverFlag = true;
        if (OpalCodeConstants.OPAuthenticateFlag.OP_AUTH_FLAG_1.equals(opAuthFlag)) {
            oldAplMemberId = aplMemInfoList.get(1).getApplicationMemberId();
            // 既に認証されているアプリ会員のOP認証解除
            opAuthReleaseMailDeliverFlag = releaseOpAuthentication(aplMemInfoList.get(1), processId);
        }

        // 3.OP認証実施
        Map<String, Boolean> resultOfOpAuth = setOpAuthentication(applicationMemberId, aplMemInfoList.get(0),
                mileAddSubRcptNum, bonusMileAmount, processId);

        // 処理結果Mapを設定
        // OP認証処理結果
        result.put("OP_AUTH_RESULT", true);
        // OP認証解除配信可否
        result.put("OP_AUTH_RELEASE_MAIL_DELIVER_FLAG", opAuthReleaseMailDeliverFlag);
        // OP認証成功配信可否
        result.put("OP_AUTH_MAIL_DELIVER_FLAG", resultOfOpAuth.get("OP_AUTH_MAIL_DELIVER_FLAG"));
        // マイル減算処理結果
        result.put("MILE_SUB_FLAG", resultOfOpAuth.get("MILE_SUB_FLAG"));
        return result;
    }

    /**
     * 既に認証されているアプリ会員のOP認証解除処理。
     * <p/>
     * 1) 既に認証されているアプリ会員のOP認証解除 <br>
     * 2) 既に認証されているアプリ会員にOP認証解除のお知らせメール送信
     *
     * @param aplMemInfoOfOldApl
     *            既に認証されているアプリ会員情報
     * @param processId
     *            処理ID
     * @return 配信可否フラグ(true:配信可 false:配信不可)
     */
    private Boolean releaseOpAuthentication(AplMemInfoEntity aplMemInfoOfOldApl, String processId) {

        // 既に認証されているアプリ会員のOP認証解除
        // 会員管理番号
        aplMemInfoOfOldApl.setMemberControlNumber(null);
        // 会員管理番号枝番
        aplMemInfoOfOldApl.setMemCtrlNumBrNum(null);
        // OP番号
        aplMemInfoOfOldApl.setOsakaPitapaNumber(null);
        // アプリ会員状態コード
        aplMemInfoOfOldApl.setApplicationMemberStatusCode(OpalCodeConstants.AplMemStatusCode.NOT_OP_MEM);
        // 最終更新者ID
        aplMemInfoOfOldApl.setUpdateUserId(processId);
        // 最終更新日時
        aplMemInfoOfOldApl.setUpdateDateTime(SystemTimeUtil.getTimestamp());

        UniversalDao.update(aplMemInfoOfOldApl);

        // 配信可否チェック
        Boolean mailDeliverFlag = true;
        if (OpalCodeConstants.MailDeliverStatusDivision.MAIL_DELIVER_STATUS_DIVISION_0
                .equals(aplMemInfoOfOldApl.getMailDeliverStatusDivision())) {

            // 既に認証されているアプリ会員にOP認証解除のお知らせメール送信
            List<String> variableItemValues = new ArrayList<String>();
            // CM010001：メール配信情報登録共通コンポーネントを呼び出す
            CM010001Component cm010001Component = new CM010001Component();
            cm010001Component.insMailLiteDeliverInfo(aplMemInfoOfOldApl.getApplicationMemberId(), processId,
                    aplMemInfoOfOldApl.getMailAddress(), MailDeliverType.MAIL_DELIVER_TYPE_1,
                    OpalDefaultConstants.MAIL_TEMP_OP_AUTHENTICATE_RELEASE_NOTICE, variableItemValues, null);
        } else {
            mailDeliverFlag = false;
        }

        return mailDeliverFlag;
    }

    /**
     * アプリ会員のOP認証実施処理
     * <p/>
     * 1)アプリ会員情報（アプリ会員登録時に作成したデータ）のOP認証状態更新 <br>
     * 2)アプリ会員情報（OP会員取込み時に作成したデータ）のOP認証状態更新 <br>
     * 3)アプリ会員のマイル残高移行 <br>
     * 4)マイル履歴情報登録 <br>
     * 5)初回OP認証ボーナスマイル付与 <br>
     * 6)会員登録ボーナス減算 <br>
     * 7)認証されたアプリ会員にOP認証成功のお知らせメール送信
     *
     * @param applicationMemberId
     *            アプリ会員ID
     * @param aplMemInfoOfOp
     *            アプリ会員情報（OP会員取込み時に作成したデータ）
     * @param mileAddSubRcptNum
     *            マイル加算減算受付番号
     * @param bonusMileAmount
     *            ボーナスマイル数
     * @param processId
     *            処理ID
     * @return 処理結果Map
     */
    private Map<String, Boolean> setOpAuthentication(Long applicationMemberId, AplMemInfoEntity aplMemInfoOfOp,
            String mileAddSubRcptNum, Long bonusMileAmount, String processId) {

        // 処理結果Map
        Map<String, Boolean> result = new HashMap<String, Boolean>();

        // アプリ会員情報取得
        SqlRow aplMemInfoOfApl = getAplMemInfo(applicationMemberId);
        // OP会員情報取得
        SqlRow opMemInfo = getOpMemInfo(aplMemInfoOfOp.getMemberControlNumber(), aplMemInfoOfOp.getMemCtrlNumBrNum());

        // OP認証フラグ
        String opAuthFlag = aplMemInfoOfOp.getOsakaPitapaAuthenticateFlag();
        // アプリ会員のOP認証回数
        int opAuthTimesOfApl = aplMemInfoOfApl.getInteger("OP_AUTH_TIMES").intValue();
        // OP会員のOP認証回数
        int opAuthTimesOfOp = aplMemInfoOfOp.getOpAuthTimes().intValue();

        // アプリ会員情報（アプリ会員登録時に作成したデータ）のOP認証状態更新
        updateAplMemInfoForApl(applicationMemberId, aplMemInfoOfOp, opMemInfo, aplMemInfoOfApl, processId);

        // アプリ会員情報（OP会員取込み時に作成したデータ）のOP認証状態更新
        updateAplMemInfoForOp(aplMemInfoOfOp, processId);

        // 論理削除日取得
        CM010004Component cm010004Component = new CM010004Component();
        String deletedDate = cm010004Component.getDeletedDateMileYearly(
                Integer.parseInt(SystemRepository.getString("mile_control_data_retention_period")));

        // 統合マイル残高取得して、アプリ会員へ統合
        Long mileAmount = uniteMileBalanceInfo(applicationMemberId, aplMemInfoOfOp.getApplicationMemberId(), opAuthFlag,
                processId, deletedDate);

        // マイル履歴情報登録
        if (mileAmount > 0) {
            registMileHistoryInfo(applicationMemberId, aplMemInfoOfOp.getApplicationMemberId(), opAuthFlag, mileAmount,
                    processId, deletedDate);
        }

        // 初回OP認証ボーナスマイル付与
        addBonusMileAmount(applicationMemberId, opAuthTimesOfApl, opAuthTimesOfOp, mileAddSubRcptNum, bonusMileAmount,
                processId);

        // 会員登録ボーナス減算
        Boolean mileSubFlag = true;
        if (opAuthTimesOfOp > OpalDefaultConstants.OP_AUTH_TIMES_0) {
            mileSubFlag = mileSub(applicationMemberId, processId);
        }

        // 配信可否チェック
        Boolean mailDeliverFlag = true;

        if (!mileSubFlag) {
            // マイル減算処理結果
            result.put("MILE_SUB_FLAG", mileSubFlag);
            // OP認証成功配信可否
            result.put("OP_AUTH_MAIL_DELIVER_FLAG", mailDeliverFlag);
            return result;
        }

        if (OpalCodeConstants.MailDeliverStatusDivision.MAIL_DELIVER_STATUS_DIVISION_0
                .equals(aplMemInfoOfApl.getString("MAIL_DELIVER_STATUS_DIVISION"))) {
            // 認証されたアプリ会員にOP認証成功のお知らせメール送信
            registMailLiteDeliverInfo(applicationMemberId, opAuthTimesOfApl, opAuthTimesOfOp,
                    aplMemInfoOfApl.getString("MAIL_ADDRESS"), processId);
        } else {
            mailDeliverFlag = false;
        }

        // マイル減算処理結果
        result.put("MILE_SUB_FLAG", mileSubFlag);
        // OP認証成功配信可否
        result.put("OP_AUTH_MAIL_DELIVER_FLAG", mailDeliverFlag);
        return result;
    }

    /**
     * アプリ会員情報（基幹システムからOP会員取込み時に作成したデータ）のOP認証状態更新
     *
     * @param aplMemInfoOfOp
     *            アプリ会員情報（OP会員取込み時に作成したデータ）
     * @param processId
     *            処理ID
     */
    private void updateAplMemInfoForOp(AplMemInfoEntity aplMemInfoOfOp, String processId) {

        // OP認証フラグ
        aplMemInfoOfOp.setOsakaPitapaAuthenticateFlag(OpalCodeConstants.OPAuthenticateFlag.OP_AUTH_FLAG_1);
        // OP認証回数
        int opAuthTimes = aplMemInfoOfOp.getOpAuthTimes().intValue();
        if (opAuthTimes != OpalDefaultConstants.OP_AUTH_TIMES_99) {
            aplMemInfoOfOp.setOpAuthTimes(opAuthTimes + 1);
        }
        // 最終更新者ID
        aplMemInfoOfOp.setUpdateUserId(processId);
        // 最終更新日時
        aplMemInfoOfOp.setUpdateDateTime(SystemTimeUtil.getTimestamp());

        UniversalDao.update(aplMemInfoOfOp);
    }

    /**
     * 統合マイル残高を取得し、アプリ会員へ統合して、統合マイル残高取得対象のマイル残高をクリアする。
     * <p/>
     * OP認証フラグが"0"(OP認証未済み)の場合、OP会員取込み時に作成したデータのマイル残高情報を取得し、統合マイル残高とする。 <br>
     * OP認証フラグが"1"(OP認証済み)の場合、既に認証されているアプリ会員のマイル残高情報を取得し、統合マイル残高とする。 <br>
     *
     * @param applicationMemberId
     *            アプリ会員ID
     * @param aplMemberIdOfOp
     *            OP会員取込み時に作成したデータのアプリ会員ID
     * @param opAuthFlag
     *            OP認証フラグ
     * @param processId
     *            処理ID
     * @param deletedDate
     *            論理削除日
     * @return 統合マイル残高合計
     */
    private Long uniteMileBalanceInfo(Long applicationMemberId, Long aplMemberIdOfOp, String opAuthFlag,
            String processId, String deletedDate) {

        // 開始年月取得
        String beginYM = getBeginYM();

        // 統合マイル残高取得
        SqlResultSet mileBalanceInfo = null;

        if (OpalCodeConstants.OPAuthenticateFlag.OP_AUTH_FLAG_0.equals(opAuthFlag)) {
            // アプリ会員ID（OP会員取込み時に作成したデータ）に紐づくマイル残高情報取得
            mileBalanceInfo = getMileBalanceInfo(aplMemberIdOfOp, beginYM);
        } else if (OpalCodeConstants.OPAuthenticateFlag.OP_AUTH_FLAG_1.equals(opAuthFlag)) {
            // アプリ会員ID（既に認証されているアプリ会員）に紐づくマイル残高情報取得
            mileBalanceInfo = getMileBalanceInfo(oldAplMemberId, beginYM);
        }

        // アプリ会員へマイル残高の統合
        Long mileAmount = 0L;
        if (mileBalanceInfo != null) {
            for (SqlRow row : mileBalanceInfo) {
                // 対象年月
                String objectYearMonth = row.getString("OBJECT_YEAR_MONTH");
                // マイル残高
                Long opMileBalance = row.getLong("MILE_BALANCE");
                // 取得した対象年月で、アプリ会員のマイル残高情報を取得する。
                SqlResultSet aplMileBalanceInfo = getMonthlyMileBalanceInfo(applicationMemberId, objectYearMonth);
                if (aplMileBalanceInfo.isEmpty()) {
                    // マイル残高情報の登録処理(アプリ会員へマイル残高統合)を行う
                    insertMileBalanceInfo(applicationMemberId, objectYearMonth, opMileBalance, processId, deletedDate);
                } else {
                    Long mileBalance = aplMileBalanceInfo.get(0).getLong("MILE_BALANCE") + opMileBalance;
                    // マイル残高情報の更新処理(アプリ会員へマイル残高統合)を行う
                    updateMileBalanceInfo(applicationMemberId, objectYearMonth, mileBalance, processId);
                }
                // マイル残高を集計
                mileAmount += opMileBalance;
            }
        }

        // 統合マイル残高取得対象のマイル残高をクリアする
        if (OpalCodeConstants.OPAuthenticateFlag.OP_AUTH_FLAG_0.equals(opAuthFlag)) {
            // OP会員のマイル残高情報論理削除
            deleteMileBalanceInfo(aplMemberIdOfOp, beginYM, processId);
        } else if (OpalCodeConstants.OPAuthenticateFlag.OP_AUTH_FLAG_1.equals(opAuthFlag)) {
            // 既に認証されているアプリ会員のマイル残高移行
            clearMileBalanceInfo(oldAplMemberId, beginYM, processId);
        }

        return mileAmount;
    }

    /**
     * マイル履歴情報登録処理
     *
     * @param applicationMemberId
     *            アプリ会員ID
     * @param aplMemberIdOfOp
     *            アプリ会員情報（OP会員取込み時に作成したデータ）
     * @param opAuthFlag
     *            OP認証フラグ
     * @param mileAmount
     *            マイル数
     * @param processId
     *            処理ID
     * @param deletedDate
     *            論理削除日
     */
    private void registMileHistoryInfo(Long applicationMemberId, Long aplMemberIdOfOp, String opAuthFlag,
            Long mileAmount, String processId, String deletedDate) {

        // マイル履歴情報TBLに、アプリ会員のマイル統合履歴を登録
        insertMileHistoryInfoForAuth(applicationMemberId, mileAmount, processId, deletedDate);

        // マイル履歴情報TBLに、アプリ会員のマイル移行履歴を登録
        insertMileHistoryInfoForRelease(oldAplMemberId, aplMemberIdOfOp, opAuthFlag, mileAmount, processId,
                deletedDate);
    }

    /**
     * アプリ会員情報（アプリ会員登録時に作成したデータ）を取得する。
     *
     * @param applicationMemberId
     *            アプリ会員ID
     * @return 処理結果
     */
    private SqlRow getAplMemInfo(Long applicationMemberId) {

        // アプリ会員情報取得用SQL条件を設定する。
        Map<String, Object> condition = new HashMap<String, Object>();

        // アプリ会員ID
        condition.put("applicationMemberId", applicationMemberId);

        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("SELECT_APL_MEM_INFO");
        SqlRow row = statement.retrieve(condition).get(0);

        return row;
    }

    /**
     * OP会員情報を取得する。
     *
     * @param memberControlNumber
     *            会員管理番号
     * @param memCtrlNumBrNum
     *            会員管理番号枝番
     * @return 処理結果
     */
    private SqlRow getOpMemInfo(String memberControlNumber, String memCtrlNumBrNum) {

        // OP会員情報取得用SQL条件を設定する。
        Map<String, Object> condition = new HashMap<String, Object>();

        // 会員管理番号
        condition.put("memberControlNumber", memberControlNumber);
        // 会員管理番号枝番
        condition.put("memCtrlNumBrNum", memCtrlNumBrNum);

        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("SELECT_OP_MEM_INFO");
        SqlRow row = statement.retrieve(condition).get(0);

        return row;
    }

    /**
     * アプリ会員情報（アプリ会員登録時に作成したデータ）のOP認証状態を更新する。
     *
     * @param applicationMemberId
     *            アプリ会員ID
     * @param aplMemInfoOfOp
     *            アプリ会員情報（OP会員取込み時に作成したデータ）
     * @param opMemInfo
     *            OP会員情報
     * @param aplMemInfoOfApl
     *            アプリ会員情報（アプリ会員登録時に作成したデータ）
     * @param processId
     *            処理ID
     */
    private void updateAplMemInfoForApl(Long applicationMemberId, AplMemInfoEntity aplMemInfoOfOp, SqlRow opMemInfo,
            SqlRow aplMemInfoOfApl, String processId) {

        // DBに更新する値を設定する。
        Map<String, Object> condition = new HashMap<String, Object>();

        // アプリ会員ID
        condition.put("applicationMemberId", applicationMemberId);
        // 会員管理番号
        condition.put("memberControlNumber", aplMemInfoOfOp.getMemberControlNumber());
        // 会員管理番号枝番
        condition.put("memCtrlNumBrNum", aplMemInfoOfOp.getMemCtrlNumBrNum());
        // OP番号
        condition.put("osakaPitapaNumber", aplMemInfoOfOp.getOsakaPitapaNumber());
        // 生年月日
        if (opMemInfo.getString("BIRTHDATE").equals(aplMemInfoOfApl.getString("BIRTHDATE"))) {
            condition.put("birthdate", aplMemInfoOfApl.getString("BIRTHDATE"));
        } else {
            condition.put("birthdate", opMemInfo.getString("BIRTHDATE"));
        }
        // 性別コード
        if (opMemInfo.getString("SEX_CODE").equals(aplMemInfoOfApl.getString("SEX_CODE"))) {
            condition.put("sexCode", aplMemInfoOfApl.getString("SEX_CODE"));
        } else {
            condition.put("sexCode", opMemInfo.getString("SEX_CODE"));
        }
        // アプリ会員状態コード
        condition.put("applicationMemberStatusCode", OpalCodeConstants.AplMemStatusCode.OP_AUTH_APL_MEM);
        // OP認証回数
        int opAuthTimes = aplMemInfoOfApl.getInteger("OP_AUTH_TIMES").intValue();
        if (opAuthTimes != OpalDefaultConstants.OP_AUTH_TIMES_99) {
            condition.put("opAuthTimes", opAuthTimes + 1);
        } else {
            condition.put("opAuthTimes", opAuthTimes);
        }
        // 最終更新者ID
        condition.put("updateUserId", processId);
        // 最終更新日時
        condition.put("updateDateTime", SystemTimeUtil.getTimestamp());

        // データを更新する
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("UPDATE_APL_MEM_INFO");
        statement.executeUpdateByMap(condition);
    }

    /**
     * アプリ会員IDに紐づくマイル残高情報を取得する。
     *
     * @param applicationMemberId
     *            アプリ会員ID
     * @param objectYearMonth
     *            対象年月
     * @return 処理結果
     */
    private SqlResultSet getMileBalanceInfo(Long applicationMemberId, String objectYearMonth) {

        // マイル残高情報取得用SQL条件を設定する。
        Map<String, Object> condition = new HashMap<String, Object>();
        // アプリ会員ID
        condition.put("applicationMemberId", applicationMemberId);
        // 対象年月
        condition.put("objectYearMonth", objectYearMonth);

        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("SELECT_MILE_BALANCE_INFO_BY_APL_MEM");
        SqlResultSet result = statement.retrieve(condition);

        return result;
    }

    /**
     * 対象年月を基に、アプリ会員のマイル残高情報を取得する。
     *
     * @param applicationMemberId
     *            アプリ会員ID
     * @param objectYearMonth
     *            対象年月
     * @return 処理結果
     */
    private SqlResultSet getMonthlyMileBalanceInfo(Long applicationMemberId, String objectYearMonth) {

        // マイル残高情報取得用SQL条件を設定する。
        Map<String, Object> condition = new HashMap<String, Object>();
        // アプリ会員ID
        condition.put("applicationMemberId", applicationMemberId);
        // 対象年月
        condition.put("objectYearMonth", objectYearMonth);

        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("SELECT_MILE_BALANCE_INFO");
        SqlResultSet result = statement.retrieve(condition);

        return result;
    }

    /**
     * マイル残高情報の登録処理(アプリ会員へマイル残高統合)を行う。
     *
     * @param applicationMemberId
     *            アプリ会員ID
     * @param objectYearMonth
     *            対象年月
     * @param mileBalance
     *            マイル残高
     * @param processId
     *            処理ID
     * @param deletedDate
     *            論理削除日
     */
    private void insertMileBalanceInfo(Long applicationMemberId, String objectYearMonth, Long mileBalance,
            String processId, String deletedDate) {

        // DBに登録する値を設定する。
        Map<String, Object> condition = new HashMap<String, Object>();

        // アプリ会員ID
        condition.put("applicationMemberId", applicationMemberId);
        // 対象年月
        condition.put("objectYearMonth", objectYearMonth);
        // マイル残高
        condition.put("mileBalance", mileBalance);
        // 登録者ID
        condition.put("insertUserId", processId);
        // 登録日時
        condition.put("insertDateTime", SystemTimeUtil.getTimestamp());
        // 最終更新者ID
        condition.put("updateUserId", processId);
        // 最終更新日時
        condition.put("updateDateTime", SystemTimeUtil.getTimestamp());
        // 削除フラグ
        condition.put("deletedFlg", OpalDefaultConstants.DELETED_FLG_0);
        // 論理削除日
        condition.put("deletedDate", deletedDate);
        // バージョン番号
        condition.put("version", OpalDefaultConstants.VERSION);

        // データを登録する
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("INSERT_MILE_BALANCE_INFO");
        statement.executeUpdateByMap(condition);
    }

    /**
     * マイル残高情報の更新処理(アプリ会員へマイル残高統合)を行う。
     *
     * @param applicationMemberId
     *            アプリ会員ID
     * @param objectYearMonth
     *            対象年月
     * @param mileBalance
     *            マイル残高
     * @param processId
     *            処理ID
     */
    private void updateMileBalanceInfo(Long applicationMemberId, String objectYearMonth, Long mileBalance,
            String processId) {

        // DBに更新する値を設定する。
        Map<String, Object> condition = new HashMap<String, Object>();

        // アプリ会員ID
        condition.put("applicationMemberId", applicationMemberId);
        // 対象年月
        condition.put("objectYearMonth", objectYearMonth);
        // マイル残高
        condition.put("mileBalance", mileBalance);
        // 最終更新者ID
        condition.put("updateUserId", processId);
        // 最終更新日時
        condition.put("updateDateTime", SystemTimeUtil.getTimestamp());

        // データを更新する
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("UPDATE_ADD_MILE_BALANCE_INFO");
        statement.executeUpdateByMap(condition);
    }

    /**
     * アプリ会員（既に認証されているアプリ会員）のマイル残高移行を行う。
     * <p/>
     * アプリ会員IDを条件に、マイル残高情報TBLにマイル残高を「0」に更新する。
     *
     * @param applicationMemberId
     *            アプリ会員ID
     * @param objectYearMonth
     *            対象年月
     * @param processId
     *            処理ID
     */
    private void clearMileBalanceInfo(Long applicationMemberId, String objectYearMonth, String processId) {

        // DBに更新する値を設定する。
        Map<String, Object> condition = new HashMap<String, Object>();

        // アプリ会員ID
        condition.put("applicationMemberId", applicationMemberId);
        // 対象年月
        condition.put("objectYearMonth", objectYearMonth);
        // マイル残高
        condition.put("mileBalance", OpalDefaultConstants.MILE_BALANCE_ZERO);
        // 最終更新者ID
        condition.put("updateUserId", processId);
        // 最終更新日時
        condition.put("updateDateTime", SystemTimeUtil.getTimestamp());

        // データを更新する
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("UPDATE_CLEAR_MILE_BALANCE_INFO");
        statement.executeUpdateByMap(condition);
    }

    /**
     * OP会員のマイル残高情報論理削除を行う。
     * <p/>
     * アプリ会員IDを条件に、マイル残高情報TBLに削除フラグを"1"(削除済)に更新する。
     *
     * @param applicationMemberId
     *            アプリ会員ID
     * @param objectYearMonth
     *            対象年月
     * @param processId
     *            処理ID
     */
    private void deleteMileBalanceInfo(Long applicationMemberId, String objectYearMonth, String processId) {

        // DBに更新する値を設定する。
        Map<String, Object> condition = new HashMap<String, Object>();

        // アプリ会員ID
        condition.put("applicationMemberId", applicationMemberId);
        // 対象年月
        condition.put("objectYearMonth", objectYearMonth);
        // 削除フラグ
        condition.put("deletedFlg", OpalDefaultConstants.DELETED_FLG_1);
        // 最終更新者ID
        condition.put("updateUserId", processId);
        // 最終更新日時
        condition.put("updateDateTime", SystemTimeUtil.getTimestamp());

        // データを更新する
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("UPDATE_DELETED_MILE_BALANCE_INFO");
        statement.executeUpdateByMap(condition);
    }

    /**
     * マイル履歴情報TBLに、アプリ会員のマイル統合履歴を登録する。
     *
     * @param applicationMemberId
     *            アプリ会員ID
     * @param mileAmount
     *            マイル数
     * @param processId
     *            処理ID
     * @param deletedDate
     *            論理削除日
     */
    private void insertMileHistoryInfoForAuth(Long applicationMemberId, Long mileAmount, String processId,
            String deletedDate) {

        // DBに登録する値を設定する。
        Map<String, Object> condition = new HashMap<String, Object>();

        // マイル履歴ID
        condition.put("mileHistoryId", IdGeneratorUtil.generateMileHistoryId());
        // アプリ会員ID
        condition.put("applicationMemberId", applicationMemberId);
        // マイル加算減算受付番号
        condition.put("mileAddSubRcptNum", null);
        // マイル種別コード
        condition.put("mileCategoryCode", OpalCodeConstants.MileCategoryCode.OP_REGIST_ADD);
        // マイル数
        condition.put("mileAmount", mileAmount);
        // マイル履歴登録日
        condition.put("mileHistoryRegistDate", SystemTimeUtil.getDateString());
        // 登録者ID
        condition.put("insertUserId", processId);
        // 登録日時
        condition.put("insertDateTime", SystemTimeUtil.getTimestamp());
        // 最終更新者ID
        condition.put("updateUserId", processId);
        // 最終更新日時
        condition.put("updateDateTime", SystemTimeUtil.getTimestamp());
        // 削除フラグ
        condition.put("deletedFlg", OpalDefaultConstants.DELETED_FLG_0);
        // 論理削除日
        condition.put("deletedDate", deletedDate);

        // データを登録する
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("INSERT_MILE_HIST_INFO");
        statement.executeUpdateByMap(condition);
    }

    /**
     * マイル履歴情報TBLに、アプリ会員のマイル移行履歴を登録する。
     *
     * @param oldAplMemberId
     *            既に認証されているアプリ会員のアプリ会員ID
     * @param aplMemberIdOfOp
     *            アプリ会員情報（OP会員取込み時に作成したデータ）
     * @param opAuthFlag
     *            OP認証フラグ
     * @param mileAmount
     *            マイル数
     * @param processId
     *            処理ID
     * @param deletedDate
     *            論理削除日
     */
    private void insertMileHistoryInfoForRelease(Long oldAplMemberId, Long aplMemberIdOfOp, String opAuthFlag,
            Long mileAmount, String processId, String deletedDate) {

        // DBに登録する値を設定する。
        Map<String, Object> condition = new HashMap<String, Object>();

        // マイル履歴ID
        condition.put("mileHistoryId", IdGeneratorUtil.generateMileHistoryId());

        if (OpalCodeConstants.OPAuthenticateFlag.OP_AUTH_FLAG_1.equals(opAuthFlag)) {
            // アプリ会員ID
            condition.put("applicationMemberId", oldAplMemberId);
            // マイル種別コード
            condition.put("mileCategoryCode", OpalCodeConstants.MileCategoryCode.OP_REGIST_RELEASE);
        } else if (OpalCodeConstants.OPAuthenticateFlag.OP_AUTH_FLAG_0.equals(opAuthFlag)) {
            // アプリ会員ID
            condition.put("applicationMemberId", aplMemberIdOfOp);
            // マイル種別コード
            condition.put("mileCategoryCode", OpalCodeConstants.MileCategoryCode.OP_REGIST_SLIDE_SUB);
        }

        // マイル加算減算受付番号
        condition.put("mileAddSubRcptNum", null);
        // マイル数
        condition.put("mileAmount", mileAmount);
        // マイル履歴登録日
        condition.put("mileHistoryRegistDate", SystemTimeUtil.getDateString());
        // 登録者ID
        condition.put("insertUserId", processId);
        // 登録日時
        condition.put("insertDateTime", SystemTimeUtil.getTimestamp());
        // 最終更新者ID
        condition.put("updateUserId", processId);
        // 最終更新日時
        condition.put("updateDateTime", SystemTimeUtil.getTimestamp());
        // 削除フラグ
        condition.put("deletedFlg", OpalDefaultConstants.DELETED_FLG_0);
        // 論理削除日
        condition.put("deletedDate", deletedDate);

        // データを登録する
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("INSERT_MILE_HIST_INFO");
        statement.executeUpdateByMap(condition);
    }

    /**
     * 初回OP認証の場合、ボーナスマイルを付与する。
     *
     * @param applicationMemberId
     *            アプリ会員ID
     * @param opAuthTimesOfAplMem
     *            アプリ会員のOP認証回数
     * @param opAuthTimesOfOpMem
     *            OP会員のOP認証回数
     * @param mileAddSubRcptNum
     *            マイル加算減算受付番号
     * @param bonusMileAmount
     *            ボーナスマイル数
     * @param processId
     *            処理ID
     */
    private void addBonusMileAmount(Long applicationMemberId, int opAuthTimesOfAplMem, int opAuthTimesOfOpMem,
            String mileAddSubRcptNum, Long bonusMileAmount, String processId) {

        // アプリ会員、及びOP会員のOP認証回数が両方0場合、
        if (opAuthTimesOfAplMem == OpalDefaultConstants.OP_AUTH_TIMES_0
                && opAuthTimesOfOpMem == OpalDefaultConstants.OP_AUTH_TIMES_0) {
            // CM010005：マイル計算共通コンポーネントを呼び出す
            CM010005Component cm010005Component = new CM010005Component();
            // OP認証ボーナスマイルを付与
            cm010005Component.addMile(applicationMemberId, mileAddSubRcptNum,
                    OpalCodeConstants.MileCategoryCode.OP_REGIST, bonusMileAmount, processId, null);
        }
    }

    /**
     * 新規登録ボーナスマイル取得
     *
     * @param applicationMemberId
     *            アプリ会員ID
     * @return 処理結果（存在する：true 存在しない：false）
     */
    private SqlResultSet getRegistBonusMile(Long applicationMemberId) {
        // 新規登録ボーナスマイル取得用SQL条件を設定する。
        Map<String, Object> condition = new HashMap<String, Object>();
        // アプリ会員ID
        condition.put("applicationMemberId", applicationMemberId);
        // マイル種別コード
        condition.put("mileCategoryCode", OpalCodeConstants.MileCategoryCode.MEM_REGIST_BONUS);
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("SELECT_MILE_HISTORY_INFORMATION");
        // 新規登録ボーナスマイル取得
        SqlResultSet result = statement.retrieve(condition);

        return result;
    }

    /**
     * マイル残高合計取得
     *
     * @param applicationMemberId
     *            アプリ会員ID
     * @return マイル残高合計
     */

    private Long getMileBalanceInfo(Long applicationMemberId) {
        // 開始年月取得
        String beginYM = getBeginYM();

        Long mileAmount = 0L;
        // マイル残高合計取得条件を設定する
        Map<String, Object> condition = new HashMap<String, Object>();
        // アプリ会員ID
        condition.put("applicationMemberId", applicationMemberId);
        // 開始年月
        condition.put("objectYearMonth", beginYM);
        // マイル残高合計取得
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("SELECT_MILE_BALANCE_SUM");
        SqlResultSet mileBalanceInfo = statement.retrieve(condition);
        mileAmount = mileBalanceInfo.get(0).getLong("MILE_BALANCE_SUM");

        return mileAmount;
    }

    /**
     * マイル減算実施
     *
     * @param applicationMemberId
     *            アプリ会員ID
     *
     * @param processId
     *            処理ID
     * @return マイル減算フラグ
     */

    private Boolean mileSub(Long applicationMemberId, String processId) {

        Boolean mileSubFlag = true;
        SqlResultSet resultSet = getRegistBonusMile(applicationMemberId);
        if (!resultSet.isEmpty()) {
            // 新規登録ボーナスマイル取得
            Long newRegistBonusMile = resultSet.get(0).getLong("MILE_AMOUNT");

            // マイル残高合計取得
            Long mileAmount = getMileBalanceInfo(applicationMemberId);

            // マイル残高合計≧新規登録ボーナスマイルの場合、マイル減算実施
            if (mileAmount >= newRegistBonusMile) {
                // CM010005：マイル計算共通コンポーネントを呼び出す。
                CM010005Component cm010005Component = new CM010005Component();
                // マイル減算。
                cm010005Component.subMile(applicationMemberId, resultSet.get(0).getString("MILE_ADD_SUB_RCPT_NUM"),
                        OpalCodeConstants.MileCategoryCode.MEM_REGIST_BONUS_ADJUST, newRegistBonusMile, processId);
            } else {
                // マイル減算不可
                mileSubFlag = false;
            }
        }
        return mileSubFlag;
    }

    /**
     * 認証されたアプリ会員にOP認証成功のお知らせメール送信。
     *
     * @param applicationMemberId
     *            アプリ会員ID
     * @param opAuthTimesOfAplMem
     *            アプリ会員のOP認証回数
     * @param opAuthTimesOfOpMem
     *            OP会員のOP認証回数
     * @param mailAddress
     *            メールアドレス
     * @param processId
     *            処理ID
     */
    private void registMailLiteDeliverInfo(Long applicationMemberId, int opAuthTimesOfAplMem, int opAuthTimesOfOpMem,
            String mailAddress, String processId) {

        // 差し込み項目
        List<String> variableItemValues = new ArrayList<String>();
        // テンプレートID
        String templateId;

        // アプリ会員、及びOP会員のOP認証回数が両方0場合、
        if (opAuthTimesOfAplMem == OpalDefaultConstants.OP_AUTH_TIMES_0
                && opAuthTimesOfOpMem == OpalDefaultConstants.OP_AUTH_TIMES_0) {
            templateId = OpalDefaultConstants.MAIL_TEMP_OP_AUTHENTICATED_ONCE_NOTICE;
        } else {
            templateId = OpalDefaultConstants.MAIL_TEMP_OP_AUTHENTICATED_TWICE_NOTICE;
        }

        // CM010001：メール配信情報登録共通コンポーネントを呼び出す
        CM010001Component cm010001Component = new CM010001Component();
        cm010001Component.insMailLiteDeliverInfo(applicationMemberId, processId, mailAddress,
                MailDeliverType.MAIL_DELIVER_TYPE_1, templateId, variableItemValues, null);
    }

    /**
     * 開始年月の取得。
     *
     * @return 開始年月
     */
    private String getBeginYM() {

        StringBuilder beginYM = new StringBuilder();

        // システム日付の年(YYYYの部分)
        int year = Integer.parseInt(DateConvertUtil.getSysYear());
        // システム日付の月日（MMDDの部分）を取得
        String sysDate = SystemTimeUtil.getDateString().substring(4, 8);

        if (sysDate.compareTo(OpalDefaultConstants.MILE_INVALID_DATE) >= 0) {
            // システム日付が4/1以降の場合、開始年月＝今年の3月（YYYYMM）
            beginYM.append(String.valueOf(year));
            beginYM.append(OpalDefaultConstants.MILE_INVALID_FROM_MONTH);
        } else {
            // システム日付が3/31以前の場合、開始年月＝昨年の3月（YYYYMM）
            beginYM.append(String.valueOf(year - 1));
            beginYM.append(OpalDefaultConstants.MILE_INVALID_FROM_MONTH);
        }

        return beginYM.toString();
    }
}
