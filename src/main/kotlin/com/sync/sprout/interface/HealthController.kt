package com.sync.sprout.`interface`

import com.sync.sprout.support.web.Constants
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
class HealthController {

    @GetMapping(Constants.Path.HEALTH)
    @ResponseStatus(HttpStatus.OK)
    fun health(): String {
        return HttpStatus.OK.reasonPhrase
    }
}