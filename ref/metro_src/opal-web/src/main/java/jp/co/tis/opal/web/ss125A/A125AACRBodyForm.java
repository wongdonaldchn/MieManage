package jp.co.tis.opal.web.ss125A;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.AssertTrue;

import com.fasterxml.jackson.annotation.JsonIgnore;

import nablarch.core.date.SystemTimeUtil;
import nablarch.core.util.DateUtil;
import nablarch.core.util.StringUtil;
import nablarch.core.validation.ee.Domain;
import nablarch.core.validation.ee.Required;

import jp.co.tis.opal.common.constants.OpalDefaultConstants;

/**
 * A125A03:乗車適用日選択フォーム。
 *
 * @author 陳
 * @since 1.0
 */
public class A125AACRBodyForm implements Serializable {

    /** シリアルバージョンUID */
    private static final long serialVersionUID = 1L;

    /** アプリ会員ID */
    @Domain("applicationMemberId")
    @Required(message = "{M000000001}")
    private String applicationMemberId;

    /** サービス区分 */
    @Domain("serviceDivision")
    @Required(message = "{M000000001}")
    private String serviceDivision;

    /** 乗車適用日情報リスト */
    @Required(message = "{M000000001}")
    @Valid
    private List<A125AACRBodyDateForm> rideApplyDateInfoList;

    /**
     * 乗車適用日情報リスト0件チェック
     *
     * @return 処理結果（0件以外：true 0件：false）
     */
    @JsonIgnore
    @AssertTrue(message = "{MA125A0301}")
    public boolean isRideApplyDateInfoListEmptyCheck() {
        if (rideApplyDateInfoList != null && rideApplyDateInfoList.size() == 0) {
            return false;
        }
        return true;
    }

    /**
     * 乗車適用日情報リストの重複データチェック
     *
     * @return 処理結果（重複データがない場合：true 重複データがある場合：false）
     */
    @JsonIgnore
    @AssertTrue(message = "{MA125A0302}")
    public boolean isRideApplyDateInfoListRepeatCheck() {
        if (rideApplyDateInfoList != null) {
            List<String> temp = new ArrayList<String>();

            for (A125AACRBodyDateForm rideApplyDate : rideApplyDateInfoList) {
                if (!StringUtil.isNullOrEmpty(rideApplyDate.getRideApplyDate())
                        && DateUtil.getParsedDate(rideApplyDate.getRideApplyDate(),
                                OpalDefaultConstants.YEAR_MONTH_DAY_FORMAT) != null) {
                    if (temp.contains(rideApplyDate.getRideApplyDate())) {
                        return false;
                    }
                }
                temp.add(rideApplyDate.getRideApplyDate());
            }
        }
        return true;
    }

    /**
     * 乗車適用日情報リストの当月日付チェック
     *
     * @return 処理結果（当月日付の場合：true 当月日付ではない場合：false）
     */
    @JsonIgnore
    @AssertTrue(message = "{MA125A0303}")
    public boolean isRideApplyDateInfoListThisMonthCheck() {
        if (rideApplyDateInfoList != null) {
            // システム日付の年月を取得する。
            String systemDateYearMonth = SystemTimeUtil.getDateString().substring(
                    OpalDefaultConstants.POSITION_YEAR_MONTH_START, OpalDefaultConstants.POSITION_YEAR_MONTH_END);

            for (A125AACRBodyDateForm rideApplyDate : rideApplyDateInfoList) {
                if (!StringUtil.isNullOrEmpty(rideApplyDate.getRideApplyDate())
                        && DateUtil.getParsedDate(rideApplyDate.getRideApplyDate(),
                                OpalDefaultConstants.YEAR_MONTH_DAY_FORMAT) != null) {
                    if (!systemDateYearMonth.equals(
                            rideApplyDate.getRideApplyDate().substring(OpalDefaultConstants.POSITION_YEAR_MONTH_START,
                                    OpalDefaultConstants.POSITION_YEAR_MONTH_END))) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * 乗車適用日情報リストの未来日付チェック
     *
     * @return 処理結果（未来日付ではない場合：true 未来日付の場合：false）
     */
    @JsonIgnore
    @AssertTrue(message = "{MA125A0304}")
    public boolean isRideApplyDateInfoListFutureCheck() {
        if (rideApplyDateInfoList != null) {
            // システム日付を取得する。
            Date systemDate = SystemTimeUtil.getDate();

            for (A125AACRBodyDateForm rideApplyDate : rideApplyDateInfoList) {
                if (!StringUtil.isNullOrEmpty(rideApplyDate.getRideApplyDate())
                        && DateUtil.getParsedDate(rideApplyDate.getRideApplyDate(),
                                OpalDefaultConstants.YEAR_MONTH_DAY_FORMAT) != null) {
                    Date dt = DateUtil.getDate(rideApplyDate.getRideApplyDate());

                    if (systemDate.compareTo(dt) < 0) {
                        return false;
                    }
                }
            }
        }
        return true;
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
     * アプリ会員IDを設定する。
     *
     * @param applicationMemberId
     *            アプリ会員ID
     */
    public void setApplicationMemberId(String applicationMemberId) {
        this.applicationMemberId = applicationMemberId;
    }

    /**
     * サービス区分を取得する。
     *
     * @return サービス区分
     */
    public String getServiceDivision() {
        return serviceDivision;
    }

    /**
     * サービス区分を設定する。
     *
     * @param serviceDivision
     *            サービス区分
     */
    public void setServiceDivision(String serviceDivision) {
        this.serviceDivision = serviceDivision;
    }

    /**
     * 乗車適用日情報リストを取得する。
     *
     * @return 乗車適用日情報リスト
     */
    public List<A125AACRBodyDateForm> getRideApplyDateInfoList() {
        return rideApplyDateInfoList;
    }

    /**
     * 乗車適用日情報リストを設定する。
     *
     * @param rideApplyDateInfoList
     *            乗車適用日情報リスト
     */
    public void setRideApplyDateInfoList(List<A125AACRBodyDateForm> rideApplyDateInfoList) {
        this.rideApplyDateInfoList = rideApplyDateInfoList;
    }
}