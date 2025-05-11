package com.sync.sprout.support.annotation

import com.sync.sprout.support.web.MessageKey

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class ErrorCodeExample(
    val messageKeys: Array<MessageKey>
)
