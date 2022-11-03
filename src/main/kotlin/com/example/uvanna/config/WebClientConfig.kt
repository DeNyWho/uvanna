package com.example.uvanna.config

import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.http.codec.json.KotlinSerializationJsonDecoder
import org.springframework.http.codec.json.KotlinSerializationJsonEncoder
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.netty.http.client.HttpClient


@Configuration
class WebClientConfig {

    private val logger = LoggerFactory.getLogger(WebClientConfig::class.java)
    @Value("\${token}")
    lateinit var token: String

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