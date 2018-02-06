package jp.co.tis.opal.web.ss113A;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import nablarch.core.db.statement.ParameterizedSqlPStatement;
import nablarch.core.db.statement.SqlRow;
import nablarch.core.message.MessageLevel;
import nablarch.core.message.MessageUtil;
import nablarch.fw.web.HttpResponse;

import jp.co.tis.opal.common.constants.OpalCodeConstants;
import jp.co.tis.opal.web.common.ResponseData.ResponseErrorData;
import jp.co.tis.opal.web.common.constants.CheckResultConstants;
import jp.co.tis.opal.web.common.rest.AbstractRestBaseAction;

/**
 * A113A01:アプリ会員情報取得APIのアクションクラス。
 *
 * @author 曹
 * @since 1.0
 */
public class A113A011Action extends AbstractRestBaseAction<A113AAARRequestData> {

    /** アプリ会員情報データマップ */
    private Map<String, Object> responseAplMemInfo = null;
    /** OP会員情報データマップ */
    private Map<String, Object> responseOpMemInfo = null;

    /**
     * アプリ会員情報取得API
     *
     * @param requestData
     *            アプリ会員情報取得電文
     * @return アプリ会員情報データ応答電文
     */
    @Consumes(MediaType.APPLICATION_JSON)
    @Valid
    public HttpResponse getAplMenInfo(A113AAARRequestData requestData) {
        HttpResponse responseData = super.execute(requestData);
        return responseData;
    }

    /**
     * 処理詳細
     *
     * @param requestData
     *            アプリ会員情報取得電文
     * @return チェック結果
     */
    @Override
    protected int executeLogic(A113AAARRequestData requestData) {

        // アプリ会員情報・OP会員情報取得
        SqlRow aplMemAndOpMemInfo = getAplMemAndOpMemInfo(
                Long.valueOf(requestData.getAplData().getApplicationMemberId()));

        // アプリ会員情報・OP会員情報が存在しない場合、応答電文を以下の通り設定して、「HTTPアクセスログ出力」を行う。
        if (aplMemAndOpMemInfo == null) {
            return CheckResultConstants.APL_MEM_DATA_ISNULL;
        }
        // 家族会員サービス登録状況取得
        String familyMemServiceRegistStatus = null;
        // パートナーサービス登録状況取得
        String partnerServiceRegistStatus = null;
        if (OpalCodeConstants.AplMemStatusCode.NOT_OP_MEM
                .equals(aplMemAndOpMemInfo.getString("APPLICATION_MEMBER_STATUS_CODE"))) {
            // アプリ会員状態コードが"D"(OP非会員)の場合、家族会員サービス登録状況を"2"(登録不可)と判定
            familyMemServiceRegistStatus = OpalCodeConstants.MemServiceRegistStatus.MEM_SERVICE_REGIST_STATUS_2;
            // アプリ会員状態コードが"D"(OP非会員)の場合、パートナーサービス登録状況を"2"(登録不可)と判定
            partnerServiceRegistStatus = OpalCodeConstants.MemServiceRegistStatus.MEM_SERVICE_REGIST_STATUS_2;
        } else {
            // 家族会員サービス登録有無チェック
            Boolean familyServiceIsInserted = checkFamilyServiceIsInserted(
                    aplMemAndOpMemInfo.getString("MEMBER_CONTROL_NUMBER"));
            if (familyServiceIsInserted) {
                // 家族会員サービス登録済みの場合、家族会員サービス登録状況を"1"(登録済み)と判定
                familyMemServiceRegistStatus = OpalCodeConstants.MemServiceRegistStatus.MEM_SERVICE_REGIST_STATUS_1;
            } else if (checkFamilyMemIsExist(aplMemAndOpMemInfo.getString("MEMBER_CONTROL_NUMBER"),
                    Long.valueOf(requestData.getAplData().getApplicationMemberId()))) {
                // 家族会員数が存在しない（家族会員数 = 0）場合、家族会員サービス登録状況を"2"(登録不可)と判定
                familyMemServiceRegistStatus = OpalCodeConstants.MemServiceRegistStatus.MEM_SERVICE_REGIST_STATUS_2;
            } else {
                // 以外の場合、家族会員サービス登録状況を"3"(登録可能)と判定
                familyMemServiceRegistStatus = OpalCodeConstants.MemServiceRegistStatus.MEM_SERVICE_REGIST_STATUS_3;
            }

            // パートナー会員サービス登録有無チェック
            Boolean partnerMemServiceIsInserted = checkPartnerMemServiceIsInserted(
                    aplMemAndOpMemInfo.getString("MEMBER_CONTROL_NUMBER"),
                    aplMemAndOpMemInfo.getString("MEM_CTRL_NUM_BR_NUM"));
            if (partnerMemServiceIsInserted) {
                // 既に登録済みの場合、パートナーサービス登録状況を"1"(登録済み)と判定
                partnerServiceRegistStatus = OpalCodeConstants.MemServiceRegistStatus.MEM_SERVICE_REGIST_STATUS_1;
            } else {
                // 以外の場合、パートナーサービス登録状況を"3"(登録可能)と判定
                partnerServiceRegistStatus = OpalCodeConstants.MemServiceRegistStatus.MEM_SERVICE_REGIST_STATUS_3;
            }
            // OP会員情報データ設定
            setOpMemInfo(aplMemAndOpMemInfo);
        }

        // アプリ会員情報データ設定
        setAplMemInfo(aplMemAndOpMemInfo, familyMemServiceRegistStatus, partnerServiceRegistStatus);

        return CheckResultConstants.CHECK_OK;
    }

    /**
     * アプリ会員情報データ応答電文を設定する。
     *
     * @param requestData
     *            アプリ会員情報取得電文
     *
     * @param result
     *            アプリ会員情報取得電文チェック結果
     *
     * @return アプリ会員情報データ応答電文
     * @throws IOException
     *             異常
     */
    @Override
    protected HttpResponse responseBuilder(A113AAARRequestData requestData, int result) throws IOException {

        // アプリ会員情報データ応答電文設定
        A113AAASResponseData responseData = new A113AAASResponseData();
        // 応答電文.処理結果コード
        responseData.setResultCode(getResultCode(result));
        if (result == CheckResultConstants.APL_MEM_DATA_ISNULL) {
            // 応答電文.エラーメッセージ
            ResponseErrorData error = new ResponseErrorData();
            error.setId("MA113A0101");
            error.setMessage(MessageUtil.createMessage(MessageLevel.ERROR, "MA113A0101").formatMessage());
            responseData.setError(error);
        } else if (result == CheckResultConstants.CHECK_OK) {
            responseData.setAplMemInfo(responseAplMemInfo);
            responseData.setOpMemInfo(responseOpMemInfo);
        }

        HttpResponse response = super.setHttpResponseData(responseData, result);

        return response;
    }

    /**
     * アプリ会員情報・OP会員情報取得。
     *
     * @param applicationMemberId
     *            アプリ会員ID
     *
     * @return アプリ会員情報・OP会員情報
     */
    private SqlRow getAplMemAndOpMemInfo(Long applicationMemberId) {

        // アプリ会員情報・OP会員情報取得用のSQLの条件を設定する
        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("applicationMemberId", applicationMemberId);
        condition.put("statusA", OpalCodeConstants.AplMemStatusCode.OP_AUTH_APL_MEM);
        condition.put("statusD", OpalCodeConstants.AplMemStatusCode.NOT_OP_MEM);

        // アプリ会員情報・OP会員情報取得
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("SELECT_APL_MEM_AND_OP_MEM_INFO");
        SqlRow aplMemAndOpMemInfo = null;
        if (!statement.retrieve(condition).isEmpty()) {
            aplMemAndOpMemInfo = statement.retrieve(condition).get(0);
        }
        return aplMemAndOpMemInfo;
    }

    /**
     * 家族会員サービス登録有無チェック。
     *
     * @param memberControlNumber
     *            会員管理番号
     *
     * @return チェックの結果
     */
    private Boolean checkFamilyServiceIsInserted(String memberControlNumber) {

        Boolean familyServiceIsInserted = false;
        // 家族会員サービス情報取得用のSQLの条件を設定する
        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("memberControlNumber", memberControlNumber);
        condition.put("registStatusDivision", OpalCodeConstants.RegistStatusDivision.REGIST_STATUS_DIVISION_1);

        // 家族会員サービス情報取得
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("SELECT_FAMILY_MEM_INFO_COUNT");
        SqlRow memCtrlNumServiceCnt = statement.retrieve(condition).get(0);
        // 既に登録済みの場合、チェック結果を「true」に設定
        if (memCtrlNumServiceCnt.getInteger("FAMILY_MEM_SERVICE_CNT") >= 1) {
            familyServiceIsInserted = true;
        }

        return familyServiceIsInserted;
    }

    /**
     * 家族会員の存在チェック。
     *
     * @param memberControlNumber
     *            会員管理番号
     * @param applicationMemberId
     *            アプリ会員ID
     *
     * @return チェックの結果
     */
    private Boolean checkFamilyMemIsExist(String memberControlNumber, Long applicationMemberId) {

        Boolean familyMemIsExist = false;
        // 家族会員情報取得用のSQLの条件を設定する
        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("memberControlNumber", memberControlNumber);
        condition.put("applicationMemberId", applicationMemberId);

        // 家族会員サービス情報取得
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("SELECT_FAMILY_MEMBER_NUMBER");
        SqlRow memberControlNumberCnt = statement.retrieve(condition).get(0);
        // 家族会員数が存在しない（家族会員数 = 0）場合、チェック結果を「true」に設定
        if (memberControlNumberCnt.getInteger("FAMILY_MEM_CNT") == 0) {
            familyMemIsExist = true;
        }

        return familyMemIsExist;
    }

    /**
     * パートナー会員サービス登録有無チェック。
     *
     * @param memberControlNumber
     *            会員管理番号
     * @param memCtrlNumBrNum
     *            会員管理番号枝番
     *
     * @return チェックの結果
     */
    private Boolean checkPartnerMemServiceIsInserted(String memberControlNumber, String memCtrlNumBrNum) {

        Boolean partnerMemServiceIsInserted = false;
        // パートナー会員サービス情報取得用のSQLの条件を設定する
        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("memberControlNumber", memberControlNumber);
        condition.put("memCtrlNumBrNum", memCtrlNumBrNum);
        condition.put("admitStatusDivision", OpalCodeConstants.AdmitStatusDivision.ADMIT_STATUS_DIVISION_1);

        // パートナー会員サービス情報取得
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("SELECT_PARTNER_MEM_SERVICE_INFO_COUNT");
        SqlRow partnerMemService = statement.retrieve(condition).get(0);

        // 既に登録済み(レコード数 >= 1)の場合、チェック結果を「true」に設定
        if (partnerMemService.getInteger("PARTNER_MEM_SERVICE_CNT") >= 1) {
            partnerMemServiceIsInserted = true;
        }
        return partnerMemServiceIsInserted;
    }

    /**
     * 応答電文Body設定
     *
     * @param aplMemAndOpMemInfo
     *            アプリ会員情報・OP会員情報
     * @param familyMemServiceRegistStatus
     *            家族会員サービス登録状況
     * @param partnerServiceRegistStatus
     *            パートナーサービス登録状況
     */
    private void setAplMemInfo(SqlRow aplMemAndOpMemInfo, String familyMemServiceRegistStatus,
            String partnerServiceRegistStatus) {
        responseAplMemInfo = new LinkedHashMap<String, Object>();
        // アプリID
        responseAplMemInfo.put("applicationId", aplMemAndOpMemInfo.getString("APPLICATION_ID"));
        // デバイスID
        responseAplMemInfo.put("deviceId", aplMemAndOpMemInfo.getString("DEVICE_ID"));
        // ログインID
        responseAplMemInfo.put("loginId", aplMemAndOpMemInfo.getString("LOGIN_ID"));
        // 生年月日
        responseAplMemInfo.put("birthdate", aplMemAndOpMemInfo.getString("BIRTHDATE_APL"));
        // 性別コード
        responseAplMemInfo.put("sexCode", aplMemAndOpMemInfo.getString("SEXCODE_APL"));
        // メールアドレス
        responseAplMemInfo.put("mailAddress", aplMemAndOpMemInfo.getString("MAIL_ADDRESS"));
        // レコメンド利用承諾可フラグ
        responseAplMemInfo.put("recommendUseAcceptFlag", aplMemAndOpMemInfo.getString("RECOMMEND_USE_ACCEPT_FLAG"));
        // アンケート1
        responseAplMemInfo.put("enquete1", aplMemAndOpMemInfo.getString("ENQUETE_1"));
        // アンケート2
        responseAplMemInfo.put("enquete2", aplMemAndOpMemInfo.getString("ENQUETE_2"));
        // アンケート3
        responseAplMemInfo.put("enquete3", aplMemAndOpMemInfo.getString("ENQUETE_3"));
        // アンケート4
        responseAplMemInfo.put("enquete4", aplMemAndOpMemInfo.getString("ENQUETE_4"));
        // アンケート5
        responseAplMemInfo.put("enquete5", aplMemAndOpMemInfo.getString("ENQUETE_5"));
        // アンケート6
        responseAplMemInfo.put("enquete6", aplMemAndOpMemInfo.getString("ENQUETE_6"));
        // アンケート7
        responseAplMemInfo.put("enquete7", aplMemAndOpMemInfo.getString("ENQUETE_7"));
        // アンケート8
        responseAplMemInfo.put("enquete8", aplMemAndOpMemInfo.getString("ENQUETE_8"));
        // アンケート9
        responseAplMemInfo.put("enquete9", aplMemAndOpMemInfo.getString("ENQUETE_9"));
        // アンケート10
        responseAplMemInfo.put("enquete10", aplMemAndOpMemInfo.getString("ENQUETE_10"));
        // 主なご利用駅1
        responseAplMemInfo.put("mainUseStation1", aplMemAndOpMemInfo.getString("MAIN_USE_STATION_1"));
        // 主なご利用駅2
        responseAplMemInfo.put("mainUseStation2", aplMemAndOpMemInfo.getString("MAIN_USE_STATION_2"));
        // 主なご利用駅3
        responseAplMemInfo.put("mainUseStation3", aplMemAndOpMemInfo.getString("MAIN_USE_STATION_3"));
        // 主なご利用駅4
        responseAplMemInfo.put("mainUseStation4", aplMemAndOpMemInfo.getString("MAIN_USE_STATION_4"));
        // 主なご利用駅5
        responseAplMemInfo.put("mainUseStation5", aplMemAndOpMemInfo.getString("MAIN_USE_STATION_5"));
        // 休日1
        responseAplMemInfo.put("dayOff1", aplMemAndOpMemInfo.getString("DAY_OFF_1"));
        // 休日2
        responseAplMemInfo.put("dayOff2", aplMemAndOpMemInfo.getString("DAY_OFF_2"));
        // 家族会員サービス登録状況
        responseAplMemInfo.put("familyMemServiceRegistStatus", familyMemServiceRegistStatus);
        // パートナーサービス登録状況
        responseAplMemInfo.put("partnerServiceRegistStatus", partnerServiceRegistStatus);
    }

    /**
     * 応答電文Body設定
     *
     * @param aplMemAndOpMemInfo
     *            アプリ会員情報・OP会員情報
     */
    private void setOpMemInfo(SqlRow aplMemAndOpMemInfo) {
        responseOpMemInfo = new LinkedHashMap<String, Object>();
        // OP番号
        responseOpMemInfo.put("osakaPitapaNumber", aplMemAndOpMemInfo.getString("OSAKA_PITAPA_NUMBER"));
        // カード種類
        responseOpMemInfo.put("cardType", aplMemAndOpMemInfo.getString("CARD_TYPE"));
        // 生年月日
        responseOpMemInfo.put("birthdate", aplMemAndOpMemInfo.getString("BIRTHDATE_OP"));
        // 性別コード
        responseOpMemInfo.put("sexCode", aplMemAndOpMemInfo.getString("SEXCODE_OP"));
        // サービス種別
        responseOpMemInfo.put("serviceCategory", aplMemAndOpMemInfo.getString("SERVICE_CATEGORY"));
        // 登録駅1
        responseOpMemInfo.put("registStation1", aplMemAndOpMemInfo.getString("REGIST_STATION_1"));
        // 登録駅2
        responseOpMemInfo.put("registStation2", aplMemAndOpMemInfo.getString("REGIST_STATION_2"));
        // 今回登録駅1
        responseOpMemInfo.put("thisTimeRegistStation1", aplMemAndOpMemInfo.getString("THIS_TIME_REGIST_STATION_1"));
        // 今回登録駅2
        responseOpMemInfo.put("thisTimeRegistStation2", aplMemAndOpMemInfo.getString("THIS_TIME_REGIST_STATION_2"));
        // 今回登録駅3
        responseOpMemInfo.put("thisTimeRegistStation3", aplMemAndOpMemInfo.getString("THIS_TIME_REGIST_STATION_3"));
        // 今回登録駅4
        responseOpMemInfo.put("thisTimeRegistStation4", aplMemAndOpMemInfo.getString("THIS_TIME_REGIST_STATION_4"));
        // 今回登録駅5
        responseOpMemInfo.put("thisTimeRegistStation5", aplMemAndOpMemInfo.getString("THIS_TIME_REGIST_STATION_5"));
    }

}
