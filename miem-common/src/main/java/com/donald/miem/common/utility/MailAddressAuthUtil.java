package com.donald.miem.common.utility;

import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.donald.miem.common.encryptor.MailAddressAuthCodeHashConverter;
import nablarch.core.date.SystemTimeUtil;
import nablarch.core.repository.SystemRepository;

/**
 * メールアドレス認証用ユーティリティ
 *
 * @author 張
 * @since 1.0
 */
public final class MailAddressAuthUtil {

    /**
     * メールアドレス認証用ユーティリティの初期化
     */
    private MailAddressAuthUtil() {
    }

    /**
     * ランダムな文字列を生成する。
     *
     * @param num
     *            桁数
     * @return ランダムな文字列
     */
    public static String getRandomString(Integer num) {
        MailAddressAuthCodeHashConverter encryptor = (MailAddressAuthCodeHashConverter) SystemRepository
                .getObject("passwordEncryptor");
        return encryptor.getRandomString(num);
    }

    /**
     * SHA-256でハッシュ化（不可逆）されたメールアドレス認証コードを生成する。
     *
     * @param mailAddressAuthKey
     *            メールアドレス認証キー
     * @param expiDate
     *            有効期限
     * @param mailAddressAuthSalt
     *            メールアドレス認証SALT
     * @return 認証コード
     * @throws NoSuchAlgorithmException
     *             エラーする
     */
    public static String getMailAddressAuthCode(Long mailAddressAuthKey, String expiDate, String mailAddressAuthSalt)
            throws NoSuchAlgorithmException {

        MailAddressAuthCodeHashConverter encryptor = (MailAddressAuthCodeHashConverter) SystemRepository
                .getObject("passwordEncryptor");
        // 「メールアドレス認証キー」＋「有効期限」＋「メールアドレス認証SALT」をハッシュ化（不可逆）した値を戻る。
        return encryptor.getHashCode(mailAddressAuthKey, expiDate, mailAddressAuthSalt);
    }

    /**
     * メールアドレス認証有効期限を取得する。（システム日時 + 有効期間）
     *
     * @param day
     *            有効期限
     * @return メールアドレス認証有効期限（Calendar日付）
     */
    public static Calendar getExpiDate(int day) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        // メールアドレス認証有効期限（yyyyMMddHHmmssSSS）の算出（システム日時 + 有効期間）
        String sysDateTime = sdf.format(SystemTimeUtil.getTimestamp());
        Calendar calendar = DateConvertUtil.stringToCalendar(sysDateTime);
        calendar.add(Calendar.DATE, day);

        return calendar;
    }

    /**
     * SHA-256でハッシュ化（不可逆）されたパスワード認証コードを生成する。
     *
     * @param password
     *            パスワード
     * @param passwordSalt
     *            パスワードSALT
     * @param stretchingTimes
     *            ストレッチング回数
     * @return 認証コード
     * @throws NoSuchAlgorithmException
     *             エラーする
     */
    public static String getPasswordAuthCode(String password, String passwordSalt, int stretchingTimes)
            throws NoSuchAlgorithmException {

        MailAddressAuthCodeHashConverter encryptor = (MailAddressAuthCodeHashConverter) SystemRepository
                .getObject("passwordEncryptor");
        // パスワード ＋ パスワードSALT をsha-256を使用してハッシュ化（不可逆）
        return encryptor.getHashCode(password, passwordSalt, stretchingTimes);
    }
}
