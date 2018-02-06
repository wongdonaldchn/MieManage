package com.donald.miem.common.encryptor;

import java.security.NoSuchAlgorithmException;

/**
 * メールアドレス認証コードハッシュ化インタフェース
 *
 * @author 張
 * @since 1.0
 */
public interface MailAddressAuthCodeHashConverter {

    /**
     * ランダムな文字列を生成する。
     *
     * @param num
     *            桁数
     * @return ランダムな文字列
     */
    String getRandomString(Integer num);

    /**
     * SHA-256でハッシュ化（不可逆）された認証コードを生成する。
     *
     * @param authKey
     *            認証キー
     * @param expiDate
     *            有効期限
     * @param authSalt
     *            認証SALT
     * @return 認証コード
     * @throws NoSuchAlgorithmException
     *             エラーする
     */
    String getHashCode(Long authKey, String expiDate, String authSalt) throws NoSuchAlgorithmException;

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
    String getHashCode(String password, String passwordSalt, int stretchingTimes) throws NoSuchAlgorithmException;
}
