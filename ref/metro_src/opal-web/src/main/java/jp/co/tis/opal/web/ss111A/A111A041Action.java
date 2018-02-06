package jp.co.tis.opal.web.ss111A;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import nablarch.common.dao.EntityList;
import nablarch.common.dao.UniversalDao;
import nablarch.core.date.SystemTimeUtil;
import nablarch.core.db.statement.ParameterizedSqlPStatement;
import nablarch.core.db.statement.SqlResultSet;
import nablarch.core.db.statement.SqlRow;
import nablarch.core.message.MessageLevel;
import nablarch.core.message.MessageUtil;
import nablarch.core.util.StringUtil;
import nablarch.fw.web.HttpResponse;

import jp.co.tis.opal.common.component.CM010001Component;
import jp.co.tis.opal.common.component.CM010005Component;
import jp.co.tis.opal.common.constants.OpalCodeConstants;
import jp.co.tis.opal.common.constants.OpalCodeConstants.MailDeliverType;
import jp.co.tis.opal.common.constants.OpalDefaultConstants;
import jp.co.tis.opal.common.entity.AplMemInfoEntity;
import jp.co.tis.opal.common.entity.MailAddressAuthInfoEntity;
import jp.co.tis.opal.common.utility.IdGeneratorUtil;
import jp.co.tis.opal.web.common.ResponseData.ResponseErrorData;
import jp.co.tis.opal.web.common.component.CM111001Component;
import jp.co.tis.opal.web.common.constants.CheckResultConstants;
import jp.co.tis.opal.web.common.rest.AbstractRestBaseAction;

/**
 * A111A04:アプリ会員登録APIのアクションクラス。
 *
 * @author 唐
 * @since 1.0
 */
public class A111A041Action extends AbstractRestBaseAction<A111AADRRequestData> {

    /** API */
    private static final String API_SERVER_ID = "A111A041";

    /**
     * アプリ会員登録処理
     * <p>
     * 応答にJSONを使用する。
     * </p>
     *
     * @param requestData
     *            アプリ会員登録要求電文
     * @return HTTPレスポンス
     */
    @Valid
    @Consumes(MediaType.APPLICATION_JSON)
    public HttpResponse insertAplMemInfo(A111AADRRequestData requestData) {
        HttpResponse responseData = super.execute(requestData);
        return responseData;
    }

    /**
     * 処理詳細ロジック
     *
     * @param requestData
     *            アプリ会員登録要求電文
     * @return 結果値
     */
    @Override
    protected int executeLogic(A111AADRRequestData requestData) {
        // メールアドレス認証情報チェック
        // 1) メールアドレス認証情報取得
        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("mailAddressAuthCode", requestData.getAplMemRegistData().getMailAddressAuthCode());
        condition.put("processDivision", OpalCodeConstants.MailAddressAuthProcessDivision.APL_MEM_REGIST);
        EntityList<MailAddressAuthInfoEntity> mailAddressAuthInfo = UniversalDao
                .findAllBySqlFile(MailAddressAuthInfoEntity.class, "SELECT_MAIL_ADDRESS_AUTH_INFO_LOGIN", condition);
        // 該当メールアドレス認証情報が存在しない場合
        if (mailAddressAuthInfo.isEmpty()) {
            return CheckResultConstants.MAIL_ADDRESS_AUTH_NULL;
        }
        // 2) 認証済チェック
        if (OpalDefaultConstants.PROCESSED_FLAG_1.equals(mailAddressAuthInfo.get(0).getProcessedFlag())) {
            return CheckResultConstants.PROCESSED_COMPLETED;
        }
        // 3) メールアドレス認証有効期限確認
        if (SystemTimeUtil.getTimestamp().compareTo(mailAddressAuthInfo.get(0).getMailAddressAuthExpiDate()) > 0) {
            return CheckResultConstants.MAIL_ADDRESS_AUTH_DATE_ERROR;
        }

        // アプリ会員登録
        Integer statusCode = insertAplMem(requestData, mailAddressAuthInfo.get(0));
        // 該当アプリ会員一時情報が存在しない場合
        if (statusCode == 1) {
            return CheckResultConstants.APL_MEM_TEMP_DATA_ISNULL;
        } else if (statusCode == 2) {
            // コンポーネントにて、OP会員情報取得エラーの場合
            return CheckResultConstants.OP_DATA_ISNULL;
        }

        return CheckResultConstants.CHECK_OK;
    }

    /**
     * HTTPレスポンスを設定する。
     *
     * @param requestData
     *            アプリ会員登録要求電文
     *
     * @param result
     *            チェック結果
     *
     * @return HTTPレスポンス
     * @throws IOException
     *             異常
     */
    @Override
    protected HttpResponse responseBuilder(A111AADRRequestData requestData, int result) throws IOException {

        // アプリ会員登録結果応答電文設定
        A111AADSResponseData responseData = new A111AADSResponseData();

        // 登録結果応答電文.処理結果コード
        responseData.setResultCode(getResultCode(result));
        if (result == CheckResultConstants.MAIL_ADDRESS_AUTH_NULL) {
            // 登録結果応答電文.エラーメッセージ
            ResponseErrorData error = new ResponseErrorData();
            error.setId("MA111A0401");
            error.setMessage(MessageUtil.createMessage(MessageLevel.ERROR, "MA111A0401").formatMessage());
            responseData.setError(error);
        }
        if (result == CheckResultConstants.PROCESSED_COMPLETED) {
            // 登録結果応答電文.エラーメッセージ
            ResponseErrorData error = new ResponseErrorData();
            error.setId("MA111A0402");
            error.setMessage(MessageUtil.createMessage(MessageLevel.ERROR, "MA111A0402").formatMessage());
            responseData.setError(error);
        }
        if (result == CheckResultConstants.MAIL_ADDRESS_AUTH_DATE_ERROR) {
            // 登録結果応答電文.エラーメッセージ
            ResponseErrorData error = new ResponseErrorData();
            error.setId("MA111A0403");
            error.setMessage(MessageUtil.createMessage(MessageLevel.ERROR, "MA111A0403").formatMessage());
            responseData.setError(error);
        }
        if (result == CheckResultConstants.APL_MEM_TEMP_DATA_ISNULL) {
            // 登録結果応答電文.エラーメッセージ
            ResponseErrorData error = new ResponseErrorData();
            error.setId("MA111A0404");
            error.setMessage(MessageUtil.createMessage(MessageLevel.ERROR, "MA111A0404").formatMessage());
            responseData.setError(error);
        }
        if (result == CheckResultConstants.OP_DATA_ISNULL) {
            // 登録結果応答電文.エラーメッセージ
            ResponseErrorData error = new ResponseErrorData();
            error.setId("MA111A0405");
            error.setMessage(MessageUtil.createMessage(MessageLevel.ERROR, "MA111A0405").formatMessage());
            responseData.setError(error);
        }
        HttpResponse response = super.setHttpResponseData(responseData, result);
        return response;
    }

    /**
     * アプリ会員情報登録
     *
     * @param requestData
     *            アプリ会員登録要求電文
     * @param mailAddressAuthInfo
     *            メールアドレス認証情報
     * @return ステータスコード(0:正常終了 1:アプリ会員一時情報が存在しない 2:コンポーネントにて、OP会員情報取得エラー)
     */
    private int insertAplMem(A111AADRRequestData requestData, MailAddressAuthInfoEntity mailAddressAuthInfo) {

        // ステータスコード設定
        int statusCode = 0;
        // 1) アプリ会員一時情報取得
        SqlResultSet aplMemTempInfo = aplMemTempInfoIsExist(mailAddressAuthInfo.getMailAddressAuthKey());
        // 該当アプリ会員一時情報が存在しない場合
        if (aplMemTempInfo.isEmpty()) {
            statusCode = 1;
            return statusCode;
        }

        EntityList<AplMemInfoEntity> aplMemInfoList = new EntityList<AplMemInfoEntity>();
        // 2) OP番号に紐づくアプリ会員情報のロック取得
        if (!StringUtil.isNullOrEmpty(aplMemTempInfo.get(0).getString("OSAKA_PITAPA_NUMBER"))) {
            Map<String, Object> exclusiveMap = new HashMap<String, Object>();
            exclusiveMap.put("osakaPitapaNumber", aplMemTempInfo.get(0).getString("OSAKA_PITAPA_NUMBER"));
            aplMemInfoList = UniversalDao.findAllBySqlFile(AplMemInfoEntity.class, "SELECT_APL_MEM_INFO_LOCK_BY_OP",
                    exclusiveMap);
        }

        // 3) アプリ会員情報登録
        // アプリ会員IDを採番する。(採番対象ID：1101)
        Long applicationMemberId = IdGeneratorUtil.generateApplicationMemberId();
        insertApplicationMemberInfo(aplMemTempInfo.get(0), applicationMemberId);

        String osakaPitapaNumber = aplMemTempInfo.get(0).getString("OSAKA_PITAPA_NUMBER");
        // 4) 新規登録ボーナスマイル付与
        if (StringUtil.isNullOrEmpty(osakaPitapaNumber) || (!StringUtil.isNullOrEmpty(osakaPitapaNumber)
                && !aplMemInfoList.isEmpty() && aplMemInfoList.get(0).getOpAuthTimes() == 0)) {
            // CM010005：マイル計算共通コンポーネントを呼び出す。
            CM010005Component cm010005Component = new CM010005Component();
            // マイル加算。
            cm010005Component.addMile(applicationMemberId, requestData.getAplMemRegistData().getRegistMileAddSubRcptNum(),
                    OpalCodeConstants.MileCategoryCode.MEM_REGIST_BONUS,
                    Long.valueOf(requestData.getAplMemRegistData().getRegistBonusMileAmount()), API_SERVER_ID, null);
        }

        // 5) OP認証実施
        if (!StringUtil.isNullOrEmpty(aplMemTempInfo.get(0).getString("OSAKA_PITAPA_NUMBER"))) {

            // OP認証処理を呼出す
            CM111001Component cm111001Component = new CM111001Component();
            Map<String, Boolean> result = cm111001Component.setOPAuth(applicationMemberId,
                    aplMemTempInfo.get(0).getString("OSAKA_PITAPA_NUMBER"),
                    requestData.getAplMemRegistData().getOpAuthMileAddSubRcptNum(),
                    Long.valueOf(requestData.getAplMemRegistData().getOpAuthBonusMileAmount()), API_SERVER_ID);
            if (!result.get("OP_AUTH_RESULT")) {
                // コンポーネントにて、OP会員情報取得エラーの場合
                statusCode = 2;
                return statusCode;
            }
            if (!result.get("OP_AUTH_RELEASE_MAIL_DELIVER_FLAG")) {
                // コンポーネントにて、OP認証解除配信不可の場合
                String message = MessageUtil.createMessage(MessageLevel.WARN, "MA111A0406").formatMessage();
                LOGGER.logWarn(message);
            }
        }

        // 6) アプリ会員一時情報更新
        updateAplMemTempInfo(mailAddressAuthInfo.getMailAddressAuthKey());

        // 7) メールアドレス認証情報更新
        // メールアドレス認証情報TBLの処理済フラグを"1"(処理済)に更新
        mailAddressAuthInfo.setProcessedFlag(OpalDefaultConstants.PROCESSED_FLAG_1);
        mailAddressAuthInfo.setUpdateUserId(API_SERVER_ID);
        mailAddressAuthInfo.setUpdateDateTime(SystemTimeUtil.getTimestamp());
        // メールアドレス認証情報更新
        UniversalDao.update(mailAddressAuthInfo);

        // 8) アプリ会員登録完了のお知らせメール送信
        mailLiteDeliverProcess(applicationMemberId, aplMemTempInfo.get(0).getString("MAIL_ADDRESS"));

        return statusCode;
    }

    /**
     * アプリ会員登録完了のお知らせメール送信
     *
     * @param applicationMemberId
     *            アプリ会員ID
     * @param mailAddress
     *            メールアドレス
     */
    private void mailLiteDeliverProcess(Long applicationMemberId, String mailAddress) {

        // 差し込み項目（空配列）
        List<String> variableItemValues = new ArrayList<String>();

        CM010001Component cm010001Component = new CM010001Component();
        // アプリ会員登録完了のお知らせメール送信
        cm010001Component.insMailLiteDeliverInfo(applicationMemberId, API_SERVER_ID, mailAddress,
                MailDeliverType.MAIL_DELIVER_TYPE_1, OpalDefaultConstants.MAIL_TEMP_APL_MEM_REGISTED_NOTICE,
                variableItemValues, null);
    }

    /**
     * アプリ会員一時情報存在チェック
     *
     * @param mailAddressAuthKey
     *            メールアドレス認証キー
     *
     * @return 処理結果（存在する：true 存在しない：false）
     */
    private SqlResultSet aplMemTempInfoIsExist(Long mailAddressAuthKey) {
        // アプリ会員一時情報存在チェック用のSQLの条件を設定する
        Map<String, Object> condition = new HashMap<String, Object>();
        // アプリ会員一時情報TBL.アプリ会員登録受付ID = 2.2.2で取得したメールアドレス認証キー
        condition.put("aplMemRegistRcptId", mailAddressAuthKey);
        // 実行する。
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("SELECT_APL_MEM_TEMP_INFO");
        SqlResultSet result = statement.retrieve(condition);
        return result;
    }

    /**
     * アプリ会員一時情報更新
     *
     * @param mailAddressAuthKey
     *            メールアドレス認証キー
     */
    private void updateAplMemTempInfo(Long mailAddressAuthKey) {
        // アプリ会員一時情報更新の項目内容を設定する。
        Map<String, Object> condition = new HashMap<String, Object>();
        // 処理済フラグ(1:処理済)
        condition.put("processedFlag1", OpalDefaultConstants.PROCESSED_FLAG_1);
        // 最終更新者
        condition.put("updateUserId", API_SERVER_ID);
        // 最終更新日時
        condition.put("updateDateTime", SystemTimeUtil.getTimestamp());
        // アプリ会員登録受付ID
        condition.put("aplMemRegistRcptId", mailAddressAuthKey);
        // 処理済フラグ(0:未処理)
        condition.put("processedFlag0", OpalDefaultConstants.PROCESSED_FLAG_0);
        // 削除フラグ(0:未削除)
        condition.put("deletedFlg", OpalDefaultConstants.DELETED_FLG_0);
        // 実行する。
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("UPDATE_APL_MEM_TEMP_INFO");
        statement.executeUpdateByMap(condition);
    }

    /**
     * アプリ会員情報登録
     *
     * @param data
     *            アプリ会員一時情報
     * @param applicationMemberId
     *            アプリ会員ID
     */
    private void insertApplicationMemberInfo(SqlRow data, Long applicationMemberId) {

        // アプリ会員情報登録の項目内容を設定する。
        Map<String, Object> condition = new HashMap<String, Object>();
        // アプリ会員ID
        condition.put("applicationMemberId", applicationMemberId);
        // 会員管理番号
        condition.put("memberControlNumber", null);
        // 会員管理番号枝番
        condition.put("memCtrlNumBrNum", null);
        // OP番号
        condition.put("osakaPitapaNumber", null);
        // アプリID
        condition.put("applicationId", data.getString("APPLICATION_ID"));
        // デバイスID
        condition.put("deviceId", data.getString("DEVICE_ID"));
        // ログインID
        condition.put("loginId", data.getString("LOGIN_ID"));
        // パスワード
        condition.put("password", data.getString("PASSWORD"));
        // パスワードSALT
        condition.put("passwordSalt", data.getString("PASSWORD_SALT"));
        // ストレッチング回数
        condition.put("stretchingTimes", data.getString("STRETCHING_TIMES"));
        // 生年月日
        condition.put("birthdate", data.getString("BIRTHDATE"));
        // 性別コード
        condition.put("sexCode", data.getString("SEX_CODE"));
        // メールアドレス
        condition.put("mailAddress", data.getString("MAIL_ADDRESS"));
        // メール配信状態区分
        condition.put("mailDeliverStatusDivision",
                OpalCodeConstants.MailDeliverStatusDivision.MAIL_DELIVER_STATUS_DIVISION_0);
        // レコメンド利用承諾可フラグ
        condition.put("recommendUseAcceptFlag", data.getString("RECOMMEND_USE_ACCEPT_FLAG"));
        // アンケート1
        condition.put("enquete1", data.getString("ENQUETE_1"));
        // アンケート2
        condition.put("enquete2", data.getString("ENQUETE_2"));
        // アンケート3
        condition.put("enquete3", data.getString("ENQUETE_3"));
        // アンケート4
        condition.put("enquete4", data.getString("ENQUETE_4"));
        // アンケート5
        condition.put("enquete5", data.getString("ENQUETE_5"));
        // アンケート6
        condition.put("enquete6", data.getString("ENQUETE_6"));
        // アンケート7
        condition.put("enquete7", data.getString("ENQUETE_7"));
        // アンケート8
        condition.put("enquete8", data.getString("ENQUETE_8"));
        // アンケート9
        condition.put("enquete9", data.getString("ENQUETE_9"));
        // アンケート10
        condition.put("enquete10", data.getString("ENQUETE_10"));
        // 主なご利用駅1
        condition.put("mainUseStation1", data.getString("MAIN_USE_STATION_1"));
        // 主なご利用駅2
        condition.put("mainUseStation2", data.getString("MAIN_USE_STATION_2"));
        // 主なご利用駅3
        condition.put("mainUseStation3", data.getString("MAIN_USE_STATION_3"));
        // 主なご利用駅4
        condition.put("mainUseStation4", data.getString("MAIN_USE_STATION_4"));
        // 主なご利用駅5
        condition.put("mainUseStation5", data.getString("MAIN_USE_STATION_5"));
        // 休日1
        condition.put("dayOff1", data.getString("DAY_OFF_1"));
        // 休日2
        condition.put("dayOff2", data.getString("DAY_OFF_2"));
        // アプリ会員状態コード
        condition.put("applicationMemberStatusCode", OpalCodeConstants.AplMemStatusCode.NOT_OP_MEM);
        // OP認証フラグ
        condition.put("osakaPitapaAuthenticateFlag", OpalCodeConstants.OPAuthenticateFlag.OP_AUTH_FLAG_0);
        // OP退会フラグ
        condition.put("osakaPitapaWithdrawFlag", OpalCodeConstants.OPWithdrawFlag.OP_WITHDRAW_FLAG_0);
        // OP認証回数
        condition.put("opAuthTimes", OpalDefaultConstants.OP_AUTH_TIMES_0);
        // 登録者ID
        condition.put("insertUserId", API_SERVER_ID);
        // 登録日時
        condition.put("insertDateTime", SystemTimeUtil.getTimestamp());
        // 最終更新者
        condition.put("updateUserId", API_SERVER_ID);
        // 最終更新日時
        condition.put("updateDateTime", SystemTimeUtil.getTimestamp());
        // 削除フラグ(0:未削除)
        condition.put("deletedFlag", OpalDefaultConstants.DELETED_FLG_0);
        // 論理削除日
        condition.put("deletedDate", null);
        // バージョン番号
        condition.put("version", OpalDefaultConstants.VERSION);
        // 実行する。
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("INSERT_APL_MEM_INFO");
        statement.executeUpdateByMap(condition);
    }
}
