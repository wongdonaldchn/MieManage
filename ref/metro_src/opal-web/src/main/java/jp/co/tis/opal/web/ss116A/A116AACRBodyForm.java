package jp.co.tis.opal.web.ss116A;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import nablarch.core.validation.ee.Domain;
import nablarch.core.validation.ee.Required;

/**
 * A116A03:ログインID・パスワード一時登録フォーム。
 *
 * @author 唐
 * @since 1.0
 */
public class A116AACRBodyForm implements Serializable {

    /** シリアルバージョンUID */
    private static final long serialVersionUID = 1L;

    /** アプリ会員ID */
    @Domain("applicationMemberId")
    @Required(message = "{M000000001}")
    private String applicationMemberId;

    /** ログインID */
    @Domain("loginId")
    @Required(message = "{M000000001}")
    private String loginId;

    /** パスワード */
    @Domain("password")
    @Required(message = "{M000000001}")
    private String password;

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
     * ログインIDを取得する。
     *
     * @return ログインID
     */
    public String getLoginId() {
        return loginId;
    }

    /**
     * ログインIDを設定する。
     *
     * @param loginId
     *            ログインID
     */
    @JsonInclude(Include.NON_NULL)
    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    /**
     * パスワードを取得する。
     *
     * @return パスワード
     */
    public String getPassword() {
        return password;
    }

    /**
     * パスワードを設定する。
     *
     * @param password
     *            パスワード
     */
    @JsonInclude(Include.NON_NULL)
    public void setPassword(String password) {
        this.password = password;
    }
}