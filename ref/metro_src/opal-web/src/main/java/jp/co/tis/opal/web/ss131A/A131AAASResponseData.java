package jp.co.tis.opal.web.ss131A;

import java.io.Serializable;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import jp.co.tis.opal.web.common.ResponseData.ResponseErrorData;

/**
 * マイル残高データ応答電文対象のクラス。
 *
 * @author 張
 * @since 1.0
 */
public class A131AAASResponseData implements Serializable {
    /** シリアルバージョンUID */
    private static final long serialVersionUID = 1L;

    /** 処理結果コード */
    private String resultCode;

    /** エラー情報 */
    private ResponseErrorData error;

    /** マイル残高情報 */
    private Map<String, Object> mileBalanceInfo;

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
     * マイル残高情報データを取得する。
     *
     * @return マイル残高情報データ
     */
    @JsonInclude(Include.NON_NULL)
    public Map<String, Object> getMileBalanceInfo() {
        return mileBalanceInfo;
    }

    /**
     * マイル残高情報データを設定する。
     *
     * @param mileBalanceInfo
     *            マイル残高情報データ
     */
    public void setMileBalanceInfo(Map<String, Object> mileBalanceInfo) {
        this.mileBalanceInfo = mileBalanceInfo;
    }
}
