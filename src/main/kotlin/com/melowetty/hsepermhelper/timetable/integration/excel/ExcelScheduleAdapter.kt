package com.melowetty.hsepermhelper.timetable.integration.excel

import com.melowetty.hsepermhelper.domain.model.file.File
import com.melowetty.hsepermhelper.util.RowUtils.Companion.getCellValue
import io.github.oshai.kotlinlogging.KotlinLogging
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.springframework.stereotype.Component

@Component
class ExcelScheduleAdapter(
    private val storage: ExcelTimetableStorage,
    processors: List<ExcelScheduleProcessor>,
) {
    private val prioritizedProcessors: List<ExcelScheduleProcessor> =
        processors.sortedByDescending { it.priority() }

    fun processAndPersist(file: File) {
        val processor = prioritizedProcessors.firstOrNull {
            it.isParseable(file.name)
        } ?: run {
            logger.warn { "No processor found for file ${file.name}" }
            return
        }

        val workbook = WorkbookFactory.create(file.toInputStream())
        preprocessWorkbook(workbook)

        logger.info { "Processing file ${file.name} with ${workbook.numberOfSheets} sheets" }

        val timetables = processor.process(workbook)

        logger.info { "Processed ${timetables.size} timetables" }

        timetables.map {
            storage.saveTimetable(it)
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