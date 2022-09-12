package com.soywiz.korio.async

import com.soywiz.korio.concurrent.createFixedThreadDispatcher
import kotlinx.coroutines.*
import java.util.concurrent.*
import kotlin.coroutines.*

fun <T> Deferred<T>.jvmSyncAwait(): T = runBlocking { await() }

operator fun ExecutorService.invoke(callback: () -> Unit) {
	this.execute(callback)
}

private val mainDispatcher by lazy { Dispatchers.createFixedThreadDispatcher("mainDispatcher", 1) }
internal val workerContext by lazy { Dispatchers.createFixedThreadDispatcher("worker", 4) }

actual fun asyncEntryPoint(callback: suspend () -> Unit) =
    //runBlocking { callback() }
	runBlocking(mainDispatcher) { callback() }
actual fun asyncTestEntryPoint(callback: suspend () -> Unit) =
    //runBlocking { callback() }
    runBlocking(mainDispatcher) { callback() }

suspend fun <T> executeInWorkerJVM(callback: suspend () -> T): T = withContext(workerContext) { callback() }
