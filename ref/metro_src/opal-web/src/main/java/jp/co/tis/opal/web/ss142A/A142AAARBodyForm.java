package jp.co.tis.opal.web.ss142A;

import java.io.Serializable;

import javax.validation.constraints.AssertTrue;

import com.fasterxml.jackson.annotation.JsonIgnore;

import nablarch.core.util.StringUtil;
import nablarch.core.validation.ee.Domain;
import nablarch.core.validation.ee.Required;

/**
 * A142A01:郵送情報登録フォーム。
 *
 * @author 陳
 * @since 1.0
 */
public class A142AAARBodyForm implements Serializable {

    /** シリアルバージョンUID */
    private static final long serialVersionUID = 1L;

    /** アプリ会員ID */
    @Domain("applicationMemberId")
    @Required(message = "{M000000001}")
    private String applicationMemberId;

    /** 郵送種別 */
    @Domain("postCategory")
    @Required(message = "{M000000001}")
    private String postCategory;

    /** 郵送管理番号 */
    @Domain("postControlNumber")
    @Required(message = "{M000000001}")
    private String postControlNumber;

    /** 郵便番号 */
    @Domain("postcode")
    @Required(message = "{M000000001}")
    private String postcode;

    /** 住所 */
    @Domain("address")
    @Required(message = "{M000000001}")
    private String address;

    /** 氏名 */
    @Domain("name")
    @Required(message = "{M000000001}")
    private String name;

    /** 電話番号 */
    @Domain("telephoneNumber")
    @Required(message = "{M000000001}")
    private String telephoneNumber;

    /** その他 */
    @Domain("other")
    private String other;

    /** マイル加算減算受付番号 */
    @Domain("mileAddSubRcptNo")
    private String mileAddSubRcptNo;

    /** マイル種別コード */
    @Domain("mileCategoryCode")
    private String mileCategoryCode;

    /** 減算マイル数 */
    @Domain("mileAmount")
    private String subMileAmount;

    /**
     * マイル加算減算受付番号、マイル種別コード、減算マイル数間の設定内容整合性について精査を実施する。
     *
     * @return 処理結果（エラーある：true エラーなし：false）
     */
    @JsonIgnore
    @AssertTrue(message = "{MA142A0101}")
    public boolean isMileInfoCheck() {
        if (StringUtil.isNullOrEmpty(mileAddSubRcptNo) && StringUtil.isNullOrEmpty(mileCategoryCode)
                && StringUtil.isNullOrEmpty(subMileAmount)
                || !StringUtil.isNullOrEmpty(mileAddSubRcptNo) && !StringUtil.isNullOrEmpty(mileCategoryCode)
                        && !StringUtil.isNullOrEmpty(subMileAmount)) {
            return true;
        }

        return false;
    }

    /**
     * アプリ会員IDを取得する。
     *
     * @return アプリ会員ID
     */
    public String getApplicationMemberId() {
        return applicationMemberId;
    }

    /**
     * 郵送種別を取得する。
     *
     * @return 郵送種別
     */
    public String getPostCategory() {
        return postCategory;
    }

    /**
     * 郵送管理番号を取得する。
     *
     * @return 郵送管理番号
     */
    public String getPostControlNumber() {
        return postControlNumber;
    }

    /**
     * 郵便番号を取得する。
     *
     * @return 郵便番号
     */
    public String getPostcode() {
        return postcode;
    }

    /**
     * 住所を取得する。
     *
     * @return 住所
     */
    public String getAddress() {
        return address;
    }

    /**
     * 氏名を取得する。
     *
     * @return 氏名
     */
    public String getName() {
        return name;
    }

    /**
     * 電話番号を取得する。
     *
     * @return 電話番号
     */
    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    /**
     * その他を取得する。
     *
     * @return その他
     */
    public String getOther() {
        return other;
    }

    /**
     * マイル加算減算受付番号を取得する。
     *
     * @return マイル加算減算受付番号
     */
    public String getMileAddSubRcptNo() {
        return mileAddSubRcptNo;
    }

    /**
     * マイル種別コードを取得する。
     *
     * @return マイル種別コード
     */
    public String getMileCategoryCode() {
        return mileCategoryCode;
    }

    /**
     * 減算マイル数を取得する。
     *
     * @return 減算マイル数
     */
    public String getSubMileAmount() {
        return subMileAmount;
    }

    /**
     * アプリ会員IDを設定する。
     *
     * @param applicationMemberId
     *            アプリ会員ID
     */
    public void setApplicationMemberId(String applicationMemberId) {
        this.applicationMemberId = applicationMemberId;
    }

    /**
     * 郵送種別を設定する。
     *
     * @param postCategory
     *            郵送種別
     */
    public void setPostCategory(String postCategory) {
        this.postCategory = postCategory;
    }

    /**
     * 郵送管理番号を設定する。
     *
     * @param postControlNumber
     *            郵送管理番号
     */
    public void setPostControlNumber(String postControlNumber) {
        this.postControlNumber = postControlNumber;
    }

    /**
     * 郵便番号を設定する。
     *
     * @param postcode
     *            郵便番号
     */
    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    /**
     * 住所を設定する。
     *
     * @param address
     *            住所
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * 氏名を設定する。
     *
     * @param name
     *            氏名
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 電話番号を設定する。
     *
     * @param telephoneNumber
     *            電話番号
     */
    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }

    /**
     * その他を設定する。
     *
     * @param other
     *            その他
     */
    public void setOther(String other) {
        this.other = other;
    }

    /**
     * マイル加算減算受付番号を設定する。
     *
     * @param mileAddSubRcptNo
     *            マイル加算減算受付番号
     */
    public void setMileAddSubRcptNo(String mileAddSubRcptNo) {
        this.mileAddSubRcptNo = mileAddSubRcptNo;
    }

    /**
     * マイル種別コードを設定する。
     *
     * @param mileCategoryCode
     *            マイル種別コード
     */
    public void setMileCategoryCode(String mileCategoryCode) {
        this.mileCategoryCode = mileCategoryCode;
    }

    /**
     * 減算マイル数を設定する。
     *
     * @param subMileAmount
     *            減算マイル数
     */
    public void setSubMileAmount(String subMileAmount) {
        this.subMileAmount = subMileAmount;
    }
}