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
public class A113AACRBodyForm implements Serializable {

    /** シリアルバージョンUID */
    private static final long serialVersionUID = 1L;

    /** アプリ会員ID */
    @Domain("applicationMemberId")
    @Required(message = "{M000000001}")
    private String applicationMemberId;

    /** メールアドレス */
    @Domain("mailAddress")
    @Required(message = "{M000000001}")
    private String mailAddress;

    /**
     * アプリ会員IDを取得する。
     *
     * @return アプリ会員ID
     */
    public String getApplicationMemberId() {
        return applicationMemberId;
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
