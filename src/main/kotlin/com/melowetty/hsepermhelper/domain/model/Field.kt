package com.melowetty.hsepermhelper.domain.model

sealed interface Field<out T> {
    data class Set<T>(val value: T) : Field<T>
    data object Unset : Field<Nothing>
}
