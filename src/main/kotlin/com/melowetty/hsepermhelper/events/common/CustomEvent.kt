package com.melowetty.hsepermhelper.events.common

open class CustomEvent<T>(
    val source: T,
    val type: EventType
)