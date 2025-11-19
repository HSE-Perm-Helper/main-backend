package com.melowetty.hsepermhelper.util

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlin.math.pow
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

object RetryUtils {
    private val logger = KotlinLogging.logger { }

    /**
     * Выполняет переданный блок кода с повторными попытками при ошибках.
     *
     * @param T тип возвращаемого значения
     * @param maxAttempts максимальное количество попыток (включая первую)
     * @param initialDelay начальная задержка между попытками
     * @param multiplier множитель для экспоненциального роста задержки
     * @param exceptionallyBlock блок кода для обработки исключений
     * @param block блок кода для выполнения
     * @return результат выполнения блока
     */
    fun <T> retryWithExponentialBackoff(
        maxAttempts: Int = 3,
        initialDelay: Duration = 1_000.toDuration(DurationUnit.MILLISECONDS),
        multiplier: Double = 2.0,
        exceptionallyBlock: (Exception) -> T = {throw it},
        block: () -> T
    ): T {
        require(maxAttempts > 0) { "maxAttempts must be > 0" }
        require(initialDelay.isPositive()) { "initialDelay must be positive" }
        require(multiplier >= 1.0) { "multiplier must be >= 1.0" }

        var lastException: Exception? = null

        repeat(maxAttempts) { attempt ->
            val currentAttempt = attempt + 1
            try {
                return block()
            } catch (e: Exception) {
                lastException = e

                if (currentAttempt < maxAttempts) {
                    val delayMillis = (initialDelay.inWholeMilliseconds * (multiplier.pow(currentAttempt - 1))).toLong()
                    val delay = delayMillis.toDuration(DurationUnit.MILLISECONDS)

                    logger.warn {
                        "Attempt $currentAttempt failed: ${e.message}. " +
                                "Retrying in ${delay.inWholeSeconds}s (${delay.inWholeMilliseconds}ms)..."
                    }

                    Thread.sleep(delay.inWholeMilliseconds)
                } else {
                    logger.error(e) { "All $maxAttempts attempts failed" }
                }
            }
        }

        val exception = lastException ?: IllegalStateException("Block did not execute and no exception was thrown")

        return exceptionallyBlock(exception)
    }
}