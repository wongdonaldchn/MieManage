package jp.co.tis.opal.web.ss112A;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import nablarch.common.dao.EntityList;
import nablarch.common.dao.UniversalDao;
import nablarch.core.date.SystemTimeUtil;
import nablarch.core.db.statement.ParameterizedSqlPStatement;
import nablarch.core.message.MessageLevel;
import nablarch.core.message.MessageUtil;
import nablarch.core.repository.SystemRepository;
import nablarch.fw.web.HttpErrorResponse;
import nablarch.fw.web.HttpResponse;

import jp.co.tis.opal.common.component.CM010004Component;
import jp.co.tis.opal.common.constants.OpalCodeConstants;
import jp.co.tis.opal.common.constants.OpalDefaultConstants;
import jp.co.tis.opal.common.entity.AplMemInfoEntity;
import jp.co.tis.opal.common.utility.IdGeneratorUtil;
import jp.co.tis.opal.common.utility.MailAddressAuthUtil;
import jp.co.tis.opal.web.common.ResponseData.ResponseErrorData;
import jp.co.tis.opal.web.common.constants.CheckResultConstants;
import jp.co.tis.opal.web.common.rest.AbstractRestBaseAction;

/**
 * A112A01:アプリ会員ログインAPIのアクションクラス。
 *
 * @author 陳
 * @since 1.0
 */
public class A112A011Action extends AbstractRestBaseAction<A112AAARRequestData> {

    /** API */
    private static final String API_SERVER_ID = "A112A011";

    /** ログイン履歴情報データ保持期間(月) */
    private static final String LOGIN_HISTORY_PERIOD = "login_history_info_retention_period";

    /** レスポンスのアプリケーションデータ */
    private Map<String, Object> responseAplMemInfo = new LinkedHashMap<String, Object>();

    /**
     * アプリ会員ログインAPI
     *
     * @param requestData
     *            アプリ会員ログイン要求電文
     * @return HTTPレスポンス
     */
    @Consumes(MediaType.APPLICATION_JSON)
    @Valid
    public HttpResponse loginAppMemInfo(A112AAARRequestData requestData) {
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
    protected int executeLogic(A112AAARRequestData requestData) {

        // アプリ会員情報確認
        // 1) アプリ会員情報取得
        // アプリ会員ログイン要求電文.ログインIDを取得
        String loginId = requestData.getAplData().getLoginId();

        Map<String, Object> condition = new HashMap<String, Object>();
        // アプリ会員情報.ログインID＝アプリ会員ログイン要求電文.ログインID
        condition.put("loginId", loginId);
        // アプリ会員情報TBL.アプリ会員状態コード IN ("A"(OP認証済みのアプリ会員),"D"(OP非会員))
        condition.put("opAuthAplMem", OpalCodeConstants.AplMemStatusCode.OP_AUTH_APL_MEM);
        condition.put("notOpMem", OpalCodeConstants.AplMemStatusCode.NOT_OP_MEM);

        // 該当アプリ会員情報を取得する。
        EntityList<AplMemInfoEntity> aplMemInfoList = UniversalDao.findAllBySqlFile(AplMemInfoEntity.class,
                "SELECT_APL_MEM_INFO_BY_LOGIN_ID", condition);

        // データが存在しない場合、応答電文を以下の通り設定して、「HTTPアクセスログ出力」を行う。
        if (aplMemInfoList.isEmpty()) {
            return CheckResultConstants.APL_MEM_DATA_ISNULL;
        }

        // 2) パスワードチェック
        AplMemInfoEntity aplMemInfo = aplMemInfoList.get(0);
        String passwordAuthCode = "";
        try {
            passwordAuthCode = MailAddressAuthUtil.getPasswordAuthCode(requestData.getAplData().getPassword(),
                    aplMemInfo.getPasswordSalt(), aplMemInfo.getStretchingTimes());
        } catch (NoSuchAlgorithmException e) {
            throw new HttpErrorResponse(500, e);
        }

        // 相違がある場合、応答電文を以下の通り設定して、「HTTPアクセスログ出力」を行う。
        if (!aplMemInfo.getPassword().equals(passwordAuthCode)) {
            return CheckResultConstants.PASSWORD_ERROR;
        }

        // 3) アプリID変更有無チェック
        // 相違がある場合、アプリ会員情報TBLを更新する。
        if (!requestData.getAplData().getApplicationId().equals(aplMemInfo.getApplicationId())) {

            // アプリ会員ログイン要求電文.アプリIDとデバイスIDをアプリ会員情報TBLに更新する。
            // アプリ会員情報.アプリID＝アプリ会員ログイン要求電文.アプリID
            aplMemInfo.setApplicationId(requestData.getAplData().getApplicationId());
            // アプリ会員情報.デバイスID＝アプリ会員ログイン要求電文.デバイスID
            aplMemInfo.setDeviceId(requestData.getAplData().getDeviceId());
            // アプリ会員情報.最終更新者ID＝'A112A011'
            aplMemInfo.setUpdateUserId(API_SERVER_ID);
            // アプリ会員情報.最終更新日時
            aplMemInfo.setUpdateDateTime(SystemTimeUtil.getTimestamp());

            // アプリ会員情報更新
            UniversalDao.update(aplMemInfo);
        }

        // ログイン履歴情報登録
        // 1) ログイン履歴IDを採番する。(採番対象ID：1102)
        Long loginHistoryId = IdGeneratorUtil.generateLoginHistoryId();
        // 2) 論理削除日の算出
        int monthSpan = Integer.valueOf(SystemRepository.get(LOGIN_HISTORY_PERIOD));

        CM010004Component cm010004Component = new CM010004Component();
        String deletedDate = cm010004Component.getDeletedDateMonthly(monthSpan);

        // 3) ログイン履歴情報を登録する。
        insertLoginHistoryInfo(loginHistoryId, aplMemInfo.getApplicationMemberId(), deletedDate, requestData);

        // 取得したアプリ会員IDを応答電文のアプリケーションデータに設定する。
        responseAplMemInfo.put("applicationMemberId", String.valueOf(aplMemInfo.getApplicationMemberId()));

        return CheckResultConstants.CHECK_OK;
    }

    /**
     * HTTPレスポンスを設定する。
     *
     * @param requestData
     *            アプリ会員ログイン要求電文
     *
     * @param result
     *            チェックの結果
     *
     * @return HTTPレスポンス
     * @throws IOException
     *             IO異常
     */
    @Override
    protected HttpResponse responseBuilder(A112AAARRequestData requestData, int result) throws IOException {

        // 応答電文設定
        A112AAASResponseData responseData = new A112AAASResponseData();

        // 応答電文.処理結果コード
        responseData.setResultCode(getResultCode(result));
        if (result == CheckResultConstants.APL_MEM_DATA_ISNULL) {
            // 応答電文.エラーメッセージ
            ResponseErrorData error = new ResponseErrorData();
            error.setId("MA112A0101");
            error.setMessage(MessageUtil.createMessage(MessageLevel.ERROR, "MA112A0101").formatMessage());
            responseData.setError(error);
        } else if (result == CheckResultConstants.PASSWORD_ERROR) {
            // 応答電文.エラーメッセージ
            ResponseErrorData error = new ResponseErrorData();
            error.setId("MA112A0102");
            error.setMessage(MessageUtil.createMessage(MessageLevel.ERROR, "MA112A0102").formatMessage());
            responseData.setError(error);
        } else if (result == CheckResultConstants.CHECK_OK) {
            responseData.setAplData(responseAplMemInfo);
        }

        HttpResponse response = super.setHttpResponseData(responseData, result);

        return response;

    }

    /**
     * ログイン履歴情報登録
     *
     * @param loginHistoryId
     *            採番されたログイン履歴ID
     * @param applicationMemberId
     *            アプリ会員ID
     * @param deletedDate
     *            論理削除日
     * @param requestData
     *            アプリ会員ログイン要求電文
     */
    private void insertLoginHistoryInfo(Long loginHistoryId, Long applicationMemberId, String deletedDate,
            A112AAARRequestData requestData) {

        // ログイン履歴情報登録用のSQL条件を設定する。
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("INSERT_LOGIN_HISTORY_INFO");

        Map<String, Object> condition = new HashMap<String, Object>();
        // ログイン履歴ID
        condition.put("loginHistoryId", loginHistoryId);
        // アプリ会員ID
        condition.put("applicationMemberId", applicationMemberId);
        // ログインID
        condition.put("loginId", requestData.getAplData().getLoginId());
        // ログイン日時
        condition.put("loginDateTime", SystemTimeUtil.getTimestamp());
        // アプリID
        condition.put("applicationId", requestData.getAplData().getApplicationId());
        // デバイスID
        condition.put("deviceId", requestData.getAplData().getDeviceId());
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
}
