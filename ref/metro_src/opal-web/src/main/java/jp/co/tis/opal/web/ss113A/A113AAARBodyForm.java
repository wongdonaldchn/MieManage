package jp.co.tis.opal.web.ss113A;

import java.io.Serializable;

import nablarch.core.validation.ee.Domain;
import nablarch.core.validation.ee.Required;

/**
 * アプリ会員情報取得フォーム。
 *
 * @author 曹
 * @since 1.0
 */
public class A113AAARBodyForm implements Serializable {

    /** シリアルバージョンUID */
    private static final long serialVersionUID = 1L;

    /** アプリ会員ID */
    @Domain("applicationMemberId")
    @Required(message = "{M000000001}")
    private String applicationMemberId;

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
}