package jp.co.tis.opal.web.ss125A;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import nablarch.core.date.SystemTimeUtil;
import nablarch.core.db.statement.ParameterizedSqlPStatement;
import nablarch.core.db.statement.SqlResultSet;
import nablarch.core.db.statement.exception.DuplicateStatementException;
import nablarch.core.message.MessageLevel;
import nablarch.core.message.MessageUtil;
import nablarch.core.repository.SystemRepository;
import nablarch.fw.web.HttpResponse;

import jp.co.tis.opal.common.component.CM010004Component;
import jp.co.tis.opal.common.constants.OpalCodeConstants;
import jp.co.tis.opal.common.constants.OpalDefaultConstants;
import jp.co.tis.opal.web.common.ResponseData.ResponseErrorData;
import jp.co.tis.opal.web.common.constants.CheckResultConstants;
import jp.co.tis.opal.web.common.rest.AbstractRestBaseAction;

/**
 * A125A01:乗車適用日登録APIのアクションクラス。
 *
 * @author 陳
 * @since 1.0
 */
public class A125A011Action extends AbstractRestBaseAction<A125AAARRequestData> {

    /** API */
    private static final String API_SERVER_ID = "A125A011";

    /** 家族乗車適用日管理データ保持期間(月) */
    private static final String FAMILY_RETENTION_PERIOD = "family_ride_apply_date_retention_period";

    /** パートナー乗車適用日管理データ保持期間(月) */
    private static final String PARTNER_RETENTION_PERIOD = "partner_ride_apply_date_retention_period";

    /** 乗車適用日 */
    private String systemDate;

    /**
     * 乗車適用日登録API
     *
     * @param requestData
     *            乗車適用日登録要求電文
     * @return HTTPレスポンス
     */
    @Consumes(MediaType.APPLICATION_JSON)
    @Valid
    public HttpResponse insertRideApplyDate(A125AAARRequestData requestData) {
        HttpResponse responseData = super.execute(requestData);
        return responseData;
    }

    /**
     * 処理詳細ロジック
     *
     * @param requestData
     *            処理詳細ロジック
     * @return チェックの結果
     */
    @Override
    protected int executeLogic(A125AAARRequestData requestData) {

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

        // 乗車適用日登録要求電文.サービス区分が"1"(家族会員サービス)の場合
        if (OpalCodeConstants.ServiceDivision.SERVICE_DIVISION_1
                .equals(requestData.getAplData().getServiceDivision())) {
            // 家族会員サービス存在チェック
            SqlResultSet familyMemServiceInfo = getFamilyMemServiceCtrlId(
                    requestData.getAplData().getApplicationMemberId());
            // 登録されていない場合、応答電文を以下の通り設定して、「HTTPアクセスログ出力」を行う。
            if (familyMemServiceInfo.isEmpty()) {
                return CheckResultConstants.FAMILY_MEM_SERVICE_CHECK_ERROR;
            }

            // 取得した家族会員サービス管理ID
            Long familyMemServiceCtrlId = familyMemServiceInfo.get(0).getLong("FAMILY_MEM_SERVICE_CTRL_ID");
            // 家族乗車適用日存在チェック
            SqlResultSet familyRideApplyDate = getRideApplyDate(familyMemServiceCtrlId,
                    "SELECT_FAMILY_RIDE_APPLY_DATE");

            // 存在する場合、応答電文を以下の通り設定して、「HTTPアクセスログ出力」を行う。
            if (!familyRideApplyDate.isEmpty()) {
                return CheckResultConstants.FAMILY_RIDE_APPLY_DATE_CHECK_ERROR;
            }

            // 論理削除日の算出
            int monthSpan = Integer.valueOf(SystemRepository.get(FAMILY_RETENTION_PERIOD));

            CM010004Component cm010004Component = new CM010004Component();
            String deletedDate = cm010004Component.getDeletedDateMonthly(monthSpan);

            try {
                // 家族乗車適用日登録
                insertRideApplyDate(familyMemServiceCtrlId, deletedDate, "INSERT_FAMILY_RIDE_APPLY_DATE");
            } catch (DuplicateStatementException e) {
                // 一意性エラーが発生する場合、応答電文を以下の通り設定して、「HTTPアクセスログ出力」を行う。
                return CheckResultConstants.FAMILY_RIDE_APPLY_DATE_CHECK_ERROR;
            }

        } else if (OpalCodeConstants.ServiceDivision.SERVICE_DIVISION_2
                .equals(requestData.getAplData().getServiceDivision())) {
            // 乗車適用日登録要求電文.サービス区分が"2"(パートナー会員サービス)の場合

            // パートナー会員サービス存在チェック
            SqlResultSet partnerMemServiceInfo = getPartnerMemServiceCtrlId(
                    requestData.getAplData().getApplicationMemberId());
            // 登録されていない場合、応答電文を以下の通り設定して、「HTTPアクセスログ出力」を行う。
            if (partnerMemServiceInfo.isEmpty()) {
                return CheckResultConstants.PARTNER_MEM_SERVICE_CHECK_ERROR;
            }

            // 取得したパートナー会員サービス管理ID
            Long partnerMemServiceCtrlId = partnerMemServiceInfo.get(0).getLong("PARTNER_MEM_SERVICE_CTRL_ID");
            // パートナー乗車適用日存在チェック
            SqlResultSet partnerRideApplyDate = getRideApplyDate(partnerMemServiceCtrlId,
                    "SELECT_PARTNER_RIDE_APPLY_DATE");

            // 存在する場合、応答電文を以下の通り設定して、「HTTPアクセスログ出力」を行う。
            if (!partnerRideApplyDate.isEmpty()) {
                return CheckResultConstants.PARTNER_RIDE_APPLY_DATE_CHECK_ERROR;
            }

            // 論理削除日の算出
            int monthSpan = Integer.valueOf(SystemRepository.get(PARTNER_RETENTION_PERIOD));

            CM010004Component cm010004Component = new CM010004Component();
            String deletedDate = cm010004Component.getDeletedDateMonthly(monthSpan);

            try {
                // パートナー乗車適用日登録
                insertRideApplyDate(partnerMemServiceCtrlId, deletedDate, "INSERT_PARTNER_RIDE_APPLY_DATE");
            } catch (DuplicateStatementException e) {
                // 一意性エラーが発生する場合、応答電文を以下の通り設定して、「HTTPアクセスログ出力」を行う。
                return CheckResultConstants.PARTNER_RIDE_APPLY_DATE_CHECK_ERROR;
            }
        }
        return CheckResultConstants.CHECK_OK;
    }

    /**
     * HTTPレスポンスを設定する。
     *
     * @param requestData
     *            乗車適用日登録要求電文
     *
     * @param result
     *            チェックの結果
     *
     * @return HTTPレスポンス
     * @throws IOException
     *             IO異常
     */
    @Override
    protected HttpResponse responseBuilder(A125AAARRequestData requestData, int result) throws IOException {

        // 応答電文設定
        A125AAASResponseData responseData = new A125AAASResponseData();

        // 応答電文.処理結果コード
        responseData.setResultCode(getResultCode(result));
        if (result == CheckResultConstants.APL_MEM_DATA_ISNULL) {
            // 応答電文.エラーメッセージ
            ResponseErrorData error = new ResponseErrorData();
            error.setId("MA125A0105");
            error.setMessage(MessageUtil.createMessage(MessageLevel.ERROR, "MA125A0105").formatMessage());
            responseData.setError(error);
        } else if (result == CheckResultConstants.APPLICATION_MEMBER_STATUS_CODE_NOT_A) {
            // 応答電文.エラーメッセージ
            ResponseErrorData error = new ResponseErrorData();
            error.setId("MA125A0106");
            error.setMessage(MessageUtil.createMessage(MessageLevel.ERROR, "MA125A0106").formatMessage());
            responseData.setError(error);
        } else if (result == CheckResultConstants.FAMILY_MEM_SERVICE_CHECK_ERROR) {
            // 応答電文.エラーメッセージ
            ResponseErrorData error = new ResponseErrorData();
            error.setId("MA125A0101");
            error.setMessage(MessageUtil.createMessage(MessageLevel.ERROR, "MA125A0101").formatMessage());
            responseData.setError(error);
        } else if (result == CheckResultConstants.FAMILY_RIDE_APPLY_DATE_CHECK_ERROR) {
            // 応答電文.エラーメッセージ
            ResponseErrorData error = new ResponseErrorData();
            error.setId("MA125A0102");
            error.setMessage(MessageUtil.createMessage(MessageLevel.ERROR, "MA125A0102").formatMessage());
            responseData.setError(error);
        } else if (result == CheckResultConstants.PARTNER_MEM_SERVICE_CHECK_ERROR) {
            // 応答電文.エラーメッセージ
            ResponseErrorData error = new ResponseErrorData();
            error.setId("MA125A0103");
            error.setMessage(MessageUtil.createMessage(MessageLevel.ERROR, "MA125A0103").formatMessage());
            responseData.setError(error);
        } else if (result == CheckResultConstants.PARTNER_RIDE_APPLY_DATE_CHECK_ERROR) {
            // 応答電文.エラーメッセージ
            ResponseErrorData error = new ResponseErrorData();
            error.setId("MA125A0104");
            error.setMessage(MessageUtil.createMessage(MessageLevel.ERROR, "MA125A0104").formatMessage());
            responseData.setError(error);
        } else if (result == CheckResultConstants.CHECK_OK) {
            responseData.setRideApplyDate(systemDate);
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
    private SqlResultSet getFamilyMemServiceCtrlId(String applicationMemberId) {

        // アプリ会員情報・家族会員サービス情報取得用のSQL条件を設定する。
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("SELECT_FAMILY_MEM_SERVICE_INFO_BY_APL");

        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("applicationMemberId", applicationMemberId);
        condition.put("applicationMemberStatusCode", OpalCodeConstants.AplMemStatusCode.OP_AUTH_APL_MEM);
        condition.put("registStatusDivision", OpalCodeConstants.RegistStatusDivision.REGIST_STATUS_DIVISION_1);
        // 実行する。
        SqlResultSet result = statement.retrieve(condition);
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
    private SqlResultSet getPartnerMemServiceCtrlId(String applicationMemberId) {

        // アプリ会員情報・パートナー会員サービス情報取得用のSQL条件を設定する。
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("SELECT_PARTNER_MEM_SERVICE_INFO_BY_APL");

        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("applicationMemberId", applicationMemberId);
        condition.put("applicationMemberStatusCode", OpalCodeConstants.AplMemStatusCode.OP_AUTH_APL_MEM);
        condition.put("admitStatusDivision", OpalCodeConstants.AdmitStatusDivision.ADMIT_STATUS_DIVISION_1);
        // 実行する。
        SqlResultSet result = statement.retrieve(condition);
        return result;
    }

    /**
     * 乗車適用日情報取得
     *
     * @param memServiceCtrlId
     *            家族会員サービス管理ID/パートナー会員サービス管理ID
     * @param sqlId
     *            SQL_ID
     *
     * @return 乗車適用日情報
     */
    private SqlResultSet getRideApplyDate(Long memServiceCtrlId, String sqlId) {

        // 乗車適用日情報取得用のSQL条件を設定する。
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement(sqlId);

        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("memServiceCtrlId", memServiceCtrlId);
        condition.put("rideApplyDate", systemDate);

        // 実行する。
        SqlResultSet result = statement.retrieve(condition);
        return result;
    }

    /**
     * 乗車適用日登録
     *
     * @param memServiceCtrlId
     *            家族会員サービス管理ID/パートナー会員サービス管理ID
     * @param deletedDate
     *            論理削除日
     * @param sqlId
     *            SQL_ID
     */
    private void insertRideApplyDate(Long memServiceCtrlId, String deletedDate, String sqlId) {

        // 乗車適用日登録用のSQL条件を設定する。
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement(sqlId);

        Map<String, Object> condition = new HashMap<String, Object>();
        // 家族会員サービス管理ID/パートナー会員サービス管理ID
        condition.put("memServiceCtrlId", memServiceCtrlId);
        // 乗車適用日
        condition.put("rideApplyDate", systemDate);
        // 乗車適用年月
        condition.put("rideApplyYearMonth", systemDate.substring(OpalDefaultConstants.POSITION_YEAR_MONTH_START,
                OpalDefaultConstants.POSITION_YEAR_MONTH_END));
        // ユーザ選択区分
        condition.put("userChooseDivision", OpalCodeConstants.UserChooseDivision.USER_CHOOSE_DIVISION_0);
        // 登録者ID
        condition.put("insertUserId", API_SERVER_ID);
        // 登録日時
        condition.put("insertDateTime", SystemTimeUtil.getTimestamp());
        // 最終更新者ID
        condition.put("updateUserId", API_SERVER_ID);
        // 最終更新日時
        condition.put("updateDateTime", SystemTimeUtil.getTimestamp());
        // 削除フラグ(0:未削除)
        condition.put("deletedFlg", OpalDefaultConstants.DELETED_FLG_0);
        // 論理削除日
        condition.put("deletedDate", deletedDate);

        // 実行する。
        statement.executeUpdateByMap(condition);
    }

    /**
     * アプリ会員情報取得
     *
     * @param form
     *            乗車適用日登録フォーム
     *
     * @return アプリ会員情報
     */
    private SqlResultSet getAplMemInfo(A125AAARBodyForm form) {

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
