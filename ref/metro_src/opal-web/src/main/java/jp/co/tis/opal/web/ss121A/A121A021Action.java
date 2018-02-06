package jp.co.tis.opal.web.ss121A;

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
import nablarch.core.db.statement.SqlRow;
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
 * A121A02:家族会員サービス登録APIのアクションクラス。
 *
 * @author 曹
 * @since 1.0
 */
public class A121A021Action extends AbstractRestBaseAction<A121AAARRequestData> {

    /** API処理ID */
    private static final String API_PROCESS_ID = "A121A021";

    /**
     * 家族会員サービス登録API
     *
     * @param requestData
     *            家族会員サービス登録要求電文
     * @return 家族会員サービス登録結果応答電文
     */
    @Consumes(MediaType.APPLICATION_JSON)
    @Valid
    public HttpResponse insertFamilyMemInfo(A121AAARRequestData requestData) {

        HttpResponse responseData = super.execute(requestData);
        return responseData;

    }

    /**
     * 処理詳細
     *
     * @param requestData
     *            家族会員サービス登録要求電文
     * @return チェック結果
     */
    @Override
    protected int executeLogic(A121AAARRequestData requestData) {

        // アプリ会員情報取得
        SqlResultSet aplMemInfo = getAplMemInfo(Long.valueOf(requestData.getAplData().getApplicationMemberId()));

        if (aplMemInfo.isEmpty()) {
            // アプリ会員情報が存在しないと判断
            return CheckResultConstants.APL_MEM_DATA_ISNULL;
        }
        // アプリ会員状態コードが「D」（OP非会員）の場合、
        if (OpalCodeConstants.AplMemStatusCode.NOT_OP_MEM
                .equals(aplMemInfo.get(0).getString("APPLICATION_MEMBER_STATUS_CODE"))) {
            // OP認証未済みと判断
            return CheckResultConstants.OP_NOT_AUTHENTICATE;
        }
        // 会員管理番号
        String memberControlNumber = aplMemInfo.get(0).getString("MEMBER_CONTROL_NUMBER");
        // 会員管理番号枝番
        String memCtrlNumBrNum = aplMemInfo.get(0).getString("MEM_CTRL_NUM_BR_NUM");

        // 家族会員全員のアプリ会員情報の排他制御を実施
        Map<String, Object> appMenInfoExclusive = new HashMap<String, Object>();
        appMenInfoExclusive.put("memberControlNumber", memberControlNumber);
        appMenInfoExclusive.put("opAuthenticateFlag", OpalCodeConstants.OPAuthenticateFlag.OP_AUTH_FLAG_0);
        EntityList<AplMemInfoEntity> aplMemInfoList = UniversalDao.findAllBySqlFile(AplMemInfoEntity.class,
                "SELECT_APL_MEM_INFO_BY_FAMILY", appMenInfoExclusive);

        // 家族会員サービス登録有無チェック
        Boolean familyMemInfoHasInserted = checkFamilyMemInfoHasInserted(memberControlNumber);

        if (!familyMemInfoHasInserted) {
            // 家族会員サービス情報が登録済みと判断
            return CheckResultConstants.FAMILY_MEM_SERVICE_INFO_HAS_INSERTED;
        } else {
            if (aplMemInfoList.size() == 1) {
                // 家族会員が存在しないと判断
                return CheckResultConstants.FAMILY_MEM_IS_NOT_ENOUGH;
            } else {
                // 家族会員サービス登録
                insertFamilyMemServiceInfo(memberControlNumber, memCtrlNumBrNum);
            }
        }

        return CheckResultConstants.CHECK_OK;

    }

    /**
     * 家族会員サービス登録結果応答電文を設定する。
     *
     * @param requestData
     *            家族会員サービス登録要求電文
     *
     * @param result
     *            家族会員サービス登録要求チェック結果
     *
     * @return 家族会員サービス登録結果応答電文
     * @throws IOException
     *             異常
     */
    @Override
    protected HttpResponse responseBuilder(A121AAARRequestData requestData, int result) throws IOException {

        // 家族会員サービス登録結果応答電文設定
        A121AAASResponseData responseData = new A121AAASResponseData();
        // 応答電文.処理結果コード
        responseData.setResultCode(getResultCode(result));
        if (result == CheckResultConstants.APL_MEM_DATA_ISNULL) {
            // 応答電文.エラーメッセージ
            ResponseErrorData error = new ResponseErrorData();
            error.setId("MA121A0201");
            error.setMessage(MessageUtil.createMessage(MessageLevel.ERROR, "MA121A0201").formatMessage());
            responseData.setError(error);
        } else if (result == CheckResultConstants.OP_NOT_AUTHENTICATE) {
            // 応答電文.エラーメッセージ
            ResponseErrorData error = new ResponseErrorData();
            error.setId("MA121A0202");
            error.setMessage(MessageUtil.createMessage(MessageLevel.ERROR, "MA121A0202").formatMessage());
            responseData.setError(error);
        } else if (result == CheckResultConstants.FAMILY_MEM_SERVICE_INFO_HAS_INSERTED) {
            // 応答電文.エラーメッセージ
            ResponseErrorData error = new ResponseErrorData();
            error.setId("MA121A0203");
            error.setMessage(MessageUtil.createMessage(MessageLevel.ERROR, "MA121A0203").formatMessage());
            responseData.setError(error);
        } else if (result == CheckResultConstants.FAMILY_MEM_IS_NOT_ENOUGH) {
            // 応答電文.エラーメッセージ
            ResponseErrorData error = new ResponseErrorData();
            error.setId("MA121A0204");
            error.setMessage(MessageUtil.createMessage(MessageLevel.ERROR, "MA121A0204").formatMessage());
            responseData.setError(error);
        }

        HttpResponse response = super.setHttpResponseData(responseData, result);

        return response;
    }

    /**
     * アプリ会員情報取得。
     *
     * @param applicationMemberId
     *            アプリ会員ID
     *
     * @return aplMemInfo アプリ会員情報
     */
    private SqlResultSet getAplMemInfo(Long applicationMemberId) {

        // アプリ会員情報取得用のSQLの条件を設定する
        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("applicationMemberId", applicationMemberId);
        condition.put("statusCodeA", OpalCodeConstants.AplMemStatusCode.OP_AUTH_APL_MEM);
        condition.put("statusCodeD", OpalCodeConstants.AplMemStatusCode.NOT_OP_MEM);

        // アプリ会員情報取得
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("SELECT_APL_MEM_INFO");
        SqlResultSet aplMemInfo = statement.retrieve(condition);

        return aplMemInfo;
    }

    /**
     * 家族会員サービス登録有無チェック。
     *
     * @param memberControlNumber
     *            会員管理番号
     *
     * @return チェックの結果
     */
    private Boolean checkFamilyMemInfoHasInserted(String memberControlNumber) {

        Boolean familyMemInfoHasInserted = true;
        // 家族会員サービス情報取得用のSQLの条件を設定する
        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("memberControlNumber", memberControlNumber);
        condition.put("registStatusDivision", OpalCodeConstants.RegistStatusDivision.REGIST_STATUS_DIVISION_1);

        // 家族会員サービス情報取得
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("SELECT_FAMILY_MEM_INFO_COUNT");
        SqlRow memCtrlNumServiceCnt = statement.retrieve(condition).get(0);
        // 既に登録済みの場合、チェック結果を「false」に設定
        if (memCtrlNumServiceCnt.getInteger("FAMILY_MEM_SERVICE_CNT") >= 1) {
            familyMemInfoHasInserted = false;
        }

        return familyMemInfoHasInserted;
    }

    /**
     * 家族会員サービス登録。
     *
     * @param memberControlNumber
     *            会員管理番号
     * @param memCtrlNumBrNum
     *            会員管理番号枝番
     *
     */
    private void insertFamilyMemServiceInfo(String memberControlNumber, String memCtrlNumBrNum) {

        // 家族会員サービス管理ID採番
        Long familyMemCtrlId = IdGeneratorUtil.generateFamilyMemCtrlId();

        // 家族会員サービス情報登録用のSQLの条件を設定する
        Map<String, Object> condition = new HashMap<String, Object>();
        // 家族会員サービス管理ID
        condition.put("familyMemCtrlId", familyMemCtrlId);
        // 会員管理番号
        condition.put("memberControlNumber", memberControlNumber);
        // 申込者会員管理番号枝番
        condition.put("applicantMemCtrlNumBrNum", memCtrlNumBrNum);
        // 受付日時
        condition.put("rcptDataTime", SystemTimeUtil.getDateTimeString());
        // 登録状況区分
        condition.put("registStatusDivision", OpalCodeConstants.RegistStatusDivision.REGIST_STATUS_DIVISION_1);
        // 適用開始日
        condition.put("applyStartDateTime", SystemTimeUtil.getDateTimeString());
        // 適用終了日時
        condition.put("applyEndDateTime", null);
        // 登録者ID
        condition.put("insertUserId", API_PROCESS_ID);
        // 登録日時
        condition.put("insertDateTime", SystemTimeUtil.getTimestamp());
        // 最終更新者ID
        condition.put("updateUserId", API_PROCESS_ID);
        // 最終更新日時
        condition.put("updateDateTime", SystemTimeUtil.getTimestamp());
        // 削除フラグ
        condition.put("deletedFlg", OpalDefaultConstants.DELETED_FLG_0);
        // 論理削除日
        condition.put("deletedDate", null);
        // バージョン番号
        condition.put("version", OpalDefaultConstants.VERSION);

        // 家族会員サービス情報登録
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("INSERT_FAMILY_MEM_SERVICE_INFO");
        statement.executeUpdateByMap(condition);

    }
}
