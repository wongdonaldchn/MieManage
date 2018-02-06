package jp.co.tis.opal.web.ss113A;

import java.io.Serializable;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import nablarch.core.validation.ee.Required;

import jp.co.tis.opal.web.common.RequestData.RequestOpalControlData;

/**
 * アプリ会員情報更新要求電文対象のクラス。
 *
 * @author 張
 * @since 1.0
 */
public class A113AABRRequestData implements Serializable {
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
     * アプリ会員情報更新フォーム。
     */
    @Valid
    @Required(message = "{M000000001}")
    private A113AABRBodyForm aplMemInfo;

    /**
     * アプリ会員情報更新フォームを取得する。
     *
     * @return フォーム
     */
    public A113AABRBodyForm getAplMemInfo() {
        return aplMemInfo;
    }

    /**
     * アプリ会員情報更新フォームを設定する。
     *
     * @param aplMemInfo
     *            フォーム
     */
    @JsonInclude(Include.NON_NULL)
    public void setAplMemInfo(A113AABRBodyForm aplMemInfo) {
        this.aplMemInfo = aplMemInfo;
    }

}
