package jp.co.tis.opal.web.ss117A;

import java.io.Serializable;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import jp.co.tis.opal.web.common.ResponseData.ResponseErrorData;

/**
 * アプリ会員本人確認結果応答電文
 *
 * @author 唐
 * @since 1.0
 */
public class A117AAASResponseData implements Serializable {

    /** シリアルバージョンUID */
    private static final long serialVersionUID = 1L;

    /** 処理結果コード */
    private String resultCode;

    /** エラー情報 */
    private ResponseErrorData error;

    /** アプリ会員情報データ */
    private Map<String, Object> aplMemInfo;

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
     * アプリ会員情報データを取得する。
     *
     * @return アプリ会員情報データ
     */
    @JsonInclude(Include.NON_NULL)
    public Map<String, Object> getAplMemInfo() {
        return aplMemInfo;
    }

    /**
     * アプリ会員情報データを設定する。
     *
     * @param aplMemInfo
     *            アプリ会員情報データ
     */
    public void setAplMemInfo(Map<String, Object> aplMemInfo) {
        this.aplMemInfo = aplMemInfo;
    }
}