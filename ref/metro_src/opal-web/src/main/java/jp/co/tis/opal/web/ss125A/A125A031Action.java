package jp.co.tis.opal.web.ss125A;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import nablarch.common.dao.EntityList;
import nablarch.common.dao.UniversalDao;
import nablarch.core.date.SystemTimeUtil;
import nablarch.core.db.statement.ParameterizedSqlPStatement;
import nablarch.core.db.statement.SqlResultSet;
import nablarch.core.message.MessageLevel;
import nablarch.core.message.MessageUtil;
import nablarch.fw.web.HttpResponse;

import jp.co.tis.opal.common.constants.OpalCodeConstants;
import jp.co.tis.opal.common.constants.OpalDefaultConstants;
import jp.co.tis.opal.common.entity.FamilyMemServiceInfoEntity;
import jp.co.tis.opal.common.entity.PartnerMemServiceInfoEntity;
import jp.co.tis.opal.web.common.ResponseData.ResponseErrorData;
import jp.co.tis.opal.web.common.constants.CheckResultConstants;
import jp.co.tis.opal.web.common.rest.AbstractRestBaseAction;

/**
 * A125A03:乗車適用日選択APIのアクションクラス。
 *
 * @author 陳
 * @since 1.0
 */
public class A125A031Action extends AbstractRestBaseAction<A125AACRRequestData> {

    /** API */
    private static final String API_SERVER_ID = "A125A031";

    /** システム日付 */
    private String systemDate;

    /** エラーの乗車適用日 */
    private String errorRideApplyDate;

    /** 乗車適用日登録上限回数 */
    private int upperLimitTimes;

    /**
     * 乗車適用日選択API
     *
     * @param requestData
     *            乗車適用日選択要求電文
     * @return HTTPレスポンス
     */
    @Consumes(MediaType.APPLICATION_JSON)
    @Valid
    public HttpResponse chooseRideApplyDate(A125AACRRequestData requestData) {
        HttpResponse responseData = super.execute(requestData);
        return responseData;
    }

    /**
     * 処理詳細ロジック
     *
     * @param requestData
     *            乗車適用日選択要求電文
     * @return チェックの結果
     */
    @Override
    protected int executeLogic(A125AACRRequestData requestData) {

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

        // システム日付を取得する。
        systemDate = SystemTimeUtil.getDateString();

        // 乗車適用日登録上限回数取得
        upperLimitTimes = 0;
        SqlResultSet rideUpperLimitTimes = getRideUpperLimitTimes(requestData.getAplData().getServiceDivision());
        if (!rideUpperLimitTimes.isEmpty()) {
            upperLimitTimes = rideUpperLimitTimes.get(0).getInteger("UPPER_LIMIT_TIMES").intValue();
        }
        // 乗車適用日登録上限回数チェック
        if (requestData.getAplData().getRideApplyDateInfoList().size() > upperLimitTimes) {
            // 超える場合、上限回数超過エラーとして、応答電文を以下の通り設定して、「HTTPアクセスログ出力」を行う。
            return CheckResultConstants.UPPER_LIMIT_TIMES_CHECK_ERROR;
        }

        // 乗車適用日選択要求電文.サービス区分が"1"(家族会員サービス)の場合
        if (OpalCodeConstants.ServiceDivision.SERVICE_DIVISION_1
                .equals(requestData.getAplData().getServiceDivision())) {
            // 家族会員サービス存在チェック
            EntityList<FamilyMemServiceInfoEntity> familyMemServiceInfo = getFamilyMemServiceCtrlId(
                    requestData.getAplData().getApplicationMemberId());
            // 登録されていない場合、応答電文を以下の通り設定して、「HTTPアクセスログ出力」を行う。
            if (familyMemServiceInfo.isEmpty()) {
                return CheckResultConstants.FAMILY_MEM_SERVICE_CHECK_ERROR;
            }

            // 取得した家族会員サービス管理ID
            Long familyMemServiceCtrlId = familyMemServiceInfo.get(0).getFamilyMemServiceCtrlId();

            // 家族乗車適用日選択状況初期化
            updateRideApplyDateClear(familyMemServiceCtrlId,
                    systemDate.substring(OpalDefaultConstants.POSITION_YEAR_MONTH_START,
                            OpalDefaultConstants.POSITION_YEAR_MONTH_END),
                    "UPDATE_FAMILY_RIDE_APPLY_DATE_CLEAR");

            // 家族乗車適用日選択
            for (A125AACRBodyDateForm date : requestData.getAplData().getRideApplyDateInfoList()) {
                // 家族乗車適用日
                String rideApplyDate = date.getRideApplyDate();

                int result = updateRideApplyDate(familyMemServiceCtrlId, rideApplyDate,
                        "UPDATE_FAMILY_RIDE_APPLY_DATE");
                // データが更新されない場合、家族乗車適用日存在しないエラーとして、応答電文を以下の通り設定して、
                // 「HTTPアクセスログ出力」を行う。
                if (result == 0) {
                    errorRideApplyDate = editDate(rideApplyDate);
                    return CheckResultConstants.FAMILY_RIDE_APPLY_DATE_CHECK_ERROR;
                }
            }

            // 乗車適用日選択要求電文.サービス区分が"2"(パートナー会員サービス)の場合
        } else if (OpalCodeConstants.ServiceDivision.SERVICE_DIVISION_2
                .equals(requestData.getAplData().getServiceDivision())) {

            // パートナー会員サービス存在チェック
            EntityList<PartnerMemServiceInfoEntity> partnerMemServiceInfo = getPartnerMemServiceCtrlId(
                    requestData.getAplData().getApplicationMemberId());
            // 登録されていない場合、応答電文を以下の通り設定して、「HTTPアクセスログ出力」を行う。
            if (partnerMemServiceInfo.isEmpty()) {
                return CheckResultConstants.PARTNER_MEM_SERVICE_CHECK_ERROR;
            }

            // 取得したパートナー会員サービス管理ID
            Long partnerMemServiceCtrlId = partnerMemServiceInfo.get(0).getPartnerMemServiceCtrlId();

            // パートナー乗車適用日選択状況初期化
            updateRideApplyDateClear(partnerMemServiceCtrlId,
                    systemDate.substring(OpalDefaultConstants.POSITION_YEAR_MONTH_START,
                            OpalDefaultConstants.POSITION_YEAR_MONTH_END),
                    "UPDATE_PARTNER_RIDE_APPLY_DATE_CLEAR");

            // パートナー乗車適用日選択
            for (A125AACRBodyDateForm date : requestData.getAplData().getRideApplyDateInfoList()) {
                // パートナー乗車適用日
                String rideApplyDate = date.getRideApplyDate();

                int result = updateRideApplyDate(partnerMemServiceCtrlId, rideApplyDate,
                        "UPDATE_PARTNER_RIDE_APPLY_DATE");
                // データが更新されない場合、パートナー乗車適用日存在しないエラーとして、応答電文を以下の通り設定して、
                // 「HTTPアクセスログ出力」を行う。
                if (result == 0) {
                    errorRideApplyDate = editDate(rideApplyDate);
                    return CheckResultConstants.PARTNER_RIDE_APPLY_DATE_CHECK_ERROR;
                }
            }
        }
        return CheckResultConstants.CHECK_OK;
    }

    /**
     * HTTPレスポンスを設定する。
     *
     * @param requestData
     *            乗車適用日選択要求電文
     *
     * @param result
     *            チェックの結果
     *
     * @return HTTPレスポンス
     * @throws IOException
     *             IO異常
     */
    @Override
    protected HttpResponse responseBuilder(A125AACRRequestData requestData, int result) throws IOException {

        // 応答電文設定
        A125AACSResponseData responseData = new A125AACSResponseData();

        // 応答電文.処理結果コード
        responseData.setResultCode(getResultCode(result));
        if (result == CheckResultConstants.APL_MEM_DATA_ISNULL) {
            // 応答電文.エラーメッセージ
            ResponseErrorData error = new ResponseErrorData();
            error.setId("MA125A0310");
            error.setMessage(MessageUtil.createMessage(MessageLevel.ERROR, "MA125A0310").formatMessage());
            responseData.setError(error);
        } else if (result == CheckResultConstants.APPLICATION_MEMBER_STATUS_CODE_NOT_A) {
            // 応答電文.エラーメッセージ
            ResponseErrorData error = new ResponseErrorData();
            error.setId("MA125A0311");
            error.setMessage(MessageUtil.createMessage(MessageLevel.ERROR, "MA125A0311").formatMessage());
            responseData.setError(error);
        } else if (result == CheckResultConstants.UPPER_LIMIT_TIMES_CHECK_ERROR) {
            // 応答電文.エラーメッセージ
            ResponseErrorData error = new ResponseErrorData();
            error.setId("MA125A0305");
            error.setMessage(MessageUtil.createMessage(MessageLevel.ERROR, "MA125A0305").formatMessage());
            responseData.setError(error);
        } else if (result == CheckResultConstants.FAMILY_MEM_SERVICE_CHECK_ERROR) {
            // 応答電文.エラーメッセージ
            ResponseErrorData error = new ResponseErrorData();
            error.setId("MA125A0306");
            error.setMessage(MessageUtil.createMessage(MessageLevel.ERROR, "MA125A0306").formatMessage());
            responseData.setError(error);
        } else if (result == CheckResultConstants.FAMILY_RIDE_APPLY_DATE_CHECK_ERROR) {
            // 応答電文.エラーメッセージ
            ResponseErrorData error = new ResponseErrorData();
            error.setId("MA125A0307");
            error.setMessage(
                    MessageUtil.createMessage(MessageLevel.ERROR, "MA125A0307", errorRideApplyDate).formatMessage());
            responseData.setError(error);
        } else if (result == CheckResultConstants.PARTNER_MEM_SERVICE_CHECK_ERROR) {
            // 応答電文.エラーメッセージ
            ResponseErrorData error = new ResponseErrorData();
            error.setId("MA125A0308");
            error.setMessage(MessageUtil.createMessage(MessageLevel.ERROR, "MA125A0308").formatMessage());
            responseData.setError(error);
        } else if (result == CheckResultConstants.PARTNER_RIDE_APPLY_DATE_CHECK_ERROR) {
            // 応答電文.エラーメッセージ
            ResponseErrorData error = new ResponseErrorData();
            error.setId("MA125A0309");
            error.setMessage(
                    MessageUtil.createMessage(MessageLevel.ERROR, "MA125A0309", errorRideApplyDate).formatMessage());
            responseData.setError(error);
        }

        HttpResponse response = super.setHttpResponseData(responseData, result);

        return response;

    }

    /**
     * アプリ会員情報・家族会員サービス情報取得
     *
     * @param applicationMemberId
     *            アプリ会員ID
     *
     * @return アプリ会員情報・家族会員サービス情報
     */
    private EntityList<FamilyMemServiceInfoEntity> getFamilyMemServiceCtrlId(String applicationMemberId) {

        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("applicationMemberId", applicationMemberId);
        condition.put("applicationMemberStatusCode", OpalCodeConstants.AplMemStatusCode.OP_AUTH_APL_MEM);
        condition.put("registStatusDivision", OpalCodeConstants.RegistStatusDivision.REGIST_STATUS_DIVISION_1);
        // 取得対象レコードを排他制御対象としてロックを取得する。(悲観的ロック)
        EntityList<FamilyMemServiceInfoEntity> result = UniversalDao.findAllBySqlFile(FamilyMemServiceInfoEntity.class,
                "SELECT_FAMILY_MEM_SERVICE_INFO_BY_APL", condition);

        return result;
    }

    /**
     * アプリ会員情報・パートナー会員サービス情報取得
     *
     * @param applicationMemberId
     *            アプリ会員ID
     *
     * @return アプリ会員情報・パートナー会員サービス情報
     */
    private EntityList<PartnerMemServiceInfoEntity> getPartnerMemServiceCtrlId(String applicationMemberId) {

        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("applicationMemberId", applicationMemberId);
        condition.put("applicationMemberStatusCode", OpalCodeConstants.AplMemStatusCode.OP_AUTH_APL_MEM);
        condition.put("admitStatusDivision", OpalCodeConstants.AdmitStatusDivision.ADMIT_STATUS_DIVISION_1);
        // 取得対象レコードを排他制御対象としてロックを取得する。(悲観的ロック)
        EntityList<PartnerMemServiceInfoEntity> result = UniversalDao.findAllBySqlFile(
                PartnerMemServiceInfoEntity.class, "SELECT_PARTNER_MEM_SERVICE_INFO_BY_APL", condition);

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
     * 乗車適用日選択状況初期化
     *
     * @param memServiceCtrlId
     *            家族会員サービス管理ID/パートナー会員サービス管理ID
     * @param rideApplyYearMonth
     *            乗車適用年月
     * @param sqlId
     *            SQL_ID
     */
    private void updateRideApplyDateClear(Long memServiceCtrlId, String rideApplyYearMonth, String sqlId) {

        // 乗車適用日選択状況初期化用のSQL条件を設定する。
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement(sqlId);

        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("userChooseDivision", OpalCodeConstants.UserChooseDivision.USER_CHOOSE_DIVISION_0);
        condition.put("updateUserId", API_SERVER_ID);
        condition.put("updateDateTime", SystemTimeUtil.getTimestamp());
        condition.put("memServiceCtrlId", memServiceCtrlId);
        condition.put("rideApplyYearMonth", rideApplyYearMonth);

        // 実行する。
        statement.executeUpdateByMap(condition);
    }

    /**
     * 乗車適用日選択
     *
     * @param memServiceCtrlId
     *            家族会員サービス管理ID/パートナー会員サービス管理ID
     * @param rideApplyDate
     *            乗車適用日
     * @param sqlId
     *            SQL_ID
     *
     * @return 実行結果
     */
    private int updateRideApplyDate(Long memServiceCtrlId, String rideApplyDate, String sqlId) {

        // 乗車適用日選択用のSQL条件を設定する。
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement(sqlId);

        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("userChooseDivision", OpalCodeConstants.UserChooseDivision.USER_CHOOSE_DIVISION_1);
        condition.put("updateUserId", API_SERVER_ID);
        condition.put("updateDateTime", SystemTimeUtil.getTimestamp());
        condition.put("memServiceCtrlId", memServiceCtrlId);
        condition.put("rideApplyDate", rideApplyDate);

        // 実行する。
        return statement.executeUpdateByMap(condition);
    }

    /**
     * アプリ会員情報取得
     *
     * @param form
     *            乗車適用日選択フォーム
     *
     * @return アプリ会員情報
     */
    private SqlResultSet getAplMemInfo(A125AACRBodyForm form) {

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

    /**
     * <p>
     * 日付編集。<br />
     * YYYYMMDD形式の日付をYYYY/MM/DD形式に編集する。<br />
     * </p>
     *
     * @param pstrString
     *            入力文字列
     * @return strReturn 出力文字列
     */
    private String editDate(String pstrString) {
        return pstrString.replaceAll("^(\\d{4})(\\d{2})(\\d{2})$", "$1/$2/$3");
    }
}
