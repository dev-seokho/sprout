package com.sync.sprout.domain.command

data class CheckCountriesCommand(
    val countries: List<String> // ["KR", "JP"]
)
