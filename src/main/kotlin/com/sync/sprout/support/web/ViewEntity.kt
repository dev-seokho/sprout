package com.sync.sprout.support.web

import org.springframework.http.HttpStatus

sealed class ViewEntity (
    open val code: String
) {
    data class Success(
        override val code: String = HttpStatus.OK.name,
        val data: Any? = null,
    ) : ViewEntity(code = code)

    data class Error(
        override val code: String,
        val title: String? = null,
        val message: String,
    ) : ViewEntity(code = code)
}