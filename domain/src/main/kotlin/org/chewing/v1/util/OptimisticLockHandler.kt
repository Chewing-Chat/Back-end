package org.chewing.v1.util

import mu.KotlinLogging
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.stereotype.Component

@Component
class OptimisticLockHandler {
    private val logger = KotlinLogging.logger {}
    fun <T> retryOnOptimisticLock(
        maxRetry: Int = 5,
        initialDelay: Long = 100L,
        maxDelay: Long = 800L,
        action: () -> T,
    ): T? {
        var retryCount = 0
        var delayTime = initialDelay

        while (retryCount < maxRetry) {
            try {
                return action()
            } catch (ex: OptimisticLockingFailureException) {
                retryCount++
                Thread.sleep(delayTime)
                delayTime = (delayTime * 2).coerceAtMost(maxDelay)
                logger.info("OptimisticLockingFailureException retryCount: $retryCount")
            }
        }
        return null
    }
}
