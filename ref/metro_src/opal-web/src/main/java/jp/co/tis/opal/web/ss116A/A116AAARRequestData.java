package jp.co.tis.opal.web.ss116A;

import java.io.Serializable;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import nablarch.core.validation.ee.Required;

import jp.co.tis.opal.web.common.RequestData.RequestOpalControlData;

/**
 * パスワード変更要求電文
 *
 * @author 陳
 * @since 1.0
 */
public class A116AAARRequestData implements Serializable {

    /** シリアルバージョンUID */
    private static final long serialVersionUID = 1L;

    /** OPAL制御データ */
    @Valid
    @Required(message = "{M000000001}")
    private RequestOpalControlData opalControlData;

    /** アプリケーションデータ */
    @Valid
    @Required(message = "{M000000001}")
    private A116AAARBodyForm aplData;

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
     * アプリケーションデータを取得する。
     *
     * @return アプリケーションデータ
     */
    public A116AAARBodyForm getAplData() {
        return aplData;
    }

    /**
     * アプリケーションデータを設定する。
     *
     * @param aplData
     *            アプリケーションデータ
     */
    @JsonInclude(Include.NON_NULL)
    public void setAplData(A116AAARBodyForm aplData) {
        this.aplData = aplData;
    }

}