package jp.co.tis.opal.web.ss125A;

import java.io.Serializable;

import nablarch.core.validation.ee.Domain;

/**
 * A125A03:乗車適用日選択の乗車適用日フォーム。
 *
 * @author 陳
 * @since 1.0
 */
public class A125AACRBodyDateForm implements Serializable {

    /** シリアルバージョンUID */
    private static final long serialVersionUID = 1L;

    /** 乗車適用日 */
    @Domain("date")
    private String rideApplyDate;

    /**
     * 乗車適用日を取得する。
     *
     * @return 乗車適用日
     */
    public String getRideApplyDate() {
        return rideApplyDate;
    }

    /**
     * 乗車適用日を設定する。
     *
     * @param rideApplyDate
     *            乗車適用日
     */
    public void setRideApplyDate(String rideApplyDate) {
        this.rideApplyDate = rideApplyDate;
    }
}