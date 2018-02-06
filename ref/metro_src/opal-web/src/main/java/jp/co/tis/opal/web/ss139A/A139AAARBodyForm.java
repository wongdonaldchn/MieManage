package jp.co.tis.opal.web.ss139A;

import java.io.Serializable;

import javax.validation.constraints.AssertTrue;

import com.fasterxml.jackson.annotation.JsonIgnore;

import nablarch.core.util.DateUtil;
import nablarch.core.util.StringUtil;
import nablarch.core.validation.ee.Domain;
import nablarch.core.validation.ee.Required;

import jp.co.tis.opal.common.constants.OpalDefaultConstants;
import jp.co.tis.opal.common.utility.DateConvertUtil;

/**
 * A139A01:PiTaPa利用実績取得APIの検索フォーム。
 *
 * @author 唐
 * @since 1.0
 */
public class A139AAARBodyForm implements Serializable {

    /** シリアルバージョンUID */
    private static final long serialVersionUID = 1L;

    /** アプリ会員ID */
    @Domain("applicationMemberId")
    @Required(message = "{M000000001}")
    private String applicationMemberID;

    /** PiTaPaご利用年月 */
    @Domain("yearMonth")
    @Required(message = "{M000000001}")
    private String pitapaUseYearMonth;

    /**
     * PiTaPaご利用年月の過去日チェック
     *
     * @return 処理結果
     */
    @JsonIgnore
    @AssertTrue(message = "{MA139A0101}")
    public boolean isPitaPaUseYearMonthPastCheck() {
        // 未入力の場合は、相関バリデーションは実施しない。(バリデーションOKとする)
        if (!StringUtil.isNullOrEmpty(pitapaUseYearMonth)
                && DateUtil.getParsedDate(pitapaUseYearMonth, OpalDefaultConstants.YEAR_MONTH_FORMAT) != null) {
            // PiTaPa利用実績照会開始年月=( システム日付の年 - 2 )年 ＋ システム日付の月
            StringBuffer beginYearMonth = new StringBuffer();
            beginYearMonth.append(Integer.parseInt(DateConvertUtil.getSysYear()) - 2);
            beginYearMonth.append(DateConvertUtil.getSysMonth());
            // PiTaPa利用実績取得要求電文.PiTaPaご利用年月がPiTaPa利用実績照会開始年月より古い月の場合
            if (beginYearMonth.toString().compareTo(pitapaUseYearMonth) > 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * 照会対象年月の未来日付チェック
     *
     * @return 処理結果
     */
    @JsonIgnore
    @AssertTrue(message = "{MA139A0102}")
    public boolean isPitapaUseYearMonthFutureCheck() {
        // 未入力の場合は、相関バリデーションは実施しない。(バリデーションOKとする)
        if (!StringUtil.isNullOrEmpty(pitapaUseYearMonth)
                && DateUtil.getParsedDate(pitapaUseYearMonth, OpalDefaultConstants.YEAR_MONTH_FORMAT) != null) {
            // PiTaPa利用実績取得要求電文.PiTaPaご利用年月がシステム日付の年月以降の場合
            if (DateConvertUtil.getSysYearMonth().compareTo(pitapaUseYearMonth) <= 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * アプリ会員IDを取得する。
     *
     * @return アプリ会員ID
     */
    public String getApplicationMemberID() {
        return applicationMemberID;
    }

    /**
     * アプリ会員IDを設定する。
     *
     * @param applicationMemberID
     *            アプリ会員ID
     */
    public void setApplicationMemberID(String applicationMemberID) {
        this.applicationMemberID = applicationMemberID;
    }

    /**
     * PiTaPaご利用年月を取得する。
     *
     * @return PiTaPaご利用年月
     */
    public String getPitapaUseYearMonth() {
        return pitapaUseYearMonth;
    }

    /**
     * PiTaPaご利用年月を設定する。
     *
     * @param pitapaUseYearMonth
     *            PiTaPaご利用年月
     */
    public void setPitapaUseYearMonth(String pitapaUseYearMonth) {
        this.pitapaUseYearMonth = pitapaUseYearMonth;
    }
}