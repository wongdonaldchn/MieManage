package jp.co.tis.opal.web.ss123A;

import java.io.Serializable;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import jp.co.tis.opal.web.common.ResponseData.ResponseErrorData;

/**
 * パートナー会員サービス情報データ応答電文
 *
 * @author 陳
 * @since 1.0
 */
public class A123AABSResponseData implements Serializable {

    /** シリアルバージョンUID */
    private static final long serialVersionUID = 1L;

    /** 処理結果コード */
    private String resultCode;

    /** エラー情報 */
    private ResponseErrorData error;

    /** パートナー会員サービス情報データ */
    private Map<String, Object> partnerData;

    /**
     * 処理結果コードを取得する。
     *
     * @return 処理結果コード
     */
    public String getResultCode() {
        return resultCode;
    }

    /**
     * 処理結果コードを設定する。
     *
     * @param resultCode
     *            処理結果コード
     */
    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    /**
     * エラー情報を取得する。
     *
     * @return エラー情報
     */
    @JsonInclude(Include.NON_NULL)
    public ResponseErrorData getError() {
        return error;
    }

    /**
     * エラー情報を設定する。
     *
     * @param error
     *            エラー情報
     */
    public void setError(ResponseErrorData error) {
        this.error = error;
    }

    /**
     * パートナー会員サービス情報データを取得する。
     *
     * @return パートナー会員サービス情報データ
     */
    @JsonInclude(Include.NON_NULL)
    public Map<String, Object> getPartnerData() {
        return partnerData;
    }

    /**
     * パートナー会員サービス情報データを設定する。
     *
     * @param partnerData
     *            パートナー会員サービス情報データ
     */
    public void setPartnerData(Map<String, Object> partnerData) {
        this.partnerData = partnerData;
    }
}