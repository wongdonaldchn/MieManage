package jp.co.tis.opal.web.ss131A;

import java.io.Serializable;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import nablarch.core.validation.ee.Required;

import jp.co.tis.opal.web.common.RequestData.RequestOpalControlData;

/**
 * マイル残高取得要求電文対象のクラス。
 *
 * @author 張
 * @since 1.0
 */
public class A131AAARRequestData implements Serializable {
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
     * マイル残高取得フォーム。
     */
    @Valid
    @Required(message = "{M000000001}")
    private A131AAARBodyForm aplData;

    /**
     * マイル残高取得フォームを取得する。
     *
     * @return フォーム
     */
    public A131AAARBodyForm getAplData() {
        return aplData;
    }

    /**
     * マイル残高取得フォームを設定する。
     *
     * @param aplData
     *            フォーム
     */
    @JsonInclude(Include.NON_NULL)
    public void setAplData(A131AAARBodyForm aplData) {
        this.aplData = aplData;
    }

}
