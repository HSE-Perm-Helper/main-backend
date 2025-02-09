package com.melowetty.hsepermhelper.domain.model.event

import com.melowetty.hsepermhelper.excel.model.ExcelSchedule
import com.melowetty.hsepermhelper.excel.model.ExcelScheduleDifference

data class ExcelSchedulesChanging(
    val added: List<ExcelSchedule> = listOf(),
    val changed: List<ExcelScheduleDifference> = listOf(),
    val deleted: List<ExcelSchedule> = listOf(),
)