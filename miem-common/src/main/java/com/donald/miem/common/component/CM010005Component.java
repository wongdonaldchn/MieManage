package com.donald.miem.common.component;

import java.util.HashMap;
import java.util.Map;

import com.donald.miem.common.utility.DateConvertUtil;
import com.donald.miem.common.utility.IdGeneratorUtil;
import nablarch.core.date.SystemTimeUtil;
import nablarch.core.db.statement.ParameterizedSqlPStatement;
import nablarch.core.db.statement.SqlResultSet;
import nablarch.core.db.support.DbAccessSupport;
import nablarch.core.repository.SystemRepository;
import nablarch.core.util.StringUtil;

import com.donald.miem.common.constants.OpalCodeConstants;
import com.donald.miem.common.constants.OpalDefaultConstants;

/**
 * CM010005:マイル計算の共通コンポーネント
 *
 * @author 曹
 * @since 1.0
 */
public class CM010005Component extends DbAccessSupport {

    /** マイル残高情報出力件数 */
    private int mileBalanceOutputCnt;

    /**
     * {@inneritDoc} マイル加算。
     * <p/>
     *
     * @param applicationMemberId
     *            アプリ会員ID
     * @param mileAddSubRcptNum
     *            マイル加算減算受付番号
     * @param mileCategoryCode
     *            マイル種別コード
     * @param mileAmount
     *            マイル数
     * @param processedId
     *            処理ID
     * @param objectYearMonth
     *            対象年月
     * @return mileBalanceUpdateFlg マイル残高情報更新フラグ
     */
    public String addMile(Long applicationMemberId, String mileAddSubRcptNum, String mileCategoryCode, Long mileAmount,
            String processedId, String objectYearMonth) {

        // マイル残高情報更新フラグ
        String mileBalanceUpdateFlg;

        // 対象年月設定
        if (StringUtil.isNullOrEmpty(objectYearMonth)) {
            objectYearMonth = DateConvertUtil.getSysYearMonth();
        }

        // マイル残高情報検索
        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("applicationMemberId", applicationMemberId);
        condition.put("objectYearMonth", objectYearMonth);
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("SELECT_MILE_BALANCE_INFO_ADD");
        SqlResultSet result = statement.retrieve(condition);

        // 論理削除日を導出する。
        CM010004Component cM010004Component = new CM010004Component();
        String deletedDate = cM010004Component.getDeletedDateMileYearly(
                Integer.parseInt(SystemRepository.getString("mile_control_data_retention_period")));

        if (result.isEmpty()) {
            // マイル残高情報更新フラグに「0：登録」を設定する。
            mileBalanceUpdateFlg = OpalDefaultConstants.MILE_BALANCE_UPDATE_FLG_0;

            // 検索結果がなかった場合、マイル残高情報TBLに、マイル残高情報を新規登録する。
            insertMileBalanceInfo(applicationMemberId, objectYearMonth, mileAmount, processedId, deletedDate);
        } else {
            // マイル残高情報更新フラグに「1：更新」を設定する。
            mileBalanceUpdateFlg = OpalDefaultConstants.MILE_BALANCE_UPDATE_FLG_1;

            // 現有マイル残高取得
            Long mileBalance = result.get(0).getLong("MILE_BALANCE");

            // マイル残高 = マイル残高 + 加算マイル数
            mileBalance = Long.valueOf(mileBalance.longValue() + mileAmount.longValue());

            // 検索結果があった場合、マイル残高情報TBLに、マイル残高情報を更新する。
            updateMileBalanceInfo(applicationMemberId, objectYearMonth, mileBalance, processedId);
        }

        // マイル種類コードが「A12：会員登録ボーナス」以外、かつ「A08：OSAKA PiTaPa登録」以外、またはマイル数が0以外の場合
        if ((!mileCategoryCode.equals(OpalCodeConstants.MileCategoryCode.MEM_REGIST_BONUS)
                && !mileCategoryCode.equals(OpalCodeConstants.MileCategoryCode.OP_REGIST)) || mileAmount > 0) {
            // マイル履歴情報登録
            insertMileHistoryInfo(applicationMemberId, mileAddSubRcptNum, mileCategoryCode, mileAmount, processedId,
                    deletedDate);
        }
        return mileBalanceUpdateFlg;
    }

    /**
     * {@inneritDoc} マイル減算。
     * <p/>
     *
     * @param applicationMemberId
     *            アプリ会員ID
     * @param mileAddSubRcptNum
     *            マイル加算減算受付番号
     * @param mileCategoryCode
     *            マイル種別コード
     * @param mileAmount
     *            マイル数
     * @param processedId
     *            処理ID
     * @return resultMap 処理結果Map
     */
    public Map<String, String> subMile(Long applicationMemberId, String mileAddSubRcptNum, String mileCategoryCode,
            Long mileAmount, String processedId) {

        // マイル残高情報出力件数初期化
        mileBalanceOutputCnt = 0;
        // マイル履歴情報出力件数
        int mileHistoryInsertCnt = 0;

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

        // マイル残高情報検索条件
        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("applicationMemberId", applicationMemberId);
        condition.put("objectYearMonth", objectYearMonth);
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("SELECT_MILE_BALANCE_INFO_SUB");
        SqlResultSet result = statement.retrieve(condition);

        // マイル減算を行う。
        updateMileBalanceInfoSub(result, applicationMemberId, mileAmount, processedId);

        // 論理削除日を導出する。
        CM010004Component cM010004Component = new CM010004Component();
        String deletedDate = cM010004Component.getDeletedDateMileYearly(
                Integer.parseInt(SystemRepository.getString("mile_control_data_retention_period")));

        // マイル履歴情報登録
        insertMileHistoryInfo(applicationMemberId, mileAddSubRcptNum, mileCategoryCode, mileAmount, processedId,
                deletedDate);
        // 出力データ(マイル履歴情報)(登録)件数をカウントアップする。
        mileHistoryInsertCnt++;

        Map<String, String> resultMap = new HashMap<String, String>();
        resultMap.put("mileBalanceOutputCnt", String.valueOf(mileBalanceOutputCnt));
        resultMap.put("mileHistoryInsertCnt", String.valueOf(mileHistoryInsertCnt));

        return resultMap;
    }

    /**
     * マイル残高情報（減算）更新。
     *
     * @param result
     *            マイル残高情報
     * @param applicationMemberId
     *            アプリ会員ID
     * @param mileAmount
     *            減算マイル数
     * @param processedId
     *            処理ID
     */
    private void updateMileBalanceInfoSub(SqlResultSet result, Long applicationMemberId, Long mileAmount,
            String processedId) {

        for (int i = 0; i < result.size(); i++) {
            // マイル残高
            Long mileBalance = result.get(i).getLong("MILE_BALANCE");
            // 対象年月
            String objectYearMonth = result.get(i).getString("OBJECT_YEAR_MONTH");
            // マイル残高 < 減算マイル数の場合
            if (mileBalance.longValue() < mileAmount.longValue()) {
                // 減算マイル数 = 減算マイル数 - マイル残高
                mileAmount = Long.valueOf(mileAmount.longValue() - mileBalance.longValue());
                // マイル残高 = 0;
                mileBalance = Long.valueOf(OpalDefaultConstants.MILE_BALANCE_ZERO);

                // マイル残高情報更新
                mileBalanceOutputCnt += updateMileBalanceInfo(applicationMemberId, objectYearMonth, mileBalance,
                        processedId);
            } else {
                // マイル残高 = マイル残高 - 減算マイル数
                mileBalance = Long.valueOf(mileBalance.longValue() - mileAmount.longValue());

                // マイル残高情報更新
                mileBalanceOutputCnt += updateMileBalanceInfo(applicationMemberId, objectYearMonth, mileBalance,
                        processedId);
                break;
            }
        }
    }

    /**
     * マイル残高情報登録。
     *
     * @param applicationMemberId
     *            アプリ会員ID
     * @param objectYearMonth
     *            対象年月
     * @param mileBalance
     *            マイル残高
     * @param processedId
     *            処理ID
     * @param deletedDate
     *            論理削除日
     */
    private void insertMileBalanceInfo(Long applicationMemberId, String objectYearMonth, Long mileBalance,
            String processedId, String deletedDate) {

        // マイル残高情報登録条件設定
        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("applicationMemberId", applicationMemberId);
        condition.put("objectYearMonth", objectYearMonth);
        condition.put("mileBalance", mileBalance);
        condition.put("insertUserId", processedId);
        condition.put("insertDateTime", SystemTimeUtil.getTimestamp());
        condition.put("updateUserId", processedId);
        condition.put("updateDateTime", SystemTimeUtil.getTimestamp());
        condition.put("deletedFlg", OpalDefaultConstants.DELETED_FLG_0);
        condition.put("deletedDate", deletedDate);
        condition.put("version", OpalDefaultConstants.VERSION);

        // マイル残高情報登録
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("INSERT_MILE_BALANCE_INFO_ADD");
        statement.executeUpdateByMap(condition);
    }

    /**
     * マイル残高情報更新。
     *
     * @param applicationMemberId
     *            アプリ会員ID
     * @param objectYearMonth
     *            対象年月
     * @param mileBalance
     *            マイル残高
     * @param processedId
     *            処理ID
     * @return マイル残高情報更新件数
     */
    private int updateMileBalanceInfo(Long applicationMemberId, String objectYearMonth, Long mileBalance,
            String processedId) {

        // マイル残高情報更新条件設定
        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("applicationMemberId", applicationMemberId);
        condition.put("objectYearMonth", objectYearMonth);
        condition.put("mileBalance", mileBalance);
        condition.put("updateUserId", processedId);
        condition.put("updateDateTime", SystemTimeUtil.getTimestamp());

        // マイル残高情報更新
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("UPDATE_MILE_BALANCE_INFO");
        return statement.executeUpdateByMap(condition);
    }

    /**
     * マイル履歴情報登録。
     *
     * @param applicationMemberId
     *            アプリ会員ID
     * @param mileAddSubRcptNum
     *            マイル加算減算受付番号
     * @param mileCategoryCode
     *            マイル種別コード
     * @param mileAmount
     *            マイル数
     * @param processedId
     *            処理ID
     * @param deletedDate
     *            論理削除日
     */
    private void insertMileHistoryInfo(Long applicationMemberId, String mileAddSubRcptNum, String mileCategoryCode,
            Long mileAmount, String processedId, String deletedDate) {

        // マイル履歴IDを採番する。
        Long mileHistoryId = IdGeneratorUtil.generateMileHistoryId();

        // マイル履歴登録用のSQL条件を設定する。
        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("mileHistoryId", mileHistoryId);
        condition.put("applicationMemberId", applicationMemberId);
        condition.put("mileAddSubRcptNum", mileAddSubRcptNum);
        condition.put("mileCategoryCode", mileCategoryCode);
        condition.put("mileAmount", mileAmount);
        condition.put("mileHistoryRegistDate", SystemTimeUtil.getDateString());
        condition.put("insertUserId", processedId);
        condition.put("insertDateTime", SystemTimeUtil.getTimestamp());
        condition.put("updateUserId", processedId);
        condition.put("updateDateTime", SystemTimeUtil.getTimestamp());
        condition.put("deletedFlg", OpalDefaultConstants.DELETED_FLG_0);
        condition.put("deletedDate", deletedDate);

        // マイル履歴を登録する。
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("INSERT_MILE_HISTORY_INFORMATION");
        statement.executeUpdateByMap(condition);
    }

}
