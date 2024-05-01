package com.melowetty.hsepermhelper.repository.impl

import com.melowetty.hsepermhelper.repository.ScheduleFilesRepository
import org.jsoup.Jsoup
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.io.InputStream
import java.net.URL

@Component
class ScheduleFilesRepositoryImpl: ScheduleFilesRepository {
    init {
        fetchScheduleFilesAsInputStream()
    }
    private var scheduleFiles: List<InputStream> = listOf()
    final override fun fetchScheduleFilesAsInputStream() {
        val response = Jsoup.connect(SCHEDULE_BASE_URL).get()
        val elements = response.select(".content__inner.post__text p")
        val files = mutableListOf<InputStream>()
        for (element in elements) {
            if (element.html().contains("Бакалавриат")) continue
            if(element.html().lowercase().contains("английский")) continue
            if(element.html().contains("Магистратура")) break
            val childLink = element.select("a")
            if (childLink.isEmpty()) continue
            val link = childLink.attr("href")
            if (link.isEmpty()) continue
            val inputStream = downloadFileAsInputStream(path = link)
            if (inputStream != null) files.add(inputStream)
        }
        scheduleFiles = files
    }

    private fun downloadFileAsInputStream(path: String): InputStream? {
        return try {
            val urlParts = path.split("/data")
            URL("$SCHEDULE_BASE_DOWNLOAD_URL/data${urlParts[1]}").openStream().readAllBytes().inputStream()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun getScheduleFiles(): List<InputStream> {
        return scheduleFiles
    }

    companion object {
        const val SCHEDULE_BASE_URL = "http://students.perm.hse.ru/timetable"
        const val SCHEDULE_BASE_DOWNLOAD_URL = "http://students.perm.hse.ru"
    }
}