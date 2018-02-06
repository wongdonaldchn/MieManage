package jp.co.tis.opal.web.ss116A;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import nablarch.core.validation.ee.Domain;
import nablarch.core.validation.ee.Required;

/**
 * A116A02:ログインID・パスワード再登録フォーム。
 *
 * @author 唐
 * @since 1.0
 */
public class A116AABRBodyForm implements Serializable {

    /** シリアルバージョンUID */
    private static final long serialVersionUID = 1L;

    /** ログインID・パスワード再登録認証コード */
    @Domain("mailAddressAuthCode")
    @Required(message = "{M000000001}")
    private String loginIdPasswordReregistAuthCode;

    /**
     * ログインID・パスワード再登録認証コードを取得する。
     *
     * @return ログインID・パスワード再登録認証コード
     */
    public String getLoginIdPasswordReregistAuthCode() {
        return loginIdPasswordReregistAuthCode;
    }

    /**
     * ログインID・パスワード再登録認証コードを設定する。
     *
     * @param loginIdPasswordReregistAuthCode
     *            ログインID・パスワード再登録認証コード
     */
    @JsonInclude(Include.NON_NULL)
    public void setLoginIdPasswordReregistAuthCode(String loginIdPasswordReregistAuthCode) {
        this.loginIdPasswordReregistAuthCode = loginIdPasswordReregistAuthCode;
    }
}