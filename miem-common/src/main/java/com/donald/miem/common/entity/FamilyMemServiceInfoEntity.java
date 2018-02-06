package com.donald.miem.common.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

/**
 * FamilyMemServiceInfoエンティティクラス
 *
 * @author 張
 * @since 1.0
 */
@Generated("GSP")
@Entity
@Table(name = "FAMILY_MEM_SERVICE_INFO")
public class FamilyMemServiceInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /** familyMemServiceCtrlIdプロパティ */
    private Long familyMemServiceCtrlId;

    /** memberControlNumberプロパティ */
    private String memberControlNumber;

    /** applicantMemCtrlNumBrNumプロパティ */
    private String applicantMemCtrlNumBrNum;

    /** rcptDateTimeプロパティ */
    private Date rcptDateTime;

    /** registStatusDivisionプロパティ */
    private String registStatusDivision;

    /** applyStartDateTimeプロパティ */
    private Date applyStartDateTime;

    /** applyEndDateTimeプロパティ */
    private Date applyEndDateTime;

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

    /**
     * familyMemServiceCtrlIdを返します。
     *
     * @return familyMemServiceCtrlId
     */
    @Id
    @Column(name = "FAMILY_MEM_SERVICE_CTRL_ID", precision = 10, nullable = false, unique = true)
    public Long getFamilyMemServiceCtrlId() {
        return familyMemServiceCtrlId;
    }

    /**
     * familyMemServiceCtrlIdを設定します。
     *
     * @param familyMemServiceCtrlId
     *            家族会員サービス管理ID
     */
    public void setFamilyMemServiceCtrlId(Long familyMemServiceCtrlId) {
        this.familyMemServiceCtrlId = familyMemServiceCtrlId;
    }

    /**
     * memberControlNumberを返します。
     *
     * @return memberControlNumber
     */
    @Column(name = "MEMBER_CONTROL_NUMBER", length = 10, nullable = false, unique = false)
    public String getMemberControlNumber() {
        return memberControlNumber;
    }

    /**
     * applicantMemCtrlNumBrNumを設定します。
     *
     * @param applicantMemCtrlNumBrNum
     *            申込者会員管理番号枝番
     */
    public void setApplicantMemCtrlNumBrNum(String applicantMemCtrlNumBrNum) {
        this.applicantMemCtrlNumBrNum = applicantMemCtrlNumBrNum;
    }

    /**
     * applicantMemCtrlNumBrNumを返します。
     *
     * @return applicantMemCtrlNumBrNum
     */
    @Column(name = "APPLICANT_MEM_CTRL_NUM_BR_NUM", length = 3, nullable = false, unique = false)
    public String getApplicantMemCtrlNumBrNum() {
        return applicantMemCtrlNumBrNum;
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
     * rcptDateTimeを返します。
     *
     * @return rcptDateTime
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "RCPT_DATE_TIME", nullable = false, unique = false)
    public Date getRcptDateTime() {
        return rcptDateTime;
    }

    /**
     * rcptDateTimeを設定します。
     *
     * @param rcptDateTime
     *            受付日時
     */
    public void setRcptDateTime(Date rcptDateTime) {
        this.rcptDateTime = rcptDateTime;
    }

    /**
     * registStatusDivisionを返します。
     *
     * @return registStatusDivision
     */
    @Column(name = "REGIST_STATUS_DIVISION", length = 1, nullable = false, unique = false)
    public String getRegistStatusDivision() {
        return registStatusDivision;
    }

    /**
     * registStatusDivisionを設定します。
     *
     * @param registStatusDivision
     *            登録状況区分
     */
    public void setRegistStatusDivision(String registStatusDivision) {
        this.registStatusDivision = registStatusDivision;
    }

    /**
     * applyStartDateTimeを返します。
     *
     * @return applyStartDateTime
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "APPLY_START_DATE_TIME", nullable = false, unique = false)
    public Date getApplyStartDateTime() {
        return applyStartDateTime;
    }

    /**
     * applyStartDateTimeを設定します。
     *
     * @param applyStartDateTime
     *            適用開始日時
     */
    public void setApplyStartDateTime(Date applyStartDateTime) {
        this.applyStartDateTime = applyStartDateTime;
    }

    /**
     * applyEndDateTimeを返します。
     *
     * @return applyEndDateTime
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "APPLY_END_DATE_TIME", nullable = true, unique = false)
    public Date getApplyEndDateTime() {
        return applyEndDateTime;
    }

    /**
     * applyEndDateTimeを設定します。
     *
     * @param applyEndDateTime
     *            適用終了日時
     */
    public void setApplyEndDateTime(Date applyEndDateTime) {
        this.applyEndDateTime = applyEndDateTime;
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
}
