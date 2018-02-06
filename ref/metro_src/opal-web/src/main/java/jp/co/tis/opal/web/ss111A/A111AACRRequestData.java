package jp.co.tis.opal.web.ss111A;

import java.io.Serializable;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import nablarch.core.validation.ee.Required;

import jp.co.tis.opal.web.common.RequestData.RequestOpalControlData;

/**
 * アプリ会員一時登録要求電文対象のクラス。
 *
 * @author 唐
 * @since 1.0
 */
public class A111AACRRequestData implements Serializable {

    /** シリアルバージョンUID */
    private static final long serialVersionUID = 1L;

    /** OPAL制御データ */
    @Valid
    @Required(message = "{M000000001}")
    private RequestOpalControlData opalControlData;

    /** アプリ会員一時情報 */
    @Valid
    @Required(message = "{M000000001}")
    private A111AACRBodyForm aplMemTempInfo;

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
     * アプリ会員一時情報を取得する。
     *
     * @return アプリ会員一時情報
     */
    public A111AACRBodyForm getAplMemTempInfo() {
        return aplMemTempInfo;
    }

    /**
     * アプリ会員一時情報を設定する。
     *
     * @param aplMemTempInfo
     *            アプリ会員一時情報
     */
    @JsonInclude(Include.NON_NULL)
    public void setAplMemTempInfo(A111AACRBodyForm aplMemTempInfo) {
        this.aplMemTempInfo = aplMemTempInfo;
    }

}