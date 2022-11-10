package com.example.uvanna.config

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import reactor.core.publisher.Mono


@Configuration
class WebClientConfig {


    private val logger = LoggerFactory.getLogger(WebClientConfig::class.java)

    fun requestLoggerFilter() = ExchangeFilterFunction.ofRequestProcessor {
        println("Logging request: ${it.method()} ${it.url()}")
        logger.info("Logging request: ${it.method()} ${it.url()}")

        Mono.just(it)
    }

    fun responseLoggerFilter() = ExchangeFilterFunction.ofResponseProcessor {
        println("Response status code: ${it.statusCode()}")
        logger.info("Response status code: ${it.statusCode()}")

        Mono.just(it)
    }
}