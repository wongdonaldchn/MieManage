package jp.co.tis.opal.web.ss142A;

import java.io.Serializable;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import nablarch.core.validation.ee.Required;

import jp.co.tis.opal.web.common.RequestData.RequestOpalControlData;

/**
 * 郵送情報登録要求電文
 *
 * @author 陳
 * @since 1.0
 */
public class A142AAARRequestData implements Serializable {

    /** シリアルバージョンUID */
    private static final long serialVersionUID = 1L;

    /** OPAL制御データ */
    @Valid
    @Required(message = "{M000000001}")
    private RequestOpalControlData opalControlData;

    /** アプリケーションデータ */
    @Valid
    @Required(message = "{M000000001}")
    private A142AAARBodyForm postInfoData;

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
    public A142AAARBodyForm getPostInfoData() {
        return postInfoData;
    }

    /**
     * アプリケーションデータを設定する。
     *
     * @param postInfoData
     *            アプリケーションデータ
     */
    @JsonInclude(Include.NON_NULL)
    public void setPostInfoData(A142AAARBodyForm postInfoData) {
        this.postInfoData = postInfoData;
    }

}