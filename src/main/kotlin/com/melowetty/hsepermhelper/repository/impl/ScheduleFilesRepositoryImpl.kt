package com.melowetty.hsepermhelper.repository.impl

import com.melowetty.hsepermhelper.repository.ScheduleFilesRepository
import org.jsoup.Jsoup
import org.springframework.stereotype.Component
import java.io.InputStream
import java.net.URL
import java.security.cert.X509Certificate
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

@Component
class ScheduleFilesRepositoryImpl : ScheduleFilesRepository {
    private var scheduleFiles: List<ByteArray> = listOf()

    init {
        disableSSLVerification()
        fetchScheduleFiles()
    }

    final override fun fetchScheduleFiles() {
        val response = Jsoup.connect(SCHEDULE_BASE_URL).get()
        val elements = response.select(".content__inner.post__text p")
        val files = mutableListOf<InputStream>()
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
            if (inputStream != null) files.add(inputStream)
        }
        scheduleFiles = files.map { it.readAllBytes() }
    }

    private fun isProcessable(name: String): Boolean {
        val banWords = setOf("английский", "программные системы")

        for (word in banWords) {
            if (name.contains(word)) return false
        }

        return true
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

    override fun getScheduleFilesAsByteArray(): List<ByteArray> {
        return scheduleFiles
    }

    companion object {
        const val SCHEDULE_BASE_URL = "https://students.perm.hse.ru/timetable"
        const val SCHEDULE_BASE_DOWNLOAD_URL = "https://students.perm.hse.ru"

        fun disableSSLVerification() {
            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
                override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
                override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
            })

            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, java.security.SecureRandom())
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.socketFactory)
            HttpsURLConnection.setDefaultHostnameVerifier { _, _ -> true }
        }
    }
}