package jp.co.tis.opal.web.ss132A;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import jp.co.tis.opal.web.common.ResponseData.ResponseErrorData;

/**
 * マイル履歴データ応答電文
 *
 * @author 唐
 * @since 1.0
 */
public class A132AAASResponseData implements Serializable {

    /** シリアルバージョンUID */
    private static final long serialVersionUID = 1L;

    /** 処理結果コード */
    private String resultCode;

    /** エラー情報 */
    private ResponseErrorData error;

    /** マイル履歴情報 */
    private List<Map<String, Object>> mileHistoryInfo;

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
     * マイル履歴情報データを取得する。
     *
     * @return マイル履歴情報データ
     */
    @JsonInclude(Include.NON_NULL)
    public List<Map<String, Object>> getMileHistoryInfo() {
        return mileHistoryInfo;
    }

    /**
     * マイル履歴情報データを設定する。
     *
     * @param mileHistoryInfo
     *            マイル履歴情報データ
     */
    public void setMileHistoryInfo(List<Map<String, Object>> mileHistoryInfo) {
        this.mileHistoryInfo = mileHistoryInfo;
    }
}