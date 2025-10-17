package com.melowetty.hsepermhelper.timetable.integration.excel

import com.melowetty.hsepermhelper.domain.model.file.File
import com.melowetty.hsepermhelper.util.RowUtils.Companion.getCellValue
import io.github.oshai.kotlinlogging.KotlinLogging
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.springframework.stereotype.Component

@Component
class ExcelTimetableAdapter(
    private val storage: ExcelTimetableStorage,
    processors: List<ExcelTimetableProcessor>,
) {
    private val prioritizedProcessors: List<ExcelTimetableProcessor> =
        processors.sortedByDescending { it.priority() }

    fun processAndPersist(file: File): List<String> {
        // todo: идея такая, парсим все расписания как есть и сохраняем во временное хранилище,
        // после чего берем все timetable info, объединяем нужные расписания и сохраняем
        // после, берем по парно timetable info и сверяем контент пар в прод таблице и во временной таблице, но сначала чекаем хэш, если все окей,
        // то просто стираем из временной и также сохраняем айдишники которые мы процессили, если есть расписания которые мы не тронули,
        // то сносим их
        val processor = prioritizedProcessors.firstOrNull {
            it.isParseable(file.name)
        } ?: run {
            logger.warn { "No processor found for file ${file.name}" }
            return listOf()
        }

        val workbook = WorkbookFactory.create(file.toInputStream())
        preprocessWorkbook(workbook)

        logger.info { "Processing file ${file.name} with ${workbook.numberOfSheets} sheets" }

        val timetables = processor.process(workbook)

        logger.info { "Processed ${timetables.size} timetables" }

        val ids = timetables.map {
            storage.saveTimetableAsHidden(it).id()
        }

        logger.info { "Saved ${ids.size} timetables" }

        return ids
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