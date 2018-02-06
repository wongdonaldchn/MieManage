package jp.co.tis.opal.web.ss115A;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import nablarch.core.validation.ee.Domain;
import nablarch.core.validation.ee.Required;

/**
 * OP認証データフォーム。
 *
 * @author 趙
 * @since 1.0
 */
public class A115AAARBodyForm implements Serializable {

    /** シリアルバージョンUID */
    private static final long serialVersionUID = 1L;

    /** アプリ会員ID */
    @Domain("applicationMemberId")
    @Required(message = "{M000000001}")
    private String applicationMemberId;

    /** OP番号 */
    @Domain("opNum")
    @Required(message = "{M000000001}")
    private String osakaPitapaNumber;

    /** マイル加算減算受付番号 */
    @Domain("mileAddSubRcptNo")
    @Required(message = "{M000000001}")
    private String mileAddSubRcptNum;

    /** OP認証ボーナスマイル数 */
    @Domain("mileAmount")
    @Required(message = "{M000000001}")
    private String opAuthBonusMileAmount;

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
    @JsonInclude(Include.NON_NULL)
    public void setApplicationMemberId(String applicationMemberId) {
        this.applicationMemberId = applicationMemberId;
    }

    /**
     * OP番号を取得する。
     *
     * @return OP番号
     */
    public String getOsakaPitapaNumber() {
        return osakaPitapaNumber;
    }

    /**
     * OP番号を設定する。
     *
     * @param osakaPitapaNumber
     *            OP番号
     */
    @JsonInclude(Include.NON_NULL)
    public void setOsakaPitapaNumber(String osakaPitapaNumber) {
        this.osakaPitapaNumber = osakaPitapaNumber;
    }

    /**
     * マイル加算減算受付番号を取得する。
     *
     * @return マイル加算減算受付番号
     */
    public String getMileAddSubRcptNum() {
        return mileAddSubRcptNum;
    }

    /**
     * マイル加算減算受付番号を設定する。
     *
     * @param mileAddSubRcptNum
     *            マイル加算減算受付番号
     */
    @JsonInclude(Include.NON_NULL)
    public void setMileAddSubRcptNum(String mileAddSubRcptNum) {
        this.mileAddSubRcptNum = mileAddSubRcptNum;
    }

    /**
     * OP認証ボーナスマイル数を取得する。
     *
     * @return OP認証ボーナスマイル数
     */
    public String getOpAuthBonusMileAmount() {
        return opAuthBonusMileAmount;
    }

    /**
     * OP認証ボーナスマイル数を設定する。
     *
     * @param opAuthBonusMileAmount
     *            OP認証ボーナスマイル数
     */
    @JsonInclude(Include.NON_NULL)
    public void setOpAuthBonusMileAmount(String opAuthBonusMileAmount) {
        this.opAuthBonusMileAmount = opAuthBonusMileAmount;
    }
}
