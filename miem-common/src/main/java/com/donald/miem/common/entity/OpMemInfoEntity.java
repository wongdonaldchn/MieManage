package com.donald.miem.common.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

/**
 * OpMemInfoエンティティクラス
 *
 * @author 張
 * @since 1.0
 */
@Generated("GSP")
@Entity
@Table(name = "OP_MEM_INFO")
public class OpMemInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /** memberControlNumberプロパティ */
    private String memberControlNumber;

    /** memCtrlNumBrNumプロパティ */
    private String memCtrlNumBrNum;

    /** osakaPitapaNumberプロパティ */
    private String osakaPitapaNumber;

    /** pitapaExpirationDateプロパティ */
    private String pitapaExpirationDate;

    /** oldPitapaExpirationDateプロパティ */
    private String oldPitapaExpirationDate;

    /** cardTypeプロパティ */
    private String cardType;

    /** birthdateプロパティ */
    private String birthdate;

    /** sexCodeプロパティ */
    private String sexCode;

    /** telephoneNumberプロパティ */
    private String telephoneNumber;

    /** cellphoneNumberプロパティ */
    private String cellphoneNumber;

    /** postcodeプロパティ */
    private String postcode;

    /** serviceCategoryサービス種別 */
    private String serviceCategory;

    /** registStation1プロパティ */
    private String registStation1;

    /** registStation2プロパティ */
    private String registStation2;

    /** relationshipCodeプロパティ */
    private String relationshipCode;

    /** osakaPitapaWithdrawFlagプロパティ */
    private String osakaPitapaWithdrawFlag;

    /** insertUserIdプロパティ */
    private String insertUserId;

    /** insertDateTimeプロパティ */
    private Timestamp insertDateTime;

    /** updateUserIdプロパティ */
    private String updateUserId;

    /** updateDateTimeプロパティ */
    private Timestamp updateDateTime;

    /** deletedFlgプロパティ */
    private String deletedFlg;

    /** deletedDateプロパティ */
    private String deletedDate;

    /** versionプロパティ */
    private Long version;

    /** aplMemInfoList関連プロパティ */
    private List<AplMemInfoEntity> aplMemInfoList;

    /**
     * memberControlNumberを返します。
     *
     * @return memberControlNumber
     */
    @Id
    @Column(name = "MEMBER_CONTROL_NUMBER", length = 10, nullable = false, unique = false)
    public String getMemberControlNumber() {
        return memberControlNumber;
    }

    /**
     * memberControlNumberを設定します。
     *
     * @param memberControlNumber
     *            会員管理番号
     */
    public void setMemberControlNumber(String memberControlNumber) {
        this.memberControlNumber = memberControlNumber;
    }

    /**
     * memCtrlNumBrNumを返します。
     *
     * @return memCtrlNumBrNum
     */
    @Id
    @Column(name = "MEM_CTRL_NUM_BR_NUM", length = 3, nullable = false, unique = false)
    public String getMemCtrlNumBrNum() {
        return memCtrlNumBrNum;
    }

    /**
     * memCtrlNumBrNumを設定します。
     *
     * @param memCtrlNumBrNum
     *            会員管理番号枝番
     */
    public void setMemCtrlNumBrNum(String memCtrlNumBrNum) {
        this.memCtrlNumBrNum = memCtrlNumBrNum;
    }

    /**
     * osakaPitapaNumberを返します。
     *
     * @return osakaPitapaNumber
     */
    @Column(name = "OSAKA_PITAPA_NUMBER", length = 10, nullable = false, unique = false)
    public String getOsakaPitapaNumber() {
        return osakaPitapaNumber;
    }

    /**
     * osakaPitapaNumberを設定します。
     *
     * @param osakaPitapaNumber
     *            OP番号
     */
    public void setOsakaPitapaNumber(String osakaPitapaNumber) {
        this.osakaPitapaNumber = osakaPitapaNumber;
    }

    /**
     * pitapaExpirationDateを返します。
     *
     * @return pitapaExpirationDate
     */
    @Column(name = "PITAPA_EXPIRATION_DATE", length = 6, nullable = false, unique = false)
    public String getPitapaExpirationDate() {
        return pitapaExpirationDate;
    }

    /**
     * pitapaExpirationDateを設定します。
     *
     * @param pitapaExpirationDate
     *            PiTaPa有効期限
     */
    public void setPitapaExpirationDate(String pitapaExpirationDate) {
        this.pitapaExpirationDate = pitapaExpirationDate;
    }

    /**
     * oldPitapaExpirationDateを設定します。
     *
     * @param oldPitapaExpirationDate
     *            更新前PiTaPa有効期限
     */
    public void setOldPitapaExpirationDate(String oldPitapaExpirationDate) {
        this.oldPitapaExpirationDate = oldPitapaExpirationDate;
    }

    /**
     * oldPitapaExpirationDateを返します。
     *
     * @return oldPitapaExpirationDate
     */
    @Column(name = "OLD_PITAPA_EXPIRATION_DATE", length = 6, nullable = true, unique = false)
    public String getOldPitapaExpirationDate() {
        return oldPitapaExpirationDate;
    }

    /**
     * cardTypeを返します。
     *
     * @return cardType
     */
    @Column(name = "CARD_TYPE", length = 4, nullable = false, unique = false)
    public String getCardType() {
        return cardType;
    }

    /**
     * cardTypeを設定します。
     *
     * @param cardType
     *            カード種類
     */
    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    /**
     * birthdateを返します。
     *
     * @return birthdate
     */
    @Column(name = "BIRTHDATE", length = 8, nullable = false, unique = false)
    public String getBirthdate() {
        return birthdate;
    }

    /**
     * birthdateを設定します。
     *
     * @param birthdate
     *            生年月日
     */
    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    /**
     * sexCodeを返します。
     *
     * @return sexCode
     */
    @Column(name = "SEX_CODE", length = 1, nullable = false, unique = false)
    public String getSexCode() {
        return sexCode;
    }

    /**
     * sexCodeを設定します。
     *
     * @param sexCode
     *            性別コード
     */
    public void setSexCode(String sexCode) {
        this.sexCode = sexCode;
    }

    /**
     * telephoneNumberを返します。
     *
     * @return telephoneNumber
     */
    @Column(name = "TELEPHONE_NUMBER", length = 15, nullable = true, unique = false)
    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    /**
     * telephoneNumberを設定します。
     *
     * @param telephoneNumber
     *            自宅電話番号
     */
    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }

    /**
     * cellphoneNumberを返します。
     *
     * @return cellphoneNumber
     */
    @Column(name = "CELLPHONE_NUMBER", length = 15, nullable = true, unique = false)
    public String getCellphoneNumber() {
        return cellphoneNumber;
    }

    /**
     * cellphoneNumberを設定します。
     *
     * @param cellphoneNumber
     *            携帯電話番号
     */
    public void setCellphoneNumber(String cellphoneNumber) {
        this.cellphoneNumber = cellphoneNumber;
    }

    /**
     * postcodeを返します。
     *
     * @return postcode
     */
    @Column(name = "POSTCODE", length = 7, nullable = true, unique = false)
    public String getPostcode() {
        return postcode;
    }

    /**
     * postcodeを設定します。
     *
     * @param postcode
     *            郵便番号
     */
    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    /**
     * serviceCategoryを返します。
     *
     * @return serviceCategory
     */
    @Column(name = "SERVICE_CATEGORY", length = 1, nullable = false, unique = false)
    public String getServiceCategory() {
        return serviceCategory;
    }

    /**
     * serviceCategoryを設定します。
     *
     * @param serviceCategory
     *            サービス種別
     */
    public void setServiceCategory(String serviceCategory) {
        this.serviceCategory = serviceCategory;
    }

    /**
     * registStation1を返します。
     *
     * @return registStation1
     */
    @Column(name = "REGIST_STATION_1", length = 3, nullable = true, unique = false)
    public String getRegistStation1() {
        return registStation1;
    }

    /**
     * registStation1を設定します。
     *
     * @param registStation1
     *            登録駅1
     */
    public void setRegistStation1(String registStation1) {
        this.registStation1 = registStation1;
    }

    /**
     * registStation2を返します。
     *
     * @return registStation2
     */
    @Column(name = "REGIST_STATION_2", length = 3, nullable = true, unique = false)
    public String getRegistStation2() {
        return registStation2;
    }

    /**
     * registStation2を設定します。
     *
     * @param registStation2
     *            登録駅2
     */
    public void setRegistStation2(String registStation2) {
        this.registStation2 = registStation2;
    }

    /**
     * relationshipCodeを返します。
     *
     * @return relationshipCode
     */
    @Column(name = "RELATIONSHIP_CODE", length = 1, nullable = false, unique = false)
    public String getRelationshipCode() {
        return relationshipCode;
    }

    /**
     * relationshipCodeを設定します。
     *
     * @param relationshipCode
     *            続柄コード
     */
    public void setRelationshipCode(String relationshipCode) {
        this.relationshipCode = relationshipCode;
    }

    /**
     * osakaPitapaWithdrawFlagを返します。
     *
     * @return osakaPitapaWithdrawFlag
     */
    @Column(name = "OSAKA_PITAPA_WITHDRAW_FLAG", length = 1, nullable = false, unique = false)
    public String getOsakaPitapaWithdrawFlag() {
        return osakaPitapaWithdrawFlag;
    }

    /**
     * osakaPitapaWithdrawFlagを設定します。
     *
     * @param osakaPitapaWithdrawFlag
     *            OP退会フラグ
     */
    public void setOsakaPitapaWithdrawFlag(String osakaPitapaWithdrawFlag) {
        this.osakaPitapaWithdrawFlag = osakaPitapaWithdrawFlag;
    }

    /**
     * insertUserIdを返します。
     *
     * @return insertUserId
     */
    @Column(name = "INSERT_USER_ID", length = 20, nullable = false, unique = false)
    public String getInsertUserId() {
        return insertUserId;
    }

    /**
     * insertUserIdを設定します。
     *
     * @param insertUserId
     *            登録者ID
     */
    public void setInsertUserId(String insertUserId) {
        this.insertUserId = insertUserId;
    }

    /**
     * insertDateTimeを返します。
     *
     * @return insertDateTime
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "INSERT_DATE_TIME", nullable = false, unique = false)
    public Timestamp getInsertDateTime() {
        return insertDateTime;
    }

    /**
     * insertDateTimeを設定します。
     *
     * @param insertDateTime
     *            登録日時
     */
    public void setInsertDateTime(Timestamp insertDateTime) {
        this.insertDateTime = insertDateTime;
    }

    /**
     * updateUserIdを返します。
     *
     * @return updateUserId
     */
    @Column(name = "UPDATE_USER_ID", length = 20, nullable = false, unique = false)
    public String getUpdateUserId() {
        return updateUserId;
    }

    /**
     * updateUserIdを設定します。
     *
     * @param updateUserId
     *            最終更新者ID
     */
    public void setUpdateUserId(String updateUserId) {
        this.updateUserId = updateUserId;
    }

    /**
     * updateDateTimeを返します。
     *
     * @return updateDateTime
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UPDATE_DATE_TIME", nullable = false, unique = false)
    public Timestamp getUpdateDateTime() {
        return updateDateTime;
    }

    /**
     * updateDateTimeを設定します。
     *
     * @param updateDateTime
     *            最終更新日時
     */
    public void setUpdateDateTime(Timestamp updateDateTime) {
        this.updateDateTime = updateDateTime;
    }

    /**
     * deletedFlgを返します。
     *
     * @return deletedFlg
     */
    @Column(name = "DELETED_FLG", length = 1, nullable = false, unique = false)
    public String getDeletedFlg() {
        return deletedFlg;
    }

    /**
     * deletedFlgを設定します。
     *
     * @param deletedFlg
     *            削除フラグ
     */
    public void setDeletedFlg(String deletedFlg) {
        this.deletedFlg = deletedFlg;
    }

    /**
     * deletedDateを返します。
     *
     * @return deletedDate
     */
    @Column(name = "DELETED_DATE", length = 8, nullable = true, unique = false)
    public String getDeletedDate() {
        return deletedDate;
    }

    /**
     * deletedDateを設定します。
     *
     * @param deletedDate
     *            論理削除日
     */
    public void setDeletedDate(String deletedDate) {
        this.deletedDate = deletedDate;
    }

    /**
     * versionを返します。
     *
     * @return version
     */
    @Version
    @Column(name = "VERSION", precision = 10, nullable = false, unique = false)
    public Long getVersion() {
        return version;
    }

    /**
     * versionを設定します。
     *
     * @param version
     *            バージョン番号
     */
    public void setVersion(Long version) {
        this.version = version;
    }

    /**
     * aplMemInfoListを返します。
     *
     * @return aplMemInfoList
     */
    @OneToMany(mappedBy = "opMemInfo")
    public List<AplMemInfoEntity> getAplMemInfoList() {
        return aplMemInfoList;
    }

    /**
     * aplMemInfoListを設定します。
     *
     * @param aplMemInfoList
     *            aplMemInfoList
     */
    public void setAplMemInfoList(List<AplMemInfoEntity> aplMemInfoList) {
        this.aplMemInfoList = aplMemInfoList;
    }
}
