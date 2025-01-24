package org.chewing.v1.util

import kotlinx.coroutines.*
import org.springframework.stereotype.Component

@Component
class AsyncJobExecutor(
    @IoScope private val ioScope: CoroutineScope,
) {

    fun <T> executeAsyncJobs(
        items: List<T>,
        action: suspend (T) -> Unit,
    ) = runBlocking {
        val jobs = items.map { item ->
            ioScope.async {
                action(item)
            }
        }
        jobs.awaitAll()
    }

    fun <T> executeAsyncJob(
        item: T,
        action: suspend (T) -> Unit,
    ) = runBlocking {
        val job = ioScope.async {
            action(item)
        }
        job.await()
    }

    fun <T, R> executeAsyncReturnJob(
        item: T,
        action: suspend (T) -> R,
    ): R = runBlocking {
        val job = ioScope.async {
            action(item)
        }
        job.await()
    }
}
