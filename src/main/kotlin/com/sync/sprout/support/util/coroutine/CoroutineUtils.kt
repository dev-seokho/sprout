package com.sync.sprout.support.util.coroutine

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.slf4j.MDCContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.NestedExceptionUtils
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

class SproutCoroutineExceptionHandler :
    AbstractCoroutineContextElement(CoroutineExceptionHandler), CoroutineExceptionHandler {

    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    override fun handleException(context: CoroutineContext, exception: Throwable) {
        (NestedExceptionUtils.getRootCause(exception) ?: exception)
            .also { logger.error(it.message, it) }
    }
}

fun <T> withBlocking(
    context: CoroutineContext = EmptyCoroutineContext,
    block: suspend CoroutineScope.() -> T,
) = runBlocking(context + SupervisorJob() + SproutCoroutineExceptionHandler() + MDCContext()) {
    block()
}

fun CoroutineScope.withLaunch(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit,
): Job {
    return this.async(context + SupervisorJob() + SproutCoroutineExceptionHandler() + MDCContext(), start, block)
}

fun <T> CoroutineScope.withAsync(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> T,
): Deferred<T> {
    return this.async(context + SupervisorJob() + SproutCoroutineExceptionHandler() + MDCContext(), start, block)
}

suspend fun <T, R> Iterable<T>.pmap(
    block: suspend CoroutineScope.(T) -> R,
): Pair<List<R>, List<Throwable>> = coroutineScope {
    val results = map {
        withAsync<AsyncMapResult<R>> {
            try {
                AsyncSuccess(block(it))
            } catch (e: Exception) {
                AsyncFail(e)
            }
        }
    }.awaitAll()

    val (successes, exceptions) = results.partition { it is AsyncSuccess }

    Pair(successes.mapNotNull { it.value }, exceptions.map { (it as AsyncFail).throwable })
}

suspend fun <T> Pair<List<T>, List<Throwable>>.onFailure(
    action: CoroutineScope.(exceptions: List<Throwable>, success: List<T>) -> Unit,
): Pair<List<T>, List<Throwable>> = coroutineScope {
    val (successes, exceptions) = this@onFailure
    if (exceptions.isNotEmpty()) {
        action.invoke(this, exceptions, successes)
    }
    this@onFailure
}

fun <T> Pair<List<T>, List<Throwable>>.onErrorReturn(
    transform: (Pair<List<T>, List<Throwable>>) -> Pair<List<T>, List<Throwable>>
): Pair<List<T>, List<Throwable>> {
    val (successes, exceptions) = this
    return if (exceptions.isNotEmpty()) transform(this) else successes to exceptions
}

fun <T> Pair<List<T>, List<Throwable>>.getOrEmpty(): List<T> = this.first

fun <T> Pair<List<T>, List<Throwable>>.getOrThrow(): List<T> {
    val (successes, exceptions) = this
    if (exceptions.isNotEmpty()) {
        throw exceptions.first()
    }
    return successes
}