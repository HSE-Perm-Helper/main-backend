package com.melowetty.hsepermhelper.repository.impl

import com.melowetty.hsepermhelper.repository.ScheduleFilesRepository
import org.jsoup.Jsoup
import org.springframework.stereotype.Component
import java.io.InputStream
import java.net.URL

@Component
class ScheduleFilesRepositoryImpl: ScheduleFilesRepository {
    override fun fetchScheduleFilesAsInputStream(): List<InputStream> {
        val response = Jsoup.connect(SCHEDULE_BASE_URL).get()
        val elements = response.select(".content__inner.post__text p")
        val files = mutableListOf<InputStream>()
        for (element in elements) {
            if (element.html().contains("Бакалавриат")) continue
            if(element.html().contains("Магистратура")) break
            val childLink = element.select("a")
            if (childLink.isEmpty()) continue
            val link = childLink.attr("href")
            if (link.isEmpty()) continue
            val inputStream = downloadFileAsInputStream(path = link)
            if (inputStream != null) files.add(inputStream)
        }
        return files
    }
    private fun downloadFileAsInputStream(path: String): InputStream? {
        return try {
            URL("$SCHEDULE_BASE_URL${path}").openStream()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    companion object {
        const val SCHEDULE_BASE_URL = "http://students.perm.hse.ru/timetable"
    }
}