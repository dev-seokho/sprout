package com.sync.sprout.infrastructure.insurance.request

import com.sync.sprout.domain.command.EnrollCommand

data class EnrollRequest(
    val userId: String,
    val coverage: String,
) {
    companion object{
        fun of(
            command: EnrollCommand
        ): EnrollRequest {
            return EnrollRequest(
                userId = command.userId,
                coverage = command.coverage,
            )
        }
    }
}
