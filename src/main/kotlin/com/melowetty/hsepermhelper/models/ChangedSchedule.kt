package com.melowetty.hsepermhelper.models

import Schedule

data class ChangedSchedule(
    val before: Schedule?,
    val after: Schedule?
)