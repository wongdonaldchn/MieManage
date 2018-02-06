package com.donald.miem.common.component;

import java.text.SimpleDateFormat;

import com.donald.miem.common.utility.DateConvertUtil;
import nablarch.core.date.SystemTimeUtil;

/**
 * 論理削除日算出
 *
 * @author Donald Wong
 * @since opal
 */
public class CM010004Component {

    /**
     * {@inneritDoc} システム日付と指定されたデータ保持期間から、マイル系データの論理削除日を算出する。
     * <p/>
     *
     * @param yearSpan
     *            データ保持期間年数
     * @return 論理削除日(YYYYMMDDの形式)
     */
    public String getDeletedDateMileYearly(int yearSpan) {

        StringBuilder deletedDateMileYearly = new StringBuilder();

        // システム日付の年(YYYYの部分)
        int year = Integer.valueOf(DateConvertUtil.getSysYear());
        // システム日付の月（MMの部分）
        int month = Integer.valueOf(DateConvertUtil.getSysMonth());
        // システム日付の月（MM）が"01"或いは"02"の場合 (1月或いは2月)
        if (month >= 1 && month <= 2) {

            // システム日付の年(YYYYの部分)に(パラメータ.データ保持期間の設定値-1)を加算して、文字列"0401"を連結する。
            deletedDateMileYearly.append(String.valueOf(year + yearSpan - 1));
            deletedDateMileYearly.append("0401");
        } else if (month >= 3 && month <= 12) {
            // システム日付の年(YYYYの部分)に(パラメータ.データ保持期間の設定値)を加算して、文字列"0401"を連結する。
            deletedDateMileYearly.append(String.valueOf(year + yearSpan));
            deletedDateMileYearly.append("0401");
        }

        // 算出した論理削除日を戻り値として返却する。
        return deletedDateMileYearly.toString();
    }

    /**
     * {@inneritDoc} システム日付と指定されたデータ保持期間（年単位）から、論理削除日を算出する。
     * <p/>
     *
     * @param yearSpan
     *            データ保持期間年数
     * @return 論理削除日(YYYYMMDDの形式)
     */
    public String getDeletedDateYearly(int yearSpan) {

        String deletedDateYearly = "";

        // システム日付
        String sysDate = this.getStringSysDate();
        // システム日付と指定されたデータ保持期間（年単位）から、論理削除日を算出する。
        deletedDateYearly = nablarch.core.util.DateUtil.addMonth(sysDate, yearSpan * 12);

        // 算出した論理削除日を戻り値として返却する。
        return deletedDateYearly;
    }

    /**
     * {@inneritDoc} システム日付を yyyyMMdd 形式の文字列で取得する。
     * <p/>
     *
     * @return システム日付(YYYYMMDDの形式)
     */
    private String getStringSysDate() {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        // システム日付
        String sysDate = sdf.format(SystemTimeUtil.getTimestamp()).substring(0, 8);

        return sysDate;
    }

    /**
     * {@inneritDoc} システム日付と指定されたデータ保持期間（月単位）から、論理削除日を算出する。
     * <p/>
     *
     * @param monthSpan
     *            データ保持期間月数
     * @return 論理削除日(YYYYMMDDの形式)
     */
    public String getDeletedDateMonthly(int monthSpan) {

        String deletedDateMonthly = "";

        // システム日付
        String sysDate = this.getStringSysDate();
        // システム日付と指定されたデータ保持期間（月単位）から、論理削除日を算出する。
        deletedDateMonthly = nablarch.core.util.DateUtil.addMonth(sysDate, monthSpan);

        // 算出した論理削除日を戻り値として返却する。
        return deletedDateMonthly;
    }

    /**
     * {@inneritDoc} システム日付と指定されたデータ保持期間（日単位）から、論理削除日を算出する。
     * <p/>
     *
     * @param daySpan
     *            データ保持期間日数
     * @return 論理削除日(YYYYMMDDの形式)
     */
    public String getDeletedDateDaily(int daySpan) {

        String deletedDateDaily = "";

        // システム日付
        String sysDate = this.getStringSysDate();
        // システム日付と指定されたデータ保持期間（日単位）から、論理削除日を算出する。
        deletedDateDaily = nablarch.core.util.DateUtil.addDay(sysDate, daySpan);

        // 算出した論理削除日を戻り値として返却する。
        return deletedDateDaily;
    }
}
