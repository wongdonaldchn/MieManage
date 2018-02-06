package jp.co.tis.opal.web.ss111A;

import java.io.Serializable;

import nablarch.core.validation.ee.Domain;
import nablarch.core.validation.ee.Required;

/**
 * A111A02:OP会員情報取得フォーム。
 *
 * @author 陳
 * @since 1.0
 */
public class A111AABRBodyForm implements Serializable {

    /** シリアルバージョンUID */
    private static final long serialVersionUID = 1L;

    /** OP番号 */
    @Domain("opNum")
    @Required(message = "{M000000001}")
    private String osakaPitapaNumber;

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
}