package jp.co.tis.opal.web.ss111A;

import java.io.Serializable;

import nablarch.core.validation.ee.Domain;
import nablarch.core.validation.ee.Required;

/**
 * A111A01:OP会員本人確認フォーム。
 *
 * @author 陳
 * @since 1.0
 */
public class A111AAARBodyForm implements Serializable {

    /** シリアルバージョンUID */
    private static final long serialVersionUID = 1L;

    /** OP番号 */
    @Domain("opNum")
    @Required(message = "{M000000001}")
    private String osakaPitapaNumber;

    /** 生年月日 */
    @Domain("date")
    @Required(message = "{M000000001}")
    private String birthdate;

    /** 電話番号 */
    @Domain("telephoneNumber")
    @Required(message = "{M000000001}")
    private String telephoneNumber;

    /** PiTaPa有効期限 */
    @Domain("yearMonth")
    @Required(message = "{M000000001}")
    private String pitapaExpirationDate;

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
    public void setOsakaPitapaNumber(String osakaPitapaNumber) {
        this.osakaPitapaNumber = osakaPitapaNumber;
    }

    /**
     * 生年月日を取得する。
     *
     * @return 生年月日
     */
    public String getBirthdate() {
        return birthdate;
    }

    /**
     * 生年月日を設定する。
     *
     * @param birthdate
     *            生年月日
     */
    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    /**
     * 電話番号を取得する。
     *
     * @return 電話番号
     */
    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    /**
     * 電話番号を設定する。
     *
     * @param telephoneNumber
     *            電話番号
     */
    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }

    /**
     * PiTaPa有効期限を取得する。
     *
     * @return PiTaPa有効期限
     */
    public String getPitapaExpirationDate() {
        return pitapaExpirationDate;
    }

    /**
     * PiTaPa有効期限を設定する。
     *
     * @param pitapaExpirationDate
     *            PiTaPa有効期限
     */
    public void setPitapaExpirationDate(String pitapaExpirationDate) {
        this.pitapaExpirationDate = pitapaExpirationDate;
    }
}