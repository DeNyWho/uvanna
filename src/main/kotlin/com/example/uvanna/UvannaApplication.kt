package com.example.uvanna

import com.example.uvanna.service.OrderService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime

@Component
class ScheduleTasks {

    @Autowired
    private lateinit var orderService: OrderService

    @Scheduled(fixedRate = 86400000)
    fun deleteOrderBySchedule() {
        orderService.scheduleCheckForDelete()
    }

    @Scheduled(fixedRate = 60000)
    fun sendPaymentSuccess() {
        orderService.scheduleCheckForMessage()
    }
}

@SpringBootApplication
@EnableScheduling
class UvannaApplication

fun main(args: Array<String>) {
    runApplication<UvannaApplication>(*args)
}
