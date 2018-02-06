package jp.co.tis.opal.web.ss115A;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import nablarch.common.dao.EntityList;
import nablarch.common.dao.UniversalDao;
import nablarch.core.message.MessageLevel;
import nablarch.core.message.MessageUtil;
import nablarch.fw.web.HttpResponse;

import jp.co.tis.opal.common.constants.OpalCodeConstants;
import jp.co.tis.opal.common.entity.AplMemInfoEntity;
import jp.co.tis.opal.common.entity.MileBalanceInfoEntity;
import jp.co.tis.opal.web.common.ResponseData.ResponseErrorData;
import jp.co.tis.opal.web.common.component.CM111001Component;
import jp.co.tis.opal.web.common.constants.CheckResultConstants;
import jp.co.tis.opal.web.common.rest.AbstractRestBaseAction;

/**
 * {@link A115A011Action} OP認証APIのアクションクラス。
 *
 * @author 趙
 * @since 1.0
 */
public class A115A011Action extends AbstractRestBaseAction<A115AAARRequestData> {

    /** API処理ID */
    private static final String API_SERVER_ID = "A115A011";

    /**
     * OP認証処理。
     * <p>
     * 応答にJSONを使用する。
     * </p>
     *
     * @param requestData
     *            OP認証要求電文
     * @return OP認証結果応答電文
     */
    @Consumes(MediaType.APPLICATION_JSON)
    @Valid
    public HttpResponse setOpAuthentication(A115AAARRequestData requestData) {
        HttpResponse responseData = super.execute(requestData);
        return responseData;
    }

    /**
     * 処理詳細。
     *
     * @param requestData
     *            OP認証要求電文
     * @return チェック結果
     */
    @Override
    protected int executeLogic(A115AAARRequestData requestData) {

        // アプリ会員ID
        Long applicationMemberId = Long.valueOf(requestData.getOpAuthenticateData().getApplicationMemberId());
        // OP番号
        String osakaPitapaNumber = requestData.getOpAuthenticateData().getOsakaPitapaNumber();
        // マイル加算減算受付番号
        String mileAddSubRcptNum = requestData.getOpAuthenticateData().getMileAddSubRcptNum();
        // OP認証ボーナスマイル数
        Long opAuthBonusMileAmount = Long.valueOf(requestData.getOpAuthenticateData().getOpAuthBonusMileAmount());

        // アプリ会員情報（アプリ会員登録時に作成したデータ）のロックを取得
        Map<String, Object> exclusiveMap = new HashMap<String, Object>();
        exclusiveMap.put("applicationMemberId", applicationMemberId);
        exclusiveMap.put("opAuthAplMem", OpalCodeConstants.AplMemStatusCode.OP_AUTH_APL_MEM);
        exclusiveMap.put("notOpMem", OpalCodeConstants.AplMemStatusCode.NOT_OP_MEM);

        EntityList<AplMemInfoEntity> aplMemInfoList = UniversalDao.findAllBySqlFile(AplMemInfoEntity.class,
                "SELECT_APL_MEM_INFO_BY_APL", exclusiveMap);
        // アプリ会員情報データが存在しない場合
        if (aplMemInfoList.isEmpty()) {
            return CheckResultConstants.APL_MEM_DATA_ISNULL;
        }

        // OP認証済チェック
        if (OpalCodeConstants.AplMemStatusCode.OP_AUTH_APL_MEM
                .equals(aplMemInfoList.get(0).getApplicationMemberStatusCode())) {
            return CheckResultConstants.OP_AUTHENTICATE;
        }

        // OP番号に紐づくアプリ会員情報のロック取得
        exclusiveMap.put("osakaPitapaNumber", osakaPitapaNumber);
        UniversalDao.findAllBySqlFile(AplMemInfoEntity.class, "SELECT_APL_MEM_INFO_LOCK_BY_OP", exclusiveMap);

        // マイル残高情報のロック取得
        UniversalDao.findAllBySqlFile(MileBalanceInfoEntity.class, "SELECT_MILE_BALANCE_INFO", exclusiveMap);

        // OP認証実施
        // OP認証共通コンポーネントを呼出す
        CM111001Component cm111001Component = new CM111001Component();
        Map<String, Boolean> result = cm111001Component.setOPAuth(applicationMemberId, osakaPitapaNumber,
                mileAddSubRcptNum, opAuthBonusMileAmount, API_SERVER_ID);

        if (!result.get("OP_AUTH_RESULT")) {
            // コンポーネントにて、OP会員情報取得エラーの場合、
            return CheckResultConstants.OP_DATA_ISNULL;
        }
        if (!result.get("OP_AUTH_RELEASE_MAIL_DELIVER_FLAG")) {
            // コンポーネントにて、OOP認証解除配信不可の場合、
            String message = MessageUtil.createMessage(MessageLevel.WARN, "MA115A0104").formatMessage();
            LOGGER.logWarn(message);
        }
        if (!result.get("MILE_SUB_FLAG")) {
            // コンポーネントにて、マイル減算不可の場合、
            return CheckResultConstants.MAIL_BALANCE_NOT_ENOUGH;
        }
        if (!result.get("OP_AUTH_MAIL_DELIVER_FLAG")) {
            // コンポーネントにて、OOP認証成功配信不可の場合、
            String message = MessageUtil.createMessage(MessageLevel.WARN, "MA115A0105").formatMessage();
            LOGGER.logWarn(message);
        }

        return CheckResultConstants.CHECK_OK;
    }

    /**
     * HTTPレスポンスを設定する。
     *
     * @param requestData
     *            OP認証要求電文
     *
     * @param result
     *            OP認証要求電文チェック結果
     *
     * @return HTTPレスポンス
     * @throws IOException
     *             異常
     */
    @Override
    protected HttpResponse responseBuilder(A115AAARRequestData requestData, int result) throws IOException {

        // OP認証結果応答電文設定
        A115AAASResponseData responseData = new A115AAASResponseData();
        // OP認証結果応答電文.処理結果コード
        responseData.setResultCode(getResultCode(result));
        if (result == CheckResultConstants.APL_MEM_DATA_ISNULL) {
            // OP認証結果応答電文.エラーメッセージ
            ResponseErrorData error = new ResponseErrorData();
            error.setId("MA115A0101");
            error.setMessage(MessageUtil.createMessage(MessageLevel.ERROR, "MA115A0101").formatMessage());
            responseData.setError(error);
        } else if (result == CheckResultConstants.OP_AUTHENTICATE) {
            // OP認証結果応答電文.エラーメッセージ
            ResponseErrorData error = new ResponseErrorData();
            error.setId("MA115A0102");
            error.setMessage(MessageUtil.createMessage(MessageLevel.ERROR, "MA115A0102").formatMessage());
            responseData.setError(error);
        } else if (result == CheckResultConstants.OP_DATA_ISNULL) {
            // OP認証結果応答電文.エラーメッセージ
            ResponseErrorData error = new ResponseErrorData();
            error.setId("MA115A0103");
            error.setMessage(MessageUtil.createMessage(MessageLevel.ERROR, "MA115A0103").formatMessage());
            responseData.setError(error);
        } else if (result == CheckResultConstants.MAIL_BALANCE_NOT_ENOUGH) {
            // OP認証結果応答電文.エラーメッセージ
            ResponseErrorData error = new ResponseErrorData();
            error.setId("MA115A0106");
            error.setMessage(MessageUtil.createMessage(MessageLevel.ERROR, "MA115A0106").formatMessage());
            responseData.setError(error);
        }

        HttpResponse response = super.setHttpResponseData(responseData, result);

        return response;
    }
}
