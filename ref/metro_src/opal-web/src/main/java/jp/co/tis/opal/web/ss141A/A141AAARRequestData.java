package jp.co.tis.opal.web.ss141A;

import java.io.Serializable;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import nablarch.core.validation.ee.Required;

import jp.co.tis.opal.web.common.RequestData.RequestOpalControlData;

/**
 * 郵送情報取得要求電文
 *
 * @author 陳
 * @since 1.0
 */
public class A141AAARRequestData implements Serializable {

    /** シリアルバージョンUID */
    private static final long serialVersionUID = 1L;

    /** OPAL制御データ */
    @Valid
    @Required(message = "{M000000001}")
    private RequestOpalControlData opalControlData;

    /** アプリケーションデータ */
    @Valid
    @Required(message = "{M000000001}")
    private A141AAARBodyForm aplData;

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
    public A141AAARBodyForm getAplData() {
        return aplData;
    }

    /**
     * アプリケーションデータを設定する。
     *
     * @param aplData
     *            アプリケーションデータ
     */
    @JsonInclude(Include.NON_NULL)
    public void setAplData(A141AAARBodyForm aplData) {
        this.aplData = aplData;
    }

}