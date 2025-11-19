package com.melowetty.hsepermhelper.controller.request

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.melowetty.hsepermhelper.domain.model.Field
import com.melowetty.hsepermhelper.serialization.FieldDeserializer

data class ApiUserUpdateRequest(
    @JsonDeserialize(using = FieldDeserializer::class)
    val group: Field<String> = Field.Unset,

    @JsonDeserialize(using = FieldDeserializer::class)
    val isEnabledNewScheduleNotifications: Field<Boolean> = Field.Unset,

    @JsonDeserialize(using = FieldDeserializer::class)
    val isEnabledChangedScheduleNotifications: Field<Boolean> = Field.Unset,

    @JsonDeserialize(using = FieldDeserializer::class)
    val isEnabledComingLessonsNotifications: Field<Boolean> = Field.Unset,
)
