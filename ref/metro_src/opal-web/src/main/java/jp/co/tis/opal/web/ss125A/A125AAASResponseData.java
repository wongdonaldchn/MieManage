package jp.co.tis.opal.web.ss125A;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import jp.co.tis.opal.web.common.ResponseData.ResponseErrorData;

/**
 * 乗車適用日登録結果応答電文
 *
 * @author 陳
 * @since 1.0
 */
public class A125AAASResponseData implements Serializable {

    /** シリアルバージョンUID */
    private static final long serialVersionUID = 1L;

    /** 処理結果コード */
    private String resultCode;

    /** エラー情報 */
    private ResponseErrorData error;

    /** 登録乗車適用日 */
    private String rideApplyDate;

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
     * 登録乗車適用日を取得する。
     *
     * @return 登録乗車適用日
     */
    @JsonInclude(Include.NON_NULL)
    public String getRideApplyDate() {
        return rideApplyDate;
    }

    /**
     * 登録乗車適用日を設定する。
     *
     * @param rideApplyDate
     *            登録乗車適用日
     */
    public void setRideApplyDate(String rideApplyDate) {
        this.rideApplyDate = rideApplyDate;
    }
}