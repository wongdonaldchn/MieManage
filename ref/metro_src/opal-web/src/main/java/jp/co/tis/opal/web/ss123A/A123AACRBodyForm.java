package jp.co.tis.opal.web.ss123A;

import java.io.Serializable;

import nablarch.core.validation.ee.Domain;
import nablarch.core.validation.ee.Required;

/**
 * A123A03:パートナー会員サービス更新フォーム。
 *
 * @author 陳
 * @since 1.0
 */
public class A123AACRBodyForm implements Serializable {

    /** シリアルバージョンUID */
    private static final long serialVersionUID = 1L;

    /** アプリ会員ID */
    @Domain("applicationMemberId")
    @Required(message = "{M000000001}")
    private String applicationMemberId;

    /** パートナー会員サービス管理ID */
    @Domain("applicationMemberId")
    @Required(message = "{M000000001}")
    private String partnerMemServiceCtrlId;

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
     * パートナー会員サービス管理IDを取得する。
     *
     * @return パートナー会員サービス管理ID
     */
    public String getPartnerMemServiceCtrlId() {
        return partnerMemServiceCtrlId;
    }

    /**
     * パートナー会員サービス管理IDを設定する。
     *
     * @param partnerMemServiceCtrlId
     *            パートナー会員サービス管理ID
     */
    public void setPartnerMemServiceCtrlId(String partnerMemServiceCtrlId) {
        this.partnerMemServiceCtrlId = partnerMemServiceCtrlId;
    }
}