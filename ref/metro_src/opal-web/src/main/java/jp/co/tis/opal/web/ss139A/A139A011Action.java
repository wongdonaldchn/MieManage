package jp.co.tis.opal.web.ss139A;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import nablarch.core.db.statement.ParameterizedSqlPStatement;
import nablarch.core.db.statement.SqlResultSet;
import nablarch.core.message.MessageLevel;
import nablarch.core.message.MessageUtil;
import nablarch.fw.web.HttpResponse;

import jp.co.tis.opal.common.constants.OpalCodeConstants;
import jp.co.tis.opal.web.common.ResponseData.ResponseErrorData;
import jp.co.tis.opal.web.common.constants.CheckResultConstants;
import jp.co.tis.opal.web.common.rest.AbstractRestBaseAction;

/**
 * A139A01:PiTaPa利用実績取得APIのアクションクラス。
 *
 * @author 唐
 * @since 1.0
 */
public class A139A011Action extends AbstractRestBaseAction<A139AAARRequestData> {

    /** PiTaPa利用実績データ応答電文のPiTaPa利用実績情報データ */
    private Map<String, Object> responsePitapaUseResInfo = new LinkedHashMap<String, Object>();

    /**
     * PiTaPa利用実績取得API
     *
     * @param requestData
     *            PiTaPa利用実績取得要求電文
     * @return HTTPレスポンス
     * @throws Exception
     *             異常
     */
    @Valid
    @Consumes(MediaType.APPLICATION_JSON)
    public HttpResponse getPitapaUseRes(A139AAARRequestData requestData) {
        HttpResponse responseData = super.execute(requestData);
        return responseData;
    }

    /**
     * 処理詳細ロジック
     *
     * @param requestData
     *            PiTaPa利用実績取得要求電文
     * @return 処理結果
     */
    @Override
    protected int executeLogic(A139AAARRequestData requestData) {
        // アプリ会員情報存在チェック
        Boolean aplMemInfoIsExist = checkAplMemInfo(Long.valueOf(requestData.getAplData().getApplicationMemberID()));

        // データが取得されない場合、応答電文を以下の通り設定して、「HTTPアクセスログ出力」を行う。
        if (!aplMemInfoIsExist) {
            return CheckResultConstants.APL_MEM_DATA_ISNULL;
        }

        // アプリ会員情報・PiTaPa利用実績情報取得
        SqlResultSet pitapaUseResInfo = getPitapaUseResInfo(requestData.getAplData());

        // アプリ会員情報・PiTaPa利用実績情報が存在しない場合、応答電文を以下の通り設定して、「HTTPアクセスログ出力」を行う。
        if (pitapaUseResInfo.isEmpty()) {
            return CheckResultConstants.APL_MEM_DATA_PITAPA_USE_DATA_ISNULL;
        }

        // PiTaPa利用実績情報データ設定
        setResponseParams(requestData.getAplData(), pitapaUseResInfo);

        return CheckResultConstants.CHECK_OK;
    }

    /**
     * HTTPレスポンスを設定する。
     *
     * @param requestData
     *            PiTaPa利用実績取得要求電文
     *
     * @param result
     *            チェックの結果
     *
     * @return HTTPレスポンス
     * @throws IOException
     *             異常
     */
    @Override
    protected HttpResponse responseBuilder(A139AAARRequestData requestData, int result) throws IOException {

        // PiTaPa利用実績データ応答電文設定
        A139AAASResponseData responseData = new A139AAASResponseData();
        // 応答電文.処理結果コード
        responseData.setResultCode(getResultCode(result));
        if (result == CheckResultConstants.APL_MEM_DATA_PITAPA_USE_DATA_ISNULL) {
            // アプリ会員情報・PiTaPa利用実績情報データかない
            ResponseErrorData error = new ResponseErrorData();
            error.setId("MA139A0103");
            error.setMessage(MessageUtil.createMessage(MessageLevel.ERROR, "MA139A0103").formatMessage());
            responseData.setError(error);
        } else if (result == CheckResultConstants.APL_MEM_DATA_ISNULL) {
            // アプリ会員情報データかない
            ResponseErrorData error = new ResponseErrorData();
            error.setId("MA139A0104");
            error.setMessage(MessageUtil.createMessage(MessageLevel.ERROR, "MA139A0104").formatMessage());
            responseData.setError(error);
        } else if (result == CheckResultConstants.CHECK_OK) {
            responseData.setPitapaUseResInfo(responsePitapaUseResInfo);
        }
        HttpResponse response = super.setHttpResponseData(responseData, result);
        return response;
    }

    /**
     * アプリ会員情報存在チェック
     *
     * @param applicationMemberId
     *            アプリ会員ID
     *
     * @return 処理結果
     */
    private Boolean checkAplMemInfo(Long applicationMemberId) {
        Boolean aplMemInfoIsExist = true;
        // アプリ会員情報取得用のSQL条件を設定する。
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("SELECT_APL_MEM_INFO");
        Map<String, Object> condition = new HashMap<String, Object>();
        // アプリ会員ID
        condition.put("applicationMemberId", applicationMemberId);
        // アプリ会員状態コード.OP認証済みのアプリ会員
        condition.put("statusA", OpalCodeConstants.AplMemStatusCode.OP_AUTH_APL_MEM);
        // アプリ会員状態コード.OP非会員
        condition.put("statusD", OpalCodeConstants.AplMemStatusCode.NOT_OP_MEM);

        // 実行する。
        SqlResultSet result = statement.retrieve(condition);
        if (result.isEmpty()) {
            aplMemInfoIsExist = false;
        }
        return aplMemInfoIsExist;
    }

    /**
     * アプリ会員情報・PiTaPa利用実績情報データ取得
     *
     * @param form
     *            PiTaPa利用実績取得APIの検索フォーム
     *
     * @return PiTaPa利用実績情報
     */
    private SqlResultSet getPitapaUseResInfo(A139AAARBodyForm form) {

        // PiTaPa利用実績情報取得用のSQL条件を設定する。
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("SELECT_PITAPA_USE_RES_INFO");
        Map<String, Object> condition = new HashMap<String, Object>();
        // アプリ会員ID
        condition.put("applicationMemberId", form.getApplicationMemberID());
        // PiTaPaご利用年月
        condition.put("pitapaUseYearMonth", form.getPitapaUseYearMonth());
        // アプリ会員状態コード(A：OP認証済みのアプリ会員)
        condition.put("statusA", OpalCodeConstants.AplMemStatusCode.OP_AUTH_APL_MEM);

        // 実行する。
        SqlResultSet result = statement.retrieve(condition);
        return result;
    }

    /**
     * PiTaPa利用実績情報データ設定
     *
     * @param form
     *            PiTaPa利用実績取得APIの検索フォーム
     * @param pitapaUseResInfo
     *            PiTaPa利用実績情報取得
     */
    private void setResponseParams(A139AAARBodyForm form, SqlResultSet pitapaUseResInfo) {
        // PiTaPaご利用年月
        responsePitapaUseResInfo.put("pitapaUseYearMonth", form.getPitapaUseYearMonth());
        // プランコード
        responsePitapaUseResInfo.put("planCode", pitapaUseResInfo.get(0).getString("PLAN_CODE"));
        // 会員単位支払合計
        responsePitapaUseResInfo.put("memberUnitPayTotal", pitapaUseResInfo.get(0).getString("MEMBER_UNIT_PAY_TOTAL"));
        // 会員単位支払合計の合計
        String memberUnitPayTotalTotal = pitapaUseResInfo.get(0).getString("MEMBER_UNIT_PAY_TOTAL_TOTAL");
        if (!"000".equals(pitapaUseResInfo.get(0).getString("MEM_CTRL_NUM_BR_NUM"))) {
            // 家族会員の場合、ブランクに変換
            memberUnitPayTotalTotal = "";
        }
        responsePitapaUseResInfo.put("memberUnitPayTotalTotal", memberUnitPayTotalTotal);

        // 明細書発送手数料
        responsePitapaUseResInfo.put("detailBookPostCharge",
                pitapaUseResInfo.get(0).getString("DETAIL_BOOK_POST_CHARGE"));
        // ショップdeポイント割引
        responsePitapaUseResInfo.put("shopDePointDiscount",
                pitapaUseResInfo.get(0).getString("SHOP_DE_POINT_DISCOUNT"));
        // 口座単位支払合計
        responsePitapaUseResInfo.put("accountUnitPayTotal",
                pitapaUseResInfo.get(0).getString("ACCOUNT_UNIT_PAY_TOTAL"));
        // 登録駅ご利用 適用金額
        responsePitapaUseResInfo.put("registStaUseApplyMoney",
                pitapaUseResInfo.get(0).getString("REGIST_STA_USE_APPLY_MONEY"));
        // 登録駅ご利用 割引後金額
        responsePitapaUseResInfo.put("registStaUseDisMoney",
                pitapaUseResInfo.get(0).getString("REGIST_STA_USE_DIS_MONEY"));
        // 登録駅外ご利用 適用金額
        responsePitapaUseResInfo.put("notRegistStaUseApplyMoney",
                pitapaUseResInfo.get(0).getString("NOT_REGIST_STA_USE_APPLY_MONEY"));
        // 登録駅外ご利用 割引後金額
        responsePitapaUseResInfo.put("notRegistStaUseDisMoney",
                pitapaUseResInfo.get(0).getString("NOT_REGIST_STA_USE_DIS_MONEY"));
        // 非登録型ご利用 適用金額
        responsePitapaUseResInfo.put("notRegistUseApplyMoney",
                pitapaUseResInfo.get(0).getString("NOT_REGIST_USE_APPLY_MONEY"));
        // 非登録型ご利用 割引後金額
        responsePitapaUseResInfo.put("notRegistUseDisMoney",
                pitapaUseResInfo.get(0).getString("NOT_REGIST_USE_DIS_MONEY"));
        // その他鉄道バスご利用
        responsePitapaUseResInfo.put("otherRailwayBusUse", pitapaUseResInfo.get(0).getString("OTHER_RAILWAY_BUS_USE"));
        // PiTaPaショッピング
        responsePitapaUseResInfo.put("pitapaShopping", pitapaUseResInfo.get(0).getString("PITAPA_SHOPPING"));
    }

}
