package com.melowetty.hsepermhelper.timetable.integration.excel

import com.melowetty.hsepermhelper.context.ExcelTimetableParseContextHolder
import com.melowetty.hsepermhelper.domain.model.file.File
import com.melowetty.hsepermhelper.notification.ServiceWarnNotification
import com.melowetty.hsepermhelper.service.NotificationService
import com.melowetty.hsepermhelper.util.RowUtils.Companion.getCellValue
import io.github.oshai.kotlinlogging.KotlinLogging
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.springframework.stereotype.Component

@Component
class ExcelTimetableAdapter(
    private val storage: ExcelTimetableStorage,
    private val notificationService: NotificationService,
    processors: List<ExcelTimetableProcessor>,
) {
    private val prioritizedProcessors: List<ExcelTimetableProcessor> =
        processors.sortedByDescending { it.priority() }

    fun processAndPersist(file: File): List<String> {
        val processor = prioritizedProcessors.firstOrNull {
            it.isParseable(file.name)
        } ?: run {
            logger.warn { "No processor found for file ${file.name}" }
            return listOf()
        }

        val workbook = WorkbookFactory.create(file.toInputStream())
        preprocessWorkbook(workbook)

        logger.info { "Processing file ${file.name} with ${workbook.numberOfSheets} sheets" }

        try {
            val timetables = processor.process(workbook)

            val parseContext = ExcelTimetableParseContextHolder.get()

            if (parseContext.errors.isNotEmpty()) {
                logger.warn { "Errors occurred during parsing: ${parseContext.errors}" }

                notificationService.sendNotificationV2(
                    ServiceWarnNotification(
                        "Ошибка при парсинге файла ${file.name}\n\nОшибки: ${parseContext.errors}",
                    )
                )
            }

            logger.info { "Processed ${timetables.size} timetables" }

            val ids = timetables.map {
                storage.saveTimetableAsHidden(it).id()
            }

            logger.info { "Saved ${ids.size} timetables" }

            return ids
        } finally {
            ExcelTimetableParseContextHolder.clear()
        }
    }

    private fun preprocessWorkbook(workbook: Workbook) {
        for (sheet in workbook.sheetIterator()) {
            unmergeRegions(sheet)
        }
    }

    private fun unmergeRegions(sheet: Sheet) {
        for (region in sheet.mergedRegions) {
            val cellValue = sheet.getRow(region.firstRow).getCellValue(region.firstColumn)
            for (cell in region) {
                sheet.getRow(cell.row).getCell(cell.column).setCellValue(cellValue)
            }
        }
    }

    companion object {
        private val logger = KotlinLogging.logger {  }
    }
}