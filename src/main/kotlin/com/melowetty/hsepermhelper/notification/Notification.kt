package com.melowetty.hsepermhelper.notification

import com.fasterxml.jackson.annotation.JsonIgnore

abstract class Notification {
    @JsonIgnore
    abstract fun getNotificationType(): String
}