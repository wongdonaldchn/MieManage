package jp.co.tis.opal.web.ss113A;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import nablarch.core.validation.ee.Domain;
import nablarch.core.validation.ee.Required;

/**
 * アプリケーションデータ。
 *
 * @author 唐
 * @since 1.0
 */
public class A113AADRBodyForm implements Serializable {

    /** シリアルバージョンUID */
    private static final long serialVersionUID = 1L;

    /** メールアドレス変更認証コード */
    @Domain("mailAddressAuthCode")
    @Required(message = "{M000000001}")
    private String mailAddressChangeAuthCode;

    /**
     * メールアドレス変更認証コードを取得する。
     *
     * @return メールアドレス変更認証コード
     */
    public String getMailAddressChangeAuthCode() {
        return mailAddressChangeAuthCode;
    }

    /**
     * メールアドレス変更認証コードを設定する。
     *
     * @param mailAddressChangeAuthCode
     *            メールアドレス変更認証コード
     */
    @JsonInclude(Include.NON_NULL)
    public void setMailAddressChangeAuthCode(String mailAddressChangeAuthCode) {
        this.mailAddressChangeAuthCode = mailAddressChangeAuthCode;
    }
}
