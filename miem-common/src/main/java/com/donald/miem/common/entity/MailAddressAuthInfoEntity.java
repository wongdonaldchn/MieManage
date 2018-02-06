package com.donald.miem.common.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

/**
 * MailAddressAuthInfoエンティティクラス
 *
 * @author 張
 * @since 1.0
 */
@Generated("GSP")
@Entity
@Table(name = "MAIL_ADDRESS_AUTH_INFO")
public class MailAddressAuthInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /** mailAddressAuthCodeプロパティ */
    private String mailAddressAuthCode;

    /** mailAddressAuthKeyプロパティ */
    private Long mailAddressAuthKey;

    /** mailAddressAuthSaltプロパティ */
    private String mailAddressAuthSalt;

    /** mailAddressAuthExpiDateプロパティ */
    private Timestamp mailAddressAuthExpiDate;

    /** processDivisionプロパティ */
    private String processDivision;

    /** mailAddressプロパティ */
    private String mailAddress;

    /** processedFlagプロパティ */
    private String processedFlag;

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
     * mailAddressAuthCodeを返します。
     *
     * @return mailAddressAuthCode
     */
    @Id
    @Column(name = "MAIL_ADDRESS_AUTH_CODE", length = 64, nullable = false, unique = true)
    public String getMailAddressAuthCode() {
        return mailAddressAuthCode;
    }

    /**
     * mailAddressAuthCodeを設定します。
     *
     * @param mailAddressAuthCode
     *            メールアドレス認証コード
     */
    public void setMailAddressAuthCode(String mailAddressAuthCode) {
        this.mailAddressAuthCode = mailAddressAuthCode;
    }

    /**
     * mailAddressAuthKeyを返します。
     *
     * @return mailAddressAuthKey
     */
    @Column(name = "MAIL_ADDRESS_AUTH_KEY", precision = 20, nullable = false, unique = false)
    public Long getMailAddressAuthKey() {
        return mailAddressAuthKey;
    }

    /**
     * mailAddressAuthKeyを設定します。
     *
     * @param mailAddressAuthKey
     *            メールアドレス認証キー
     */
    public void setMailAddressAuthKey(Long mailAddressAuthKey) {
        this.mailAddressAuthKey = mailAddressAuthKey;
    }

    /**
     * mailAddressAuthSaltを返します。
     *
     * @return mailAddressAuthSalt
     */
    @Column(name = "MAIL_ADDRESS_AUTH_SALT", length = 20, nullable = false, unique = false)
    public String getMailAddressAuthSalt() {
        return mailAddressAuthSalt;
    }

    /**
     * mailAddressAuthSaltを設定します。
     *
     * @param mailAddressAuthSalt
     *            メールアドレス認証SALT
     */
    public void setMailAddressAuthSalt(String mailAddressAuthSalt) {
        this.mailAddressAuthSalt = mailAddressAuthSalt;
    }

    /**
     * mailAddressAuthExpiDateを返します。
     *
     * @return mailAddressAuthExpiDate
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "MAIL_ADDRESS_AUTH_EXPI_DATE", nullable = false, unique = false)
    public Timestamp getMailAddressAuthExpiDate() {
        return mailAddressAuthExpiDate;
    }

    /**
     * mailAddressAuthExpiDateを設定します。
     *
     * @param mailAddressAuthExpiDate
     *            メールアドレス認証有効期限
     */
    public void setMailAddressAuthExpiDate(Timestamp mailAddressAuthExpiDate) {
        this.mailAddressAuthExpiDate = mailAddressAuthExpiDate;
    }

    /**
     * processDivisionを返します。
     *
     * @return processDivision
     */
    @Column(name = "PROCESS_DIVISION", length = 1, nullable = false, unique = false)
    public String getProcessDivision() {
        return processDivision;
    }

    /**
     * processDivisionを設定します。
     *
     * @param processDivision
     *            処理区分
     */
    public void setProcessDivision(String processDivision) {
        this.processDivision = processDivision;
    }

    /**
     * mailAddressを返します。
     *
     * @return mailAddress
     */
    @Column(name = "MAIL_ADDRESS", length = 200, nullable = false, unique = false)
    public String getMailAddress() {
        return mailAddress;
    }

    /**
     * mailAddressを設定します。
     *
     * @param mailAddress
     *            メールアドレス
     */
    public void setMailAddress(String mailAddress) {
        this.mailAddress = mailAddress;
    }

    /**
     * processedFlagを返します。
     *
     * @return processedFlag
     */
    @Column(name = "PROCESSED_FLAG", length = 1, nullable = false, unique = false)
    public String getProcessedFlag() {
        return processedFlag;
    }

    /**
     * processedFlagを設定します。
     *
     * @param processedFlag
     *            処理済フラグ
     */
    public void setProcessedFlag(String processedFlag) {
        this.processedFlag = processedFlag;
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
