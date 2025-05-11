package com.sync.sprout.support.util.coroutine

import java.io.Serializable

interface AsyncMapResult<T>: Serializable {
    val value: T?
}

data class AsyncSuccess<T>(
    override val value: T?,
) : AsyncMapResult<T>

data class AsyncFail<T>(
    val throwable: Throwable,
) : AsyncMapResult<T> {
    override val value: T? = null
}