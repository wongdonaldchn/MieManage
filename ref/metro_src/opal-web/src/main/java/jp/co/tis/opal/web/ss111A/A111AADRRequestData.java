package jp.co.tis.opal.web.ss111A;

import java.io.Serializable;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import nablarch.core.validation.ee.Required;

import jp.co.tis.opal.web.common.RequestData.RequestOpalControlData;

/**
 * アプリ会員登録要求電文対象のクラス。
 *
 * @author 唐
 * @since 1.0
 */
public class A111AADRRequestData implements Serializable {

    /** シリアルバージョンUID */
    private static final long serialVersionUID = 1L;

    /** OPAL制御データ */
    @Valid
    @Required(message = "{M000000001}")
    private RequestOpalControlData opalControlData;

    /** アプリ会員登録データ */
    @Valid
    @Required(message = "{M000000001}")
    private A111AADRBodyForm aplMemRegistData;

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
     * アプリ会員登録データを取得する。
     *
     * @return アプリ会員登録データ
     */
    public A111AADRBodyForm getAplMemRegistData() {
        return aplMemRegistData;
    }

    /**
     * アプリ会員登録データを設定する。
     *
     * @param aplMemRegistData
     *            アプリ会員登録データ
     */
    @JsonInclude(Include.NON_NULL)
    public void setAplMemRegistData(A111AADRBodyForm aplMemRegistData) {
        this.aplMemRegistData = aplMemRegistData;
    }

}