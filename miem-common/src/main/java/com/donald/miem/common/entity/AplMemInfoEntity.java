package com.donald.miem.common.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

/**
 * AplMemInfoエンティティクラス
 *
 * @author 張
 * @since 1.0
 */
@Generated("GSP")
@Entity
@Table(name = "APL_MEM_INFO")
public class AplMemInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /** applicationMemberIdプロパティ */
    private Long applicationMemberId;

    /** memberControlNumberプロパティ */
    private String memberControlNumber;

    /** memCtrlNumBrNumプロパティ */
    private String memCtrlNumBrNum;

    /** osakaPitapaNumberプロパティ */
    private String osakaPitapaNumber;

    /** applicationIdプロパティ */
    private String applicationId;

    /** deviceIdプロパティ */
    private String deviceId;

    /** loginIdプロパティ */
    private String loginId;

    /** passwordプロパティ */
    private String password;

    /** passwordSaltプロパティ */
    private String passwordSalt;

    /** stretchingTimesプロパティ */
    private Integer stretchingTimes;

    /** birthdateプロパティ */
    private String birthdate;

    /** sexCodeプロパティ */
    private String sexCode;

    /** mailAddressプロパティ */
    private String mailAddress;

    /** dayOff1プロパティ */
    private String dayOff1;

    /** dayOff2プロパティ */
    private String dayOff2;

    /** mailDeliverStatusDivisionプロパティ */
    private String mailDeliverStatusDivision;

    /** recommendUseAcceptFlagプロパティ */
    private String recommendUseAcceptFlag;

    /** enquete1プロパティ */
    private String enquete1;

    /** enquete2プロパティ */
    private String enquete2;

    /** enquete3プロパティ */
    private String enquete3;

    /** enquete4プロパティ */
    private String enquete4;

    /** enquete5プロパティ */
    private String enquete5;

    /** enquete6プロパティ */
    private String enquete6;

    /** enquete7プロパティ */
    private String enquete7;

    /** enquete8プロパティ */
    private String enquete8;

    /** enquete9プロパティ */
    private String enquete9;

    /** enquete10プロパティ */
    private String enquete10;

    /** mainUseStation1プロパティ */
    private String mainUseStation1;

    /** mainUseStation2プロパティ */
    private String mainUseStation2;

    /** mainUseStation3プロパティ */
    private String mainUseStation3;

    /** mainUseStation4プロパティ */
    private String mainUseStation4;

    /** mainUseStation5プロパティ */
    private String mainUseStation5;

    /** opAuthTimesプロパティ */
    private Integer opAuthTimes;

    /** applicationMemberStatusCodeプロパティ */
    private String applicationMemberStatusCode;

    /** osakaPitapaAuthenticateFlagプロパティ */
    private String osakaPitapaAuthenticateFlag;

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

    /** opMemInfo関連プロパティ */
    private OpMemInfoEntity opMemInfo;

    /** mileAdjustInstrInfoList関連プロパティ */
    private List<MileAdjustInstrInfoEntity> mileAdjustInstrInfoList;

    /**
     * applicationMemberIdを返します。
     *
     * @return applicationMemberId
     */
    @Id
    @GeneratedValue(generator = "APPLICATION_MEMBER_ID_SEQ", strategy = GenerationType.AUTO)
    @SequenceGenerator(name = "APPLICATION_MEMBER_ID_SEQ", sequenceName = "APPLICATION_MEMBER_ID_SEQ", initialValue = 1, allocationSize = 1)
    @Column(name = "APPLICATION_MEMBER_ID", precision = 10, nullable = false, unique = true)
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
     * memberControlNumberを返します。
     *
     * @return memberControlNumber
     */
    @Column(name = "MEMBER_CONTROL_NUMBER", length = 10, nullable = true, unique = false, insertable = false, updatable = false)
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
    @Column(name = "MEM_CTRL_NUM_BR_NUM", length = 3, nullable = true, unique = false, insertable = false, updatable = false)
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
    @Column(name = "OSAKA_PITAPA_NUMBER", length = 10, nullable = true, unique = false)
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
     * applicationIdを返します。
     *
     * @return applicationId
     */
    @Column(name = "APPLICATION_ID", length = 50, nullable = true, unique = false)
    public String getApplicationId() {
        return applicationId;
    }

    /**
     * applicationIdを設定します。
     *
     * @param applicationId
     *            アプリID
     */
    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    /**
     * deviceIdを返します。
     *
     * @return deviceId
     */
    @Column(name = "DEVICE_ID", length = 7, nullable = true, unique = false)
    public String getDeviceId() {
        return deviceId;
    }

    /**
     * deviceIdを設定します。
     *
     * @param deviceId
     *            デバイスID
     */
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    /**
     * loginIdを返します。
     *
     * @return loginId
     */
    @Column(name = "LOGIN_ID", length = 16, nullable = true, unique = false)
    public String getLoginId() {
        return loginId;
    }

    /**
     * loginIdを設定します。
     *
     * @param loginId
     *            ログインID
     */
    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    /**
     * passwordを返します。
     *
     * @return password
     */
    @Column(name = "PASSWORD", length = 64, nullable = true, unique = false)
    public String getPassword() {
        return password;
    }

    /**
     * passwordを設定します。
     *
     * @param password
     *            パスワード
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * passwordSaltを返します。
     *
     * @return passwordSalt
     */
    @Column(name = "PASSWORD_SALT", length = 20, nullable = true, unique = false)
    public String getPasswordSalt() {
        return passwordSalt;
    }

    /**
     * passwordSaltを設定します。
     *
     * @param passwordSalt
     *            パスワードSALT
     */
    public void setPasswordSalt(String passwordSalt) {
        this.passwordSalt = passwordSalt;
    }

    /**
     * stretchingTimesを返します。
     *
     * @return stretchingTimes
     */
    @Column(name = "STRETCHING_TIMES", length = 1, nullable = true, unique = false)
    public Integer getStretchingTimes() {
        return stretchingTimes;
    }

    /**
     * stretchingTimesを設定します。
     *
     * @param stretchingTimes
     *            ストレッチング回数
     */
    public void setStretchingTimes(Integer stretchingTimes) {
        this.stretchingTimes = stretchingTimes;
    }

    /**
     * birthdateを返します。
     *
     * @return birthdate
     */
    @Column(name = "BIRTHDATE", length = 8, nullable = true, unique = false)
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
    @Column(name = "SEX_CODE", length = 1, nullable = true, unique = false)
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
     * mailAddressを返します。
     *
     * @return mailAddress
     */
    @Column(name = "MAIL_ADDRESS", length = 200, nullable = true, unique = false)
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
     * getDayOff1を返します。
     *
     * @return dayOff1
     */
    @Column(name = "DAY_OFF_1", length = 1, nullable = true, unique = false)
    public String getDayOff1() {
        return dayOff1;
    }

    /**
     * dayOff1を設定します。
     *
     * @param dayOff1
     *            プッシュ通知フラグ
     */
    public void setDayOff1(String dayOff1) {
        this.dayOff1 = dayOff1;
    }

    /**
     * dayOff2を返します。
     *
     * @return dayOff2
     */
    @Column(name = "DAY_OFF_2", length = 1, nullable = true, unique = false)
    public String getDayOff2() {
        return dayOff2;
    }

    /**
     * dayOff2を設定します。
     *
     * @param dayOff2
     *            メール通知フラグ
     */
    public void setDayOff2(String dayOff2) {
        this.dayOff2 = dayOff2;
    }

    /**
     * mailDeliverStatusDivisionを返します。
     *
     * @return mailDeliverStatusDivision
     */
    @Column(name = "MAIL_DELIVER_STATUS_DIVISION", length = 1, nullable = true, unique = false)
    public String getMailDeliverStatusDivision() {
        return mailDeliverStatusDivision;
    }

    /**
     * mailDeliverStatusDivisionを設定します。
     *
     * @param mailDeliverStatusDivision
     *            メール配信状態区分
     */
    public void setMailDeliverStatusDivision(String mailDeliverStatusDivision) {
        this.mailDeliverStatusDivision = mailDeliverStatusDivision;
    }

    /**
     * recommendUseAcceptFlagを返します。
     *
     * @return recommendUseAcceptFlag
     */
    @Column(name = "RECOMMEND_USE_ACCEPT_FLAG", length = 1, nullable = true, unique = false)
    public String getRecommendUseAcceptFlag() {
        return recommendUseAcceptFlag;
    }

    /**
     * recommendUseAcceptFlagを設定します。
     *
     * @param recommendUseAcceptFlag
     *            レコメンド利用承諾可否フラグ
     */
    public void setRecommendUseAcceptFlag(String recommendUseAcceptFlag) {
        this.recommendUseAcceptFlag = recommendUseAcceptFlag;
    }

    /**
     * enquete1を返します。
     *
     * @return enquete1
     */
    @Column(name = "ENQUETE_1", length = 3, nullable = true, unique = false)
    public String getEnquete1() {
        return enquete1;
    }

    /**
     * enquete1を設定します。
     *
     * @param enquete1
     *            アンケート1
     */
    public void setEnquete1(String enquete1) {
        this.enquete1 = enquete1;
    }

    /**
     * enquete2を返します。
     *
     * @return enquete2
     */
    @Column(name = "ENQUETE_2", length = 3, nullable = true, unique = false)
    public String getEnquete2() {
        return enquete2;
    }

    /**
     * enquete2を設定します。
     *
     * @param enquete2
     *            アンケート2
     */
    public void setEnquete2(String enquete2) {
        this.enquete2 = enquete2;
    }

    /**
     * enquete3を返します。
     *
     * @return enquete3
     */
    @Column(name = "ENQUETE_3", length = 3, nullable = true, unique = false)
    public String getEnquete3() {
        return enquete3;
    }

    /**
     * enquete3を設定します。
     *
     * @param enquete3
     *            アンケート3
     */
    public void setEnquete3(String enquete3) {
        this.enquete3 = enquete3;
    }

    /**
     * enquete4を返します。
     *
     * @return enquete4
     */
    @Column(name = "ENQUETE_4", length = 3, nullable = true, unique = false)
    public String getEnquete4() {
        return enquete4;
    }

    /**
     * enquete4を設定します。
     *
     * @param enquete4
     *            アンケート4
     */
    public void setEnquete4(String enquete4) {
        this.enquete4 = enquete4;
    }

    /**
     * enquete5を返します。
     *
     * @return enquete5
     */
    @Column(name = "ENQUETE_5", length = 3, nullable = true, unique = false)
    public String getEnquete5() {
        return enquete5;
    }

    /**
     * enquete5を設定します。
     *
     * @param enquete5
     *            アンケート5
     */
    public void setEnquete5(String enquete5) {
        this.enquete5 = enquete5;
    }

    /**
     * enquete6を返します。
     *
     * @return enquete6
     */
    @Column(name = "ENQUETE_6", length = 3, nullable = true, unique = false)
    public String getEnquete6() {
        return enquete6;
    }

    /**
     * enquete6を設定します。
     *
     * @param enquete6
     *            アンケート6
     */
    public void setEnquete6(String enquete6) {
        this.enquete6 = enquete6;
    }

    /**
     * enquete7を返します。
     *
     * @return enquete7
     */
    @Column(name = "ENQUETE_7", length = 3, nullable = true, unique = false)
    public String getEnquete7() {
        return enquete7;
    }

    /**
     * enquete7を設定します。
     *
     * @param enquete7
     *            アンケート7
     */
    public void setEnquete7(String enquete7) {
        this.enquete7 = enquete7;
    }

    /**
     * enquete8を返します。
     *
     * @return enquete8
     */
    @Column(name = "ENQUETE_8", length = 3, nullable = true, unique = false)
    public String getEnquete8() {
        return enquete8;
    }

    /**
     * enquete8を設定します。
     *
     * @param enquete8
     *            アンケート8
     */
    public void setEnquete8(String enquete8) {
        this.enquete8 = enquete8;
    }

    /**
     * enquete9を返します。
     *
     * @return enquete9
     */
    @Column(name = "ENQUETE_9", length = 3, nullable = true, unique = false)
    public String getEnquete9() {
        return enquete9;
    }

    /**
     * enquete9を設定します。
     *
     * @param enquete9
     *            アンケート9
     */
    public void setEnquete9(String enquete9) {
        this.enquete9 = enquete9;
    }

    /**
     * enquete10を返します。
     *
     * @return enquete10
     */
    @Column(name = "ENQUETE_10", length = 3, nullable = true, unique = false)
    public String getEnquete10() {
        return enquete10;
    }

    /**
     * enquete10を設定します。
     *
     * @param enquete10
     *            アンケート10
     */
    public void setEnquete10(String enquete10) {
        this.enquete10 = enquete10;
    }

    /**
     * mainUseStation1を返します。
     *
     * @return mainUseStation1
     */
    @Column(name = "MAIN_USE_STATION_1", length = 20, nullable = true, unique = false)
    public String getMainUseStation1() {
        return mainUseStation1;
    }

    /**
     * mainUseStation1を設定します。
     *
     * @param mainUseStation1
     *            主なご利用駅1
     */
    public void setMainUseStation1(String mainUseStation1) {
        this.mainUseStation1 = mainUseStation1;
    }

    /**
     * mainUseStation2を返します。
     *
     * @return mainUseStation2
     */
    @Column(name = "MAIN_USE_STATION_2", length = 20, nullable = true, unique = false)
    public String getMainUseStation2() {
        return mainUseStation2;
    }

    /**
     * mainUseStation2を設定します。
     *
     * @param mainUseStation2
     *            主なご利用駅2
     */
    public void setMainUseStation2(String mainUseStation2) {
        this.mainUseStation2 = mainUseStation2;
    }

    /**
     * mainUseStation3を返します。
     *
     * @return mainUseStation3
     */
    @Column(name = "MAIN_USE_STATION_3", length = 20, nullable = true, unique = false)
    public String getMainUseStation3() {
        return mainUseStation3;
    }

    /**
     * mainUseStation3を設定します。
     *
     * @param mainUseStation3
     *            主なご利用駅3
     */
    public void setMainUseStation3(String mainUseStation3) {
        this.mainUseStation3 = mainUseStation3;
    }

    /**
     * mainUseStation4を返します。
     *
     * @return mainUseStation4
     */
    @Column(name = "MAIN_USE_STATION_4", length = 20, nullable = true, unique = false)
    public String getMainUseStation4() {
        return mainUseStation4;
    }

    /**
     * mainUseStation4を設定します。
     *
     * @param mainUseStation4
     *            主なご利用駅4
     */
    public void setMainUseStation4(String mainUseStation4) {
        this.mainUseStation4 = mainUseStation4;
    }

    /**
     * mainUseStation5を返します。
     *
     * @return mainUseStation5
     */
    @Column(name = "MAIN_USE_STATION_5", length = 20, nullable = true, unique = false)
    public String getMainUseStation5() {
        return mainUseStation5;
    }

    /**
     * mainUseStation5を設定します。
     *
     * @param mainUseStation5
     *            主なご利用駅5
     */
    public void setMainUseStation5(String mainUseStation5) {
        this.mainUseStation5 = mainUseStation5;
    }

    /**
     * opAuthTimesを返します。
     *
     * @return opAuthTimes
     */
    @Column(name = "OP_AUTH_TIMES", length = 2, nullable = false, unique = false)
    public Integer getOpAuthTimes() {
        return opAuthTimes;
    }

    /**
     * opAuthTimesを設定します。
     *
     * @param opAuthTimes
     *            OP認証回数
     */
    public void setOpAuthTimes(Integer opAuthTimes) {
        this.opAuthTimes = opAuthTimes;
    }

    /**
     * applicationMemberStatusCodeを返します。
     *
     * @return applicationMemberStatusCode
     */
    @Column(name = "APPLICATION_MEMBER_STATUS_CODE", length = 1, nullable = true, unique = false)
    public String getApplicationMemberStatusCode() {
        return applicationMemberStatusCode;
    }

    /**
     * applicationMemberStatusCodeを設定します。
     *
     * @param applicationMemberStatusCode
     *            アプリ会員状態コード
     */
    public void setApplicationMemberStatusCode(String applicationMemberStatusCode) {
        this.applicationMemberStatusCode = applicationMemberStatusCode;
    }

    /**
     * osakaPitapaAuthenticateFlagを返します。
     *
     * @return osakaPitapaAuthenticateFlag
     */
    @Column(name = "OSAKA_PITAPA_AUTHENTICATE_FLAG", length = 1, nullable = false, unique = false)
    public String getOsakaPitapaAuthenticateFlag() {
        return osakaPitapaAuthenticateFlag;
    }

    /**
     * osakaPitapaAuthenticateFlagを設定します。
     *
     * @param osakaPitapaAuthenticateFlag
     *            OP認証フラグ
     */
    public void setOsakaPitapaAuthenticateFlag(String osakaPitapaAuthenticateFlag) {
        this.osakaPitapaAuthenticateFlag = osakaPitapaAuthenticateFlag;
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
     * opMemInfoを返します。
     *
     * @return opMemInfo
     */
    @ManyToOne
    @JoinColumns({ @JoinColumn(name = "MEMBER_CONTROL_NUMBER", referencedColumnName = "MEMBER_CONTROL_NUMBER"),
            @JoinColumn(name = "MEM_CTRL_NUM_BR_NUM", referencedColumnName = "MEM_CTRL_NUM_BR_NUM") })
    public OpMemInfoEntity getOpMemInfo() {
        return opMemInfo;
    }

    /**
     * opMemInfoを設定します。
     *
     * @param opMemInfo
     *            opMemInfo
     */
    public void setOpMemInfo(OpMemInfoEntity opMemInfo) {
        this.opMemInfo = opMemInfo;
    }

    /**
     * mileAdjustInstrInfoListを返します。
     *
     * @return mileAdjustInstrInfoList
     */
    @OneToMany(mappedBy = "aplMemInfo")
    public List<MileAdjustInstrInfoEntity> getMileAdjustInstrInfoList() {
        return mileAdjustInstrInfoList;
    }

    /**
     * mileAdjustInstrInfoListを設定します。
     *
     * @param mileAdjustInstrInfoList
     *            mileAdjustInstrInfoList
     */
    public void setMileAdjustInstrInfoList(List<MileAdjustInstrInfoEntity> mileAdjustInstrInfoList) {
        this.mileAdjustInstrInfoList = mileAdjustInstrInfoList;
    }
}
