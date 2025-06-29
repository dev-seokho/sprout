package com.sync.sprout.domain.command

data class CheckAvailabilityCommand(
    val countries: List<String>, // ["KR", "JP"]
    val policyNumber: String,
)
