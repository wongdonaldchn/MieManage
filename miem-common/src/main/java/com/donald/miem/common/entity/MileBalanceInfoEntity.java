package com.donald.miem.common.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

/**
 * MileBalanceInfoエンティティクラス
 *
 * @author 張
 * @since 1.0
 */
@Generated("GSP")
@Entity
@Table(name = "MILE_BALANCE_INFO")
public class MileBalanceInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /** applicationMemberIdプロパティ */
    private Long applicationMemberId;

    /** objectYearMonthプロパティ */
    private String objectYearMonth;

    /** mileBalanceプロパティ */
    private Integer mileBalance;

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
     * applicationMemberIdを返します。
     *
     * @return applicationMemberId
     */
    @Id
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
     * objectYearMonthを返します。
     *
     * @return 対象年月
     */
    @Id
    @Column(name = "OBJECT_YEAR_MONTH", length = 6, nullable = false, unique = false, insertable = false, updatable = false)
    public String getObjectYearMonth() {
        return objectYearMonth;
    }

    /**
     * objectYearMonthを設定します。
     *
     * @param objectYearMonth
     *            対象年月
     */
    public void setObjectYearMonth(String objectYearMonth) {
        this.objectYearMonth = objectYearMonth;
    }

    /**
     * mileBalanceを返します。
     *
     * @return マイル残高
     */
    @Column(name = "MILE_BALANCE", precision = 7, nullable = false, unique = false)
    public Integer getMileBalance() {
        return mileBalance;
    }

    /**
     * mileBalanceを設定します。
     *
     * @param mileBalance
     *            マイル残高
     *
     */
    public void setMileBalance(Integer mileBalance) {
        this.mileBalance = mileBalance;
    }

    /**
     * insertUserIdを返します。
     *
     * @return 登録者ID
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
     * @return 登録日時
     *
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
     * @return 最終更新者ID
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
     * @return 最終更新日時
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
     * @return 削除フラグ
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
     * @return 論理削除日
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
     * @return アプリ会員情報
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
     *            アプリ会員情報
     */
    public void setAplMemInfo(AplMemInfoEntity aplMemInfo) {
        this.aplMemInfo = aplMemInfo;
    }
}
