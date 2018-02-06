package com.donald.miem.common.constants;

/**
 * 共通使用するコード値の定義クラス。
 *
 * @author 張
 * @since 1.0
 */
public class OpalCodeConstants {

    /**
     * 性別コード
     *
     * @author 張
     * @since 1.0
     */
    public static class SexCode {
        /** 9：不明 */
        public static final String UNKNOWN = "9";
        /** 1：男性 */
        public static final String MALE = "1";
        /** 2：女性 */
        public static final String FEMALE = "2";
    }

    /**
     * アプリ会員状態コード
     *
     * @author 趙
     * @since 1.0
     */
    public static class AplMemStatusCode {
        /** A：OP認証済みのアプリ会員 */
        public static final String OP_AUTH_APL_MEM = "A";
        /** B：乗車マイル有OP会員 */
        public static final String HAVE_RIDE_MILE_OP_MEM = "B";
        /** C：乗車マイルなしOP会員 */
        public static final String WITHOUT_RIDE_MILE_OP_MEM = "C";
        /** D：OP非会員 */
        public static final String NOT_OP_MEM = "D";
    }

    /**
     * OP認証フラグ
     *
     * @author 趙
     * @since 1.0
     */
    public static class OPAuthenticateFlag {
        /** 0：OP認証未済み */
        public static final String OP_AUTH_FLAG_0 = "0";
        /** 1：OP認証済み */
        public static final String OP_AUTH_FLAG_1 = "1";
    }

    /**
     * OP退会フラグ
     *
     * @author 唐
     * @since 1.0
     */
    public static class OPWithdrawFlag {
        /** 0：OP未退会 */
        public static final String OP_WITHDRAW_FLAG_0 = "0";
        /** 1：OP退会済み */
        public static final String OP_WITHDRAW_FLAG_1 = "1";
    }

    /**
     * 再登録処理区分
     *
     * @author 唐
     * @since 1.0
     */
    public static class ReregistProcessDivision {
        /** 1：ログインID再登録 */
        public static final String LOGIN_ID_REREGIST = "1";
        /** 2：パスワード再登録 */
        public static final String PASSWORD_REREGIST = "2";
        /** 3：ログインID・パスワード再登録 */
        public static final String LOGIN_ID_PASSWORD_REREGIST = "3";
    }

    /**
     * メールアドレス認証処理区分
     *
     * @author 唐
     * @since 1.0
     */
    public static class MailAddressAuthProcessDivision {
        /** 1：アプリ会員登録 */
        public static final String APL_MEM_REGIST = "1";
        /** 2：ログインID・パスワード再登録 */
        public static final String LOGIN_ID_PASSWORD_REREGIST = "2";
        /** 3：メールアドレス変更 */
        public static final String MAIL_ADDRESS_CHANGE = "3";
    }

    /**
     * データ連携区分
     *
     * @author 趙
     * @since 1.0
     */
    public static class DataRelateDivision {
        /** 1：新規追加 */
        public static final String NEW_ADD = "1";
        /** 2：属性変更 */
        public static final String PROP_MODIFY = "2";
        /** 3：退会（解約） */
        public static final String WITHDRAW = "3";
    }

    /**
     * マイル種別コード
     *
     * @author 趙
     * @since 1.0
     */
    public static class MileCategoryCode {
        /** A00：基本乗車マイル */
        public static final String BASIC_RIDE_MILE = "A00";
        /** A01：パートナー乗車マイル */
        public static final String PARTNER_RIDE_MILE = "A01";
        /** A02：家族乗車マイル */
        public static final String FAMILY_RIDE_MILE = "A02";
        /** A03：イベント */
        public static final String EVENT = "A03";
        /** A04：スタンプラリー */
        public static final String STAMP_RALLY = "A04";
        /** A05：アンケート */
        public static final String ENQUETE = "A05";
        /** A06：マイル調整（加算） */
        public static final String MILE_ADJUST_ADD = "A06";
        /** A07：買い物 */
        public static final String SHOPPING = "A07";
        /** A08：OSAKA PiTaPa登録 */
        public static final String OP_REGIST = "A08";
        /** A09：ゲーム獲得） */
        public static final String GAME_ACQUIRE = "A09";
        /** A10：その他（マイル獲得） */
        public static final String OTHER_MILE_ACQUIRE = "A10";
        /** A11：OSAKA PiTaPa分登録時加算 */
        public static final String OP_REGIST_ADD = "A11";
        /** A12：会員登録ボーナス */
        public static final String MEM_REGIST_BONUS = "A12";
        /** S00：商品／乗車券 */
        public static final String GOODS_TICKET = "S00";
        /** S01：抽選応募 */
        public static final String SELECT_SUBSCRIBE = "S01";
        /** S02：マイル調整（減算） */
        public static final String MILE_ADJUST_SUB = "S02";
        /** S03：マイル失効 */
        public static final String MILE_INV = "S03";
        /** S04：OSAKA PiTaPaポイント自動移行 */
        public static final String OP_POINT_AUTO_TRANS = "S04";
        /** S05：OSAKA PiTaPaポイント任意移行 */
        public static final String OP_POINT_ANY_TRANS = "S05";
        /** S06：OSAKA PiTaPa登録解除 */
        public static final String OP_REGIST_RELEASE = "S06";
        /** S07：その他（マイル利用） */
        public static final String OTHER_MILE_USE = "S07";
        /** S08：OSAKA PiTaPa登録スライド減算用 */
        public static final String OP_REGIST_SLIDE_SUB = "S08";
        /** S09：会員登録ボーナス（調整） */
        public static final String MEM_REGIST_BONUS_ADJUST = "S09";
    }

    /**
     * 家族／パートナー登録状況区分
     *
     * @author 曹
     * @since 1.0
     */
    public static class MemServiceRegistStatus {

        /** 1:登録済み */
        public static final String MEM_SERVICE_REGIST_STATUS_1 = "1";

        /** 2:登録不可 */
        public static final String MEM_SERVICE_REGIST_STATUS_2 = "2";

        /** 3:登録可能 */
        public static final String MEM_SERVICE_REGIST_STATUS_3 = "3";
    }

    /**
     * 登録状況区分
     *
     * @author 曹
     * @since 1.0
     */
    public static class RegistStatusDivision {

        /** 1:登録 */
        public static final String REGIST_STATUS_DIVISION_1 = "1";

        /** 2:解除 */
        public static final String REGIST_STATUS_DIVISION_2 = "2";
    }

    /**
     * 承認状況区分
     *
     * @author 曹
     * @since 1.0
     */
    public static class AdmitStatusDivision {

        /** 1:承認済み */
        public static final String ADMIT_STATUS_DIVISION_1 = "1";

        /** 2:解除 */
        public static final String ADMIT_STATUS_DIVISION_2 = "2";
    }

    /**
     * メール配信区分
     *
     * @author 唐
     * @since 1.0
     */
    public static class MailDeliverDivision {

        /** 1:個別配信 */
        public static final String MAIL_DELIVER_DIVISION_1 = "1";

        /** 2:一括配信 */
        public static final String MAIL_DELIVER_DIVISION_2 = "2";
    }

    /**
     * プッシュ通知送信区分
     *
     * @author 唐
     * @since 1.0
     */
    public static class PushNoticeDivision {

        /** 1:個別送信 */
        public static final String PUSH_NOTICE_DIVISION_1 = "1";

        /** 2:一括送信 */
        public static final String PUSH_NOTICE_DIVISION_2 = "2";
    }

    /**
     * メール配信タイプ
     *
     * @author 唐
     * @since 1.0
     */
    public static class MailDeliverType {

        /** 1:即時配信 */
        public static final String MAIL_DELIVER_TYPE_1 = "1";

        /** 2:予約配信 */
        public static final String MAIL_DELIVER_TYPE_2 = "2";
    }

    /**
     * プッシュ通知送信タイプ
     *
     * @author 唐
     * @since 1.0
     */
    public static class PushNoticeType {

        /** 1:即時送信 */
        public static final String PUSH_NOTICE_TYPE_1 = "1";

        /** 2:予約送信 */
        public static final String PUSH_NOTICE_TYPE_2 = "2";
    }

    /**
     * サービス区分
     *
     * @author 曹
     * @since 1.0
     */
    public static class ServiceDivision {

        /** 1:家族会員サービス */
        public static final String SERVICE_DIVISION_1 = "1";

        /** 2:パートナー会員サービス */
        public static final String SERVICE_DIVISION_2 = "2";
    }

    /**
     * ユーザ選択区分
     *
     * @author 曹
     * @since 1.0
     */
    public static class UserChooseDivision {

        /** 0:未選択 */
        public static final String USER_CHOOSE_DIVISION_0 = "0";

        /** 1:選択済み */
        public static final String USER_CHOOSE_DIVISION_1 = "1";
    }

    /**
     * メール配信状況
     *
     * @author 張 成剛
     * @since 1.0
     */
    public static class MailDeliverStatus {

        /** 1：処理待ち */
        public static final String MAIL_DELIVER_STATUS_1 = "1";

        /** 2：処理済 */
        public static final String MAIL_DELIVER_STATUS_2 = "2";

        /** 3：タイムアウト */
        public static final String MAIL_DELIVER_STATUS_3 = "3";

        /** 4：HTTP送信エラー */
        public static final String MAIL_DELIVER_STATUS_4 = "4";

        /** 5：API連携エラー */
        public static final String MAIL_DELIVER_STATUS_5 = "5";
    }

    /**
     * マイル移行区分
     *
     * @author 曹
     * @since 1.0
     */
    public static class MileTransiTionDivision {

        /** 1：自動移行 */
        public static final String MILE_TRANSITION_DIVISION_1 = "1";

        /** 2:任意移行 */
        public static final String MILE_TRANSITION_DIVISION_2 = "2";
    }

    /**
     * マイル調整状況区分
     *
     * @author 曹
     * @since 1.0
     */
    public static class MileAdjustStatusDivi {

        /** 0：指示受付済 */
        public static final String MILE_ADJUST_STATUS_DIVI_0 = "0";

        /** 1:調整済 */
        public static final String MILE_ADJUST_STATUS_DIVI_1 = "1";
    }

    /**
     * メール配信状態区分
     *
     * @author 唐
     * @since 1.0
     */
    public static class MailDeliverStatusDivision {

        /** 0:配信可 */
        public static final String MAIL_DELIVER_STATUS_DIVISION_0 = "0";

        /** 1:配信停止 */
        public static final String MAIL_DELIVER_STATUS_DIVISION_1 = "1";
    }

    /**
     * 続柄コード
     *
     * @author 陳
     * @since 1.0
     */
    public static class RelationshipCode {

        /** 0:本会員 */
        public static final String RELATIONSHIP_CODE_0 = "0";

        /** 1:家族 */
        public static final String RELATIONSHIP_CODE_1 = "1";

        /** 2:ジュニア */
        public static final String RELATIONSHIP_CODE_2 = "2";

        /** 3:キッズ */
        public static final String RELATIONSHIP_CODE_3 = "3";
    }

}
