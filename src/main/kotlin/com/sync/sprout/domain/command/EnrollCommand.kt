package com.sync.sprout.domain.command

data class EnrollCommand(
    val userId: String,
    val coverage: String,
)
