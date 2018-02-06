package com.donald.miem.common.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * メールアドレスであることを表すアノテーション。<br>
 *
 * @author 張
 * @since 1.0
 */
@Documented
@Constraint(validatedBy = MailAddressValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR,
        ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface MailAddress {
    /**
     * グループ
     *
     * @return グループ
     */
    Class<?>[] groups() default {};

    /**
     * バリデーションエラー時のメッセージ
     *
     * @return デフォルトのエラーメッセージ
     */
    String message() default "";

    /**
     * ペイロード
     *
     * @return Payload
     */
    Class<? extends Payload>[] payload() default {};

    /**
     * 複数指定用のアノテーション
     *
     * @author 張
     * @since 1.0
     */
    @Target({ ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR,
            ElementType.PARAMETER })
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        /**
         * MailAddressの配列を取得する。
         *
         * @return {@link MailAddress}の配列
         */
        MailAddress[] value();
    }
}
