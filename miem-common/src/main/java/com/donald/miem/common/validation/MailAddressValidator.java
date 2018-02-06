package com.donald.miem.common.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import nablarch.core.util.StringUtil;

/**
 * メールアドレスが有効であるかをチェックするバリデータクラス。
 *
 * @author 張
 * @since 1.0
 */
public class MailAddressValidator implements ConstraintValidator<MailAddress, String> {

    @Override
    public void initialize(MailAddress constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (StringUtil.isNullOrEmpty(value)) {
            return true;
        }
        if (!VariousValidationUtil.isValidMailAddress((String) value)) {
            return false;
        }
        return true;
    }
}
