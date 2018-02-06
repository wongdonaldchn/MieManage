package com.donald.miem.common.domain;

import com.donald.miem.common.validation.TelephoneNumber;
import com.donald.miem.common.validation.YYYYMM;
import nablarch.common.code.validator.ee.CodeValue;
import nablarch.core.validation.ee.Digits;
import nablarch.core.validation.ee.Length;
import nablarch.core.validation.ee.NumberRange;
import nablarch.core.validation.ee.SystemChar;

import com.donald.miem.common.validation.MailAddress;
import com.donald.miem.common.validation.YYYYMMDD;

/**
 * ドメイン定義。
 *
 * @author 趙
 * @since 1.0
 */
public class DomainBean {

    /**
     * アプリID
     */
    @Length(max = 50, message = "{M000000006}")
    @SystemChar(charsetDef = "半角英数字記号", message = "{M000000012}")
    private String applicationId;

    /**
     * アプリ採番ID
     */
    @NumberRange(min = 1, max = 9999999999L, message = "{M000000011}")
    @Digits(integer = 10, fraction = 0, message = "{M000000003}")
    private String applicationMemberId;

    /**
     * 会員管理番号。
     */
    @Length(min = 10, max = 10, message = "{M000000007}")
    @SystemChar(charsetDef = "半角英数字", message = "{M000000012}")
    private String memCtrlNum;

    /**
     * 会員管理番号枝番。
     */
    @Length(min = 3, max = 3, message = "{M000000007}")
    @SystemChar(charsetDef = "半角英数字", message = "{M000000012}")
    private String memrCtrlBrNum;

    /**
     * 駅Id。
     */
    @Length(min = 3, max = 3, message = "{M000000007}")
    @SystemChar(charsetDef = "半角英数字", message = "{M000000012}")
    private String stationId;

    /**
     * ファイルID。
     */
    @Length(min = 8, max = 8, message = "{M000000007}")
    @SystemChar(charsetDef = "半角英数字", message = "{M000000012}")
    private String fileId;

    /**
     * 日付
     */
    @YYYYMMDD(message = "{M000000013}")
    private String date;

    /**
     * 時刻
     */
    @Length(min = 6, max = 6, message = "{M000000007}")
    @SystemChar(charsetDef = "半角数字", message = "{M000000012}")
    private String time;

    /**
     * 年月
     */
    @YYYYMM(message = "{M000000014}")
    private String yearMonth;

    /**
     * OP番号
     */
    @Length(min = 10, max = 10, message = "{M000000007}")
    @SystemChar(charsetDef = "半角英数字", message = "{M000000012}")
    private String opNum;

    /**
     * マイル種別コード
     */
    @CodeValue(codeId = "C1300001", message = "{M000000002}")
    private String mileCategoryCode;

    /**
     * マイル数
     */
    @NumberRange(min = 0, message = "{M000000009}")
    @Digits(integer = 7, fraction = 0, message = "{M000000003}")
    private String mileAmount;

    /**
     * マイル加算減算受付番号
     */
    @Length(max = 20, message = "{M000000006}")
    @SystemChar(charsetDef = "半角英数字", message = "{M000000012}")
    private String mileAddSubRcptNo;

    /**
     * データ連携区分
     */
    @CodeValue(codeId = "C1100007", message = "{M000000002}")
    private String dataRelateDivision;

    /**
     * カード種類
     */
    @CodeValue(codeId = "C1100013", message = "{M000000002}")
    private String cardType;

    /**
     * 性別コード
     */
    @CodeValue(codeId = "C1100001", message = "{M000000002}")
    private String sexCode;

    /**
     * 電話番号
     */
    @TelephoneNumber(message = "{M000000015}")
    private String telephoneNumber;

    /**
     * 郵便番号
     */
    @Length(min = 7, max = 7, message = "{M000000007}")
    @SystemChar(charsetDef = "半角数字", message = "{M000000012}")
    private String postcode;

    /**
     * 続柄コード
     */
    @CodeValue(codeId = "C1100009", message = "{M000000002}")
    private String relationshipCode;

    /**
     * 再登録処理区分
     */
    @CodeValue(codeId = "C1100012", message = "{M000000002}")
    private String relogKubun;

    /**
     * フラグ
     */
    @Length(min = 1, max = 1, message = "{M000000007}")
    @SystemChar(charsetDef = "半角数字", message = "{M000000012}")
    private String flag;

    /**
     * ログインID
     */
    @Length(max = 16, message = "{M000000006}")
    @SystemChar(charsetDef = "半角英数字記号", message = "{M000000012}")
    private String loginId;

    /**
     * アンケート
     */
    @Length(min = 3, max = 3, message = "{M000000007}")
    @SystemChar(charsetDef = "半角数字", message = "{M000000012}")
    private String enquete;

    /**
     * パスワード
     */
    @Length(max = 64, message = "{M000000006}")
    @SystemChar(charsetDef = "半角英数字記号", message = "{M000000012}")
    private String password;

    /**
     * デバイスID
     */
    @Length(max = 7, message = "{M000000006}")
    @SystemChar(charsetDef = "半角英字", message = "{M000000012}")
    private String deviceId;

    /**
     * 郵送種別
     */
    @Length(min = 2, max = 2, message = "{M000000007}")
    @SystemChar(charsetDef = "半角数字", message = "{M000000012}")
    private String postCategory;

    /**
     * 郵送管理番号
     */
    @Length(min = 32, max = 32, message = "{M000000007}")
    @SystemChar(charsetDef = "半角数字", message = "{M000000012}")
    private String postControlNumber;

    /**
     * その他
     */
    @Length(max = 100, message = "{M000000006}")
    @SystemChar(charsetDef = "全半角文字", message = "{M000000012}", allowLineSeparator = true)
    private String other;

    /**
     * 住所
     */
    @Length(max = 140, message = "{M000000006}")
    @SystemChar(charsetDef = "全半角文字", message = "{M000000012}")
    private String address;

    /**
     * 氏名
     */
    @Length(max = 20, message = "{M000000006}")
    @SystemChar(charsetDef = "全半角文字", message = "{M000000012}")
    private String name;

    /**
     * メールアドレス認証コード
     */
    @Length(max = 64, message = "{M000000006}")
    @SystemChar(charsetDef = "半角英数字", message = "{M000000012}")
    private String mailAddressAuthCode;

    /**
     * メールアドレス
     */
    @MailAddress(message = "{M000000016}")
    private String mailAddress;

    /**
     * 休日コード
     */
    @CodeValue(codeId = "C1100015", message = "{M000000002}")
    private String dayOff;

    /**
     * プランコード
     */
    @Length(min = 6, max = 6, message = "{M000000007}")
    @SystemChar(charsetDef = "半角英数字", message = "{M000000012}")
    private String planCode;

    /**
     * サービス種別
     */
    @CodeValue(codeId = "C1100008", message = "{M000000002}")
    private String serviceCategory;

    /**
     * 合計金額
     */
    @Digits(integer = 11, fraction = 0, message = "{M000000003}")
    @NumberRange(min = -99999999999L, max = 99999999999L, message = "{M000000011}")
    private String totalMoney;

    /**
     * サービス区分
     */
    @CodeValue(codeId = "C1200006", message = "{M000000002}")
    private String serviceDivision;

    /**
     * レスポンスステータス
     */
    @Length(min = 2, max = 2, message = "{M000000007}")
    @SystemChar(charsetDef = "半角英字", message = "{M000000012}")
    private String status;

    /**
     * プッシュ通知識別ID
     */
    @NumberRange(min = 1, max = 9999999999L, message = "{M000000011}")
    @Digits(integer = 10, fraction = 0, message = "{M000000003}")
    private String id;

    /**
     * レコメンド利用承諾可フラグ
     */
    @CodeValue(codeId = "C1100005", message = "{M000000002}")
    private String recommendUseAcceptFlag;
}
