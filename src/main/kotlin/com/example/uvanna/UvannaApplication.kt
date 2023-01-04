package com.example.uvanna

import com.example.uvanna.service.OrderService
import com.example.uvanna.service.PromoService
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

    @Autowired
    private lateinit var promoService: PromoService

    @Scheduled(cron = "0 0 * * * *")
    fun deleteOrderBySchedule() {
        orderService.scheduleCheckForDelete()
    }

    @Scheduled(cron = "0 0 * * * *")
    fun deletePromoBySchedule() {
        promoService.scheduleCheckForDelete()
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
