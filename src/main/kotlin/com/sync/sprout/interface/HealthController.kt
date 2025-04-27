package com.sync.sprout.`interface`

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
class HealthController {

    @GetMapping("/health")
    @ResponseStatus(HttpStatus.OK)
    fun health(): String {
        return HttpStatus.OK.reasonPhrase
    }
}