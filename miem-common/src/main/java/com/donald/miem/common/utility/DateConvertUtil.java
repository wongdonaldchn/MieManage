package com.donald.miem.common.utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import nablarch.core.date.SystemTimeUtil;

/**
 * 日付変換ユーティリティ
 *
 * @author 趙
 * @since 1.0
 */
public final class DateConvertUtil {

    /**
     * {@inneritDoc} システム日付の年（YYYY）を取得する。
     * <p/>
     *
     * @return システム年（YYYY）
     */
    public static String getSysYear() {
        return SystemTimeUtil.getDateTimeString().substring(0, 4);
    }

    /**
     * {@inneritDoc} システム日付の年月（YYYYMM）を取得する。
     * <p/>
     *
     * @return システム年月（YYYYMM）
     */
    public static String getSysYearMonth() {
        return SystemTimeUtil.getDateTimeString().substring(0, 6);
    }

    /**
     * {@inneritDoc} システム日付の月（MM）を取得する。
     * <p/>
     *
     * @return システム月（MM）
     */
    public static String getSysMonth() {
        return SystemTimeUtil.getDateTimeString().substring(4, 6);
    }

    /**
     * 文字列形式の対象日時(yyyyMMddHHmmssSSS)に対して、指定する分を加算する。<br/>
     * <p/>
     * 負の値が指定された場合は、減算を行う。
     * <p/>
     * 例）addMinute("20000101120000", 1) //--> "2000-01-01 12:01:00"<br>
     *
     * @param date
     *            日時文字列(yyyyMMddHHmmssSSS形式)
     * @param minute
     *            加算する分(負の値の場合は、減算を行う。)
     * @return 計算後の日時(カレンダー(Calendar)形式)
     */
    public static Calendar addMinute(String date, int minute) {
        Calendar calendar = stringToCalendar(date);

        calendar.add(Calendar.MINUTE, minute);

        return calendar;
    }

    /**
     * 文字列形式の対象日時(yyyyMMddHHmmssSSS)に対して、指定する分を加算する。<br/>
     * <p/>
     * 負の値が指定された場合は、減算を行う。
     * <p/>
     * 例）addMinute("20000101120000", 1) //--> "2000-01-01 12:01:00"<br>
     *
     * @param date
     *            日時文字列(yyyyMMddHHmmssSSS形式)
     * @param minute
     *            加算する分(負の値の場合は、減算を行う。)
     * @param format
     *            日時文字列形式
     * @return 計算後の日時(文字列(String)形式)
     */
    public static String addMinute(String date, int minute, String format) {
        Calendar calendar = addMinute(date, minute);
        return new SimpleDateFormat(format).format(calendar.getTime());
    }

    /**
     * 文字列形式の対象日時(yyyyMMddHHmmssSSS)に対して、指定する日数を加算する。<br/>
     * <p/>
     * 負の値が指定された場合は、減算を行う。
     * <p/>
     * 例）addDay("20000101120000", 1) //--> "2000-01-02 12:01:00"<br>
     *
     * @param date
     *            日時文字列(yyyyMMddHHmmssSSS形式)
     * @param day
     *            加算する日(負の値の場合は、減算を行う。)
     * @return 計算後の日時(カレンダー(Calendar)形式)
     */
    public static Calendar addDay(String date, int day) {
        Calendar calendar = stringToCalendar(date);

        calendar.add(Calendar.DATE, day);

        return calendar;
    }

    /**
     * 文字列形式の対象日時(yyyyMMddHHmmssSSS)に対して、指定する日数を加算する。<br/>
     * <p/>
     * 負の値が指定された場合は、減算を行う。
     * <p/>
     * 例）addDay("20000101120000", 1) //--> "2000-01-02 12:01:00"<br>
     *
     * @param date
     *            日時文字列(yyyyMMddHHmmssSSS形式)
     * @param day
     *            加算する日(負の値の場合は、減算を行う。)
     * @param format
     *            日時文字列形式
     * @return 計算後の日時(文字列(String)形式)
     */
    public static String addDay(String date, int day, String format) {
        Calendar calendar = addDay(date, day);
        return new SimpleDateFormat(format).format(calendar.getTime());
    }

    /**
     * 文字列形式の対象日時(yyyyMMddHHmmssSSS)をカレンダー(Calendar)形式に転換する。
     *
     * @param date
     *            日時文字列(yyyyMMddHHmmssSSS形式)
     * @return 日時(カレンダー(Calendar)形式)
     */
    public static Calendar stringToCalendar(String date) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Integer.parseInt(date.substring(0, 4)), Integer.parseInt(date.substring(4, 6)) - 1,
                Integer.parseInt(date.substring(6, 8)), Integer.parseInt(date.substring(8, 10)),
                Integer.parseInt(date.substring(10, 12)), Integer.parseInt(date.substring(12, 14)));

        calendar.set(Calendar.MILLISECOND, Integer.parseInt(date.substring(14, 17)));

        return calendar;
    }

    /**
     * 文字列形式の対象日時(yyyyMMddHHmmssSSS)を日付(Date)形式に転換する。
     *
     * @param date
     *            日時文字列
     * @param format
     *            日付文字列のフォーマット
     * @return 日付文字列の日付が設定された、{@link java.util.Date}クラスのインスタンス
     * @throws IllegalArgumentException
     *             日付文字列のフォーマットが format形式ではなかった場合
     */
    public static Date stringToDate(String date, String format) {
        Date sb = null;
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            sb = sdf.parse(date);
        } catch (ParseException e) {
            throw new IllegalArgumentException("the string was not formatted " + format + ". date = " + date + ".", e);
        }
        return sb;
    }
}
