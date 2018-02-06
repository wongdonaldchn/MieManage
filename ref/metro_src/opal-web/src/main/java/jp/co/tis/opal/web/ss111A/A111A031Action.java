package jp.co.tis.opal.web.ss111A;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
import nablarch.core.message.MessageLevel;
import nablarch.core.message.MessageUtil;
import nablarch.core.repository.SystemRepository;
import nablarch.fw.web.HttpErrorResponse;
import nablarch.fw.web.HttpResponse;

import jp.co.tis.opal.common.component.CM010001Component;
import jp.co.tis.opal.common.component.CM010004Component;
import jp.co.tis.opal.common.constants.OpalCodeConstants;
import jp.co.tis.opal.common.constants.OpalCodeConstants.MailDeliverType;
import jp.co.tis.opal.common.constants.OpalDefaultConstants;
import jp.co.tis.opal.common.entity.MailAddressAuthInfoEntity;
import jp.co.tis.opal.common.utility.IdGeneratorUtil;
import jp.co.tis.opal.common.utility.MailAddressAuthUtil;
import jp.co.tis.opal.web.common.ResponseData.ResponseErrorData;
import jp.co.tis.opal.web.common.constants.CheckResultConstants;
import jp.co.tis.opal.web.common.rest.AbstractRestBaseAction;

/**
 * A111A03:アプリ会員一時登録APIのアクションクラス。
 *
 * @author 唐
 * @since 1.0
 */
public class A111A031Action extends AbstractRestBaseAction<A111AACRRequestData> {

    /** API */
    private static final String API_SERVER_ID = "A111A031";

    /** ストレッチング回数_アプリ会員登録 */
    private static final String STRETCHING_TIMES = "stretching_times_regist";

    /** メールアドレス認証情報データ保持期間_アプリ会員登録 */
    private static final String MAIL_ADDRESS_AUTH_INFO = "mail_address_auth_info_retention_period_regist";

    /** 有効期間_アプリ会員一時登録 */
    private static final String EXPI_DATE_APL_MEM_TEMP = "expi_date_apl_mem_temp";

    /** メールアドレス確認用URL */
    private static final String MAIL_ADDRESS_CONFIRM_URL = "mail_address_confirm_url";

    /**
     * アプリ会員一時登録処理
     * <p>
     * 応答にJSONを使用する。
     * </p>
     *
     * @param requestData
     *            アプリ会員一時登録要求電文
     * @return HTTPレスポンス
     * @throws Exception
     *             異常
     */
    @Valid
    @Consumes(MediaType.APPLICATION_JSON)
    public HttpResponse insertAplMemTempInfo(A111AACRRequestData requestData) {
        HttpResponse responseData = super.execute(requestData);
        return responseData;
    }

    /**
     * 処理詳細ロジック
     *
     * @param requestData
     *            アプリ会員一時登録要求電文
     * @return 結果値
     */
    @Override
    protected int executeLogic(A111AACRRequestData requestData) {

        // ログインID存在チェック
        Boolean isExist = loginIdIsExist(requestData.getAplMemTempInfo().getLoginId(),
                requestData.getAplMemTempInfo().getPassword());
        if (isExist) {
            return CheckResultConstants.LOGIN_ID_EXIST;
        }

        // メールアドレス存在チェック
        Boolean isExistMail = mailAddressIsExist(requestData.getAplMemTempInfo().getMailAddress());
        if (isExistMail) {
            return CheckResultConstants.MAIL_ADDRESS_EXIST;
        }

        // アプリ会員登録受付IDを採番する。(採番対象ID：1100)
        Long aplMemRcptId = IdGeneratorUtil.generateAplMemRcptId();

        // 論理削除日を導出する。
        CM010004Component cm010004Component = new CM010004Component();
        String deletedDate = cm010004Component
                .getDeletedDateMonthly(Integer.valueOf(SystemRepository.get(MAIL_ADDRESS_AUTH_INFO)));

        // アプリ会員一時情報登録処理
        insertAplMemTempInfoByPassword(aplMemRcptId, requestData, deletedDate);

        // メールアドレス認証情報登録処理
        insertMailAddressAuthInfoByAuthCode(aplMemRcptId, requestData, deletedDate);

        return CheckResultConstants.CHECK_OK;
    }

    /**
     * HTTPレスポンスを設定する。
     *
     * @param requestData
     *            アプリ会員一時登録要求電文
     *
     * @param result
     *            チェック結果
     *
     * @return HTTPレスポンス
     * @throws IOException
     *             異常
     */
    @Override
    protected HttpResponse responseBuilder(A111AACRRequestData requestData, int result) throws IOException {

        // アプリ会員一時登録結果応答電文設定
        A111AACSResponseData responseData = new A111AACSResponseData();
        // 登録結果応答電文.処理結果コード
        responseData.setResultCode(getResultCode(result));
        if (result == CheckResultConstants.LOGIN_ID_EXIST) {
            // ログインID存在する場合
            // 登録結果応答電文.エラーメッセージ
            ResponseErrorData error = new ResponseErrorData();
            error.setId("MA111A0301");
            error.setMessage(MessageUtil.createMessage(MessageLevel.ERROR, "MA111A0301").formatMessage());
            responseData.setError(error);
        } else if (result == CheckResultConstants.MAIL_ADDRESS_EXIST) {
            // メールアドレス存在する場合
            // 登録結果応答電文.エラーメッセージ
            ResponseErrorData error = new ResponseErrorData();
            error.setId("MA111A0302");
            error.setMessage(MessageUtil.createMessage(MessageLevel.ERROR, "MA111A0302").formatMessage());
            responseData.setError(error);
        }

        HttpResponse response = super.setHttpResponseData(responseData, result);
        return response;
    }

    /**
     * ログインID存在チェック
     *
     * @param loginId
     *            アプリ会員一時登録要求電文.ログインID
     * @param password
     *            アプリ会員一時登録要求電文.パスワード
     * @return 処理結果（存在する：true 存在しない：false）
     */
    private Boolean loginIdIsExist(String loginId, String password) {

        // 存在チェック用のSQLの条件を設定する
        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("loginId", loginId);

        // アプリ会員情報TBLにログインIDの存在チェックを実行する。
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("SELECT_APL_MEM_INFO_BY_LOGIN_ID");
        SqlResultSet aplMemInfoResult = statement.retrieve(condition);
        // アプリ会員情報存在する場合
        if (!aplMemInfoResult.isEmpty()) {
            return true;
        }

        // ログイン情報再登録一時情報TBLにログインIDの存在チェックを実行する。
        ParameterizedSqlPStatement statementLogin = getParameterizedSqlStatement(
                "SELECT_LOGIN_INFO_REREGIST_TEMP_INFO");
        SqlResultSet loginTempInfoResult = statementLogin.retrieve(condition);
        // ログイン情報再登録一時情報存在する場合
        if (!loginTempInfoResult.isEmpty()) {
            return true;
        }

        // アプリ会員一時情報TBLにログインIDの存在チェックを実行する。
        ParameterizedSqlPStatement statementTemp = getParameterizedSqlStatement("SELECT_APL_MEM_TEMP_INFO_BY_LOGIN_ID");
        SqlResultSet aplMemTempInfoResult = statementTemp.retrieve(condition);

        // アプリ会員一時情報存在する場合
        if (!aplMemTempInfoResult.isEmpty()) {
            // パスワードのハッシュ化
            // パスワードのハッシュ化に用いるSALT（パスワードSALT）を取得する
            String passwordSalt = aplMemTempInfoResult.get(0).getString("PASSWORD_SALT");
            // パスワードのストレッチング回数を取得する
            int stretchingTimes = aplMemTempInfoResult.get(0).getInteger("STRETCHING_TIMES");

            // UC000001：認証用ユーティリティ：パスワード認証コード生成
            String passwordAuthCode = "";
            try {
                passwordAuthCode = MailAddressAuthUtil.getPasswordAuthCode(password, passwordSalt, stretchingTimes);
            } catch (NoSuchAlgorithmException e) {
                throw new HttpErrorResponse(500, e);
            }

            // ハッシュ化後のアプリ会員一時登録要求電文.パスワードとアプリ会員一時情報TBL.パスワードが同一でない場合
            if (!passwordAuthCode.equals(aplMemTempInfoResult.get(0).getString("PASSWORD"))) {
                return true;
            } else {
                // ハッシュ化後のアプリ会員一時登録要求電文.パスワードとアプリ会員一時情報TBL.パスワードが同一の場合
                // アプリ会員一時情報TBLの処理済フラグを"1"(処理済)に更新する
                updateAplMemTempInfo(loginId);

                // メールアドレス認証情報チェック
                Map<String, Object> condition1 = new HashMap<String, Object>();
                condition1.put("mailAddressAuthKey", aplMemTempInfoResult.get(0).getString("APL_MEM_REGIST_RCPT_ID"));
                EntityList<MailAddressAuthInfoEntity> mailAddressAuthInfoList = UniversalDao.findAllBySqlFile(
                        MailAddressAuthInfoEntity.class, "SELECT_MAIL_ADDRESS_AUTH_INFO_LOCK_BY_KEY", condition1);

                // メールアドレス認証情報TBLの処理済フラグを"1"(処理済)に更新
                for (MailAddressAuthInfoEntity mailAddressAuthInfo : mailAddressAuthInfoList) {
                    mailAddressAuthInfo.setProcessedFlag(OpalDefaultConstants.PROCESSED_FLAG_1);
                    mailAddressAuthInfo.setUpdateUserId(API_SERVER_ID);
                    mailAddressAuthInfo.setUpdateDateTime(SystemTimeUtil.getTimestamp());
                    // メールアドレス認証情報更新
                    UniversalDao.update(mailAddressAuthInfo);
                }
            }
        }

        // ログインID存在しない場合
        return false;
    }

    /**
     * アプリ会員一時情報更新
     *
     * @param loginId
     *            アプリ会員一時登録要求電文.ログインID
     */
    private void updateAplMemTempInfo(String loginId) {
        // アプリ会員一時情報更新の項目内容を設定する。
        Map<String, Object> condition = new HashMap<String, Object>();
        // 最終更新者
        condition.put("updateUserId", API_SERVER_ID);
        // 最終更新日時
        condition.put("updateDateTime", SystemTimeUtil.getTimestamp());
        // ログインID
        condition.put("loginId", loginId);
        // 実行する。
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("UPDATE_APL_MEM_TEMP_INFO");
        statement.executeUpdateByMap(condition);
    }

    /**
     * アプリ会員一時情報登録処理
     *
     * @param aplMemRcptId
     *            アプリ会員登録受付ID(採番)
     * @param requestData
     *            アプリ会員一時登録要求電文
     * @param deletedDate
     *            論理削除日
     */
    private void insertAplMemTempInfoByPassword(Long aplMemRcptId, A111AACRRequestData requestData,
            String deletedDate) {

        // パスワードのハッシュ化
        String password = requestData.getAplMemTempInfo().getPassword();
        // パスワードのハッシュ化に用いるSALT（パスワードSALT）を生成する
        String passwordSalt = MailAddressAuthUtil.getRandomString(OpalDefaultConstants.SALT_PARAM_20);
        // パスワードのストレッチング回数を取得する
        int stretchingTimes = Integer.valueOf(SystemRepository.get(STRETCHING_TIMES));

        // UC000001：認証用ユーティリティ：パスワード認証コード生成
        String passwordAuthCode = "";
        try {
            passwordAuthCode = MailAddressAuthUtil.getPasswordAuthCode(password, passwordSalt, stretchingTimes);
        } catch (NoSuchAlgorithmException e) {
            throw new HttpErrorResponse(500, e);
        }

        // アプリ会員一時登録要求電文の内容をアプリ会員一時情報TBLに登録する
        insertApplicationMemberTempInfo(requestData, aplMemRcptId, passwordSalt, stretchingTimes, passwordAuthCode,
                deletedDate);
    }

    /**
     * メールアドレス存在チェック
     *
     * @param mailAddress
     *            アプリ会員一時登録要求電文.メールアドレス
     *
     * @return 処理結果（存在する：true 存在しない：false）
     */
    private Boolean mailAddressIsExist(String mailAddress) {

        // 存在チェック用のSQLの条件を設定する
        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("mailAddress", mailAddress);

        // アプリ会員情報TBLにメールアドレスの存在チェックを実行する。
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("SELECT_APPLICATION_MEMBER_ID");
        SqlResultSet aplMemInfoResult = statement.retrieve(condition);
        // メールアドレス存在する場合
        if (!aplMemInfoResult.isEmpty()) {
            return true;
        }

        // システム日時
        condition.put("sysDateTime", SystemTimeUtil.getTimestamp());
        // 処理区分
        condition.put("processDivision", OpalCodeConstants.MailAddressAuthProcessDivision.APL_MEM_REGIST);

        // アプリ会員一時情報TBLにメールアドレスの存在チェックを実行する。
        ParameterizedSqlPStatement statementTemp = getParameterizedSqlStatement("SELECT_APL_MEM_TEMP_INFO");
        SqlResultSet aplMemTempInfoResult = statementTemp.retrieve(condition);
        // メールアドレス存在する場合
        if (!aplMemTempInfoResult.isEmpty()) {
            return true;
        }

        // 処理区分
        condition.replace("processDivision", OpalCodeConstants.MailAddressAuthProcessDivision.MAIL_ADDRESS_CHANGE);

        // メールアドレス変更一時情報TBLにメールアドレスの存在チェックを実行する。
        ParameterizedSqlPStatement statementMail = getParameterizedSqlStatement("SELECT_MAIL_ADDRESS_CHANGE_TEMP_INFO");
        SqlResultSet mailAddressTempInfoResult = statementMail.retrieve(condition);
        // メールアドレス存在する場合
        if (!mailAddressTempInfoResult.isEmpty()) {
            return true;
        }

        // メールアドレス存在しない場合
        return false;
    }

    /**
     * アプリ会員一時情報登録
     *
     * @param data
     *            アプリ会員一時登録要求電文
     * @param aplMemRcptId
     *            アプリ会員登録受付ID
     * @param passwordSalt
     *            パスワードSALT
     * @param strechingTimes
     *            ストレッチング回数
     * @param passwordAuthCode
     *            パスワード認証コード
     * @param deletedDate
     *            論理削除日
     */
    private void insertApplicationMemberTempInfo(A111AACRRequestData data, Long aplMemRcptId, String passwordSalt,
            Integer strechingTimes, String passwordAuthCode, String deletedDate) {
        // アプリ会員一時情報登録の項目内容を設定する。
        Map<String, Object> condition = new HashMap<String, Object>();
        // アプリ会員登録受付ID
        condition.put("aplMemRegistRcptId", aplMemRcptId);
        // OP番号
        condition.put("osakaPitapaNumber", data.getAplMemTempInfo().getOsakaPitapaNumber());
        // アプリID
        condition.put("applicationId", data.getAplMemTempInfo().getApplicationId());
        // デバイスID
        condition.put("deviceId", data.getAplMemTempInfo().getDeviceId());
        // ログインID
        condition.put("loginId", data.getAplMemTempInfo().getLoginId());
        // パスワード
        condition.put("password", passwordAuthCode);
        // パスワードSALT
        condition.put("passwordSalt", passwordSalt);
        // ストレッチング回数
        condition.put("stretchingTimes", strechingTimes);
        // 生年月日
        condition.put("birthdate", data.getAplMemTempInfo().getBirthdate());
        // 性別コード
        condition.put("sexCode", data.getAplMemTempInfo().getSexCode());
        // メールアドレス
        condition.put("mailAddress", data.getAplMemTempInfo().getMailAddress());
        // レコメンド利用承諾可フラグ
        condition.put("recommendUseAcceptFlag", data.getAplMemTempInfo().getRecommendUseAcceptFlag());
        // アンケート1
        condition.put("enquete1", data.getAplMemTempInfo().getEnquete1());
        // アンケート2
        condition.put("enquete2", data.getAplMemTempInfo().getEnquete2());
        // アンケート3
        condition.put("enquete3", data.getAplMemTempInfo().getEnquete3());
        // アンケート4
        condition.put("enquete4", data.getAplMemTempInfo().getEnquete4());
        // アンケート5
        condition.put("enquete5", data.getAplMemTempInfo().getEnquete5());
        // アンケート6
        condition.put("enquete6", data.getAplMemTempInfo().getEnquete6());
        // アンケート7
        condition.put("enquete7", data.getAplMemTempInfo().getEnquete7());
        // アンケート8
        condition.put("enquete8", data.getAplMemTempInfo().getEnquete8());
        // アンケート9
        condition.put("enquete9", data.getAplMemTempInfo().getEnquete9());
        // アンケート10
        condition.put("enquete10", data.getAplMemTempInfo().getEnquete10());
        // 主なご利用駅1
        condition.put("mainUseStation1", data.getAplMemTempInfo().getMainUseStation1());
        // 主なご利用駅2
        condition.put("mainUseStation2", data.getAplMemTempInfo().getMainUseStation2());
        // 主なご利用駅3
        condition.put("mainUseStation3", data.getAplMemTempInfo().getMainUseStation3());
        // 主なご利用駅4
        condition.put("mainUseStation4", data.getAplMemTempInfo().getMainUseStation4());
        // 主なご利用駅5
        condition.put("mainUseStation5", data.getAplMemTempInfo().getMainUseStation5());
        // 休日1
        condition.put("dayOff1", data.getAplMemTempInfo().getDayOff1());
        // 休日2
        condition.put("dayOff2", data.getAplMemTempInfo().getDayOff2());
        // 処理済フラグ
        condition.put("processedFlag", OpalDefaultConstants.PROCESSED_FLAG_0);
        // 登録者ID
        condition.put("insertUserId", API_SERVER_ID);
        // 登録日時
        condition.put("insertDateTime", SystemTimeUtil.getTimestamp());
        // 最終更新者
        condition.put("updateUsetId", API_SERVER_ID);
        // 最終更新日時
        condition.put("updateDateTime", SystemTimeUtil.getTimestamp());
        // 削除フラグ(0:未削除)
        condition.put("deletedFlg", OpalDefaultConstants.DELETED_FLG_0);
        // 論理削除日
        condition.put("deletedDate", deletedDate);
        // 実行する。
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("INSERT_APL_MEM_TEMP_INFO");
        statement.executeUpdateByMap(condition);
    }

    /**
     * メールアドレス認証情報登録処理
     *
     * @param aplMemRcptId
     *            アプリ会員登録受付ID(採番)
     * @param requestData
     *            アプリ会員一時登録要求電文
     * @param deletedDate
     *            論理削除日
     */
    private void insertMailAddressAuthInfoByAuthCode(Long aplMemRcptId, A111AACRRequestData requestData,
            String deletedDate) {

        // 認証用ユーティリティ：salt生成
        String salt = MailAddressAuthUtil.getRandomString(OpalDefaultConstants.SALT_PARAM_20);

        // メールアドレス認証有効期限の算出（システム日時 + 有効期間）
        Calendar calendar = MailAddressAuthUtil
                .getExpiDate(Integer.valueOf(SystemRepository.get(EXPI_DATE_APL_MEM_TEMP)));

        // 認証用ユーティリティ：メールアドレス認証コード生成
        String mailAddressAuthCode = "";
        try {
            // メールアドレス認証有効期限（yyyyMMddHHmmssSSS）
            String mailAddressAuthDate = new SimpleDateFormat(OpalDefaultConstants.TIME_FORMAT)
                    .format(calendar.getTime());
            // メールアドレス認証コードの生成
            mailAddressAuthCode = MailAddressAuthUtil.getMailAddressAuthCode(aplMemRcptId, mailAddressAuthDate, salt);
        } catch (NoSuchAlgorithmException e) {
            throw new HttpErrorResponse(500, e);
        }

        // メールアドレス認証有効期限（yyyy-MM-dd HH:mm:ss.SSS）
        String mailAddressDate = new SimpleDateFormat(OpalDefaultConstants.DATE_TIME_MILLISECOND_FORMAT)
                .format(calendar.getTime());
        // メールアドレス
        String mailAddress = requestData.getAplMemTempInfo().getMailAddress();
        // メールアドレス認証情報TBLに登録する。
        insertMailAddressAuthInfo(mailAddressAuthCode, aplMemRcptId, salt, mailAddressDate, mailAddress, deletedDate);

        // メールアドレス確認用URL作成メール送信
        // URL＋メールアドレス認証コード
        StringBuffer mailAddressAuthCodeURL = new StringBuffer(SystemRepository.get(MAIL_ADDRESS_CONFIRM_URL));
        mailAddressAuthCodeURL.append(mailAddressAuthCode);

        // 差し込み項目
        List<String> variableItemValues = new ArrayList<String>();
        variableItemValues.add(mailAddressAuthCodeURL.toString());

        CM010001Component cm010001Component = new CM010001Component();
        // メールアドレス確認用URL作成メール送信
        cm010001Component.insMailLiteDeliverInfo(aplMemRcptId, API_SERVER_ID, mailAddress,
                MailDeliverType.MAIL_DELIVER_TYPE_1, OpalDefaultConstants.MAIL_TEMP_APL_MEM_REGIST_CONFIRM,
                variableItemValues, null);
    }

    /**
     * メールアドレス認証情報登録
     *
     * @param mailAddressAuthCode
     *            メールアドレス認証コード
     * @param mailAddressAuthKey
     *            メールアドレス認証キー
     * @param salt
     *            メールアドレス認証SALT
     * @param mailAddressDate
     *            メールアドレス認証有効期限
     * @param mailAddress
     *            メールアドレス
     * @param deletedDate
     *            論理削除日
     */
    private void insertMailAddressAuthInfo(String mailAddressAuthCode, Long mailAddressAuthKey, String salt,
            String mailAddressDate, String mailAddress, String deletedDate) {
        // メールアドレス認証情報登録の項目内容を設定する。
        Map<String, Object> condition = new HashMap<String, Object>();
        // メールアドレス認証コード
        condition.put("mailAddressAuthCode", mailAddressAuthCode);
        // メールアドレス認証キー
        condition.put("mailAddressAuthKey", mailAddressAuthKey);
        // メールアドレス認証SALT
        condition.put("mailAddressAuthSalt", salt);
        // メールアドレス認証有効期限
        condition.put("mailAddressAuthExpiDate", Timestamp.valueOf(mailAddressDate));
        // 処理区分
        condition.put("processDivision", OpalCodeConstants.MailAddressAuthProcessDivision.APL_MEM_REGIST);
        // メールアドレス
        condition.put("mailAddress", mailAddress);
        // 処理済フラグ
        condition.put("processFlag", OpalDefaultConstants.PROCESSED_FLAG_0);
        // 登録者ID
        condition.put("insertUserId", API_SERVER_ID);
        // 登録日時
        condition.put("insertDateTime", SystemTimeUtil.getTimestamp());
        // 最終更新者
        condition.put("updateUserId", API_SERVER_ID);
        // 最終更新日時
        condition.put("updareDateTime", SystemTimeUtil.getTimestamp());
        // 削除フラグ(0:未削除)
        condition.put("deletedFlg", OpalDefaultConstants.DELETED_FLG_0);
        // 論理削除日
        condition.put("deletedDate", deletedDate);
        // バージョン番号
        condition.put("version", OpalDefaultConstants.VERSION);
        // 実行する。
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("INSERT_MAIL_ADDRESS_AUTH_INFO");
        statement.executeUpdateByMap(condition);
    }
}
