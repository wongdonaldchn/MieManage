package jp.co.tis.opal.web.ss112A;

import java.io.Serializable;

import nablarch.core.validation.ee.Domain;
import nablarch.core.validation.ee.Required;

/**
 * A112A01:アプリ会員ログインフォーム。
 *
 * @author 陳
 * @since 1.0
 */
public class A112AAARBodyForm implements Serializable {

    /** シリアルバージョンUID */
    private static final long serialVersionUID = 1L;

    /** ログインID */
    @Domain("loginId")
    @Required(message = "{M000000001}")
    private String loginId;

    /** パスワード */
    @Domain("password")
    @Required(message = "{M000000001}")
    private String password;

    /** アプリID */
    @Domain("applicationId")
    @Required(message = "{M000000001}")
    private String applicationId;

    /** デバイスID */
    @Domain("deviceId")
    @Required(message = "{M000000001}")
    private String deviceId;

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
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * アプリIDを取得する。
     *
     * @return アプリID
     */
    public String getApplicationId() {
        return applicationId;
    }

    /**
     * アプリIDを設定する。
     *
     * @param applicationId
     *            アプリID
     */
    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    /**
     * デバイスIDを取得する。
     *
     * @return デバイスID
     */
    public String getDeviceId() {
        return deviceId;
    }

    /**
     * デバイスIDを設定する。
     *
     * @param deviceId
     *            デバイスID
     */
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}