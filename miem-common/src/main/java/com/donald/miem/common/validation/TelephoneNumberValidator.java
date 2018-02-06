package com.donald.miem.common.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 電話番号の形式であることをであるかをチェックするクラス。 <br>
 * <br>
 * 電話番号の精査仕様 <br>
 * <br>
 *
 * @author 趙
 * @since 1.0
 */
public class TelephoneNumberValidator implements ConstraintValidator<TelephoneNumber, String> {

    /**
     * 電話番号で入力可能な文字種を示す正規表現
     */
    private static final String TELPHONE_NUMBER_REGEX = "[0-9\\-]+";

    /**
     * 電話番号の最大桁数
     */
    private static final int MAX_LENGTH = 15;

    @Override
    public void initialize(TelephoneNumber constraintAnnotation) {

    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        if (value == null || value.length() == 0) {
            return true;
        }

        // 文字種精査
        if (!value.matches(TELPHONE_NUMBER_REGEX)) {
            return false;
        }

        // 文字長精査
        if (value.length() > MAX_LENGTH) {
            return false;
        }

        return true;
    }
}
