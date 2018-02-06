package jp.co.tis.opal.web.ss134A;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import nablarch.common.dao.UniversalDao;
import nablarch.core.date.SystemTimeUtil;
import nablarch.core.db.statement.ParameterizedSqlPStatement;
import nablarch.core.db.statement.SqlRow;
import nablarch.core.message.MessageLevel;
import nablarch.core.message.MessageUtil;
import nablarch.fw.web.HttpResponse;

import jp.co.tis.opal.common.component.CM010005Component;
import jp.co.tis.opal.common.constants.OpalCodeConstants;
import jp.co.tis.opal.common.constants.OpalDefaultConstants;
import jp.co.tis.opal.common.entity.MileBalanceInfoEntity;
import jp.co.tis.opal.common.utility.DateConvertUtil;
import jp.co.tis.opal.web.common.ResponseData.ResponseErrorData;
import jp.co.tis.opal.web.common.constants.CheckResultConstants;
import jp.co.tis.opal.web.common.rest.AbstractRestBaseAction;

/**
 * A134A02:マイル減算APIのアクションクラス。
 *
 * @author 曹
 * @since 1.0
 */
public class A134A021Action extends AbstractRestBaseAction<A134AABRRequestData> {

    /** API処理ID */
    private static final String API_PROCESS_ID = "A134A021";

    /**
     * マイル減算API
     *
     * @param requestData
     *            マイル減算要求電文
     * @return マイル減算結果応答電文
     */
    @Consumes(MediaType.APPLICATION_JSON)
    @Valid
    public HttpResponse subMile(A134AABRRequestData requestData) {

        HttpResponse responseData = super.execute(requestData);
        return responseData;

    }

    /**
     * 処理詳細
     *
     * @param requestData
     *            マイル減算要求電文
     * @return チェック結果
     */
    @Override
    protected int executeLogic(A134AABRRequestData requestData) {

        // アプリ会員存在チェック
        Boolean aplMemDataIsNull = chekAplMemDataIsNull(
                Long.valueOf(requestData.getAplData().getApplicationMemberId()));
        if (aplMemDataIsNull) {
            // アプリ会員情報が存在しないと判断
            return CheckResultConstants.APL_MEM_DATA_ISNULL;
        }

        // マイル残高情報排他制御（アプリ会員単位）
        Map<String, Object> mileBalanceInfoExclusive = new HashMap<String, Object>();
        mileBalanceInfoExclusive.put("applicationMemberId", requestData.getAplData().getApplicationMemberId());
        UniversalDao.findAllBySqlFile(MileBalanceInfoEntity.class, "SELECT_MILE_BALANCE_INFO",
                mileBalanceInfoExclusive);

        // マイル残高のチェック
        Boolean mileBlanceIsEnough = checkMileBalance(Long.valueOf(requestData.getAplData().getApplicationMemberId()),
                Long.valueOf(requestData.getAplData().getSubMileAmount()));
        if (!mileBlanceIsEnough) {
            return CheckResultConstants.MAIL_BALANCE_NOT_ENOUGH;
        }

        // マイル減算処理
        CM010005Component cm010005Component = new CM010005Component();
        cm010005Component.subMile(Long.valueOf(requestData.getAplData().getApplicationMemberId()),
                requestData.getAplData().getMileAddSubRcptNo(), requestData.getAplData().getMileCategoryCode(),
                Long.valueOf(requestData.getAplData().getSubMileAmount()), API_PROCESS_ID);

        return CheckResultConstants.CHECK_OK;
    }

    /**
     * マイル減算結果応答電文を設定する。
     *
     * @param requestData
     *            マイル減算要求電文
     *
     * @param result
     *            マイル減算要求電文チェック結果
     *
     * @return マイル減算結果応答電文
     * @throws IOException
     *             異常
     */
    @Override
    protected HttpResponse responseBuilder(A134AABRRequestData requestData, int result) throws IOException {

        // マイル減算結果応答電文設定
        A134AABSResponseData responseData = new A134AABSResponseData();
        // 応答電文.処理結果コード
        responseData.setResultCode(getResultCode(result));
        if (result == CheckResultConstants.APL_MEM_DATA_ISNULL) {
            // 応答電文.エラーメッセージ
            ResponseErrorData error = new ResponseErrorData();
            error.setId("MA134A0201");
            error.setMessage(MessageUtil.createMessage(MessageLevel.ERROR, "MA134A0201").formatMessage());
            responseData.setError(error);
        } else if (result == CheckResultConstants.MAIL_BALANCE_NOT_ENOUGH) {
            // 応答電文.エラーメッセージ
            ResponseErrorData error = new ResponseErrorData();
            error.setId("MA134A0202");
            error.setMessage(MessageUtil.createMessage(MessageLevel.ERROR, "MA134A0202").formatMessage());
            responseData.setError(error);
        }

        HttpResponse response = super.setHttpResponseData(responseData, result);

        return response;
    }

    /**
     * アプリ会員存在チェック。
     *
     * @param applicationMemberId
     *            アプリ会員ID
     *
     * @return aplMemInfoIsNull アプリ会員存在チェック結果
     */
    private Boolean chekAplMemDataIsNull(Long applicationMemberId) {
        Boolean aplMemDataIsNull = false;
        // アプリ会員情報取得用のSQLの条件を設定する
        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("applicationMemberId", applicationMemberId);
        condition.put("statusCodeA", OpalCodeConstants.AplMemStatusCode.OP_AUTH_APL_MEM);
        condition.put("statusCodeD", OpalCodeConstants.AplMemStatusCode.NOT_OP_MEM);

        // アプリ会員情報取得
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("SELECT_APL_MEM_INFO");
        if (statement.retrieve(condition).isEmpty()) {
            // 取得できない場合、チェック結果を「true」に設定
            aplMemDataIsNull = true;
        }

        return aplMemDataIsNull;
    }

    /**
     * {@inneritDoc} マイル残高のチェック。
     * <p/>
     *
     * @param applicationMemberId
     *            アプリ会員ID
     * @param mileBalance
     *            マイル残高
     * @return mileBlanceIsEnough マイル残高チェック結果
     */
    private Boolean checkMileBalance(Long applicationMemberId, Long mileBalance) {
        Boolean mileBlanceIsEnough = true;
        StringBuilder stringForFromMonth = new StringBuilder();
        // システム日付の月日を取得
        String sysDate = SystemTimeUtil.getDateString().substring(4, 8);
        if (sysDate.compareTo(OpalDefaultConstants.MILE_INVALID_DATE) >= 0) {
            // システム日付が4/1以降の場合、開始年月＝今年の3月
            stringForFromMonth.append(DateConvertUtil.getSysYear());
            stringForFromMonth.append(OpalDefaultConstants.MILE_INVALID_FROM_MONTH);

        } else {
            // システム日付が3/31以前の場合、開始年月＝昨年の3月
            stringForFromMonth.append(Integer.parseInt(DateConvertUtil.getSysYear()) - 1);
            stringForFromMonth.append(OpalDefaultConstants.MILE_INVALID_FROM_MONTH);
        }
        // 開始年月を取得
        String objectYearMonth = stringForFromMonth.toString();

        // マイル残高合計取得用のSQLの条件を設定する
        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("applicationMemberId", applicationMemberId);
        condition.put("objectYearMonth", objectYearMonth);
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("SELECT_MILE_BALANCE_SUM");
        SqlRow mileBalanceResult = statement.retrieve(condition).get(0);
        Long mileBalanceSum = mileBalanceResult.getLong("SUM_MILE_BALANCE");

        // マイル残高合計がnullの場合、「0」に設定する。
        if (mileBalanceSum == null) {
            mileBalanceSum = OpalDefaultConstants.MILE_BALANCE_ZERO;
        }
        // マイル残高のチェック
        if (mileBalanceSum.longValue() < mileBalance.longValue()) {
            mileBlanceIsEnough = false;
        }
        return mileBlanceIsEnough;
    }

}
