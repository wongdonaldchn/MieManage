package jp.co.tis.opal.web.ss131A;

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
import jp.co.tis.opal.common.utility.DateConvertUtil;
import jp.co.tis.opal.web.common.ResponseData.ResponseErrorData;
import jp.co.tis.opal.web.common.constants.CheckResultConstants;
import jp.co.tis.opal.web.common.rest.AbstractRestBaseAction;

/**
 * {@link A131A011Action} マイル残高取得APIのアクションクラス。
 *
 * @author 張
 * @since 1.0
 */
public class A131A011Action extends AbstractRestBaseAction<A131AAARRequestData> {

    /** マイル残高情報データマップ */
    private Map<String, Object> mileBalanceInfo = new LinkedHashMap<String, Object>();
    /** 当年度日付情報 */
    private Map<String, String> thisYearMonth = new LinkedHashMap<String, String>();
    /** 来年度日付情報 */
    private Map<String, String> nextYearMonth = new LinkedHashMap<String, String>();
    /** 使用可能マイル数（当年度まで） */
    private Long thisYearUsableMile = 0L;
    /** 使用可能マイル数（来年度まで） */
    private Long nextYearUsableMile = 0L;
    /** 使用可能マイル数（合計） */
    private Long usableTotalMile = 0L;
    /** マイル有効期限日付（当年度） */
    private String thisYearMileExpiDate;
    /** マイル有効期限日付（来年度） */
    private String nextYearMileExpiDate;

    /**
     * マイル残高取得処理。
     * <p>
     * 応答にJSONを使用する。
     * </p>
     *
     * @param requestData
     *            HTTPリクエスト
     * @return HTTPレスポンス
     */
    @Consumes(MediaType.APPLICATION_JSON)
    @Valid
    public HttpResponse getMileBalanceInfo(A131AAARRequestData requestData) {
        HttpResponse responseData = super.execute(requestData);
        return responseData;
    }

    /**
     * 処理詳細
     *
     * @param requestData
     *            マイル残高取得要求電文
     * @return 実行結果
     */
    @Override
    protected int executeLogic(A131AAARRequestData requestData) {

        // アプリ会員ID
        Long applicationMemberId = Long.valueOf(requestData.getAplData().getApplicationMemberId());
        // アプリ会員情報データ存在チェック
        if (!this.checkAplMemInfoIsExist(applicationMemberId)) {
            // データが取得されない場合、無効なアプリ会員エラーとして、応答電文を以下の通り設定して、
            return CheckResultConstants.APL_MEM_DATA_ISNULL;
        }

        // 当年度日付情報設定
        this.setThisYearMonth();
        // 来年度日付情報設定
        this.setNextYearMonth();

        // 使用可能マイル数（当年度3月末日までの有効）取得用のSQLの条件を設定する
        Map<String, Object> condition = new HashMap<String, Object>();
        // 条件:アプリ会員ID
        condition.put("applicationMemberId", applicationMemberId);
        // 条件:当年度開始年月
        condition.put("beginYearMonth", (this.thisYearMonth.get("beginYear") + this.thisYearMonth.get("beginMonth")));
        // 条件:当年度終了年月
        condition.put("endYearMonth", (this.thisYearMonth.get("endYear") + this.thisYearMonth.get("endMonth")));
        // マイル残高情報取得
        ParameterizedSqlPStatement selectThisYearMileBalanceStatement = getParameterizedSqlStatement(
                "SELECT_THIS_YEAR_MILE_BALANCE");
        // 使用可能マイル数（当年度まで）
        this.thisYearUsableMile = selectThisYearMileBalanceStatement.retrieve(condition).get(0)
                .getLong("SUM_MILE_BALANCE");

        condition.clear();
        // 使用可能マイル数（来年度3月末日までの有効）取得用のSQLの条件を設定する
        condition = new HashMap<String, Object>();
        // 条件:アプリ会員ID
        condition.put("applicationMemberId", applicationMemberId);
        // 条件:来年度開始年月
        condition.put("beginYearMonth", (this.nextYearMonth.get("beginYear") + this.nextYearMonth.get("beginMonth")));
        // 条件:来年度終了年月
        condition.put("endYearMonth", (this.nextYearMonth.get("endYear") + this.nextYearMonth.get("endMonth")));
        // マイル残高情報取得
        ParameterizedSqlPStatement selectNextYearMileBalanceStatement = getParameterizedSqlStatement(
                "SELECT_NEXT_YEAR_MILE_BALANCE");
        // 使用可能マイル数（来年度まで）
        this.nextYearUsableMile = selectNextYearMileBalanceStatement.retrieve(condition).get(0)
                .getLong("SUM_MILE_BALANCE");

        // マイル有効期限日付（当年度）（当年度終了年 + "0331"）
        this.thisYearMileExpiDate = this.thisYearMonth.get("endYear") + "0331";
        // マイル有効期限日付（来年度）（来年度終了年 + "0331"）
        this.nextYearMileExpiDate = this.nextYearMonth.get("endYear") + "0331";
        // 使用可能マイル数（合計）の算出（当年度使用可能マイル残高 ＋ 来年度使用可能マイル残高）
        this.usableTotalMile = this.thisYearUsableMile + this.nextYearUsableMile;

        // マイル残高情報データを設定する。
        this.setMileBalanceInfo();

        return CheckResultConstants.CHECK_OK;
    }

    /**
     * アプリ会員情報データ存在チェック処理
     *
     * @param applicationMemberId
     *            アプリ会員Id
     *
     * @return 処理結果（存在：true 存在しない：false）
     */
    private Boolean checkAplMemInfoIsExist(Long applicationMemberId) {
        Boolean isExist = true;

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
            isExist = false;
        }
        return isExist;
    }

    /**
     * 当年度日付情報を設定する。
     */
    private void setThisYearMonth() {

        // 当年度開始年
        int beginYear = 0;
        // 当年度終了年
        int endYear = 0;
        // システム日付の月
        int sysMonth = Integer.valueOf(DateConvertUtil.getSysMonth());
        // システム日付の月 < 4月の場合
        if (sysMonth < 4) {
            // 当年度開始年設定（システム日付の年 - 1年）
            beginYear = Integer.valueOf(DateConvertUtil.getSysYear()) - 1;
            // 当年度終了年設定（システム日付の年）
            endYear = Integer.valueOf(DateConvertUtil.getSysYear());
        } else {
            // 当年度開始年設定（システム日付の年）
            beginYear = Integer.valueOf(DateConvertUtil.getSysYear());
            // 当年度終了年設定（システム日付の年 + 1年）
            endYear = Integer.valueOf(DateConvertUtil.getSysYear()) + 1;
        }

        // 当年度開始年
        this.thisYearMonth.put("beginYear", String.valueOf(beginYear));
        // 当年度開始月
        this.thisYearMonth.put("beginMonth", "03");
        // 当年度終了年
        this.thisYearMonth.put("endYear", String.valueOf(endYear));
        // 当年度終了月
        this.thisYearMonth.put("endMonth", "02");
    }

    /**
     * 来年度日付情報を設定する。
     */
    private void setNextYearMonth() {

        // 来年度開始年
        int beginYear = 0;
        // 来年度終了年
        int endYear = 0;
        // システム日付の月
        int sysMonth = Integer.valueOf(DateConvertUtil.getSysMonth());
        // システム日付の月 < 4月の場合
        if (sysMonth < 4) {
            // 来年度開始年設定（システム日付の年）
            beginYear = Integer.valueOf(DateConvertUtil.getSysYear());
            // 来年度終了年設定（システム日付の年 + 1年）
            endYear = Integer.valueOf(DateConvertUtil.getSysYear()) + 1;
        } else {
            // 来年度開始年設定（システム日付の年 + 1年）
            beginYear = Integer.valueOf(DateConvertUtil.getSysYear()) + 1;
            // 来年度終了年設定（システム日付の年 + 2年）
            endYear = Integer.valueOf(DateConvertUtil.getSysYear()) + 2;
        }

        // 来年度開始年
        this.nextYearMonth.put("beginYear", String.valueOf(beginYear));
        // 来年度開始月
        this.nextYearMonth.put("beginMonth", "03");
        // 来年度終了年
        this.nextYearMonth.put("endYear", String.valueOf(endYear));
        // 来年度終了月
        this.nextYearMonth.put("endMonth", "02");
    }

    /**
     * マイル残高情報データを設定する。
     */
    private void setMileBalanceInfo() {
        // 使用可能マイル数（合計）
        this.mileBalanceInfo.put("usableTotalMile", String.valueOf(this.usableTotalMile));
        // マイル有効期限日付（当年度）
        this.mileBalanceInfo.put("thisYearMileExpiDate", this.thisYearMileExpiDate);
        // 使用可能マイル数（当年度まで）
        this.mileBalanceInfo.put("thisYearUsableMile", String.valueOf(this.thisYearUsableMile));
        // マイル有効期限日付（来年度）
        this.mileBalanceInfo.put("nextYearMileExpiDate", this.nextYearMileExpiDate);
        // 使用可能マイル数（来年度まで）
        this.mileBalanceInfo.put("nextYearUsableMile", String.valueOf(this.nextYearUsableMile));
    }

    /**
     * マイル残高データ応答電文を設定する。
     *
     * @param requestData
     *            マイル残高取得要求電文
     *
     * @param result
     *            マイル残高取得要求電文チェック結果
     *
     * @return マイル残高データ応答電文
     * @throws IOException
     *             異常
     */
    @Override
    protected HttpResponse responseBuilder(A131AAARRequestData requestData, int result) throws IOException {
        // 応答電文設定
        A131AAASResponseData responseData = new A131AAASResponseData();
        // 応答電文.処理結果コード
        responseData.setResultCode(super.getResultCode(result));
        if (result == CheckResultConstants.APL_MEM_DATA_ISNULL) {
            // アプリ会員情報データかない
            ResponseErrorData error = new ResponseErrorData();
            error.setId("MA131A0101");
            error.setMessage(MessageUtil.createMessage(MessageLevel.ERROR, "MA131A0101").formatMessage());
            responseData.setError(error);
        } else {
            // マイル残高データ応答電文設定
            responseData.setMileBalanceInfo(this.mileBalanceInfo);
        }

        HttpResponse response = super.setHttpResponseData(responseData, result);

        return response;
    }
}
