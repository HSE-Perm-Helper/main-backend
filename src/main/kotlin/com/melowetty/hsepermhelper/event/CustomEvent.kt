package com.melowetty.hsepermhelper.event

open class CustomEvent<T>(
    val source: T,
    val type: EventType
)