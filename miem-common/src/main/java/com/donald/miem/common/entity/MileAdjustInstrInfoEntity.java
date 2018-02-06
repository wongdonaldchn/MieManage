package com.donald.miem.common.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

/**
 * MileAdjustInstrInfoエンティティクラス
 *
 * @author 張
 * @since 1.0
 */
@Generated("GSP")
@Entity
@Table(name = "MILE_ADJUST_INSTR_INFO")
public class MileAdjustInstrInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /** mileAdjustInstrIdプロパティ */
    private Long mileAdjustInstrId;

    /** applicationMemberIdプロパティ */
    private Long applicationMemberId;

    /** adjustMileAmountプロパティ */
    private Integer adjustMileAmount;

    /** mileAdjustInstrDateプロパティ */
    private String mileAdjustInstrDate;

    /** mileCategoryCodeプロパティ */
    private String mileCategoryCode;

    /** mileAdjustStatusDivプロパティ */
    private String mileAdjustStatusDivi;

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

    /** aplMemInfo関連プロパティ */
    private AplMemInfoEntity aplMemInfo;

    /**
     * mileAdjustInstrIdを返します。
     *
     * @return mileAdjustInstrId
     */
    @Id
    @GeneratedValue(generator = "MILE_ADJUST_INSTR_ID_SEQ", strategy = GenerationType.AUTO)
    @SequenceGenerator(name = "MILE_ADJUST_INSTR_ID_SEQ", sequenceName = "MILE_ADJUST_INSTR_ID_SEQ", initialValue = 1, allocationSize = 1)
    @Column(name = "MILE_ADJUST_INSTR_ID", precision = 10, nullable = false, unique = true)
    public Long getMileAdjustInstrId() {
        return mileAdjustInstrId;
    }

    /**
     * mileAdjustInstrIdを設定します。
     *
     * @param mileAdjustInstrId
     *            マイル調整指示ID
     */
    public void setMileAdjustInstrId(Long mileAdjustInstrId) {
        this.mileAdjustInstrId = mileAdjustInstrId;
    }

    /**
     * applicationMemberIdを返します。
     *
     * @return applicationMemberId
     */
    @Column(name = "APPLICATION_MEMBER_ID", precision = 10, nullable = false, unique = false, insertable = false, updatable = false)
    public Long getApplicationMemberId() {
        return applicationMemberId;
    }

    /**
     * applicationMemberIdを設定します。
     *
     * @param applicationMemberId
     *            アプリ会員ID
     */
    public void setApplicationMemberId(Long applicationMemberId) {
        this.applicationMemberId = applicationMemberId;
    }

    /**
     * adjustMileAmountを返します。
     *
     * @return adjustMileAmount
     */
    @Column(name = "ADJUST_MILE_AMOUNT", precision = 7, nullable = false, unique = false)
    public Integer getAdjustMileAmount() {
        return adjustMileAmount;
    }

    /**
     * adjustMileAmountを設定します。
     *
     * @param adjustMileAmount
     *            調整マイル数
     */
    public void setAdjustMileAmount(Integer adjustMileAmount) {
        this.adjustMileAmount = adjustMileAmount;
    }

    /**
     * mileAdjustInstrDateを返します。
     *
     * @return mileAdjustInstrDate
     */
    @Column(name = "MILE_ADJUST_INSTR_DATE", length = 8, nullable = false, unique = false)
    public String getMileAdjustInstrDate() {
        return mileAdjustInstrDate;
    }

    /**
     * mileAdjustInstrDateを設定します。
     *
     * @param mileAdjustInstrDate
     *            マイル調整指示日
     */
    public void setMileAdjustInstrDate(String mileAdjustInstrDate) {
        this.mileAdjustInstrDate = mileAdjustInstrDate;
    }

    /**
     * mileCategoryCodeを返します。
     *
     * @return mileCategoryCode
     */
    @Column(name = "MILE_CATEGORY_CODE", length = 2, nullable = false, unique = false)
    public String getMileCategoryCode() {
        return mileCategoryCode;
    }

    /**
     * mileCategoryCodeを設定します。
     *
     * @param mileCategoryCode
     *            マイル種別コード
     */
    public void setMileCategoryCode(String mileCategoryCode) {
        this.mileCategoryCode = mileCategoryCode;
    }

    /**
     * mileAdjustStatusDivを返します。
     *
     * @return mileAdjustStatusDivi
     */
    @Column(name = "MILE_ADJUST_STATUS_DIVI", length = 1, nullable = false, unique = false)
    public String getMileAdjustStatusDivi() {
        return mileAdjustStatusDivi;
    }

    /**
     * mileAdjustStatusDivを設定します。
     *
     * @param mileAdjustStatusDivi
     *            マイル調整状況区分
     */
    public void setMileAdjustStatusDivi(String mileAdjustStatusDivi) {
        this.mileAdjustStatusDivi = mileAdjustStatusDivi;
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
     * aplMemInfoを返します。
     *
     * @return aplMemInfo
     */
    @ManyToOne
    @JoinColumn(name = "APPLICATION_MEMBER_ID", referencedColumnName = "APPLICATION_MEMBER_ID")
    public AplMemInfoEntity getAplMemInfo() {
        return aplMemInfo;
    }

    /**
     * aplMemInfoを設定します。
     *
     * @param aplMemInfo
     *            aplMemInfo
     */
    public void setAplMemInfo(AplMemInfoEntity aplMemInfo) {
        this.aplMemInfo = aplMemInfo;
    }
}
