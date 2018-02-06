package jp.co.tis.opal.web.common.constants;

/**
 * チェックの結果。
 *
 * @author 陳
 * @since 1.0
 */
public class CheckResultConstants {
    /** */
    public static final int DEFAULT = 0;
    /** */
    public static final int REQUEST_DATA_ISNULL = 1;
    /** */
    public static final int OPAL_CONTROL_DATA_ISNULL = 2;
    /** */
    public static final int REQUEST_BODY_ISNULL = 3;
    /** */
    public static final int BEAN_CHECK_ERROR = 4;
    /** メールアドレス存在 */
    public static final int MAIL_ADDRESS_EXIST = 5;
    /** ログインID存在 */
    public static final int LOGIN_ID_EXIST = 6;
    /** メールアドレス認証情報が存在しない */
    public static final int MAIL_ADDRESS_AUTH_NULL = 7;
    /** 処理済 */
    public static final int PROCESSED_COMPLETED = 8;
    /** システム日時がメールアドレス認証有効期限を超える */
    public static final int MAIL_ADDRESS_AUTH_DATE_ERROR = 9;
    /** アプリ会員一時情報が存在しない */
    public static final int APL_MEM_TEMP_DATA_ISNULL = 10;
    /** アプリ会員情報が存在しない */
    public static final int APL_MEM_DATA_ISNULL = 11;
    /** 家族会員サービス情報が登録済み */
    public static final int FAMILY_MEM_SERVICE_INFO_HAS_INSERTED = 12;
    /** 家族会員数が存在しない */
    public static final int FAMILY_MEM_IS_NOT_ENOUGH = 13;
    /** OP会員情報取得エラー */
    public static final int OP_DATA_ISNULL = 14;
    /** マイル残高不足 */
    public static final int MAIL_BALANCE_NOT_ENOUGH = 15;
    /** 続柄コードチェックエラー */
    public static final int RELATIONSHIP_CODE_ERROR = 16;
    /** パスワードチェックエラー */
    public static final int PASSWORD_ERROR = 17;
    /** 正常終了 */
    public static final int CHECK_OK = 18;
    /** パートナー登録アプリ会員チェックエラー1 */
    public static final int PARTNER_REGIST_MEM_INFO_CHECK_ERROR1 = 19;
    /** パートナー登録アプリ会員チェックエラー2 */
    public static final int PARTNER_REGIST_MEM_INFO_CHECK_ERROR2 = 20;
    /** パートナー登録アプリ会員チェックエラー3 */
    public static final int PARTNER_REGIST_MEM_INFO_CHECK_ERROR3 = 21;
    /** パートナー登録アプリ会員チェックエラー4 */
    public static final int PARTNER_REGIST_MEM_INFO_CHECK_ERROR4 = 22;
    /** パートナーアプリ会員チェックエラー1 */
    public static final int PARTNER_MEM_INFO_CHECK_ERROR1 = 23;
    /** パートナーアプリ会員チェックエラー2 */
    public static final int PARTNER_MEM_INFO_CHECK_ERROR2 = 24;
    /** パートナーアプリ会員チェックエラー3 */
    public static final int PARTNER_MEM_INFO_CHECK_ERROR3 = 25;
    /** パートナーアプリ会員チェックエラー4 */
    public static final int PARTNER_MEM_INFO_CHECK_ERROR4 = 26;
    /** パートナーアプリ会員チェックエラー5 */
    public static final int PARTNER_MEM_INFO_CHECK_ERROR5 = 27;
    /** マイル履歴情報が存在しない */
    public static final int MILE_HISTORY_INFORMATION_ISNULL = 28;
    /** アプリ会員状態コードが"A"(OP認証済)であることチェックエラー */
    public static final int APPLICATION_MEMBER_STATUS_CODE_NOT_A = 29;
    /** パートナー会員サービス情報データ取得エラー */
    public static final int PARTNER_MEM_SERVICE_INFO_ISNULL = 30;
    /** 承認状況区分が"1"(承認済み)ではない */
    public static final int ADMIT_STATUS_DIVISION_NOT_ADMITTED = 31;
    /** 休日の重複チェック */
    public static final int DAY_OFF_CHECK_ERROR = 32;
    /** 家族会員サービス存在チェック */
    public static final int FAMILY_MEM_SERVICE_CHECK_ERROR = 33;
    /** 家族乗車適用日存在チェック */
    public static final int FAMILY_RIDE_APPLY_DATE_CHECK_ERROR = 34;
    /** パートナー会員サービス存在チェック */
    public static final int PARTNER_MEM_SERVICE_CHECK_ERROR = 35;
    /** パートナー乗車適用日存在チェック */
    public static final int PARTNER_RIDE_APPLY_DATE_CHECK_ERROR = 36;
    /** アプリ会員情報・PiTaPa利用実績情報が存在しない */
    public static final int APL_MEM_DATA_PITAPA_USE_DATA_ISNULL = 37;
    /** マイル集計情報が存在しない */
    public static final int MILE_SUMMARY_INFORMATION_ISNULL = 38;
    /** 乗車適用日登録上限回数チェック */
    public static final int UPPER_LIMIT_TIMES_CHECK_ERROR = 39;
    /** OP認証未済み */
    public static final int OP_NOT_AUTHENTICATE = 40;
    /** 配信可否チェック(メール配信状態区分が"0"(送信可)ではない場合) */
    public static final int MAIL_DELIVER_STATUS_ISNOT_ZERO = 41;
    /** OP認証済み */
    public static final int OP_AUTHENTICATE = 42;
    /** 照会終了年月が照会開始年月より24ヶ月超える */
    public static final int END_YEAR_MONTH_ERROR = 43;
    /** 照会対象年月が照会対象開始年月より古い日付の場合 */
    public static final int OBJECT_YEAR_MONTH_ERROR = 44;
}
