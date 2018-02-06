package jp.co.tis.opal.web.ss132A;

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
import nablarch.core.util.DateUtil;
import nablarch.fw.web.HttpResponse;

import jp.co.tis.opal.common.constants.OpalCodeConstants;
import jp.co.tis.opal.web.common.ResponseData.ResponseErrorData;
import jp.co.tis.opal.web.common.constants.CheckResultConstants;
import jp.co.tis.opal.web.common.rest.AbstractRestBaseAction;

/**
 * A132A02:マイル履歴取得APIのアクションクラス。
 *
 * @author 唐
 * @since 1.0
 */
public class A132A021Action extends AbstractRestBaseAction<A132AAARRequestData> {

    /** マイル履歴データ応答電文のマイル履歴情報 */
    private List<Map<String, Object>> responseMileHistoryInfo = new ArrayList<Map<String, Object>>();

    /**
     * マイル履歴取得API
     *
     * @param requestData
     *            マイル履歴取得要求電文
     * @return HTTPレスポンス
     * @throws Exception
     *             異常
     */
    @Valid
    @Consumes(MediaType.APPLICATION_JSON)
    public HttpResponse getMileHistoryInfo(A132AAARRequestData requestData) {
        HttpResponse responseData = super.execute(requestData);
        return responseData;
    }

    /**
     * 処理詳細ロジック
     *
     * @param requestData
     *            マイル履歴取得要求電文
     * @return 処理結果
     */
    @Override
    protected int executeLogic(A132AAARRequestData requestData) {

        // マイル履歴取得要求電文.照会終了年月がマイル履歴取得要求電文.照会開始年月より24ヶ月超える場合
        if (requestData.getAplData().getEndYearMonth()
                .compareTo(DateUtil.addMonth(requestData.getAplData().getStartYearMonth(), 24)) > 0) {
            return CheckResultConstants.END_YEAR_MONTH_ERROR;
        }

        // アプリ会員情報存在チェック
        Boolean aplMemInfoIsExist = checkAplMemInfo(Long.valueOf(requestData.getAplData().getApplicationMemberId()));

        // データが取得されない場合、応答電文を以下の通り設定して、「HTTPアクセスログ出力」を行う。
        if (!aplMemInfoIsExist) {
            return CheckResultConstants.APL_MEM_DATA_ISNULL;
        }

        // マイル集計情報取得
        SqlResultSet mileSummaryInfo = getMileSummaryInfo(requestData.getAplData());

        // マイル集計情報が存在しない場合、応答電文を以下の通り設定して、「HTTPアクセスログ出力」を行う。
        if (mileSummaryInfo.isEmpty()) {
            return CheckResultConstants.MILE_SUMMARY_INFORMATION_ISNULL;
        }
        for (int i = 0; i < mileSummaryInfo.size(); i++) {
            // マイル履歴情報データ設定
            setMileHistoryInfo(requestData.getAplData(), mileSummaryInfo.get(i));
        }
        return CheckResultConstants.CHECK_OK;
    }

    /**
     * HTTPレスポンスを設定する。
     *
     * @param requestData
     *            マイル履歴取得要求電文
     *
     * @param result
     *            チェックの結果
     *
     * @return HTTPレスポンス
     * @throws IOException
     *             異常
     */
    @Override
    protected HttpResponse responseBuilder(A132AAARRequestData requestData, int result) throws IOException {

        // マイル履歴データ応答電文設定
        A132AAASResponseData responseData = new A132AAASResponseData();
        // 応答電文.処理結果コード
        responseData.setResultCode(getResultCode(result));
        // 照会終了年月が照会開始年月より24ヶ月超える場合
        if (result == CheckResultConstants.END_YEAR_MONTH_ERROR) {
            // 応答電文.エラーメッセージ
            ResponseErrorData error = new ResponseErrorData();
            error.setId("MA132A0202");
            error.setMessage(MessageUtil.createMessage(MessageLevel.ERROR, "MA132A0202").formatMessage());
            responseData.setError(error);
        } else if (result == CheckResultConstants.MILE_SUMMARY_INFORMATION_ISNULL) {
            // マイル集計情報が存在しない場合
            ResponseErrorData error = new ResponseErrorData();
            error.setId("MA132A0203");
            error.setMessage(MessageUtil.createMessage(MessageLevel.ERROR, "MA132A0203").formatMessage());
            responseData.setError(error);
        } else if (result == CheckResultConstants.APL_MEM_DATA_ISNULL) {
            // アプリ会員情報データかない
            ResponseErrorData error = new ResponseErrorData();
            error.setId("MA132A0204");
            error.setMessage(MessageUtil.createMessage(MessageLevel.ERROR, "MA132A0204").formatMessage());
            responseData.setError(error);
        } else if (result == CheckResultConstants.CHECK_OK) {
            responseData.setMileHistoryInfo(responseMileHistoryInfo);
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
        // アプリ会員情報存在チェック用のSQL条件を設定する。
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
     * マイル集計情報取得
     *
     * @param form
     *            マイル履歴取得APIの検索フォーム
     *
     * @return マイル集計情報
     */
    private SqlResultSet getMileSummaryInfo(A132AAARBodyForm form) {

        // マイル集計情報取得用のSQL条件を設定する。
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("SELECT_MILE_SUMMARY_INFO");
        Map<String, Object> condition = new HashMap<String, Object>();
        // アプリ会員ID
        condition.put("applicationMemberId", form.getApplicationMemberId());
        // 照会開始年月
        condition.put("startYearMonth", form.getStartYearMonth());
        // 照会終了年月
        condition.put("endYearMonth", form.getEndYearMonth());

        // 実行する。
        SqlResultSet result = statement.retrieve(condition);
        return result;
    }

    /**
     * マイル履歴情報データ設定
     *
     * @param form
     *            マイル履歴取得APIの検索フォーム
     * @param mileSummaryInfo
     *            マイル集計情報
     */
    private void setMileHistoryInfo(A132AAARBodyForm form, SqlRow mileSummaryInfo) {

        Map<String, Object> mileHistoryInfo = new LinkedHashMap<String, Object>();

        // 年月
        mileHistoryInfo.put("yearMonth", mileSummaryInfo.getString("MILE_SUM_YEAR_MONTH"));
        // 獲得マイル数
        mileHistoryInfo.put("acquireMile", mileSummaryInfo.getString("ACQUIRE_MILE_TOTAL"));
        // 使用マイル数
        mileHistoryInfo.put("useMile", mileSummaryInfo.getString("USE_MILE_TOTAL"));

        // マイル種別集計情報取得
        SqlResultSet mileCategorySummaryInfo = getMileCategorySummaryInfo(form, mileSummaryInfo);

        // マイル種別集計情報
        List<Object> mileCategorySumList = new ArrayList<Object>();
        for (int j = 0; j < mileCategorySummaryInfo.size(); j++) {
            Map<String, Object> mileCategorySumInfo = new LinkedHashMap<String, Object>();
            // マイル種別コード
            mileCategorySumInfo.put("mileCategoryCode", mileCategorySummaryInfo.get(j).getString("MILE_CATEGORY_CODE"));
            // マイル種別名称
            mileCategorySumInfo.put("mileCategoryName", CodeUtil.getName("C1300001",
                    mileCategorySummaryInfo.get(j).getString("MILE_CATEGORY_CODE"), Locale.JAPANESE));
            // マイル集計数
            mileCategorySumInfo.put("mileSummaryAmount", mileCategorySummaryInfo.get(j).getString("MILE_TOTAL"));

            mileCategorySumList.add(mileCategorySumInfo);
        }
        if (!mileCategorySumList.isEmpty()) {

            mileHistoryInfo.put("mileCategorySumInfo", mileCategorySumList);
        }
        this.responseMileHistoryInfo.add(mileHistoryInfo);
    }

    /**
     * マイル種別集計情報取得
     *
     * @param form
     *            マイル履歴取得APIの検索フォーム
     * @param mileSummaryInfo
     *            マイル集計情報
     *
     * @return マイル種別集計情報
     */
    private SqlResultSet getMileCategorySummaryInfo(A132AAARBodyForm form, SqlRow mileSummaryInfo) {

        // マイル種別名称取得用のSQL条件を設定する。
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("SELECT_MILE_CATEGORY_SUMMARY_INFO");
        Map<String, Object> condition = new HashMap<String, Object>();
        // アプリ会員ID
        condition.put("applicationMemberId", form.getApplicationMemberId());
        // マイル集計年月
        condition.put("objectYearMonth", mileSummaryInfo.getString("MILE_SUM_YEAR_MONTH"));

        // 実行する。
        SqlResultSet result = statement.retrieve(condition);

        return result;
    }
}
