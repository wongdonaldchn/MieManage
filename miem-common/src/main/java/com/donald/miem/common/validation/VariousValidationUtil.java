package com.donald.miem.common.validation;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import nablarch.core.util.StringUtil;

/**
 * 拡張バリデーションに関するユーティリティクラス。
 *
 * @author 張
 * @since 1.0
 */
public final class VariousValidationUtil {

    /**
     * 隠蔽コンストラクタ
     */
    private VariousValidationUtil() {
    }

    /**
     */
    public static final String AVAILABLE_CHARS_FOR_MAIL_ADDRESS = "$%&\\*+-./0123456789=?@ABCDEFGHIJKLMNOPQRSTUVWXYZ^_`"
            + "abcdefghijklmnopqrstuvwxyz{|}~!#";

    /**
     * メールアドレスのローカルパートの最大長
     */
    private static final int MAX_LENGTH_OF_LOCAL_PART = 64;

    /**
     * メールアドレスのドメインパートの最大長
     */
    private static final int MAX_LENGTH_OF_DOMAIN_PART = 135;

    /**
     * メールアドレスに関する精査を行う。<br>
     * RFCに則ったローカル部とドメイン部それぞれの桁数精査を行う。</br>
     * アプリケーションで許容するメールアドレス全体の長さはこのメソッドの呼び出し側で精査すること。<br>
     * <br>
     * 精査仕様：<br>
     * ローカル部に関しては、RFC違反のメールアドレスも存在しえる。そのため、ローカル部に対して厳密なチェックを行うことは、
     * ユーザーがメールを登録できない危険性がある。 また、ローカル部に対して厳密なチェックを行わなくても害はないと判断している。
     * よって、ローカル部に対して行う精査は、桁数と文字種に関する精査のみである。<br>
     * また、これらの精査に加えて、JavaMailのアドレス精査を行うAPIを直接呼び出すことで
     * JavaMailでメールを送信する際に形式チェックでエラーとならないことも検証している。
     *
     * <br>
     * メールアドレスに関する精査仕様は下記の通りである。
     * <ul>
     * <li>メールアドレス全体に関する精査仕様</li>
     * <ul>
     * <li>空文字、nullでないこと。</li>
     * <li>メールアドレスとして有効な文字種のみで構成されていること。有効な文字種は、次の通りである。</li>
     * <ul>
     * <li>大文字アルファベット A B C D E F G H I J K L M N O P Q R S T U V W X Y Z</li>
     * <li>小文字アルファベット a b c d e f g h i j k l m n o p q r s t u v w x y z</li>
     * <li>数字 0 1 2 3 4 5 6 7 8 9</li>
     * <li>その他記号 ! # $ % & * + - . / = ? @ ^ _ ` { | } ~</li>
     * </ul>
     * <li>‘@’（アットマーク）が存在し、1つのみであること。</li>
     * </ul>
     * <li>ローカル部に関する精査仕様</li>
     * <ul>
     * <li>メールアドレスの先頭が’@’（アットマーク）ではないこと。（ローカル部が存在すること。）</li>
     * <li>ローカル部が64文字以下であること。</li>
     * </ul>
     * <li>ドメイン部に関する精査仕様</li>
     * <ul>
     * <li>メールアドレスの末尾が’@’（アットマーク）ではないこと。（ドメイン部が存在すること。）</li>
     * <li>ドメイン部が255文字以下であること。</li>
     * <li>ドメイン部の末尾が’.’（ドット）ではないこと。</li>
     * <li>ドメイン部に’.’（ドット）が存在すること。</li>
     * <li>ドメイン部の先頭が’.’（ドット）ではないこと。</li>
     * <li>ドメイン部にて’.’（ドット）が連続していないこと。</li>
     * </ul>
     * </ul>
     *
     * @param value
     *            精査対象文字列
     * @return 上記の精査仕様に則った有効なメールアドレスの場合、{@code true}。
     */
    public static boolean isValidMailAddress(String value) {

        if (StringUtil.isNullOrEmpty(value)) {
            return false;
        }

        // ローカル部の長さチェック
        int indexOfAtMark = value.indexOf('@');
        if (indexOfAtMark > MAX_LENGTH_OF_LOCAL_PART) {
            return false;
        }

        // @に関するチェック
        if (indexOfAtMark <= 0) {
            // @が先頭にある場合
            // @が存在しない場合
            return false;
        }
        if (indexOfAtMark != value.lastIndexOf('@')) {
            // @が二つ以上存在する場合
            return false;
        }
        if (indexOfAtMark == value.length() - 1) {
            // @が末尾に存在する場合
            return false;
        }

        // "."に関するチェック
        String domainPart = value.substring(indexOfAtMark + 1);
        if (domainPart.length() > MAX_LENGTH_OF_DOMAIN_PART || domainPart.endsWith(".") || domainPart.indexOf('.') <= 0
                || domainPart.contains("..")) {
            return false;
        }

        // 文字種に関するチェック
        for (int i = 0; i < value.length(); i++) {
            if (AVAILABLE_CHARS_FOR_MAIL_ADDRESS.indexOf(value.charAt(i)) < 0) {
                return false;
            }
        }

        try {
            new InternetAddress(value, false);

        } catch (AddressException e) {
            return false;
        }

        return true;
    }
}
