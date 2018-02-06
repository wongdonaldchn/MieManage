package jp.co.tis.opal.web.ss134A;

import java.io.Serializable;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import nablarch.core.validation.ee.Required;

import jp.co.tis.opal.web.common.RequestData.RequestOpalControlData;

/**
 * マイル減算要求電文
 *
 * @author 曹
 * @since 1.0
 */
public class A134AABRRequestData implements Serializable {

    /** シリアルバージョンUID */
    private static final long serialVersionUID = 1L;

    /**
     * アプリケーションデータ
     */
    @Valid
    @Required(message = "{M000000001}")
    private A134AABRBodyForm aplData;

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
     * アプリケーションデータを取得する。
     *
     * @return アプリケーションデータ
     */
    public A134AABRBodyForm getAplData() {
        return aplData;
    }

    /**
     * アプリケーションデータを設定する。
     *
     * @param aplData
     *            アプリケーションデータ
     */
    public void setAplData(A134AABRBodyForm aplData) {
        this.aplData = aplData;
    }

}
