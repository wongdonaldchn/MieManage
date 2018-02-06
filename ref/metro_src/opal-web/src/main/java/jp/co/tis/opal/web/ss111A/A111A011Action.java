package jp.co.tis.opal.web.ss111A;

import java.io.IOException;
import java.util.HashMap;
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
 * A111A01:OP会員本人確認APIのアクションクラス。
 *
 * @author 陳
 * @since 1.0
 */
public class A111A011Action extends AbstractRestBaseAction<A111AAARRequestData> {

    /**
     * OP会員本人確認API
     *
     * @param requestData
     *            OP会員本人確認要求電文
     * @return HTTPレスポンス
     * @throws Exception
     *             異常
     */
    @Consumes(MediaType.APPLICATION_JSON)
    @Valid
    public HttpResponse confirmOpMemInfo(A111AAARRequestData requestData) {

        HttpResponse responseData = super.execute(requestData);
        return responseData;

    }

    /**
     * 処理詳細ロジック
     *
     * @param requestData
     *            OP会員本人確認要求電文
     * @return チェックの結果
     */
    @Override
    protected int executeLogic(A111AAARRequestData requestData) {

        // OP会員情報データ取得
        SqlResultSet rs = getOpMemInfo(requestData.getAplData());

        // OP会員情報が存在しない場合、応答電文を以下の通り設定して、「HTTPアクセスログ出力」を行う。
        if (rs.isEmpty()) {
            return CheckResultConstants.OP_DATA_ISNULL;
        }
        // 続柄コードチェック
        // OP会員情報TBL．続柄コードが"2"(ジュニア)、または"3"(キッズ)の場合、
        // 応答電文を以下の通り設定して、「HTTPアクセスログ出力」を行う。
        if (OpalCodeConstants.RelationshipCode.RELATIONSHIP_CODE_2.equals(rs.get(0).getString("RELATIONSHIP_CODE"))
                || OpalCodeConstants.RelationshipCode.RELATIONSHIP_CODE_3
                        .equals(rs.get(0).getString("RELATIONSHIP_CODE"))) {
            return CheckResultConstants.RELATIONSHIP_CODE_ERROR;
        }

        return CheckResultConstants.CHECK_OK;
    }

    /**
     * HTTPレスポンスを設定する。
     *
     * @param requestData
     *            OP会員本人確認要求電文
     *
     * @param result
     *            チェックの結果
     *
     * @return HTTPレスポンス
     * @throws IOException
     *             異常
     */
    @Override
    protected HttpResponse responseBuilder(A111AAARRequestData requestData, int result) throws IOException {

        // OP会員本人確認応答電文設定
        A111AAASResponseData responseData = new A111AAASResponseData();

        // 応答電文.処理結果コード
        responseData.setResultCode(getResultCode(result));
        if (result == CheckResultConstants.OP_DATA_ISNULL) {
            // 応答電文.エラーメッセージ
            ResponseErrorData error = new ResponseErrorData();
            error.setId("MA111A0101");
            error.setMessage(MessageUtil.createMessage(MessageLevel.ERROR, "MA111A0101").formatMessage());
            responseData.setError(error);
        } else if (result == CheckResultConstants.RELATIONSHIP_CODE_ERROR) {
            // 応答電文.エラーメッセージ
            ResponseErrorData error = new ResponseErrorData();
            error.setId("MA111A0102");
            error.setMessage(MessageUtil.createMessage(MessageLevel.ERROR, "MA111A0102").formatMessage());
            responseData.setError(error);
        }

        HttpResponse response = super.setHttpResponseData(responseData, result);

        return response;

    }

    /**
     * OP会員情報データ取得
     *
     * @param form
     *            OP会員本人確認フォーム
     *
     * @return OP会員情報
     */
    private SqlResultSet getOpMemInfo(A111AAARBodyForm form) {

        // OP会員情報取得用のSQL条件を設定する。
        ParameterizedSqlPStatement statement = getParameterizedSqlStatement("SELECT_RELATIONSHIP_CODE");

        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("osakaPitapaNumber", form.getOsakaPitapaNumber());
        condition.put("birthdate", form.getBirthdate());
        condition.put("telephoneNumber", form.getTelephoneNumber());
        condition.put("pitapaExpirationDate", form.getPitapaExpirationDate());

        // 実行する。
        SqlResultSet result = statement.retrieve(condition);
        return result;
    }

}
