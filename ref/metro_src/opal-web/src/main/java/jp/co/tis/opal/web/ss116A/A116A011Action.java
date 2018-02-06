package jp.co.tis.opal.web.ss116A;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import nablarch.common.dao.EntityList;
import nablarch.common.dao.UniversalDao;
import nablarch.core.date.SystemTimeUtil;
import nablarch.core.message.MessageLevel;
import nablarch.core.message.MessageUtil;
import nablarch.core.repository.SystemRepository;
import nablarch.fw.web.HttpErrorResponse;
import nablarch.fw.web.HttpResponse;

import jp.co.tis.opal.common.constants.OpalCodeConstants;
import jp.co.tis.opal.common.constants.OpalDefaultConstants;
import jp.co.tis.opal.common.entity.AplMemInfoEntity;
import jp.co.tis.opal.common.utility.MailAddressAuthUtil;
import jp.co.tis.opal.web.common.ResponseData.ResponseErrorData;
import jp.co.tis.opal.web.common.constants.CheckResultConstants;
import jp.co.tis.opal.web.common.rest.AbstractRestBaseAction;

/**
 * A116A01:パスワード変更APIのアクションクラス。
 *
 * @author 陳
 * @since 1.0
 */
public class A116A011Action extends AbstractRestBaseAction<A116AAARRequestData> {

    /** API */
    private static final String API_SERVER_ID = "A116A011";

    /** ストレッチング回数_パスワード変更 */
    private static final String STRETCHING_TIMES = "stretching_times_password_change";

    /**
     * パスワード変更API
     *
     * @param requestData
     *            パスワード変更要求電文
     * @return HTTPレスポンス
     */
    @Consumes(MediaType.APPLICATION_JSON)
    @Valid
    public HttpResponse resetPassword(A116AAARRequestData requestData) {
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
    protected int executeLogic(A116AAARRequestData requestData) {

        // パスワード変更
        // 1) アプリ会員情報取得
        // パスワード変更要求電文.アプリ会員IDを取得する。
        Long applicationMemberId = Long.valueOf(requestData.getAplData().getApplicationMemberId());

        Map<String, Object> condition = new HashMap<String, Object>();
        // アプリ会員情報.アプリ会員ID＝パスワード変更要求電文.アプリ会員ID
        condition.put("applicationMemberId", applicationMemberId);
        condition.put("opAuthAplMem", OpalCodeConstants.AplMemStatusCode.OP_AUTH_APL_MEM);
        condition.put("notOpMem", OpalCodeConstants.AplMemStatusCode.NOT_OP_MEM);

        // 該当アプリ会員情報を取得する。
        EntityList<AplMemInfoEntity> aplMemInfoList = UniversalDao.findAllBySqlFile(AplMemInfoEntity.class,
                "SELECT_APL_MEM_INFO_BY_APL", condition);

        // データが存在しない場合、応答電文を以下の通り設定して、「HTTPアクセスログ出力」を行う。
        if (aplMemInfoList.size() == 0) {
            return CheckResultConstants.APL_MEM_DATA_ISNULL;
        }

        // 2) 旧パスワードチェック
        AplMemInfoEntity aplMemInfo = aplMemInfoList.get(0);
        String passwordAuthCode;
        try {
            passwordAuthCode = MailAddressAuthUtil.getPasswordAuthCode(requestData.getAplData().getOldPassword(),
                    aplMemInfo.getPasswordSalt(), aplMemInfo.getStretchingTimes());
        } catch (NoSuchAlgorithmException e) {
            throw new HttpErrorResponse(500, e);
        }

        // 相違がある場合、応答電文を以下の通り設定して、「HTTPアクセスログ出力」を行う。
        if (!aplMemInfo.getPassword().equals(passwordAuthCode)) {
            return CheckResultConstants.PASSWORD_ERROR;
        }

        // 3) アプリ会員情報更新
        // ①更新用パスワードSALTを生成する。
        String passwordSalt = MailAddressAuthUtil.getRandomString(OpalDefaultConstants.SALT_PARAM_20);

        // ②更新用ストレッチング回数を取得する。
        Integer strechingTimes = Integer.valueOf(SystemRepository.get(STRETCHING_TIMES));

        // ③更新用パスワードのハッシュ化を行う。
        String newPasswordAuthCode;
        try {
            newPasswordAuthCode = MailAddressAuthUtil.getPasswordAuthCode(requestData.getAplData().getNewPassword(),
                    passwordSalt, strechingTimes);
        } catch (NoSuchAlgorithmException e) {
            throw new HttpErrorResponse(500, e);
        }

        // ④アプリ会員情報TBLを更新する。
        // アプリ会員情報.パスワード
        aplMemInfo.setPassword(newPasswordAuthCode);
        // アプリ会員情報.パスワードSALT
        aplMemInfo.setPasswordSalt(passwordSalt);
        // アプリ会員情報.ストレッチング回数
        aplMemInfo.setStretchingTimes(strechingTimes);
        // アプリ会員情報.最終更新者ID＝'A116A011'
        aplMemInfo.setUpdateUserId(API_SERVER_ID);
        // アプリ会員情報.最終更新日時
        aplMemInfo.setUpdateDateTime(SystemTimeUtil.getTimestamp());

        // アプリ会員情報更新
        UniversalDao.update(aplMemInfo);

        return CheckResultConstants.CHECK_OK;
    }

    /**
     * HTTPレスポンスを設定する。
     *
     * @param requestData
     *            パスワード変更要求電文
     *
     * @param result
     *            チェックの結果
     *
     * @return HTTPレスポンス
     * @throws IOException
     *             IO異常
     */
    @Override
    protected HttpResponse responseBuilder(A116AAARRequestData requestData, int result) throws IOException {

        // 応答電文設定
        A116AAASResponseData responseData = new A116AAASResponseData();

        // 応答電文.処理結果コード
        responseData.setResultCode(getResultCode(result));
        if (result == CheckResultConstants.APL_MEM_DATA_ISNULL) {
            // 応答電文.エラーメッセージ
            ResponseErrorData error = new ResponseErrorData();
            error.setId("MA116A0101");
            error.setMessage(MessageUtil.createMessage(MessageLevel.ERROR, "MA116A0101").formatMessage());
            responseData.setError(error);
        } else if (result == CheckResultConstants.PASSWORD_ERROR) {
            // 応答電文.エラーメッセージ
            ResponseErrorData error = new ResponseErrorData();
            error.setId("MA116A0102");
            error.setMessage(MessageUtil.createMessage(MessageLevel.ERROR, "MA116A0102").formatMessage());
            responseData.setError(error);
        }

        HttpResponse response = super.setHttpResponseData(responseData, result);

        return response;

    }
}
