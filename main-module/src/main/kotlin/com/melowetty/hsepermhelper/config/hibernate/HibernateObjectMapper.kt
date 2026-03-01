package com.melowetty.hsepermhelper.config.hibernate

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.kotlinModule
import org.hibernate.type.descriptor.WrapperOptions
import org.hibernate.type.descriptor.java.JavaType
import org.hibernate.type.format.FormatMapper
import org.hibernate.type.format.jackson.JacksonJsonFormatMapper

class HibernateObjectMapper : FormatMapper {
    private var delegate: FormatMapper

    init {
        val objectMapper = createObjectMapper()
        delegate = JacksonJsonFormatMapper(objectMapper)
    }

    private fun createObjectMapper(): ObjectMapper {
        val objectMapper = ObjectMapper()
            .registerModule(JavaTimeModule())
            .registerModule(kotlinModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        return objectMapper
    }

    override fun <T> fromString(
        charSequence: CharSequence?,
        javaType: JavaType<T>?,
        wrapperOptions: WrapperOptions?
    ): T {
        return delegate.fromString(charSequence, javaType, wrapperOptions)
    }

    override fun <T> toString(t: T, javaType: JavaType<T>?, wrapperOptions: WrapperOptions?): String {
        return delegate.toString(t, javaType, wrapperOptions)
    }
}