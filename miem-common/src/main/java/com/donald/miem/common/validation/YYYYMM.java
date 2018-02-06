package com.donald.miem.common.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * 許容する年月フォーマットを指定するアノテーション。
 *
 * @author 張
 * @since 1.0
 */
@Documented
@Constraint(validatedBy = YYYYMMValidator.class)
@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface YYYYMM {

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
     * 許容するフォーマット
     *
     * @return 許容するフォーマット
     */
    String allowFormat() default "yyyyMM";

    /**
     * 複数指定用のアノテーション
     *
     * @author 張
     * @since 1.0
     */
    @Target({ ElementType.FIELD, ElementType.METHOD })
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        /**
         * YYYYMMの配列を取得する。
         *
         * @return {@link YYYYMM}の配列
         */
        YYYYMM[] value();
    }
}
