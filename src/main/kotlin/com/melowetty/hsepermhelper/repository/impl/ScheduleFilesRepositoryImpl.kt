package com.melowetty.hsepermhelper.repository.impl

import com.melowetty.hsepermhelper.annotation.Slf4j
import com.melowetty.hsepermhelper.domain.model.file.File
import com.melowetty.hsepermhelper.repository.ScheduleFilesRepository
import java.io.InputStream
import java.net.URL
import org.jsoup.Jsoup
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Repository

@Repository
class ScheduleFilesRepositoryImpl(
    @Value("\${api.timetable.base-url}")
    private val timetableBaseUrl: String,

    @Value("\${api.timetable.download-url}")
    private val timetableDownloadUrl: String
) : ScheduleFilesRepository {
    private var scheduleFiles: List<File> = listOf()

    // TODO: Should be refactored
    init {
        this.fetchScheduleFiles()
    }

    override fun fetchScheduleFiles() {
        val response = Jsoup.connect(timetableBaseUrl).get()
        val elements = response.select(".content__inner.post__text p")
        val files = mutableListOf<File>()
        for (element in elements) {
            val html = element.html()

            if (html.contains("Бакалавриат")) continue
            if (html.contains("Магистратура")) break

            if (!isProcessable(html.lowercase())) continue

            val childLink = element.select("a")
            if (childLink.isEmpty()) continue
            val link = childLink.attr("href")
            if (link.isEmpty()) continue
            val inputStream = downloadFileAsInputStream(path = link)
            if (inputStream != null) {
                val file = File(
                    data = inputStream.readAllBytes(),
                    name = element.text()
                )
                files.add(file)
            }
        }
        scheduleFiles = files
    }

    private fun isProcessable(name: String): Boolean {
        val banWords = setOf("английский", "программные системы", "управление бизнесом", "модуль")

        for (word in banWords) {
            if (name.contains(word)) return false
        }

        return true
    }

    private fun downloadFileAsInputStream(path: String): InputStream? {
        return try {
            val urlParts = path.split("/data")
            val url = "$timetableDownloadUrl/data${urlParts[1]}"
            URL(url).openStream().readAllBytes().inputStream()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun getScheduleFiles(): List<File> {
        return scheduleFiles
    }
}