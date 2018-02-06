package jp.co.tis.opal.web.ss113A;

import java.io.Serializable;
import java.util.Objects;

import javax.validation.constraints.AssertTrue;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import nablarch.core.util.StringUtil;
import nablarch.core.validation.ee.Domain;
import nablarch.core.validation.ee.Required;

/**
 * アプリ会員情報更新フォーム。
 *
 * @author 張
 * @since 1.0
 */
public class A113AABRBodyForm implements Serializable {

    /** シリアルバージョンUID */
    private static final long serialVersionUID = 1L;

    /** アプリ会員ID */
    @Domain("applicationMemberId")
    @Required(message = "{M000000001}")
    private String applicationMemberId;

    /** 生年月日 */
    @Domain("date")
    @Required(message = "{M000000001}")
    private String birthdate;

    /** 性別コード */
    @Domain("sexCode")
    @Required(message = "{M000000001}")
    private String sexCode;

    /** レコメンド利用承諾可否フラグ */
    @Domain("recommendUseAcceptFlag")
    @Required(message = "{M000000001}")
    private String recommendUseAcceptFlag;

    /** アンケート1 */
    @Domain("enquete")
    @Required(message = "{M000000001}")
    private String enquete1;

    /** アンケート2 */
    @Domain("enquete")
    @Required(message = "{M000000001}")
    private String enquete2;

    /** アンケート3 */
    @Domain("enquete")
    @Required(message = "{M000000001}")
    private String enquete3;

    /** アンケート4 */
    @Domain("enquete")
    @Required(message = "{M000000001}")
    private String enquete4;

    /** アンケート5 */
    @Domain("enquete")
    @Required(message = "{M000000001}")
    private String enquete5;

    /** アンケート6 */
    @Domain("enquete")
    @Required(message = "{M000000001}")
    private String enquete6;

    /** アンケート7 */
    @Domain("enquete")
    @Required(message = "{M000000001}")
    private String enquete7;

    /** アンケート8 */
    @Domain("enquete")
    @Required(message = "{M000000001}")
    private String enquete8;

    /** アンケート9 */
    @Domain("enquete")
    @Required(message = "{M000000001}")
    private String enquete9;

    /** アンケート10 */
    @Domain("enquete")
    @Required(message = "{M000000001}")
    private String enquete10;

    /** 主なご利用駅1 */
    @Domain("stationId")
    private String mainUseStation1;

    /** 主なご利用駅2 */
    @Domain("stationId")
    private String mainUseStation2;

    /** 主なご利用駅3 */
    @Domain("stationId")
    private String mainUseStation3;

    /** 主なご利用駅4 */
    @Domain("stationId")
    private String mainUseStation4;

    /** 主なご利用駅5 */
    @Domain("stationId")
    private String mainUseStation5;

    /** 休日1 */
    @Domain("dayOff")
    private String dayOff1;

    /** 休日2 */
    @Domain("dayOff")
    private String dayOff2;

    /**
     * アプリ会員IDを取得する。
     *
     * @return アプリ会員ID
     */
    public String getApplicationMemberId() {
        return applicationMemberId;
    }

    /**
     * 生年月日を取得する。
     *
     * @return 生年月日
     */
    public String getBirthdate() {
        return birthdate;
    }

    /**
     * 性別コードを取得する。
     *
     * @return 性別コード
     */
    public String getSexCode() {
        return sexCode;
    }

    /**
     * レコメンド利用承諾可否フラグを取得する。
     *
     * @return レコメンド利用承諾可否フラグ
     */
    public String getRecommendUseAcceptFlag() {
        return recommendUseAcceptFlag;
    }

    /**
     * アンケート1を取得する。
     *
     * @return アンケート1
     */
    public String getEnquete1() {
        return enquete1;
    }

    /**
     * アンケート2を取得する。
     *
     * @return アンケート2
     */
    public String getEnquete2() {
        return enquete2;
    }

    /**
     * アンケート3を取得する。
     *
     * @return アンケート3
     */
    public String getEnquete3() {
        return enquete3;
    }

    /**
     * アンケート4を取得する。
     *
     * @return アンケート4
     */
    public String getEnquete4() {
        return enquete4;
    }

    /**
     * アンケート5を取得する。
     *
     * @return アンケート5
     */
    public String getEnquete5() {
        return enquete5;
    }

    /**
     * アンケート6を取得する。
     *
     * @return アンケート6
     */
    public String getEnquete6() {
        return enquete6;
    }

    /**
     * アンケート7を取得する。
     *
     * @return アンケート7
     */
    public String getEnquete7() {
        return enquete7;
    }

    /**
     * アンケート8を取得する。
     *
     * @return アンケート8
     */
    public String getEnquete8() {
        return enquete8;
    }

    /**
     * アンケート9を取得する。
     *
     * @return アンケート9
     */
    public String getEnquete9() {
        return enquete9;
    }

    /**
     * アンケート10を取得する。
     *
     * @return アンケート10
     */
    public String getEnquete10() {
        return enquete10;
    }

    /**
     * 主なご利用駅1を取得する。
     *
     * @return 主なご利用駅1
     */
    public String getMainUseStation1() {
        return mainUseStation1;
    }

    /**
     * 主なご利用駅2を取得する。
     *
     * @return 主なご利用駅2
     */
    public String getMainUseStation2() {
        return mainUseStation2;
    }

    /**
     * 主なご利用駅3を取得する。
     *
     * @return 主なご利用駅3
     */
    public String getMainUseStation3() {
        return mainUseStation3;
    }

    /**
     * 主なご利用駅4を取得する。
     *
     * @return 主なご利用駅4
     */
    public String getMainUseStation4() {
        return mainUseStation4;
    }

    /**
     * 主なご利用駅5を取得する。
     *
     * @return 主なご利用駅5
     */
    public String getMainUseStation5() {
        return mainUseStation5;
    }

    /**
     * 休日1を取得する。
     *
     * @return 休日1
     */
    public String getDayOff1() {
        return dayOff1;
    }

    /**
     * 休日2を取得する。
     *
     * @return 休日2
     */
    public String getDayOff2() {
        return dayOff2;
    }

    /**
     * アプリ会員IDを設定する。
     *
     * @param applicationMemberId
     *            アプリ会員ID
     */
    @JsonInclude(Include.NON_NULL)
    public void setApplicationMemberId(String applicationMemberId) {
        this.applicationMemberId = applicationMemberId;
    }

    /**
     * 生年月日を設定する。
     *
     * @param birthdate
     *            生年月日
     */
    @JsonInclude(Include.NON_NULL)
    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    /**
     * 性別コードを設定する。
     *
     * @param sexCode
     *            性別コード
     */
    @JsonInclude(Include.NON_NULL)
    public void setSexCode(String sexCode) {
        this.sexCode = sexCode;
    }

    /**
     * レコメンド利用承諾可否フラグを設定する。
     *
     * @param recommendUseAcceptFlag
     *            レコメンド利用承諾可否フラグ
     */
    @JsonInclude(Include.NON_NULL)
    public void setRecommendUseAcceptFlag(String recommendUseAcceptFlag) {
        this.recommendUseAcceptFlag = recommendUseAcceptFlag;
    }

    /**
     * アンケート1を設定する。
     *
     * @param enquete1
     *            アンケート1
     */
    @JsonInclude(Include.NON_NULL)
    public void setEnquete1(String enquete1) {
        this.enquete1 = enquete1;
    }

    /**
     * アンケート2を設定する。
     *
     * @param enquete2
     *            アンケート2
     */
    @JsonInclude(Include.NON_NULL)
    public void setEnquete2(String enquete2) {
        this.enquete2 = enquete2;
    }

    /**
     * アンケート3を設定する。
     *
     * @param enquete3
     *            アンケート3
     */
    @JsonInclude(Include.NON_NULL)
    public void setEnquete3(String enquete3) {
        this.enquete3 = enquete3;
    }

    /**
     * アンケート4を設定する。
     *
     * @param enquete4
     *            アンケート4
     */
    @JsonInclude(Include.NON_NULL)
    public void setEnquete4(String enquete4) {
        this.enquete4 = enquete4;
    }

    /**
     * アンケート5を設定する。
     *
     * @param enquete5
     *            アンケート5
     */
    @JsonInclude(Include.NON_NULL)
    public void setEnquete5(String enquete5) {
        this.enquete5 = enquete5;
    }

    /**
     * アンケート6を設定する。
     *
     * @param enquete6
     *            アンケート6
     */
    @JsonInclude(Include.NON_NULL)
    public void setEnquete6(String enquete6) {
        this.enquete6 = enquete6;
    }

    /**
     * アンケート7を設定する。
     *
     * @param enquete7
     *            アンケート7
     */
    @JsonInclude(Include.NON_NULL)
    public void setEnquete7(String enquete7) {
        this.enquete7 = enquete7;
    }

    /**
     * アンケート8を設定する。
     *
     * @param enquete8
     *            アンケート8
     */
    @JsonInclude(Include.NON_NULL)
    public void setEnquete8(String enquete8) {
        this.enquete8 = enquete8;
    }

    /**
     * アンケート9を設定する。
     *
     * @param enquete9
     *            アンケート9
     */
    @JsonInclude(Include.NON_NULL)
    public void setEnquete9(String enquete9) {
        this.enquete9 = enquete9;
    }

    /**
     * アンケート10を設定する。
     *
     * @param enquete10
     *            アンケート10
     */
    @JsonInclude(Include.NON_NULL)
    public void setEnquete10(String enquete10) {
        this.enquete10 = enquete10;
    }

    /**
     * 主なご利用駅1を設定する。
     *
     * @param mainUseStation1
     *            主なご利用駅1
     */
    @JsonInclude(Include.NON_NULL)
    public void setMainUseStation1(String mainUseStation1) {
        this.mainUseStation1 = mainUseStation1;
    }

    /**
     * 主なご利用駅2を設定する。
     *
     * @param mainUseStation2
     *            主なご利用駅2
     */
    @JsonInclude(Include.NON_NULL)
    public void setMainUseStation2(String mainUseStation2) {
        this.mainUseStation2 = mainUseStation2;
    }

    /**
     * 主なご利用駅3を設定する。
     *
     * @param mainUseStation3
     *            主なご利用駅3
     */
    @JsonInclude(Include.NON_NULL)
    public void setMainUseStation3(String mainUseStation3) {
        this.mainUseStation3 = mainUseStation3;
    }

    /**
     * 主なご利用駅4を設定する。
     *
     * @param mainUseStation4
     *            主なご利用駅4
     */
    @JsonInclude(Include.NON_NULL)
    public void setMainUseStation4(String mainUseStation4) {
        this.mainUseStation4 = mainUseStation4;
    }

    /**
     * 主なご利用駅5を設定する。
     *
     * @param mainUseStation5
     *            主なご利用駅5
     */
    @JsonInclude(Include.NON_NULL)
    public void setMainUseStation5(String mainUseStation5) {
        this.mainUseStation5 = mainUseStation5;
    }

    /**
     * 休日1を設定する。
     *
     * @param dayOff1
     *            休日1
     */
    @JsonInclude(Include.NON_NULL)
    public void setDayOff1(String dayOff1) {
        this.dayOff1 = dayOff1;
    }

    /**
     * 休日2を設定する。
     *
     * @param dayOff2
     *            休日2
     */
    @JsonInclude(Include.NON_NULL)
    public void setDayOff2(String dayOff2) {
        this.dayOff2 = dayOff2;
    }

    /**
     * 休日1と休日2は重複している場合
     *
     * @return 処理結果（重複していない場合：true 重複している場合：false）
     */
    @JsonIgnore
    @AssertTrue(message = "{MA113A0201}")
    public boolean isEqualsDayOff() {
        if (StringUtil.isNullOrEmpty(dayOff1) || StringUtil.isNullOrEmpty(dayOff2)) {
            // どちらかが未入力の場合は、相関バリデーションは実施しない。(バリデーションOKとする)
            return true;
        }
        return !Objects.equals(dayOff1, dayOff2);
    }
}
