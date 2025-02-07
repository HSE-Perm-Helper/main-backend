package com.melowetty.hsepermhelper.domain.model.event

import com.melowetty.hsepermhelper.model.excel.ExcelSchedule
import com.melowetty.hsepermhelper.model.excel.ExcelScheduleDifference

data class ExcelSchedulesChanging(
    val added: List<ExcelSchedule> = listOf(),
    val changed: List<ExcelScheduleDifference> = listOf(),
    val deleted: List<ExcelSchedule> = listOf(),
)