package jp.co.tis.opal.web.ss125A;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import nablarch.core.date.SystemTimeUtil;
import nablarch.core.db.statement.ParameterizedSqlPStatement;
import nablarch.core.db.statement.SqlResultSet;
import nablarch.core.db.statement.SqlRow;
import nablarch.core.message.MessageLevel;
import nablarch.core.message.MessageUtil;
import nablarch.core.util.DateUtil;
import nablarch.fw.web.HttpResponse;

import jp.co.tis.opal.common.constants.OpalCodeConstants;
import jp.co.tis.opal.common.constants.OpalDefaultConstants;
import jp.co.tis.opal.web.common.ResponseData.ResponseErrorData;
import jp.co.tis.opal.web.common.constants.CheckResultConstants;
import jp.co.tis.opal.web.common.rest.AbstractRestBaseAction;

/**
 * A125A02:乗車適用日参照APIのアクションクラス。
 *
 * @author 陳
 * @since 1.0
 */
public class A125A021Action extends AbstractRestBaseAction<A125AABRRequestData> {

    /** 前月乗車適用日情報 */
    private List<Map<String, Object>> lastMonthRideApplyDateInfo = new ArrayList<Map<String, Object>>();

    /** 今月乗車適用日情報 */
    private List<Map<String, Object>> thisMonthRideApplyDateInfo = new ArrayList<Map<String, Object>>();

    /** システム日付 */
    private String systemDate;

    /** 乗車適用日登録上限回数 */
    private int upperLimitTimes;

    /**
     * 乗車適用日参照API
     *
     * @param requestData
     *            乗車適用日参照要求電文
     * @return HTTPレスポンス
     */
    @Consumes(MediaType.APPLICATION_JSON)
    @Valid
    public HttpResponse getRideApplyDate(A125AABRRequestData requestData) {
        HttpResponse responseData = super.execute(requestData);
        return responseData;
    }

    /**
     * 処理詳細ロジック
     *
     * @param requestData
     *            乗車適用日参照要求電文
     * @return チェックの結果
     */
    @Override
    protected int executeLogic(A125AABRRequestData requestData) {

        // アプリ会員情報取得
        SqlResultSet aplMemInfo = getAplMemInfo(requestData.getAplData());

        // データが取得されない場合、無効なアプリ会員エラーとして、応答電文を以下の通り設定して、「HTTPアクセスログ出力」を行う。
        if (aplMemInfo.isEmpty()) {
            return CheckResultConstants.APL_MEM_DATA_ISNULL;
        }

        // サービス利用可チェック
        if (OpalCodeConstants.AplMemStatusCode.NOT_OP_MEM
                .equals(aplMemInfo.get(0).getString("APPLICATION_MEMBER_STATUS_CODE"))) {
            return CheckResultConstants.APPLICATION_MEMBER_STATUS_CODE_NOT_A;
        }

        // システム日付を乗車適用日として取得する。
        systemDate = SystemTimeUtil.getDateString();

        // 乗車適用日登録上限回数取得
        upperLimitTimes = 0;
        SqlResultSet rideUpperLimitTimes = getRideUpperLimitTimes(requestData.getAplData().getServiceDivision());
        if (!rideUpperLimitTimes.isEmpty()) {
            upperLimitTimes = rideUpperLimitTimes.get(0).getInteger("UPPER_LIMIT_TIMES").intValue();
        }

        // 前月家族/パートナー乗車適用日取得
        String lastDate = DateUtil.addMonth(systemDate, OpalDefaultConstants.ADD_MONTH_1_MINUS);

        // 乗車適用日参照要求電文.サービス区分が"1"(家族会員サービス)の場合
        if (OpalCodeConstants.ServiceDivision.SERVICE_DIVISION_1
                .equals(requestData.getAplData().getServiceDivision())) {
            // 家族会員サービス存在チェック
            SqlResultSet familyMemServiceInfo = getFamilyMemServiceCtrlId(
                    requestData.getAplData().getApplicationMemberId());
            // データが存在しない場合、応答電文を以下の通り設定して、「HTTPアクセスログ出力」を行う。
            if (familyMemServiceInfo.isEmpty()) {
                return CheckResultConstants.FAMILY_MEM_SERVICE_CHECK_ERROR;
            }

            // 取得した家族会員サービス管理ID
            Long familyMemServiceCtrlId = familyMemServiceInfo.get(0).getLong("FAMILY_MEM_SERVICE_CTRL_ID");
            // 家族乗車適用日情報取得
            SqlResultSet lastMonthFamilyRideApplyDate = getRideApplyDate(familyMemServiceCtrlId,
                    lastDate.substring(OpalDefaultConstants.POSITION_YEAR_MONTH_START,
                            OpalDefaultConstants.POSITION_YEAR_MONTH_END),
                    "SELECT_FAMILY_RIDE_APPLY_DATE_LAST_MONTH");

            if (!lastMonthFamilyRideApplyDate.isEmpty()) {
                for (SqlRow row : lastMonthFamilyRideApplyDate) {
                    Map<String, Object> map = new LinkedHashMap<String, Object>();
                    // 乗車適用日
                    map.put("rideApplyDate", row.getString("RIDE_APPLY_DATE"));
                    // ユーザ選択区分
                    map.put("userChooseDivision", row.getString("USER_CHOOSE_DIVISION"));

                    lastMonthRideApplyDateInfo.add(map);
                }
            }

            // 今月家族乗車適用日情報取得
            SqlResultSet thisMonthFamilyRideApplyDate = getRideApplyDate(familyMemServiceCtrlId,
                    systemDate.substring(OpalDefaultConstants.POSITION_YEAR_MONTH_START,
                            OpalDefaultConstants.POSITION_YEAR_MONTH_END),
                    "SELECT_FAMILY_RIDE_APPLY_DATE_THIS_MONTH");

            if (!thisMonthFamilyRideApplyDate.isEmpty()) {
                for (SqlRow row : thisMonthFamilyRideApplyDate) {
                    Map<String, Object> map = new LinkedHashMap<String, Object>();
                    // 乗車適用日
                    map.put("rideApplyDate", row.getString("RIDE_APPLY_DATE"));
                    // ユーザ選択区分
                    map.put("userChooseDivision", row.getString("USER_CHOOSE_DIVISION"));

                    thisMonthRideApplyDateInfo.add(map);
                }
            }
        // 乗車適用日参照要求電文.サービス区分が"2"(パートナー会員サービス)の場合
        } else if (OpalCodeConstants.ServiceDivision.SERVICE_DIVISION_2
                .equals(requestData.getAplData().getServiceDivision())) {

            // パートナー会員サービス存在チェック
            SqlResultSet partnerMemServiceInfo = getPartnerMemServiceCtrlId(
                    requestData.getAplData().getApplicationMemberId());
            // データが存在しない場合、応答電文を以下の通り設定して、「HTTPアクセスログ出力」を行う。
            if (partnerMemServiceInfo.isEmpty()) {
                return CheckResultConstants.PARTNER_MEM_SERVICE_CHECK_ERROR;
            }

            // 取得したパートナー会員サービス管理ID
            Long partnerMemServiceCtrlId = partnerMemServiceInfo.get(0).getLong("PARTNER_MEM_SERVICE_CTRL_ID");
            // パートナー乗車適用日情報取得
            SqlResultSet lastMonthPartnerRideApplyDate = getRideApplyDate(partnerMemServiceCtrlId,
                    lastDate.substring(OpalDefaultConstants.POSITION_YEAR_MONTH_START,
                            OpalDefaultConstants.POSITION_YEAR_MONTH_END),
                    "SELECT_PARTNER_RIDE_APPLY_DATE_LAST_MONTH");

            if (!lastMonthPartnerRideApplyDate.isEmpty()) {
                for (SqlRow row : lastMonthPartnerRideApplyDate) {
                    Map<String, Object> map = new LinkedHashMap<String, Object>();
                    // 乗車適用日
                    map.put("rideApplyDate", row.getString("RIDE_APPLY_DATE"));
                    // ユーザ選択区分
                    map.put("userChooseDivision", row.getString("USER_CHOOSE_DIVISION"));

                    lastMonthRideApplyDateInfo.add(map);
                }
            }

            // 今月パートナー乗車適用日情報取得
            SqlResultSet thisMonthPartnerRideApplyDate = getRideApplyDate(partnerMemServiceCtrlId,
                    systemDate.substring(OpalDefaultConstants.POSITION_YEAR_MONTH_START,
                            OpalDefaultConstants.POSITION_YEAR_MONTH_END),
                    "SELECT_PARTNER_RIDE_APPLY_DATE_THIS_MONTH");

            if (!thisMonthPartnerRideApplyDate.isEmpty()) {
                for (SqlRow row : thisMonthPartnerRideApplyDate) {
                    Map<String, Object> map = new LinkedHashMap<String, Object>();
                    // 乗車適用日
                    map.put("rideApplyDate", row.getString("RIDE_APPLY_DATE"));
                    // ユーザ選択区分
                    map.put("userChooseDivision", row.getString("USER_CHOOSE_DIVISION"));

                    thisMonthRideApplyDateInfo.add(map);
                }
            }
        }
        return CheckResultConstants.CHECK_OK;
    }

    /**
     * HTTPレスポンスを設定する。
     *
     * @param requestData
     *            乗車適用日参照要求電文
     *
     * @param result
     *            チェックの結果
     *
     * @return HTTPレスポンス
     * @throws IOException
     *             IO異常
     */
    @Override
    protected HttpResponse responseBuilder(A125AABRRequestData requestData, int result) throws IOException {

        // 応答電文設定
        A125AABSResponseData responseData = new A125AABSResponseData();

        // 応答電文.処理結果コード
        responseData.setResultCode(getResultCode(result));
        if (result == CheckResultConstants.APL_MEM_DATA_ISNULL) {
            // 応答電文.エラーメッセージ
            ResponseErrorData error = new ResponseErrorData();
            error.setId("MA125A0203");
            error.setMessage(MessageUtil.createMessage(MessageLevel.ERROR, "MA125A0203").formatMessage());
            responseData.setError(error);
        } else if (result == CheckResultConstants.APPLICATION_MEMBER_STATUS_CODE_NOT_A) {
            // 応答電文.エラーメッセージ
            ResponseErrorData error = new ResponseErrorData();
            error.setId("MA125A0204");
            error.setMessage(MessageUtil.createMessage(MessageLevel.ERROR, "MA125A0204").formatMessage());
            responseData.setError(error);
        } else if (result == CheckResultConstants.FAMILY_MEM_SERVICE_CHECK_ERROR) {
            // 応答電文.エラーメッセージ
            ResponseErrorData error = new ResponseErrorData();
            error.setId("MA125A0201");
            error.setMessage(MessageUtil.createMessage(MessageLevel.ERROR, "MA125A0201").formatMessage());
            responseData.setError(error);
        } else if (result == CheckResultConstants.PARTNER_MEM_SERVICE_CHECK_ERROR) {
            // 応答電文.エラーメッセージ
            ResponseErrorData error = new ResponseErrorData();
            error.setId("MA125A0202");
            error.setMessage(MessageUtil.createMessage(MessageLevel.ERROR, "MA125A0202").formatMessage());
            responseData.setError(error);
        } else if (result == CheckResultConstants.CHECK_OK) {
            // 上限回数
            responseData.setUpperLimitTimes(String.valueOf(upperLimitTimes));

            // 前月乗車適用日情報
            if (lastMonthRideApplyDateInfo.size() > 0) {
                responseData.setLastMonthRideApplyDateInfo(lastMonthRideApplyDateInfo);
            }
            // 今月乗車適用日情報
            if (thisMonthRideApplyDateInfo.size() > 0) {
                responseData.setThisMonthRideApplyDateInfo(thisMonthRideApplyDateInfo);
            }
        }

        HttpResponse response = super.setHttpResponseData(responseData, result);

        return response;

    }

    /**
     * 家族会員サービス情報取得
     *
     * @param applicationMemberId
     *            アプリ会員ID
     *
     * @return 家族会員サービス情報
     */
    private SqlResultSet getFamilyMemServiceCtrlId(String applicationMemberId) {

        // アプリ会員情報・家族会員サービス情報取得用のSQL条件を設定する。
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("SELECT_FAMILY_MEM_SERVICE_CTRL_ID");

        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("applicationMemberId", applicationMemberId);
        condition.put("applicationMemberStatusCode", OpalCodeConstants.AplMemStatusCode.OP_AUTH_APL_MEM);
        condition.put("registStatusDivision", OpalCodeConstants.RegistStatusDivision.REGIST_STATUS_DIVISION_1);

        // 実行する。
        SqlResultSet result = statement.retrieve(condition);

        return result;
    }

    /**
     * パートナー会員サービス情報取得
     *
     * @param applicationMemberId
     *            アプリ会員ID
     *
     * @return パートナー会員サービス情報
     */
    private SqlResultSet getPartnerMemServiceCtrlId(String applicationMemberId) {

        // パートナー会員サービス情報取得用のSQL条件を設定する。
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("SELECT_PARTNER_MEM_SERVICE_CTRL_ID");

        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("applicationMemberId", applicationMemberId);
        condition.put("applicationMemberStatusCode", OpalCodeConstants.AplMemStatusCode.OP_AUTH_APL_MEM);
        condition.put("admitStatusDivision", OpalCodeConstants.AdmitStatusDivision.ADMIT_STATUS_DIVISION_1);

        // 実行する。
        SqlResultSet result = statement.retrieve(condition);

        return result;
    }

    /**
     * 乗車適用日登録上限回数取得
     *
     * @param serviceDivision
     *            サービス区分
     *
     * @return 乗車適用日登録上限回数
     */
    private SqlResultSet getRideUpperLimitTimes(String serviceDivision) {

        // 乗車適用日登録上限回数取得用のSQL条件を設定する。
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("SELECT_RIDE_UPPER_LIMIT_TIMES");

        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("serviceDivision", serviceDivision);
        condition.put("rideApplyYearMonth", systemDate.substring(OpalDefaultConstants.POSITION_YEAR_MONTH_START,
                OpalDefaultConstants.POSITION_YEAR_MONTH_END));

        // 実行する。
        SqlResultSet result = statement.retrieve(condition);
        return result;
    }

    /**
     * 乗車適用日情報取得
     *
     * @param memServiceCtrlId
     *            家族会員サービス管理ID/パートナー会員サービス管理ID
     * @param rideApplyYearMonth
     *            乗車適用年月
     * @param sqlId
     *            SQL_ID
     *
     * @return 乗車適用日情報
     */
    private SqlResultSet getRideApplyDate(Long memServiceCtrlId, String rideApplyYearMonth, String sqlId) {

        // 乗車適用日情報取得用のSQL条件を設定する。
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement(sqlId);

        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("memServiceCtrlId", memServiceCtrlId);
        condition.put("rideApplyYearMonth", rideApplyYearMonth);

        // 実行する。
        SqlResultSet result = statement.retrieve(condition);
        return result;
    }

    /**
     * アプリ会員情報取得
     *
     * @param form
     *            乗車適用日参照フォーム
     *
     * @return アプリ会員情報
     */
    private SqlResultSet getAplMemInfo(A125AABRBodyForm form) {

        // アプリ会員情報取得用のSQL条件を設定する。
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("SELECT_APL_MEM_INFO");

        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("applicationMemberId", form.getApplicationMemberId());
        condition.put("opAuthAplMem", OpalCodeConstants.AplMemStatusCode.OP_AUTH_APL_MEM);
        condition.put("notOpMem", OpalCodeConstants.AplMemStatusCode.NOT_OP_MEM);
        // 実行する。
        SqlResultSet result = statement.retrieve(condition);
        return result;
    }

}
