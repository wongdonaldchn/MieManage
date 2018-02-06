package com.donald.miem.common.constants;

/**
 * 共通使用するデフォルト値の定義クラス
 *
 * @author 張
 * @since 1.0
 */
public class OpalDefaultConstants {

    /** 処理済フラグ：0(未処理) */
    public static final String PROCESSED_FLAG_0 = "0";

    /** 処理済フラグ：1(処理済) */
    public static final String PROCESSED_FLAG_1 = "1";

    /** 削除フラグ：0(未削除) */
    public static final String DELETED_FLG_0 = "0";

    /** 削除フラグ：1(削除済) */
    public static final String DELETED_FLG_1 = "1";

    /** OP退会フラグ：0(OP未退会) */
    public static final String OSAKA_PITAPA_WITHDRAW_FLAG_0 = "0";

    /** OP退会フラグ：1(OP退会済み) */
    public static final String OSAKA_PITAPA_WITHDRAW_FLAG_1 = "1";

    /** マイル残高情報更新フラグ：0(登録) */
    public static final String MILE_BALANCE_UPDATE_FLG_0 = "0";

    /** マイル残高情報更新フラグ：1(更新) */
    public static final String MILE_BALANCE_UPDATE_FLG_1 = "1";

    /** マイル残高：0 */
    public static final Long MILE_BALANCE_ZERO = 0L;

    /** 処理結果コード：0(正常) */
    public static final String RESULT_CODE_0 = "0";

    /** 処理結果コード：1(異常) */
    public static final String RESULT_CODE_1 = "1";

    /** プッシュ通知フラグ：1(希望する) */
    public static final String PUSH_NOTICE_FLAG_1 = "1";

    /** バージョン番号：0 */
    public static final long VERSION = 0;

    /** マイル失効対象の終了年月の月：02 */
    public static final String MILE_INVALID_TO_MONTH = "02";

    /** マイル失効対象の開始年月の月：03 */
    public static final String MILE_INVALID_FROM_MONTH = "03";

    /** salt生成の引数(文字列桁数)：20 */
    public static final Integer SALT_PARAM_20 = 20;

    /** マイル失効日付：0401 */
    public static final String MILE_INVALID_DATE = "0401";

    /** OP認証回数：0 */
    public static final int OP_AUTH_TIMES_0 = 0;

    /** OP認証回数：99 */
    public static final int OP_AUTH_TIMES_99 = 99;

    /** 適用日時の日付 */
    public static final String APPLY_DATE_TIME_START_DATE = "01";

    /** 年月加算用（＋1ヶ月） */
    public static final int ADD_MONTH_1 = 1;

    /** 年月加算用（－1ヶ月） */
    public static final int ADD_MONTH_1_MINUS = -1;

    /** データレコード数先頭埋む文字 */
    public static final char CHAR_FOR_PADDING = '0';

    /** データレコード数の長さ */
    public static final int PAD_RECORD_LENGTH = 8;

    /** メール配信情報ファイル名の連番の長さ */
    public static final int PAD_MAIL_FILE_NAME_LENGTH = 2;

    /** メール配信情報ファイル名の開始位置 */
    public static final int MAIL_FILE_START = 0;

    /** メール配信情報ファイル名の終了位置 */
    public static final int MAIL_FILE_END = 35;

    /** 乗車適用日件数の長さ */
    public static final int RIDE_APPLY_DATE_CNT_LENGTH = 2;

    /** 乗車適用日情報の長さ */
    public static final int RIDE_APPLY_DATE_INFO_LENGTH = 62;

    /** subString定数(処理時間用：開始位置) */
    public static final int POSITION_TIME_START = 8;

    /** subString定数(処理時間用：終了位置) */
    public static final int POSITION_TIME_END = 14;

    /** subString定数(取得対象年月：開始位置) */
    public static final int POSITION_YEAR_MONTH_START = 0;

    /** subString定数(取得対象年月：終了位置) */
    public static final int POSITION_YEAR_MONTH_END = 6;

    /** subString定数(乗車適用日：開始位置) */
    public static final int POSITION_DATE_START = 6;

    /** subString定数(乗車適用日：終了位置) */
    public static final int POSITION_DATE_END = 8;

    /** プッシュ通知テンプレート：主なご利用駅の更新通知 */
    public static final String PUSH_TEMP_MAIN_USE_STA_UPDATE_NOTICE = "PUSHTEMP01";

    /** プッシュ通知テンプレート：乗車マイル獲得の通知 */
    public static final String PUSH_TEMP_RIDE_MILE_ACQUIRE_NOTICE = "PUSHTEMP02";

    /** プッシュ通知テンプレート：マイル移行通知 */
    public static final String PUSH_TEMP_MILE_TRANS_NOTICE = "PUSHTEMP03";

    /** プッシュ通知テンプレート：マイル調整通知（マイル加算） */
    public static final String PUSH_TEMP_MILE_ADJUST_ADD_NOTICE = "PUSHTEMP04";

    /** テンプレート名：マイル調整通知（マイル減算） */
    public static final String PUSH_TEMP_MILE_ADJUST_SUB_NOTICE = "PUSHTEMP05";

    /** メールテンプレート：アプリ会員登録確定依頼 */
    public static final String MAIL_TEMP_APL_MEM_REGIST_CONFIRM = "MAILTEMP01";

    /** メールテンプレート：アプリ会員新規登録完了通知 */
    public static final String MAIL_TEMP_APL_MEM_REGISTED_NOTICE = "MAILTEMP02";

    /** メールテンプレート：メールアドレス変更確定依頼 */
    public static final String MAIL_TEMP_MAIL_ADDRESS_UPDATE_CONFIRM = "MAILTEMP03";

    /** メールテンプレート：メールアドレス変更完了通知 */
    public static final String MAIL_TEMP_MAIL_ADDRESS_UPDATED_NOTICE = "MAILTEMP04";

    /** メールテンプレート：OP認証完了通知（初回） */
    public static final String MAIL_TEMP_OP_AUTHENTICATED_ONCE_NOTICE = "MAILTEMP05";

    /** メールテンプレート：OP認証完了通知（2回目以降） */
    public static final String MAIL_TEMP_OP_AUTHENTICATED_TWICE_NOTICE = "MAILTEMP06";

    /** メールテンプレート：ログインID・パスワード再登録完了通知 */
    public static final String MAIL_TEMP_LOGIN_REREGISTED_NOTICE = "MAILTEMP07";

    /** メールテンプレート：ログインID・パスワード再登録確定依頼 */
    public static final String MAIL_TEMP_LOGIN_REREGIST_CONFIRM = "MAILTEMP08";

    /** メールテンプレート：マイル移行通知 */
    public static final String MAIL_TEMP_MILE_TRANS_NOTICE = "MAILTEMP09";

    /** メールテンプレート：マイル加算調整通知 */
    public static final String MAIL_TEMP_MILE_ADJUST_ADD_NOTICE = "MAILTEMP10";

    /** メールテンプレート：マイル減算調整通知 */
    public static final String MAIL_TEMP_MILE_ADJUST_SUB_NOTICE = "MAILTEMP11";

    /** メールテンプレート：OP認証解除通知 */
    public static final String MAIL_TEMP_OP_AUTHENTICATE_RELEASE_NOTICE = "MAILTEMP12";

    /** マイナス記号：マイル利用 */
    public static final String MINUS_MARK_MILE_USE = "-";

    /** マイナス記号：マイル利用 */
    public static final String UNDER_MINUS_MARK_MILE_USE = "_";

    /** 空文字列 */
    public static final String NULL_STRING = "";

    /** 半角スペース1桁 */
    public static final String BLANK = " ";

    /** 日付フォーマット:yyyy-MM-dd */
    public static final String DATE_FORMAT = "yyyy-MM-dd";

    /** 日時フォーマット:yyyy-MM-dd HH:mm:ss */
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /** 日時フォーマット:yyyy-MM-dd HH:mm:ss.SSS */
    public static final String DATE_TIME_MILLISECOND_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

    /** 日時フォーマット:yyyyMMddHHmmssSSS */
    public static final String TIME_FORMAT = "yyyyMMddHHmmssSSS";

    /** メール配信日時フォーマット:yyyy/MM/dd HH:mm */
    public static final String DELIVER_DATE_FORMAT = "yyyy/MM/dd HH:mm";

    /** 年月フォーマット:yyyyMM */
    public static final String YEAR_MONTH_FORMAT = "yyyyMM";

    /** 日付フォーマット:yyyyMMdd */
    public static final String YEAR_MONTH_DAY_FORMAT = "yyyyMMdd";

    /** 日付フォーマット:yyyyMMddHHmmss */
    public static final String YEAR_MONTH_DAY_HOUR_MIN_SEC_FORMAT = "yyyyMMddHHmmss";

    /** マイル付与 */
    public static final String MILE_ADD = "A";

    /** マイル利用 */
    public static final String MILE_SUB = "S";

    /** API連携結果コード：10200(成功) */
    public static final String RESPONSE_CODE = "10200";
}
