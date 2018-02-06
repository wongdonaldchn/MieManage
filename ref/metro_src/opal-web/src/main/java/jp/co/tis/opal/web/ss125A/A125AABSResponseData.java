package jp.co.tis.opal.web.ss125A;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import jp.co.tis.opal.web.common.ResponseData.ResponseErrorData;

/**
 * 乗車適用日データ応答電文
 *
 * @author 陳
 * @since 1.0
 */
public class A125AABSResponseData implements Serializable {

    /** シリアルバージョンUID */
    private static final long serialVersionUID = 1L;

    /** 処理結果コード */
    private String resultCode;

    /** エラー情報 */
    private ResponseErrorData error;

    /** 上限回数 */
    private String upperLimitTimes;

    /** 前月乗車適用日情報 */
    private List<Map<String, Object>> lastMonthRideApplyDateInfo;

    /** 今月乗車適用日情報 */
    private List<Map<String, Object>> thisMonthRideApplyDateInfo;

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
     * 上限回数を取得する。
     *
     * @return 上限回数
     */
    @JsonInclude(Include.NON_NULL)
    public String getUpperLimitTimes() {
        return upperLimitTimes;
    }

    /**
     * 上限回数を設定する。
     *
     * @param upperLimitTimes
     *            上限回数
     */
    public void setUpperLimitTimes(String upperLimitTimes) {
        this.upperLimitTimes = upperLimitTimes;
    }

    /**
     * 前月乗車適用日情報を取得する。
     *
     * @return 前月乗車適用日情報
     */
    @JsonInclude(Include.NON_NULL)
    public List<Map<String, Object>> getLastMonthRideApplyDateInfo() {
        return lastMonthRideApplyDateInfo;
    }

    /**
     * 前月乗車適用日情報を設定する。
     *
     * @param lastMonthRideApplyDateInfo
     *            前月乗車適用日情報
     */
    public void setLastMonthRideApplyDateInfo(List<Map<String, Object>> lastMonthRideApplyDateInfo) {
        this.lastMonthRideApplyDateInfo = lastMonthRideApplyDateInfo;
    }

    /**
     * 今月乗車適用日情報を取得する。
     *
     * @return 今月乗車適用日情報
     */
    @JsonInclude(Include.NON_NULL)
    public List<Map<String, Object>> getThisMonthRideApplyDateInfo() {
        return thisMonthRideApplyDateInfo;
    }

    /**
     * 今月乗車適用日情報を設定する。
     *
     * @param thisMonthRideApplyDateInfo
     *            今月乗車適用日情報
     */
    public void setThisMonthRideApplyDateInfo(List<Map<String, Object>> thisMonthRideApplyDateInfo) {
        this.thisMonthRideApplyDateInfo = thisMonthRideApplyDateInfo;
    }
}