package jp.co.tis.opal.web.ss125A;

import java.io.Serializable;

import nablarch.core.validation.ee.Domain;
import nablarch.core.validation.ee.Required;

/**
 * A125A02:乗車適用日参照フォーム。
 *
 * @author 陳
 * @since 1.0
 */
public class A125AABRBodyForm implements Serializable {

    /** シリアルバージョンUID */
    private static final long serialVersionUID = 1L;

    /** アプリ会員ID */
    @Domain("applicationMemberId")
    @Required(message = "{M000000001}")
    private String applicationMemberId;

    /** サービス区分 */
    @Domain("serviceDivision")
    @Required(message = "{M000000001}")
    private String serviceDivision;

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
     * サービス区分を取得する。
     *
     * @return サービス区分
     */
    public String getServiceDivision() {
        return serviceDivision;
    }

    /**
     * サービス区分を設定する。
     *
     * @param serviceDivision
     *            サービス区分
     */
    public void setServiceDivision(String serviceDivision) {
        this.serviceDivision = serviceDivision;
    }
}