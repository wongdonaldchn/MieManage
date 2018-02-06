package jp.co.tis.opal.web.ss117A;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import nablarch.core.validation.ee.Domain;
import nablarch.core.validation.ee.Required;

/**
 * A117A01:アプリ会員本人確認APIの検索フォーム。
 *
 * @author 唐
 * @since 1.0
 */
public class A117AAARBodyForm implements Serializable {

    /** シリアルバージョンUID */
    private static final long serialVersionUID = 1L;

    /** 生年月日 */
    @Domain("date")
    @Required(message = "{M000000001}")
    private String birthdate;

    /** 性別コード */
    @Domain("sexCode")
    @Required(message = "{M000000001}")
    private String sexCode;

    /** メールアドレス */
    @Domain("mailAddress")
    @Required(message = "{M000000001}")
    private String mailAddress;

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
    @JsonInclude(Include.NON_NULL)
    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    /**
     * 性別コードを取得する。
     *
     * @return 性別コード
     */
    public String getSexCode() {
        return sexCode;
    }

    /**
     * 性別コードを設定する。
     *
     * @param sexCode
     *            性別コード
     */
    @JsonInclude(Include.NON_NULL)
    public void setSexCode(String sexCode) {
        this.sexCode = sexCode;
    }

    /**
     * メールアドレスを取得する。
     *
     * @return メールアドレス
     */
    public String getMailAddress() {
        return mailAddress;
    }

    /**
     * メールアドレスを設定する。
     *
     * @param mailAddress
     *            メールアドレス
     */
    @JsonInclude(Include.NON_NULL)
    public void setMailAddress(String mailAddress) {
        this.mailAddress = mailAddress;
    }
}