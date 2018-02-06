package jp.co.tis.opal.web.ss115A;

import java.io.Serializable;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import nablarch.core.validation.ee.Required;

import jp.co.tis.opal.web.common.RequestData.RequestOpalControlData;

/**
 * OP認証要求電文対象のクラス。
 *
 * @author 趙
 * @since 1.0
 */
public class A115AAARRequestData implements Serializable {
    /** シリアルバージョンUID */
    private static final long serialVersionUID = 1L;

    /**
     * OPAL制御データ。
     */
    @Valid
    @Required(message = "{M000000001}")
    private RequestOpalControlData opalControlData;

    /**
     * OPAL制御データを取得する。
     *
     * @return OPAL制御データ
     */
    public RequestOpalControlData getOpalControlData() {
        return opalControlData;
    }

    /**
     * OPAL制御データを設定する。
     *
     * @param opalControlData
     *            OPAL制御データ
     */
    @JsonInclude(Include.NON_NULL)
    public void setOpalControlData(RequestOpalControlData opalControlData) {
        this.opalControlData = opalControlData;
    }

    /**
     * OP認証データフォーム。
     */
    @Valid
    @Required(message = "{M000000001}")
    private A115AAARBodyForm opAuthenticateData;

    /**
     * OP認証データフォームを取得する。
     *
     * @return フォーム
     */
    public A115AAARBodyForm getOpAuthenticateData() {
        return opAuthenticateData;
    }

    /**
     * OP認証データフォームを設定する。
     *
     * @param opAuthenticateData
     *            フォーム
     */
    @JsonInclude(Include.NON_NULL)
    public void setOpAuthenticateData(A115AAARBodyForm opAuthenticateData) {
        this.opAuthenticateData = opAuthenticateData;
    }

}
