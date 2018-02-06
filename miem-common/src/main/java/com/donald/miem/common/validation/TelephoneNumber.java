package com.donald.miem.common.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * 電話番号の形式であることを表わすアノテーション。
 *
 * @author 趙
 * @since 1.0
 */
@Documented
@Constraint(validatedBy = TelephoneNumberValidator.class)
@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface TelephoneNumber {

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
     * @author 趙
     * @since 1.0
     */
    @Target({ ElementType.FIELD, ElementType.METHOD })
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        /**
         * TelephoneNumberの配列を取得する。
         *
         * @return {@link TelephoneNumber}の配列
         */
        TelephoneNumber[] value();
    }
}