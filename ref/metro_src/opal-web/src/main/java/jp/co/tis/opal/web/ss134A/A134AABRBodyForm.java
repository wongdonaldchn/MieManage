package jp.co.tis.opal.web.ss134A;

import java.io.Serializable;

import nablarch.core.validation.ee.Domain;
import nablarch.core.validation.ee.Required;

/**
 * A134A02:マイル減算フォーム。
 *
 * @author 曹
 * @since 1.0
 */
public class A134AABRBodyForm implements Serializable {

    /** シリアルバージョンUID */
    private static final long serialVersionUID = 1L;

    /** アプリ会員ID */
    @Domain("applicationMemberId")
    @Required(message = "{M000000001}")
    private String applicationMemberId;

    /** マイル加算減算受付番号 */
    @Domain("mileAddSubRcptNo")
    @Required(message = "{M000000001}")
    private String mileAddSubRcptNo;

    /** マイル種別コード */
    @Domain("mileCategoryCode")
    @Required(message = "{M000000001}")
    private String mileCategoryCode;

    /** 減算マイル数 */
    @Domain("mileAmount")
    @Required(message = "{M000000001}")
    private String subMileAmount;

    /**
     * アプリ会員IDを取得する。
     *
     * @return アプリ会員ID
     */
    public String getApplicationMemberId() {
        return applicationMemberId;
    }

    /**
     * アプリ会員IDを設定する。
     *
     * @param applicationMemberId
     *            アプリ会員ID
     */
    public void setApplicationMemberId(String applicationMemberId) {
        this.applicationMemberId = applicationMemberId;
    }

    /**
     * マイル加算減算受付番号を取得する。
     *
     * @return マイル加算減算受付番号
     */
    public String getMileAddSubRcptNo() {
        return mileAddSubRcptNo;
    }

    /**
     * マイル加算減算受付番号を設定する。
     *
     * @param mileAddSubRcptNo
     *            マイル加算減算受付番号
     */
    public void setMileAddSubRcptNo(String mileAddSubRcptNo) {
        this.mileAddSubRcptNo = mileAddSubRcptNo;
    }

    /**
     * マイル種別コードを取得する。
     *
     * @return マイル種別コード
     */
    public String getMileCategoryCode() {
        return mileCategoryCode;
    }

    /**
     * マイル種別コードを設定する。
     *
     * @param mileCategoryCode
     *            マイル種別コード
     */
    public void setMileCategoryCode(String mileCategoryCode) {
        this.mileCategoryCode = mileCategoryCode;
    }

    /**
     * 減算マイル数を取得する。
     *
     * @return 減算マイル数
     */
    public String getSubMileAmount() {
        return subMileAmount;
    }

    /**
     * 減算マイル数を設定する。
     *
     * @param subMileAmount
     *            減算マイル数
     */
    public void setSubMileAmount(String subMileAmount) {
        this.subMileAmount = subMileAmount;
    }
}
