package jp.co.tis.opal.web.ss133A;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import nablarch.common.code.CodeUtil;
import nablarch.core.db.statement.ParameterizedSqlPStatement;
import nablarch.core.db.statement.SqlResultSet;
import nablarch.core.db.statement.SqlRow;
import nablarch.core.message.MessageLevel;
import nablarch.core.message.MessageUtil;
import nablarch.fw.web.HttpResponse;

import jp.co.tis.opal.common.constants.OpalCodeConstants;
import jp.co.tis.opal.common.constants.OpalDefaultConstants;
import jp.co.tis.opal.common.utility.DateConvertUtil;
import jp.co.tis.opal.web.common.ResponseData.ResponseErrorData;
import jp.co.tis.opal.web.common.constants.CheckResultConstants;
import jp.co.tis.opal.web.common.rest.AbstractRestBaseAction;

/**
 * A133A01:マイル利用明細取得APIのアクションクラス。
 *
 * @author 唐
 * @since 1.0
 */
public class A133A011Action extends AbstractRestBaseAction<A133AAARRequestData> {

    /** マイル利用明細データ応答電文のマイル利用明細情報 */
    private List<Map<String, Object>> responseMileUseDetails = new ArrayList<Map<String, Object>>();

    /**
     * マイル利用明細取得API
     *
     * @param requestData
     *            マイル利用明細取得要求電文
     * @return HTTPレスポンス
     * @throws Exception
     *             異常
     */
    @Valid
    @Consumes(MediaType.APPLICATION_JSON)
    public HttpResponse getMileUseDetailInfo(A133AAARRequestData requestData) {
        HttpResponse responseData = super.execute(requestData);
        return responseData;
    }

    /**
     * 処理詳細ロジック
     *
     * @param requestData
     *            マイル利用明細取得要求電文
     * @return 処理結果
     */
    @Override
    protected int executeLogic(A133AAARRequestData requestData) {

        StringBuffer objectStartYearMonth = new StringBuffer();
        // システム日付の月 < 4月の場合
        if (Integer.parseInt(DateConvertUtil.getSysMonth()) < 4) {
            // マイル利用明細取得要求電文.照会対象年月が照会対象開始年月((システム日付の年 - 2年) +
            // "03")より古い日付の場合
            objectStartYearMonth.append(Integer.parseInt(DateConvertUtil.getSysYear()) - 2);
            objectStartYearMonth.append(OpalDefaultConstants.MILE_INVALID_FROM_MONTH);
            if (objectStartYearMonth.toString().compareTo(requestData.getAplData().getObjectYearMonth()) > 0) {
                return CheckResultConstants.OBJECT_YEAR_MONTH_ERROR;
            }
        } else {
            // システム日付の月 ≧ 4月の場合
            // マイル利用明細取得要求電文.照会対象年月が照会対象開始年月((システム日付の年 - 1年) +
            // "03")より古い日付の場合
            objectStartYearMonth.append(Integer.parseInt(DateConvertUtil.getSysYear()) - 1);
            objectStartYearMonth.append(OpalDefaultConstants.MILE_INVALID_FROM_MONTH);
            if (objectStartYearMonth.toString().compareTo(requestData.getAplData().getObjectYearMonth()) > 0) {
                return CheckResultConstants.OBJECT_YEAR_MONTH_ERROR;
            }
        }

        // アプリ会員情報存在チェック
        Boolean aplMemInfoIsExist = checkAplMemInfo(Long.valueOf(requestData.getAplData().getApplicationMemberId()));

        // データが取得されない場合、応答電文を以下の通り設定して、「HTTPアクセスログ出力」を行う。
        if (!aplMemInfoIsExist) {
            return CheckResultConstants.APL_MEM_DATA_ISNULL;
        }

        // マイル履歴情報取得
        SqlResultSet mileHistoryInfo = getMileHistoryInfo(requestData.getAplData());

        // マイル履歴情報が存在しない場合、応答電文を以下の通り設定して、「HTTPアクセスログ出力」を行う。
        if (mileHistoryInfo.isEmpty()) {
            return CheckResultConstants.MILE_HISTORY_INFORMATION_ISNULL;
        }

        for (int i = 0; i < mileHistoryInfo.size(); i++) {
            // マイナス記号編集
            String minus = OpalDefaultConstants.BLANK;
            if (mileHistoryInfo.get(i).getString("MILE_CATEGORY_CODE").substring(0, 1)
                    .equals(OpalDefaultConstants.MILE_SUB)) {
                minus = OpalDefaultConstants.MINUS_MARK_MILE_USE;
            } else if (mileHistoryInfo.get(i).getString("MILE_CATEGORY_CODE").substring(0, 1)
                    .equals(OpalDefaultConstants.MILE_ADD)) {
                minus = OpalDefaultConstants.NULL_STRING;
            }
            // マイル利用明細情報設定
            setResponseParams(mileHistoryInfo.get(i), minus);
        }

        return CheckResultConstants.CHECK_OK;
    }

    /**
     * HTTPレスポンスを設定する。
     *
     * @param requestData
     *            マイル利用明細取得要求電文
     *
     * @param result
     *            チェックの結果
     *
     * @return HTTPレスポンス
     * @throws IOException
     *             異常
     */
    @Override
    protected HttpResponse responseBuilder(A133AAARRequestData requestData, int result) throws IOException {

        // マイル利用明細データ応答電文設定
        A133AAASResponseData responseData = new A133AAASResponseData();
        // 応答電文.処理結果コード
        responseData.setResultCode(getResultCode(result));
        if (result == CheckResultConstants.OBJECT_YEAR_MONTH_ERROR) {
            // 応答電文.照会対象年月が照会対象開始年月より古い日付の場合
            ResponseErrorData error = new ResponseErrorData();
            error.setId("MA133A0101");
            error.setMessage(MessageUtil.createMessage(MessageLevel.ERROR, "MA133A0101").formatMessage());
            responseData.setError(error);
        } else if (result == CheckResultConstants.MILE_HISTORY_INFORMATION_ISNULL) {
            // 応答電文.エラーメッセージ
            ResponseErrorData error = new ResponseErrorData();
            error.setId("MA133A0103");
            error.setMessage(MessageUtil.createMessage(MessageLevel.ERROR, "MA133A0103").formatMessage());
            responseData.setError(error);
        } else if (result == CheckResultConstants.APL_MEM_DATA_ISNULL) {
            // アプリ会員情報データかない
            ResponseErrorData error = new ResponseErrorData();
            error.setId("MA133A0104");
            error.setMessage(MessageUtil.createMessage(MessageLevel.ERROR, "MA133A0104").formatMessage());
            responseData.setError(error);
        } else if (result == CheckResultConstants.CHECK_OK) {
            responseData.setMileUseDetailInfo(responseMileUseDetails);
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
     * マイル履歴情報取得
     *
     * @param form
     *            マイル利用明細取得APIの検索フォーム
     *
     * @return マイル履歴情報
     */
    private SqlResultSet getMileHistoryInfo(A133AAARBodyForm form) {

        // マイル履歴情報取得用のSQL条件を設定する。
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("SELECT_MILE_HISTORY_INFO");
        Map<String, Object> condition = new HashMap<String, Object>();
        // アプリ会員ID
        condition.put("applicationMemberId", form.getApplicationMemberId());
        // 照会対象年月
        condition.put("objectYearMonth", form.getObjectYearMonth());
        // 実行する。
        SqlResultSet result = statement.retrieve(condition);
        return result;
    }

    /**
     * マイル利用明細情報設定
     *
     * @param mileHistoryInfo
     *            マイル履歴情報
     * @param minus
     *            マイナス記号
     */
    private void setResponseParams(SqlRow mileHistoryInfo, String minus) {

        Map<String, Object> mileUseDetail = new LinkedHashMap<String, Object>();

        // マイル履歴登録日
        mileUseDetail.put("mileHistoryRegistDate", mileHistoryInfo.getString("MILE_HISTORY_REGIST_DATE"));
        // マイル種別コード
        mileUseDetail.put("mileCategoryCode", mileHistoryInfo.getString("MILE_CATEGORY_CODE"));
        // マイル種別名称
        mileUseDetail.put("mileCategoryName",
                CodeUtil.getName("C1300001", mileHistoryInfo.getString("MILE_CATEGORY_CODE"), Locale.JAPANESE));
        // マイナス記号
        mileUseDetail.put("minus", minus);
        // マイル数
        mileUseDetail.put("mileAmount", mileHistoryInfo.getString("MILE_AMOUNT"));

        this.responseMileUseDetails.add(mileUseDetail);
    }
}
