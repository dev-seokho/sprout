package com.sync.sprout

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SproutApplication

fun main(args: Array<String>) {
	runApplication<SproutApplication>(*args)
}
