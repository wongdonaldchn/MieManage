package com.donald.miem.common.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

/**
 * PartnerMemServiceInfoエンティティクラス
 *
 * @author 張
 * @since 1.0
 */
@Generated("GSP")
@Entity
@Table(name = "PARTNER_MEM_SERVICE_INFO")
public class PartnerMemServiceInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /** partnerMemServiceCtrlIdプロパティ */
    private Long partnerMemServiceCtrlId;

    /** partnerRegistCtrlNumプロパティ */
    private String partnerRegistCtrlNum;

    /** partnerRegistCtrlBrNumプロパティ */
    private String partnerRegistCtrlBrNum;

    /** partnerUserMemCtrlNumプロパティ */
    private String partnerUserMemCtrlNum;

    /** partnerUserMemCtrlBrNumプロパティ */
    private String partnerUserMemCtrlBrNum;

    /** admitStatusDivisionプロパティ */
    private String admitStatusDivision;

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
     * partnerMemServiceCtrlIdを返します。
     *
     * @return partnerMemServiceCtrlId
     */
    @Id
    @GeneratedValue(generator = "PARTNER_MEM_SERVICE_CTRL_ID_SEQ", strategy = GenerationType.AUTO)
    @Column(name = "PARTNER_MEM_SERVICE_CTRL_ID", precision = 10, nullable = false, unique = true)
    public Long getPartnerMemServiceCtrlId() {
        return partnerMemServiceCtrlId;
    }

    /**
     * partnerMemServiceCtrlIdを設定します。
     *
     * @param partnerMemServiceCtrlId
     *            パートナー会員サービス管理ID
     */
    public void setPartnerMemServiceCtrlId(Long partnerMemServiceCtrlId) {
        this.partnerMemServiceCtrlId = partnerMemServiceCtrlId;
    }

    /**
     * partnerRegistCtrlNumを返します。
     *
     * @return partnerRegistCtrlNum
     */
    @Column(name = "PARTNER_REGIST_CTRL_NUM", length = 10, nullable = false, unique = false)
    public String getPartnerRegistCtrlNum() {
        return partnerRegistCtrlNum;
    }

    /**
     * partnerRegistCtrlNumを設定します。
     *
     * @param partnerRegistCtrlNum
     *            パートナー登録者会員管理番号
     */
    public void setPartnerRegistCtrlNum(String partnerRegistCtrlNum) {
        this.partnerRegistCtrlNum = partnerRegistCtrlNum;
    }

    /**
     * partnerRegistCtrlBrNumを返します。
     *
     * @return partnerRegistCtrlBrNum
     */
    @Column(name = "PARTNER_REGIST_CTRL_BR_NUM", length = 3, nullable = false, unique = false)
    public String getPartnerRegistCtrlBrNum() {
        return partnerRegistCtrlBrNum;
    }

    /**
     * partnerRegistCtrlBrNumを設定します。
     *
     * @param partnerRegistCtrlBrNum
     *            パートナー登録者会員管理番号枝番
     */
    public void setPartnerRegistCtrlBrNum(String partnerRegistCtrlBrNum) {
        this.partnerRegistCtrlBrNum = partnerRegistCtrlBrNum;
    }

    /**
     * partnerUserMemCtrlNumを返します。
     *
     * @return partnerUserMemCtrlNum
     */
    @Column(name = "PARTNER_USER_MEM_CTRL_NUM", length = 10, nullable = false, unique = false)
    public String getPartnerUserMemCtrlNum() {
        return partnerUserMemCtrlNum;
    }

    /**
     * partnerUserMemCtrlNumを設定します。
     *
     * @param partnerUserMemCtrlNum
     *            パートナー会員管理番号
     */
    public void setPartnerUserMemCtrlNum(String partnerUserMemCtrlNum) {
        this.partnerUserMemCtrlNum = partnerUserMemCtrlNum;
    }

    /**
     * partnerUserMemCtrlBrNumを返します。
     *
     * @return partnerUserMemCtrlBrNum
     */
    @Column(name = "PARTNER_USER_MEM_CTRL_BR_NUM", length = 3, nullable = false, unique = false)
    public String getPartnerUserMemCtrlBrNum() {
        return partnerUserMemCtrlBrNum;
    }

    /**
     * partnerUserMemCtrlBrNumを設定します。
     *
     * @param partnerUserMemCtrlBrNum
     *            パートナー会員管理番号枝番
     */
    public void setPartnerUserMemCtrlBrNum(String partnerUserMemCtrlBrNum) {
        this.partnerUserMemCtrlBrNum = partnerUserMemCtrlBrNum;
    }

    /**
     * admitStatusDivisionを返します。
     *
     * @return admitStatusDivision
     */
    @Column(name = "ADMIT_STATUS_DIVISION", length = 1, nullable = false, unique = false)
    public String getAdmitStatusDivision() {
        return admitStatusDivision;
    }

    /**
     * admitStatusDivisionを設定します。
     *
     * @param admitStatusDivision
     *            承認状況区分
     */
    public void setAdmitStatusDivision(String admitStatusDivision) {
        this.admitStatusDivision = admitStatusDivision;
    }

    /**
     * applyStartDateTimeを返します。
     *
     * @return applyStartDateTime
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "APPLY_START_DATE_TIME", nullable = true, unique = false)
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
