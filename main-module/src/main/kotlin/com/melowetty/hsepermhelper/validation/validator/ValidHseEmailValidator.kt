package com.melowetty.hsepermhelper.validation.validator

import com.melowetty.hsepermhelper.validation.annotation.ValidHseEmail
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class ValidHseEmailValidator: ConstraintValidator<ValidHseEmail, String> {
    override fun isValid(value: String, context: ConstraintValidatorContext): Boolean {
        val normalizedValue = value.lowercase()

        return normalizedValue.endsWith("@edu.hse.ru")
                || normalizedValue.endsWith("@hse.ru")
    }
}