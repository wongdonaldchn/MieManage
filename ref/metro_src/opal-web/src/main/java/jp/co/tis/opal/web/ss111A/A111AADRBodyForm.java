package jp.co.tis.opal.web.ss111A;

import java.io.Serializable;

import nablarch.core.validation.ee.Domain;
import nablarch.core.validation.ee.Required;

/**
 * アプリ会員登録フォーム。
 *
 * @author 唐
 * @since 1.0
 */
public class A111AADRBodyForm implements Serializable {

    /** シリアルバージョンUID */
    private static final long serialVersionUID = 1L;

    /** メールアドレス認証コード */
    @Domain("mailAddressAuthCode")
    @Required(message = "{M000000001}")
    private String mailAddressAuthCode;

    /** 新規登録マイル加算減算受付番号 */
    @Domain("mileAddSubRcptNo")
    @Required(message = "{M000000001}")
    private String registMileAddSubRcptNum;

    /** 新規登録ボーナスマイル数 */
    @Domain("mileAmount")
    @Required(message = "{M000000001}")
    private String registBonusMileAmount;

    /** OP認証マイル加算減算受付番号 */
    @Domain("mileAddSubRcptNo")
    @Required(message = "{M000000001}")
    private String opAuthMileAddSubRcptNum;

    /** OP認証ボーナスマイル数 */
    @Domain("mileAmount")
    @Required(message = "{M000000001}")
    private String opAuthBonusMileAmount;

    /**
     * メールアドレス認証コードを取得する。
     *
     * @return メールアドレス認証コード
     */
    public String getMailAddressAuthCode() {
        return mailAddressAuthCode;
    }

    /**
     * 新規登録マイル加算減算受付番号を取得する。
     *
     * @return 新規登録マイル加算減算受付番号
     */
    public String getRegistMileAddSubRcptNum() {
        return registMileAddSubRcptNum;
    }

    /**
     * 新規登録ボーナスマイル数を取得する。
     *
     * @return 新規登録ボーナスマイル数
     */
    public String getRegistBonusMileAmount() {
        return registBonusMileAmount;
    }

    /**
     * OP認証マイル加算減算受付番号を取得する。
     *
     * @return OP認証マイル加算減算受付番号
     */
    public String getOpAuthMileAddSubRcptNum() {
        return opAuthMileAddSubRcptNum;
    }

    /**
     * OP認証ボーナスマイル数を取得する。
     *
     * @return OP認証ボーナスマイル数
     */
    public String getOpAuthBonusMileAmount() {
        return opAuthBonusMileAmount;
    }

    /**
     * メールアドレス認証コードを設定する。
     *
     * @param mailAddressAuthCode
     *            メールアドレス認証コード
     */
    public void setMailAddressAuthCode(String mailAddressAuthCode) {
        this.mailAddressAuthCode = mailAddressAuthCode;
    }

    /**
     * 新規登録マイル加算減算受付番号を設定する。
     *
     * @param registMileAddSubRcptNum
     *            新規登録マイル加算減算受付番号
     */
    public void setRegistMileAddSubRcptNum(String registMileAddSubRcptNum) {
        this.registMileAddSubRcptNum = registMileAddSubRcptNum;
    }

    /**
     * 新規登録ボーナスマイル数を設定する。
     *
     * @param registBonusMileAmount
     *            新規登録ボーナスマイル数
     */
    public void setRegistBonusMileAmount(String registBonusMileAmount) {
        this.registBonusMileAmount = registBonusMileAmount;
    }

    /**
     * OP認証マイル加算減算受付番号を設定する。
     *
     * @param opAuthMileAddSubRcptNum
     *            OP認証マイル加算減算受付番号
     */
    public void setOpAuthMileAddSubRcptNum(String opAuthMileAddSubRcptNum) {
        this.opAuthMileAddSubRcptNum = opAuthMileAddSubRcptNum;
    }

    /**
     * OP認証ボーナスマイル数を設定する。
     *
     * @param opAuthBonusMileAmount
     *            OP認証ボーナスマイル数
     */
    public void setOpAuthBonusMileAmount(String opAuthBonusMileAmount) {
        this.opAuthBonusMileAmount = opAuthBonusMileAmount;
    }
}
