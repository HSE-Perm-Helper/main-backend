package com.melowetty.hsepermhelper.messaging.event.notification

import com.fasterxml.jackson.annotation.JsonIgnore

abstract class Notification {
    @JsonIgnore
    abstract fun getNotificationType(): String
}