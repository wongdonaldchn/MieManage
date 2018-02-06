package jp.co.tis.opal.web.ss123A;

import java.io.Serializable;
import java.util.Objects;

import javax.validation.constraints.AssertTrue;

import com.fasterxml.jackson.annotation.JsonIgnore;

import nablarch.core.util.StringUtil;
import nablarch.core.validation.ee.Domain;
import nablarch.core.validation.ee.Required;

/**
 * A123A01:パートナー会員サービス登録フォーム。
 *
 * @author 陳
 * @since 1.0
 */
public class A123AAARBodyForm implements Serializable {

    /** シリアルバージョンUID */
    private static final long serialVersionUID = 1L;

    /** パートナー登録アプリ会員ID */
    @Domain("applicationMemberId")
    @Required(message = "{M000000001}")
    private String applicationMemberId;

    /** パートナー相手アプリ会員ID */
    @Domain("applicationMemberId")
    @Required(message = "{M000000001}")
    private String partnerApplicationMemberId;

    /**
     * パートナー登録アプリ会員とパートナー相手アプリ会員が同一アプリ会員ではないことをチェックする
     *
     * @return 処理結果（一致しない：true 一致する：false）
     */
    @JsonIgnore
    @AssertTrue(message = "{MA123A0101}")
    public boolean isSameApplicationMemberId() {

        if (!StringUtil.isNullOrEmpty(applicationMemberId) && !StringUtil.isNullOrEmpty(partnerApplicationMemberId)) {
            return !Objects.equals(applicationMemberId, partnerApplicationMemberId);
        }
        return true;
    }

    /**
     * パートナー登録アプリ会員IDを取得する。
     *
     * @return パートナー登録アプリ会員ID
     */
    public String getApplicationMemberId() {
        return applicationMemberId;
    }

    /**
     * パートナー登録アプリ会員IDを設定する。
     *
     * @param applicationMemberId
     *            パートナー登録アプリ会員ID
     */
    public void setApplicationMemberId(String applicationMemberId) {
        this.applicationMemberId = applicationMemberId;
    }

    /**
     * パートナー相手アプリ会員IDを取得する。
     *
     * @return パートナー相手アプリ会員ID
     */
    public String getPartnerApplicationMemberId() {
        return partnerApplicationMemberId;
    }

    /**
     * パートナー相手アプリ会員IDを設定する。
     *
     * @param partnerApplicationMemberId
     *            パートナー相手アプリ会員ID
     */
    public void setPartnerApplicationMemberId(String partnerApplicationMemberId) {
        this.partnerApplicationMemberId = partnerApplicationMemberId;
    }
}