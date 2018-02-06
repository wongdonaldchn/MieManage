package jp.co.tis.opal.web.ss133A;

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
 * A133A01:マイル利用明細取得APIの検索フォーム。
 *
 * @author 唐
 * @since 1.0
 */
public class A133AAARBodyForm implements Serializable {

    /** シリアルバージョンUID */
    private static final long serialVersionUID = 1L;

    /** アプリ会員ID */
    @Domain("applicationMemberId")
    @Required(message = "{M000000001}")
    private String applicationMemberId;

    /** 照会対象年月 */
    @Domain("yearMonth")
    @Required(message = "{M000000001}")
    private String objectYearMonth;

    /**
     * 照会対象年月の未来日付チェック
     *
     * @return 処理結果
     */
    @JsonIgnore
    @AssertTrue(message = "{MA133A0102}")
    public boolean isObjectYearMonthFutureCheck() {
        // 未入力の場合は、相関バリデーションは実施しない。(バリデーションOKとする)
        if (!StringUtil.isNullOrEmpty(objectYearMonth)
                && DateUtil.getParsedDate(objectYearMonth, OpalDefaultConstants.YEAR_MONTH_FORMAT) != null) {

            // マイル利用明細取得要求電文.照会対象年月 > システム日付の年月の場合
            if (DateConvertUtil.getSysYearMonth().compareTo(objectYearMonth) < 0) {
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
     * 照会対象年月を取得する。
     *
     * @return 照会対象年月
     */
    public String getObjectYearMonth() {
        return objectYearMonth;
    }

    /**
     * 照会対象年月を設定する。
     *
     * @param objectYearMonth
     *            照会対象年月
     */
    public void setObjectYearMonth(String objectYearMonth) {
        this.objectYearMonth = objectYearMonth;
    }
}