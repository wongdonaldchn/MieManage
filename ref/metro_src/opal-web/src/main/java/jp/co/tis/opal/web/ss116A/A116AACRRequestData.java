package jp.co.tis.opal.web.ss116A;

import java.io.Serializable;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import nablarch.core.validation.ee.Required;

import jp.co.tis.opal.web.common.RequestData.RequestOpalControlData;

/**
 * A116A03:ログインID・パスワード一時登録要求電文
 *
 * @author 唐
 * @since 1.0
 */
public class A116AACRRequestData implements Serializable {

    /** シリアルバージョンUID */
    private static final long serialVersionUID = 1L;

    /** OPAL制御データ */
    @Valid
    @Required(message = "{M000000001}")
    private RequestOpalControlData opalControlData;

    /** アプリケーションデータ */
    @Valid
    @Required(message = "{M000000001}")
    private A116AACRBodyForm aplData;

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
    public A116AACRBodyForm getAplData() {
        return aplData;
    }

    /**
     * アプリケーションデータを設定する。
     *
     * @param aplData
     *            アプリケーションデータ
     */
    @JsonInclude(Include.NON_NULL)
    public void setAplData(A116AACRBodyForm aplData) {
        this.aplData = aplData;
    }

}