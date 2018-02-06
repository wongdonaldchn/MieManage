package jp.co.tis.opal.web.ss132A;

import java.io.Serializable;

import javax.validation.constraints.AssertTrue;

import com.fasterxml.jackson.annotation.JsonIgnore;

import nablarch.core.util.DateUtil;
import nablarch.core.util.StringUtil;
import nablarch.core.validation.ee.Domain;
import nablarch.core.validation.ee.Required;

import jp.co.tis.opal.common.constants.OpalDefaultConstants;

/**
 * A132A02:マイル履歴取得APIの検索フォーム。
 *
 * @author 唐
 * @since 1.0
 */
public class A132AAARBodyForm implements Serializable {

    /** シリアルバージョンUID */
    private static final long serialVersionUID = 1L;

    /** アプリ会員ID */
    @Domain("applicationMemberId")
    @Required(message = "{M000000001}")
    private String applicationMemberId;

    /** 照会開始年月 */
    @Domain("yearMonth")
    @Required(message = "{M000000001}")
    private String startYearMonth;

    /** 照会終了年月 */
    @Domain("yearMonth")
    @Required(message = "{M000000001}")
    private String endYearMonth;

    /**
     * 照会開始年月と照会終了年月の相関チェック
     *
     * @return 処理結果
     */
    @JsonIgnore
    @AssertTrue(message = "{MA132A0201}")
    public boolean isStartEndYearMonthCheck() {
        // どちらかが未入力の場合は、相関バリデーションは実施しない。(バリデーションOKとする)
        if (!StringUtil.isNullOrEmpty(startYearMonth) && !StringUtil.isNullOrEmpty(endYearMonth)
                && DateUtil.getParsedDate(startYearMonth, OpalDefaultConstants.YEAR_MONTH_FORMAT) != null
                && DateUtil.getParsedDate(endYearMonth, OpalDefaultConstants.YEAR_MONTH_FORMAT) != null) {
            // マイル履歴取得要求電文.照会終了年月がマイル履歴取得要求電文.照会開始年月より古い日付の場合
            if (endYearMonth.compareTo(startYearMonth) < 0) {
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
     * 照会開始年月を取得する。
     *
     * @return 照会開始年月
     */
    public String getStartYearMonth() {
        return startYearMonth;
    }

    /**
     * 照会開始年月を設定する。
     *
     * @param startYearMonth
     *            照会開始年月
     */
    public void setStartYearMonth(String startYearMonth) {
        this.startYearMonth = startYearMonth;
    }

    /**
     * 照会終了年月を取得する。
     *
     * @return 照会終了年月
     */
    public String getEndYearMonth() {
        return endYearMonth;
    }

    /**
     * 照会終了年月を設定する。
     *
     * @param endYearMonth
     *            照会終了年月
     */
    public void setEndYearMonth(String endYearMonth) {
        this.endYearMonth = endYearMonth;
    }
}