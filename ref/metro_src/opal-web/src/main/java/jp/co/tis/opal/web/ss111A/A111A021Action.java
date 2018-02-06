package jp.co.tis.opal.web.ss111A;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import nablarch.core.db.statement.ParameterizedSqlPStatement;
import nablarch.core.db.statement.SqlResultSet;
import nablarch.core.db.statement.SqlRow;
import nablarch.core.message.MessageLevel;
import nablarch.core.message.MessageUtil;
import nablarch.fw.web.HttpResponse;

import jp.co.tis.opal.web.common.ResponseData.ResponseErrorData;
import jp.co.tis.opal.web.common.constants.CheckResultConstants;
import jp.co.tis.opal.web.common.rest.AbstractRestBaseAction;

/**
 * A111A02:OP会員情報取得APIのアクションクラス。
 *
 * @author 陳
 * @since 1.0
 */
public class A111A021Action extends AbstractRestBaseAction<A111AABRRequestData> {

    /** レスポンスのOP会員情報データ */
    private Map<String, Object> responseOpMemInfo = new LinkedHashMap<String, Object>();

    /**
     * OP会員情報取得API
     *
     * @param requestData
     *            OP会員情報取得要求電文
     * @return HTTPレスポンス
     * @throws Exception
     *             異常
     */
    @Consumes(MediaType.APPLICATION_JSON)
    @Valid
    public HttpResponse getOpMemInfo(A111AABRRequestData requestData) {

        HttpResponse responseData = super.execute(requestData);
        return responseData;

    }

    /**
     * 処理詳細ロジック
     *
     * @param requestData
     *            OP会員情報取得要求電文
     * @return チェックの結果
     */
    @Override
    protected int executeLogic(A111AABRRequestData requestData) {

        // OP会員情報取得要求電文からOP番号を取得する
        String osakaPitapaNumber = requestData.getAplData().getOsakaPitapaNumber();

        // OP会員情報データ取得
        SqlResultSet rs = getOpMemInfo(osakaPitapaNumber);

        // OP会員情報が存在しない場合、応答電文を以下の通り設定して、「HTTPアクセスログ出力」を行う。
        if (rs.isEmpty()) {
            return CheckResultConstants.OP_DATA_ISNULL;
        }

        // 取得したOP会員情報を応答電文のOP会員情報データに設定する。
        setResponseParams(rs.get(0));

        return CheckResultConstants.CHECK_OK;
    }

    /**
     * HTTPレスポンスを設定する。
     *
     * @param requestData
     *            OP会員情報取得要求電文
     *
     * @param result
     *            チェックの結果
     *
     * @return HTTPレスポンス
     * @throws IOException
     *             IO異常
     */
    @Override
    protected HttpResponse responseBuilder(A111AABRRequestData requestData, int result) throws IOException {

        // OP会員情報データ応答電文設定
        A111AABSResponseData responseData = new A111AABSResponseData();

        // 応答電文.処理結果コード
        responseData.setResultCode(getResultCode(result));
        if (result == CheckResultConstants.OP_DATA_ISNULL) {
            // 応答電文.エラーメッセージ
            ResponseErrorData error = new ResponseErrorData();
            error.setId("MA111A0201");
            error.setMessage(MessageUtil.createMessage(MessageLevel.ERROR, "MA111A0201").formatMessage());
            responseData.setError(error);
        } else if (result == CheckResultConstants.CHECK_OK) {
            responseData.setOpMemInfo(responseOpMemInfo);
        }

        HttpResponse response = super.setHttpResponseData(responseData, result);

        return response;

    }

    /**
     * OP会員情報データ取得
     *
     * @param osakaPitapaNumber
     *            OP番号
     *
     * @return OP会員情報
     */
    private SqlResultSet getOpMemInfo(String osakaPitapaNumber) {
        // OP会員情報取得用のSQL条件を設定する。
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("SELECT_OP_MEM_INFO");

        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("osakaPitapaNumber", osakaPitapaNumber);

        // 実行する。
        SqlResultSet result = statement.retrieve(condition);
        return result;
    }

    /**
     * 取得したOP会員情報を応答電文のOP会員情報データに設定する
     *
     * @param row
     *            OP会員情報
     */
    private void setResponseParams(SqlRow row) {
        // OP番号
        responseOpMemInfo.put("osakaPitapaNumber", row.getString("OSAKA_PITAPA_NUMBER"));
        // 会員管理番号
        responseOpMemInfo.put("memberControlNumber", row.getString("MEMBER_CONTROL_NUMBER"));
        // 会員管理番号枝番
        responseOpMemInfo.put("memCtrlNumBrNum", row.getString("MEM_CTRL_NUM_BR_NUM"));
        // PiTaPa有効期限
        responseOpMemInfo.put("pitapaExpirationDate", row.getString("PITAPA_EXPIRATION_DATE"));
        // カード種類
        responseOpMemInfo.put("cardType", row.getString("CARD_TYPE"));
        // 生年月日
        responseOpMemInfo.put("birthdate", row.getString("BIRTHDATE"));
        // 性別コード
        responseOpMemInfo.put("sexCode", row.getString("SEX_CODE"));
        // 自宅電話番号
        responseOpMemInfo.put("telephoneNumber", row.getString("TELEPHONE_NUMBER"));
        // 携帯電話番号
        responseOpMemInfo.put("cellphoneNumber", row.getString("CELLPHONE_NUMBER"));
        // 郵便番号
        responseOpMemInfo.put("postcode", row.getString("POSTCODE"));
        // サービス種別
        responseOpMemInfo.put("serviceCategory", row.getString("SERVICE_CATEGORY"));
        // 登録駅1
        responseOpMemInfo.put("registStation1", row.getString("REGIST_STATION_1"));
        // 登録駅2
        responseOpMemInfo.put("registStation2", row.getString("REGIST_STATION_2"));
        // 続柄コード
        responseOpMemInfo.put("relationshipCode", row.getString("RELATIONSHIP_CODE"));
        // 今回登録駅1
        responseOpMemInfo.put("thisTimeRegistStation1", row.getString("THIS_TIME_REGIST_STATION_1"));
        // 今回登録駅2
        responseOpMemInfo.put("thisTimeRegistStation2", row.getString("THIS_TIME_REGIST_STATION_2"));
        // 今回登録駅3
        responseOpMemInfo.put("thisTimeRegistStation3", row.getString("THIS_TIME_REGIST_STATION_3"));
        // 今回登録駅4
        responseOpMemInfo.put("thisTimeRegistStation4", row.getString("THIS_TIME_REGIST_STATION_4"));
        // 今回登録駅5
        responseOpMemInfo.put("thisTimeRegistStation5", row.getString("THIS_TIME_REGIST_STATION_5"));
    }

}
