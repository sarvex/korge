package com.soywiz.korio.async

import kotlinx.coroutines.*

actual val Dispatchers.CIO: CoroutineDispatcher get() = Dispatchers.Unconfined
actual val Dispatchers.ResourceDecoder: CoroutineDispatcher get() = Dispatchers.CIO

