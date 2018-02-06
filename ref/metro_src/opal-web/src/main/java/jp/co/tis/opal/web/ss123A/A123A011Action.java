package jp.co.tis.opal.web.ss123A;

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
import jp.co.tis.opal.common.entity.AplMemInfoEntity;
import jp.co.tis.opal.common.utility.IdGeneratorUtil;
import jp.co.tis.opal.web.common.ResponseData.ResponseErrorData;
import jp.co.tis.opal.web.common.constants.CheckResultConstants;
import jp.co.tis.opal.web.common.rest.AbstractRestBaseAction;

/**
 * A123A01:パートナー会員サービス登録APIのアクションクラス。
 *
 * @author 陳
 * @since 1.0
 */
public class A123A011Action extends AbstractRestBaseAction<A123AAARRequestData> {

    /** API */
    private static final String API_SERVER_ID = "A123A011";

    /**
     * パートナー会員サービス登録API
     *
     * @param requestData
     *            パートナー会員サービス登録要求電文
     * @return HTTPレスポンス
     */
    @Consumes(MediaType.APPLICATION_JSON)
    @Valid
    public HttpResponse insertPartnerMemServiceInfo(A123AAARRequestData requestData) {
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
    protected int executeLogic(A123AAARRequestData requestData) {

        // パートナー登録アプリ会員とパートナー相手アプリ会員のアプリ会員情報取得
        EntityList<AplMemInfoEntity> aplMemInfoList = getAplMemInfo(requestData.getAplData());

        // パートナー登録アプリ会員のアプリ会員情報
        AplMemInfoEntity partnerRegistAplMemInfo = getPartnerAplMemInfo(aplMemInfoList,
                requestData.getAplData().getApplicationMemberId());

        // アプリ会員情報が存在しない場合、応答電文を以下の通り設定して、「HTTPアクセスログ出力」を行う。
        if (partnerRegistAplMemInfo == null) {
            return CheckResultConstants.PARTNER_REGIST_MEM_INFO_CHECK_ERROR1;
        }

        // パートナー登録アプリ会員チェック
        // ① パートナー会員サービス利用可チェック
        if (OpalCodeConstants.AplMemStatusCode.NOT_OP_MEM
                .equals(partnerRegistAplMemInfo.getApplicationMemberStatusCode())) {
            return CheckResultConstants.PARTNER_REGIST_MEM_INFO_CHECK_ERROR2;
        }

        // ② パートナー会員サービス登録可否チェック
        // パートナー登録者会員管理番号
        String partnerRegistCtrlNum = partnerRegistAplMemInfo.getMemberControlNumber();
        // パートナー登録者会員管理番号枝番
        String partnerRegistCtrlBrNum = partnerRegistAplMemInfo.getMemCtrlNumBrNum();
        // パートナー登録者のパートナー会員サービス登録状況をパートナー会員サービス情報TBLから取得する。
        SqlResultSet partnerRegistPartnerMemServiceInfo = getPartnerMemServiceInfo(partnerRegistCtrlNum,
                partnerRegistCtrlBrNum);

        // パートナー会員サービス登録状況が取得できた場合
        if (!partnerRegistPartnerMemServiceInfo.isEmpty()) {
            return CheckResultConstants.PARTNER_REGIST_MEM_INFO_CHECK_ERROR3;
        }

        // パートナーアプリ会員情報取得
        AplMemInfoEntity partnerAplMemInfo = getPartnerAplMemInfo(aplMemInfoList,
                requestData.getAplData().getPartnerApplicationMemberId());

        // アプリ会員情報が存在しない場合、応答電文を以下の通り設定して、「HTTPアクセスログ出力」を行う。
        if (partnerAplMemInfo == null) {
            return CheckResultConstants.PARTNER_MEM_INFO_CHECK_ERROR1;
        }

        // パートナーアプリ会員チェック
        // ① パートナー会員サービス利用可チェック
        if (OpalCodeConstants.AplMemStatusCode.NOT_OP_MEM.equals(partnerAplMemInfo.getApplicationMemberStatusCode())) {
            return CheckResultConstants.PARTNER_MEM_INFO_CHECK_ERROR2;
        }

        // ② 家族会員チェック
        if (partnerAplMemInfo.getMemberControlNumber().equals(partnerRegistAplMemInfo.getMemberControlNumber())) {
            return CheckResultConstants.PARTNER_MEM_INFO_CHECK_ERROR3;
        }

        // ③ パートナー会員サービス登録可否チェック
        // パートナー会員管理番号
        String partnerUserMemCtrlNum = partnerAplMemInfo.getMemberControlNumber();
        // パートナー会員管理番号枝番
        String partnerUserMemCtrlBrNum = partnerAplMemInfo.getMemCtrlNumBrNum();
        // パートナーのパートナー会員サービス登録状況をパートナー会員サービス情報TBLから取得する。
        SqlResultSet partnerPartnerMemServiceInfo = getPartnerMemServiceInfo(partnerUserMemCtrlNum,
                partnerUserMemCtrlBrNum);

        // パートナー会員サービス登録状況が取得できた場合
        if (!partnerPartnerMemServiceInfo.isEmpty()) {
            return CheckResultConstants.PARTNER_MEM_INFO_CHECK_ERROR4;
        }

        // パートナー会員サービス情報登録
        // 1) パートナー会員サービス管理IDを採番する。(採番対象ID：1200)
        Long partnerMemServiceCtrlId = IdGeneratorUtil.generatePartnerMemCtrlId();

        // 2) パートナー会員サービス情報登録。
        insertPartnerMemServiceInfo(partnerMemServiceCtrlId, partnerRegistCtrlNum, partnerRegistCtrlBrNum,
                partnerUserMemCtrlNum, partnerUserMemCtrlBrNum);

        return CheckResultConstants.CHECK_OK;
    }

    /**
     * HTTPレスポンスを設定する。
     *
     * @param requestData
     *            パートナー会員サービス登録要求電文
     *
     * @param result
     *            チェックの結果
     *
     * @return HTTPレスポンス
     * @throws IOException
     *             IO異常
     */
    @Override
    protected HttpResponse responseBuilder(A123AAARRequestData requestData, int result) throws IOException {

        // 応答電文設定
        A123AAASResponseData responseData = new A123AAASResponseData();

        // 応答電文.処理結果コード
        responseData.setResultCode(getResultCode(result));
        if (result == CheckResultConstants.PARTNER_REGIST_MEM_INFO_CHECK_ERROR1) {
            // 応答電文.エラーメッセージ
            ResponseErrorData error = new ResponseErrorData();
            error.setId("MA123A0102");
            error.setMessage(MessageUtil.createMessage(MessageLevel.ERROR, "MA123A0102").formatMessage());
            responseData.setError(error);
        } else if (result == CheckResultConstants.PARTNER_REGIST_MEM_INFO_CHECK_ERROR2) {
            // 応答電文.エラーメッセージ
            ResponseErrorData error = new ResponseErrorData();
            error.setId("MA123A0103");
            error.setMessage(MessageUtil.createMessage(MessageLevel.ERROR, "MA123A0103").formatMessage());
            responseData.setError(error);
        } else if (result == CheckResultConstants.PARTNER_REGIST_MEM_INFO_CHECK_ERROR3) {
            // 応答電文.エラーメッセージ
            ResponseErrorData error = new ResponseErrorData();
            error.setId("MA123A0104");
            error.setMessage(MessageUtil.createMessage(MessageLevel.ERROR, "MA123A0104").formatMessage());
            responseData.setError(error);
        } else if (result == CheckResultConstants.PARTNER_MEM_INFO_CHECK_ERROR1) {
            // 応答電文.エラーメッセージ
            ResponseErrorData error = new ResponseErrorData();
            error.setId("MA123A0105");
            error.setMessage(MessageUtil.createMessage(MessageLevel.ERROR, "MA123A0105").formatMessage());
            responseData.setError(error);
        } else if (result == CheckResultConstants.PARTNER_MEM_INFO_CHECK_ERROR2) {
            // 応答電文.エラーメッセージ
            ResponseErrorData error = new ResponseErrorData();
            error.setId("MA123A0106");
            error.setMessage(MessageUtil.createMessage(MessageLevel.ERROR, "MA123A0106").formatMessage());
            responseData.setError(error);
        } else if (result == CheckResultConstants.PARTNER_MEM_INFO_CHECK_ERROR3) {
            // 応答電文.エラーメッセージ
            ResponseErrorData error = new ResponseErrorData();
            error.setId("MA123A0107");
            error.setMessage(MessageUtil.createMessage(MessageLevel.ERROR, "MA123A0107").formatMessage());
            responseData.setError(error);
        } else if (result == CheckResultConstants.PARTNER_MEM_INFO_CHECK_ERROR4) {
            // 応答電文.エラーメッセージ
            ResponseErrorData error = new ResponseErrorData();
            error.setId("MA123A0108");
            error.setMessage(MessageUtil.createMessage(MessageLevel.ERROR, "MA123A0108").formatMessage());
            responseData.setError(error);
        }

        HttpResponse response = super.setHttpResponseData(responseData, result);

        return response;

    }

    /**
     * パートナー登録アプリ会員とパートナー相手アプリ会員のアプリ会員情報取得
     *
     * @param form
     *            アプリケーションデータ
     *
     * @return アプリ会員情報
     */
    private EntityList<AplMemInfoEntity> getAplMemInfo(A123AAARBodyForm form) {

        Map<String, Object> condition = new HashMap<String, Object>();
        // パートナー会員サービス登録要求電文.パートナー登録アプリ会員ID
        condition.put("applicationMemberId", form.getApplicationMemberId());
        // パートナー会員サービス登録要求電文.パートナー相手アプリ会員ID
        condition.put("partnerApplicationMemberId", form.getPartnerApplicationMemberId());
        // アプリ会員情報TBL.アプリ会員状態コード IN ("A"(OP認証済みのアプリ会員),"D"(OP非会員))
        condition.put("opAuthAplMem", OpalCodeConstants.AplMemStatusCode.OP_AUTH_APL_MEM);
        condition.put("notOpMem", OpalCodeConstants.AplMemStatusCode.NOT_OP_MEM);

        EntityList<AplMemInfoEntity> aplMemInfoList = UniversalDao.findAllBySqlFile(AplMemInfoEntity.class,
                "SELECT_APL_MEM_INFO_BY_PARTNER", condition);

        return aplMemInfoList;
    }

    /**
     * パートナー登録アプリ会員/パートナーアプリ会員のアプリ会員情報取得
     *
     * @param list
     *            パートナー登録アプリ会員とパートナー相手アプリ会員のアプリ会員情報
     * @param applicationMemberId
     *            パートナー登録アプリ会員ID/パートナー相手アプリ会員ID
     *
     * @return アプリ会員情報
     */
    private AplMemInfoEntity getPartnerAplMemInfo(EntityList<AplMemInfoEntity> list, String applicationMemberId) {
        AplMemInfoEntity result = null;
        if (list.size() > 0) {
            for (AplMemInfoEntity aplMemInfo : list) {
                if (applicationMemberId.equals(String.valueOf(aplMemInfo.getApplicationMemberId()))) {
                    result = aplMemInfo;
                }
            }
        }
        return result;
    }

    /**
     * パートナー会員サービス情報データ取得
     *
     * @param memberControlNumber
     *            会員管理番号
     * @param memCtrlNumBrNum
     *            会員管理番号枝番
     *
     * @return パートナー会員サービス情報
     */
    private SqlResultSet getPartnerMemServiceInfo(String memberControlNumber, String memCtrlNumBrNum) {

        // パートナー会員サービス情報取得用のSQL条件を設定する。
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("SELECT_PARTNER_MEM_SERVICE_INFO");

        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("memberControlNumber", memberControlNumber);
        condition.put("memCtrlNumBrNum", memCtrlNumBrNum);
        condition.put("admitStatusDivision", OpalCodeConstants.AdmitStatusDivision.ADMIT_STATUS_DIVISION_1);

        // 実行する。
        SqlResultSet result = statement.retrieve(condition);
        return result;
    }

    /**
     * パートナー会員サービス情報登録
     *
     * @param partnerMemServiceCtrlId
     *            パートナー会員サービス管理ID
     * @param partnerRegistCtrlNum
     *            パートナー登録者会員管理番号
     * @param partnerRegistCtrlBrNum
     *            パートナー登録者会員管理番号枝番
     * @param partnerUserMemCtrlNum
     *            パートナー会員管理番号
     * @param partnerUserMemCtrlBrNum
     *            パートナー会員管理番号枝番
     */
    private void insertPartnerMemServiceInfo(Long partnerMemServiceCtrlId, String partnerRegistCtrlNum,
            String partnerRegistCtrlBrNum, String partnerUserMemCtrlNum, String partnerUserMemCtrlBrNum) {

        // パートナー会員サービス情報登録用のSQL条件を設定する。
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("INSERT_PARTNER_MEM_SERVICE_INFO");

        Map<String, Object> condition = new HashMap<String, Object>();
        // パートナー会員サービス管理ID
        condition.put("partnerMemServiceCtrlId", partnerMemServiceCtrlId);
        // パートナー登録者会員管理番号
        condition.put("partnerRegistCtrlNum", partnerRegistCtrlNum);
        // パートナー登録者会員管理番号枝番
        condition.put("partnerRegistCtrlBrNum", partnerRegistCtrlBrNum);
        // パートナー会員管理番号
        condition.put("partnerUserMemCtrlNum", partnerUserMemCtrlNum);
        // パートナー会員管理番号枝番
        condition.put("partnerUserMemCtrlBrNum", partnerUserMemCtrlBrNum);
        // 承認状況区分
        condition.put("admitStatusDivision", OpalCodeConstants.AdmitStatusDivision.ADMIT_STATUS_DIVISION_1);
        // 適用開始日時
        condition.put("applyStartDateTime", SystemTimeUtil.getDateTimeString());
        // 適用終了日時
        condition.put("applyEndDateTime", null);
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
        condition.put("deletedDate", null);
        // バージョン番号
        condition.put("version", OpalDefaultConstants.VERSION);

        // 実行する。
        statement.executeUpdateByMap(condition);
    }

}
