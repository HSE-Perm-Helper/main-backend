package com.melowetty.hsepermhelper.serialization

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.BeanProperty
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.deser.ContextualDeserializer
import com.melowetty.hsepermhelper.domain.model.Field

class FieldDeserializer : JsonDeserializer<Field<*>>(), ContextualDeserializer {
    private var valueType: JavaType? = null

    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Field<Any?> {
        val node: JsonNode = p.codec.readTree(p)
        return if (node.isNull) {
            Field.Set(null)
        } else {
            val value = if (valueType != null) {
                p.codec.treeToValue(node, valueType!!.rawClass)
            } else {
                node.toString()
            }
            Field.Set(value)
        }
    }

    override fun createContextual(ctxt: DeserializationContext, property: BeanProperty?): JsonDeserializer<*> {
        val type = property?.type
        val container = type?.containedType(0)
        return FieldDeserializer().apply { valueType = container }
    }
}