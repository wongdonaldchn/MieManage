package jp.co.tis.opal.web.ss116A;

import java.io.Serializable;

import nablarch.core.validation.ee.Domain;
import nablarch.core.validation.ee.Required;

/**
 * A116A01:パスワード変更フォーム。
 *
 * @author 陳
 * @since 1.0
 */
public class A116AAARBodyForm implements Serializable {

    /** シリアルバージョンUID */
    private static final long serialVersionUID = 1L;

    /** アプリ会員ID */
    @Domain("applicationMemberId")
    @Required(message = "{M000000001}")
    private String applicationMemberId;

    /** 旧パスワード */
    @Domain("password")
    @Required(message = "{M000000001}")
    private String oldPassword;

    /** 新パスワード */
    @Domain("password")
    @Required(message = "{M000000001}")
    private String newPassword;

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
     * 旧パスワードを取得する。
     *
     * @return 旧パスワード
     */
    public String getOldPassword() {
        return oldPassword;
    }

    /**
     * 旧パスワードを設定する。
     *
     * @param oldPassword
     *            旧パスワード
     */
    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    /**
     * 新パスワードを取得する。
     *
     * @return 新パスワード
     */
    public String getNewPassword() {
        return newPassword;
    }

    /**
     * 新パスワードを設定する。
     *
     * @param newPassword
     *            新パスワード
     */
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}