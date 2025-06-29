package com.sync.sprout.infrastructure.notification

import com.sync.sprout.domain.model.InsuranceModel
import org.springframework.stereotype.Component

@Component
class NotificationClient {

    fun sendEnrollEmail(
        insurance: InsuranceModel,
    ) {
        TODO("call [POST] https://sprout-api/notification-email")
    }

    fun sendEnrollMessage(
        insurance: InsuranceModel,
    ) {
        TODO("call [POST] https://sprout-api/notification-message")
    }
}