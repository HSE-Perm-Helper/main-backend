package com.melowetty.hsepermhelper.annotation

import com.melowetty.hsepermhelper.domain.model.event.UserEventType

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class TrackUserEvent(
    val value: UserEventType,
)