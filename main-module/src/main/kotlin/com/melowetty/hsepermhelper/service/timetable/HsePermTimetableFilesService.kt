package com.melowetty.hsepermhelper.service.timetable

import com.melowetty.hsepermhelper.domain.model.file.File
import com.melowetty.hsepermhelper.util.RetryUtils
import io.github.oshai.kotlinlogging.KotlinLogging
import org.jsoup.Jsoup
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.InputStream
import java.net.URL
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@Service
class HsePermTimetableFilesService(
    @Value("\${api.timetable.base-url}")
    private val timetableBaseUrl: String,

    @Value("\${api.timetable.download-url}")
    private val timetableDownloadUrl: String
) {
    fun getTimetableFiles(): List<File> {
        val pathsForDownloadByName = getPathsForDownloadByName()

        val files = mutableListOf<File>()

        for ((name, path) in pathsForDownloadByName) {
            val inputStream = downloadFileAsInputStream(path)
            if (inputStream != null) {
                val file = File(
                    data = inputStream.readAllBytes(),
                    name = name,
                )
                files.add(file)
            }
        }

        if (pathsForDownloadByName.isNotEmpty() && files.isEmpty()) {
            throw RuntimeException("Failed to download any files")
        }

        return files
    }

    private fun getPathsForDownloadByName(): Map<String, String> {
        return RetryUtils.retryWithExponentialBackoff(
            maxAttempts = 5,
            exceptionallyBlock = {
                logger.error(it) { "Failed to fetch timetables files" }
                throw it
            }
        ) {
            val response = Jsoup.connect(timetableBaseUrl).get()

            val elements = response.select(".content__inner.post__text p")
            val pathsForDownloadByName = mutableMapOf<String, String>()

            for (element in elements) {
                val html = element.html()

                if (html.contains("Бакалавриат")) continue
                if (html.contains("Магистратура")) break

                if (!isProcessable(html.lowercase())) continue

                val childLink = element.select("a")
                if (childLink.isEmpty()) continue
                val link = childLink.attr("href")
                if (link.isEmpty()) continue
                pathsForDownloadByName[element.text()] = link
            }

            pathsForDownloadByName
        }
    }

    private fun isProcessable(name: String): Boolean {
        val banWords = setOf("английский", "англ", "управление бизнесом")

        for (word in banWords) {
            if (name.contains(word)) return false
        }

        return true
    }

    private fun downloadFileAsInputStream(path: String): InputStream? {
        return RetryUtils.retryWithExponentialBackoff(
            maxAttempts = 5,
            initialDelay = 3.toDuration(DurationUnit.SECONDS),
            multiplier = 3.0,
            exceptionallyBlock = {
                logger.error(it) { "Failed to download file by path $path" }
                null
            }
        ) {
            val urlParts = path.split("/data")
            val url = "$timetableDownloadUrl/data${urlParts[1]}"
            val connection = URL(url).openConnection()
            connection.useCaches = false
            connection.getInputStream()
        }
    }

    companion object {
        private val logger = KotlinLogging.logger {  }
    }
}