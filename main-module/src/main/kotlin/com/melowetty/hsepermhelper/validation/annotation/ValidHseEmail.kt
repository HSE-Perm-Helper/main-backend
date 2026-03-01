package com.melowetty.hsepermhelper.validation.annotation

import com.melowetty.hsepermhelper.validation.validator.ValidHseEmailValidator
import jakarta.validation.Constraint
import jakarta.validation.Payload
import jakarta.validation.constraints.Email
import java.lang.annotation.ElementType
import kotlin.reflect.KClass

@MustBeDocumented
@Constraint(validatedBy = [ValidHseEmailValidator::class])
@Target(
    AnnotationTarget.CLASS, AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER
)
@Retention(AnnotationRetention.RUNTIME)
@Email
annotation class ValidHseEmail(
    val message: String = "Invalid email format",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)