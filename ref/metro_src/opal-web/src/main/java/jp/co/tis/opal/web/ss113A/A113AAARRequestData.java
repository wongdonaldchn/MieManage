package jp.co.tis.opal.web.ss113A;

import java.io.Serializable;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import nablarch.core.validation.ee.Required;

import jp.co.tis.opal.web.common.RequestData.RequestOpalControlData;

/**
 * アプリ会員情報取得要求電文対象のクラス。
 *
 * @author 曹
 * @since 1.0
 */
public class A113AAARRequestData implements Serializable {
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
     * アプリ会員情報取得フォーム。
     */
    @Valid
    @Required(message = "{M000000001}")
    private A113AAARBodyForm aplData;

    /**
     * アプリ会員情報取得フォームを取得する。
     *
     * @return フォーム
     */
    public A113AAARBodyForm getAplData() {
        return aplData;
    }

    /**
     * アプリ会員情報取得フォームを設定する。
     *
     * @param aplData
     *            フォーム
     */
    public void setAplData(A113AAARBodyForm aplData) {
        this.aplData = aplData;
    }

}
