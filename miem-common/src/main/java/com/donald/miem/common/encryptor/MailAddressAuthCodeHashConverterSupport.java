package com.donald.miem.common.encryptor;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * メールアドレス認証コードハッシュ化実行クラス
 *
 * @author 張
 * @since 1.0
 */
public class MailAddressAuthCodeHashConverterSupport implements MailAddressAuthCodeHashConverter {

    /**
     * ランダムな文字列を生成する。
     *
     * @param num
     *            桁数
     * @return ランダムな文字列
     */
    public String getRandomString(Integer num) {

        String word = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

        String randomString = "";
        for (int i = 0; i < num; i++) {
            // ランダム処理
            int rand = (int) (Math.random() * word.length());
            char chr = word.charAt(rand);
            randomString += chr;
        }
        return randomString;
    }

    /**
     * SHA-256でハッシュ化（不可逆）された認証コードを生成する。
     *
     * @param authKey
     *            メールアドレス認証キー
     * @param expiDate
     *            有効期限
     * @param authSalt
     *            メールアドレス認証SALT
     * @return 認証コード
     * @throws NoSuchAlgorithmException
     *             エラーする
     */
    public String getHashCode(Long authKey, String expiDate, String authSalt) throws NoSuchAlgorithmException {

        // 「メールアドレス認証キー」＋「有効期限」＋「メールアドレス認証SALT」をハッシュ化（不可逆）した値を戻る。
        StringBuilder text = new StringBuilder();
        text.append(String.valueOf(authKey));
        text.append(expiDate);
        text.append(authSalt);
        String result = getSHA(text.toString(), "SHA-256");
        return result;
    }

    /**
     * SHA-256でハッシュ化（不可逆）された認証コードを生成する。
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
    public String getHashCode(String password, String passwordSalt, int stretchingTimes)
            throws NoSuchAlgorithmException {

        // パスワード ＋ パスワードSALT
        StringBuilder text = new StringBuilder();
        text.append(password);
        text.append(passwordSalt);
        // パスワード ＋ パスワードSALT をsha-256を使用してハッシュ化（不可逆）
        String result = getSHA(text.toString(), "SHA-256");

        if (stretchingTimes > 1) {
            for (int i = 1; i <= stretchingTimes; i++) {
                text.setLength(0);
                text.append(result);
                text.append(password);
                text.append(passwordSalt);

                result = getSHA(text.toString(), "SHA-256");
            }
        }
        return result;
    }

    /**
     * SHA-256でハッシュ化（不可逆）処理
     *
     * @param text
     *            ハッシュ化対象文字列
     * @param type
     *            ハッシュ化種別
     * @return 認証コード
     * @throws NoSuchAlgorithmException
     *             エラーする
     */
    private String getSHA(final String text, final String type) throws NoSuchAlgorithmException {
        String result = "";

        MessageDigest messageDigest = MessageDigest.getInstance(type);
        messageDigest.update(text.getBytes());
        byte[] byteBuffer = messageDigest.digest();

        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < byteBuffer.length; i++) {
            String hex = Integer.toHexString(0xff & byteBuffer[i]);
            if (hex.length() == 1) {
                hexString.append("0");
            }
            hexString.append(hex);
        }
        result = hexString.toString();

        return result;
    }

}
