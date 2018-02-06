package jp.co.tis.opal.batch.ss114A;

import nablarch.core.validation.ee.Domain;
import nablarch.core.validation.ee.Required;

/**
 * OP会員情報のバリデーションに使用するフォームクラス。
 *
 * @author 趙
 * @since 1.0
 */
public class A114A001DataForm {

    /**
     * 会員管理番号
     */
    @Domain("memCtrlNum")
    @Required(message = "{M000000001}")
    private String memberControlNumber;

    /**
     * 会員管理番号枝番
     */
    @Domain("memrCtrlBrNum")
    @Required(message = "{M000000001}")
    private String memrCtrlBrNum;

    /**
     * OP番号
     */
    @Domain("opNum")
    @Required(message = "{M000000001}")
    private String osakaPitapaNumber;

    /**
     * データ連携区分
     */
    @Domain("dataRelateDivision")
    @Required(message = "{M000000001}")
    private String dataRelateDivision;

    /**
     * PiTaPa有効期限
     */
    @Domain("yearMonth")
    @Required(message = "{M000000001}")
    private String pitapaExpirationDate;

    /**
     * カード種類
     */
    @Domain("cardType")
    @Required(message = "{M000000001}")
    private String cardType;

    /**
     * 生年月日
     */
    @Domain("date")
    @Required(message = "{M000000001}")
    private String birthdate;

    /**
     * 性別コード
     */
    @Domain("sexCode")
    @Required(message = "{M000000001}")
    private String sexCode;

    /**
     * 自宅電話番号
     */
    @Domain("telephoneNumber")
    private String telephoneNumber;

    /**
     * 携帯電話番号
     */
    @Domain("telephoneNumber")
    private String cellphoneNumber;

    /**
     * 郵便番号
     */
    @Domain("postcode")
    private String postcode;

    /**
     * サービス種別
     */
    @Domain("serviceCategory")
    @Required(message = "{M000000001}")
    private String serviceCategory;

    /**
     * 登録駅1
     */
    @Domain("stationId")
    private String registStation1;

    /**
     * 登録駅2
     */
    @Domain("stationId")
    private String registStation2;

    /**
     * 続柄コード
     */
    @Domain("relationshipCode")
    @Required(message = "{M000000001}")
    private String relationshipCode;

    /**
     * 会員管理番号を返します。
     *
     * @return 会員管理番号
     */
    public String getMemberControlNumber() {
        return memberControlNumber;
    }

    /**
     * 会員管理番号を設定します。
     *
     * @param memberControlNumber
     *            会員管理番号
     */
    public void setMemberControlNumber(String memberControlNumber) {
        this.memberControlNumber = memberControlNumber;
    }

    /**
     * 会員管理番号枝番を返します。
     *
     * @return 会員管理番号枝番
     */
    public String getMemrCtrlBrNum() {
        return memrCtrlBrNum;
    }

    /**
     * 会員管理番号枝番を設定します。
     *
     * @param memrCtrlBrNum
     *            会員管理番号枝番
     */
    public void setMemrCtrlBrNum(String memrCtrlBrNum) {
        this.memrCtrlBrNum = memrCtrlBrNum;
    }

    /**
     * OP番号を返します。
     *
     * @return OP番号
     */
    public String getOsakaPitapaNumber() {
        return osakaPitapaNumber;
    }

    /**
     * OP番号を設定します。
     *
     * @param osakaPitapaNumber
     *            OP番号
     */
    public void setOsakaPitapaNumber(String osakaPitapaNumber) {
        this.osakaPitapaNumber = osakaPitapaNumber;
    }

    /**
     * データ連携区分を返します。
     *
     * @return データ連携区分
     */
    public String getDataRelateDivision() {
        return dataRelateDivision;
    }

    /**
     * データ連携区分を設定します。
     *
     * @param dataRelateDivision
     *            データ連携区分
     */
    public void setDataRelateDivision(String dataRelateDivision) {
        this.dataRelateDivision = dataRelateDivision;
    }

    /**
     * PiTaPa有効期限を返します。
     *
     * @return PiTaPa有効期限
     */
    public String getPitapaExpirationDate() {
        return pitapaExpirationDate;
    }

    /**
     * PiTaPa有効期限を設定します。
     *
     * @param pitapaExpirationDate
     *            PiTaPa有効期限
     */
    public void setPitapaExpirationDate(String pitapaExpirationDate) {
        this.pitapaExpirationDate = pitapaExpirationDate;
    }

    /**
     * カード種類を返します。
     *
     * @return カード種類
     */
    public String getCardType() {
        return cardType;
    }

    /**
     * カード種類を設定します。
     *
     * @param cardType
     *            カード種類
     */
    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    /**
     * 生年月日を返します。
     *
     * @return 生年月日
     */
    public String getBirthdate() {
        return birthdate;
    }

    /**
     * 生年月日を設定します。
     *
     * @param birthdate
     *            生年月日
     */
    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    /**
     * 性別コードを返します。
     *
     * @return 性別コード
     */
    public String getSexCode() {
        return sexCode;
    }

    /**
     * 性別コードを設定します。
     *
     * @param sexCode
     *            性別コード
     */
    public void setSexCode(String sexCode) {
        this.sexCode = sexCode;
    }

    /**
     * 自宅電話番号を返します。
     *
     * @return 自宅電話番号
     */
    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    /**
     * 自宅電話番号を設定します。
     *
     * @param telephoneNumber
     *            自宅電話番号
     */
    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }

    /**
     * 携帯電話番号を返します。
     *
     * @return 携帯電話番号
     */
    public String getCellphoneNumber() {
        return cellphoneNumber;
    }

    /**
     * 携帯電話番号を設定します。
     *
     * @param cellphoneNumber
     *            携帯電話番号
     */
    public void setCellphoneNumber(String cellphoneNumber) {
        this.cellphoneNumber = cellphoneNumber;
    }

    /**
     * 郵便番号を返します。
     *
     * @return 郵便番号
     */
    public String getPostcode() {
        return postcode;
    }

    /**
     * 郵便番号を設定します。
     *
     * @param postcode
     *            郵便番号
     */
    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    /**
     * サービス種別を返します。
     *
     * @return サービス種別
     */
    public String getServiceCategory() {
        return serviceCategory;
    }

    /**
     * サービス種別を設定します。
     *
     * @param serviceCategory
     *            サービス種別
     */
    public void setServiceCategory(String serviceCategory) {
        this.serviceCategory = serviceCategory;
    }

    /**
     * 登録駅1を返します。
     *
     * @return 登録駅1
     */
    public String getRegistStation1() {
        return registStation1;
    }

    /**
     * 登録駅1を設定します。
     *
     * @param registStation1
     *            登録駅1
     */
    public void setRegistStation1(String registStation1) {
        this.registStation1 = registStation1;
    }

    /**
     * 登録駅2を返します。
     *
     * @return 登録駅2
     */
    public String getRegistStation2() {
        return registStation2;
    }

    /**
     * 登録駅2を設定します。
     *
     * @param registStation2
     *            登録駅2
     */
    public void setRegistStation2(String registStation2) {
        this.registStation2 = registStation2;
    }

    /**
     * 続柄コードを返します。
     *
     * @return 続柄コード
     */
    public String getRelationshipCode() {
        return relationshipCode;
    }

    /**
     * 続柄コードを設定します。
     *
     * @param relationshipCode
     *            続柄コード
     */
    public void setRelationshipCode(String relationshipCode) {
        this.relationshipCode = relationshipCode;
    }
}
