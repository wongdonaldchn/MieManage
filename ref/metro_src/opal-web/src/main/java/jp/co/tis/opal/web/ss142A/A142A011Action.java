package jp.co.tis.opal.web.ss142A;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import nablarch.common.dao.UniversalDao;
import nablarch.core.date.SystemTimeUtil;
import nablarch.core.db.statement.ParameterizedSqlPStatement;
import nablarch.core.db.statement.SqlResultSet;
import nablarch.core.db.statement.SqlRow;
import nablarch.core.message.MessageLevel;
import nablarch.core.message.MessageUtil;
import nablarch.core.repository.SystemRepository;
import nablarch.core.util.StringUtil;
import nablarch.fw.web.HttpResponse;

import jp.co.tis.opal.common.component.CM010004Component;
import jp.co.tis.opal.common.component.CM010005Component;
import jp.co.tis.opal.common.constants.OpalCodeConstants;
import jp.co.tis.opal.common.constants.OpalDefaultConstants;
import jp.co.tis.opal.common.entity.MileBalanceInfoEntity;
import jp.co.tis.opal.common.utility.DateConvertUtil;
import jp.co.tis.opal.common.utility.IdGeneratorUtil;
import jp.co.tis.opal.web.common.ResponseData.ResponseErrorData;
import jp.co.tis.opal.web.common.constants.CheckResultConstants;
import jp.co.tis.opal.web.common.rest.AbstractRestBaseAction;

/**
 * A142A01:郵送情報登録APIのアクションクラス。
 *
 * @author 陳
 * @since 1.0
 */
public class A142A011Action extends AbstractRestBaseAction<A142AAARRequestData> {

    /** API */
    private static final String API_SERVER_ID = "A142A011";

    /** 郵送情報管理データ保持期間(月) */
    private static final String POST_INFO_PERIOD = "post_info_ctrl_data_retention_period";

    /**
     * 郵送情報登録API
     *
     * @param requestData
     *            郵送情報登録要求電文
     * @return HTTPレスポンス
     */
    @Consumes(MediaType.APPLICATION_JSON)
    @Valid
    public HttpResponse insertPostInfo(A142AAARRequestData requestData) {

        HttpResponse responseData = super.execute(requestData);
        return responseData;

    }

    /**
     * 処理詳細ロジック
     *
     * @param requestData
     *            郵送情報登録要求電文
     * @return チェックの結果
     */
    @Override
    protected int executeLogic(A142AAARRequestData requestData) {

        // 郵送情報登録要求電文.アプリ会員IDを取得
        String applicationMemberId = requestData.getPostInfoData().getApplicationMemberId();

        // 該当アプリ会員情報を取得する。
        SqlResultSet aplMemInfo = getAplMemInfo(applicationMemberId);

        // データが取得されない場合、応答電文を以下の通り設定して、「HTTPアクセスログ出力」を行う。
        if (aplMemInfo.isEmpty()) {
            return CheckResultConstants.APL_MEM_DATA_ISNULL;
        }

        // 郵送受付IDを採番する。(採番対象ID：1400)
        Long postReceiptId = IdGeneratorUtil.generatePostReceiptId();
        // 論理削除日の算出
        int monthSpan = Integer.valueOf(SystemRepository.get(POST_INFO_PERIOD));

        CM010004Component cm010004Component = new CM010004Component();
        String deletedDate = cm010004Component.getDeletedDateMonthly(monthSpan);

        // 郵送情報を登録する。
        insertPostInformation(postReceiptId, deletedDate, requestData, aplMemInfo.get(0).getString("APPLICATION_ID"));

        // 郵送情報登録要求電文.マイル種別コードが設定される場合、下記の処理を行う。
        if (!StringUtil.isNullOrEmpty(requestData.getPostInfoData().getMileCategoryCode())) {

            // 排他制御対象のレコードのロックを取得する。(悲観的ロック)
            Map<String, Object> mileBalanceInfoExclusive = new HashMap<String, Object>();
            // 郵送情報登録要求電文.アプリ会員ID
            mileBalanceInfoExclusive.put("applicationMemberId", applicationMemberId);
            UniversalDao.findAllBySqlFile(MileBalanceInfoEntity.class, "SELECT_MILE_BALANCE_INFO",
                    mileBalanceInfoExclusive);

            // マイル残高のチェック
            Boolean mileBlanceIsEnough = checkMileBalance(Long.valueOf(applicationMemberId),
                    Long.valueOf(requestData.getPostInfoData().getSubMileAmount()));
            if (!mileBlanceIsEnough) {
                return CheckResultConstants.MAIL_BALANCE_NOT_ENOUGH;
            }

            // マイル減算処理
            CM010005Component cm010005Component = new CM010005Component();
            cm010005Component.subMile(Long.valueOf(applicationMemberId),
                    requestData.getPostInfoData().getMileAddSubRcptNo(),
                    requestData.getPostInfoData().getMileCategoryCode(),
                    Long.valueOf(requestData.getPostInfoData().getSubMileAmount()), API_SERVER_ID);
        }

        return CheckResultConstants.CHECK_OK;
    }

    /**
     * HTTPレスポンスを設定する。
     *
     * @param requestData
     *            郵送情報登録要求電文
     *
     * @param result
     *            チェックの結果
     *
     * @return HTTPレスポンス
     * @throws IOException
     *             異常
     */
    @Override
    protected HttpResponse responseBuilder(A142AAARRequestData requestData, int result) throws IOException {

        // 郵送情報登録結果応答電文設定
        A142AAASResponseData responseData = new A142AAASResponseData();

        // 応答電文.処理結果コード
        responseData.setResultCode(getResultCode(result));
        if (result == CheckResultConstants.APL_MEM_DATA_ISNULL) {
            // 応答電文.エラーメッセージ
            ResponseErrorData error = new ResponseErrorData();
            error.setId("MA142A0102");
            error.setMessage(MessageUtil.createMessage(MessageLevel.ERROR, "MA142A0102").formatMessage());
            responseData.setError(error);
        } else if (result == CheckResultConstants.MAIL_BALANCE_NOT_ENOUGH) {
            // 応答電文.エラーメッセージ
            ResponseErrorData error = new ResponseErrorData();
            error.setId("MA142A0103");
            error.setMessage(MessageUtil.createMessage(MessageLevel.ERROR, "MA142A0103").formatMessage());
            responseData.setError(error);
        }

        HttpResponse response = super.setHttpResponseData(responseData, result);

        return response;

    }

    /**
     * 郵送情報登録
     *
     * @param postReceiptId
     *            採番された郵送受付ID
     * @param deletedDate
     *            論理削除日
     * @param requestData
     *            郵送情報登録要求電文
     * @param applicationId
     *            アプリID
     */
    private void insertPostInformation(Long postReceiptId, String deletedDate, A142AAARRequestData requestData,
            String applicationId) {

        // 郵送情報登録用のSQL条件を設定する。
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("INSERT_POST_INFORMATION");

        Map<String, Object> condition = new HashMap<String, Object>();
        // 郵送受付ID
        condition.put("postReceiptId", postReceiptId);
        // アプリ会員ID
        condition.put("applicationMemberId", requestData.getPostInfoData().getApplicationMemberId());
        // アプリID
        condition.put("applicationId", applicationId);
        // 郵送受付登録日時
        condition.put("postReceiptRegistDateTime", SystemTimeUtil.getTimestamp());
        // 郵送種別
        condition.put("postCategory", requestData.getPostInfoData().getPostCategory());
        // 郵送管理番号
        condition.put("postControlNumber", requestData.getPostInfoData().getPostControlNumber());
        // 郵便番号
        condition.put("postcode", requestData.getPostInfoData().getPostcode());
        // 住所
        condition.put("address", requestData.getPostInfoData().getAddress());
        // 氏名
        condition.put("name", requestData.getPostInfoData().getName());
        // 電話番号
        condition.put("telephoneNumber", requestData.getPostInfoData().getTelephoneNumber());
        // その他
        condition.put("other", requestData.getPostInfoData().getOther());
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

    /**
     * マイル残高のチェック。
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

    /**
     * アプリ会員情報データ取得
     *
     * @param applicationMemberId
     *            アプリ会員ID
     *
     * @return アプリ会員情報
     */
    private SqlResultSet getAplMemInfo(String applicationMemberId) {

        // アプリ会員情報取得用のSQL条件を設定する。
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("SELECT_APL_MEM_INFO");

        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("applicationMemberId", applicationMemberId);
        condition.put("opAuthAplMem", OpalCodeConstants.AplMemStatusCode.OP_AUTH_APL_MEM);
        condition.put("notOpMem", OpalCodeConstants.AplMemStatusCode.NOT_OP_MEM);

        // 実行する。
        SqlResultSet result = statement.retrieve(condition);
        return result;
    }

}
