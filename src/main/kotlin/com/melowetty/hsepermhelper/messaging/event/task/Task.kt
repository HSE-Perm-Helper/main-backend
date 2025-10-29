package com.melowetty.hsepermhelper.messaging.event.task

import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "taskType")
sealed class Task(
    val taskType: TaskType
)