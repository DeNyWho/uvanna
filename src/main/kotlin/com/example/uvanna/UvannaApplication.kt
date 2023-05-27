package com.example.uvanna

import com.example.uvanna.service.OrderService
import com.example.uvanna.service.ProductService
import com.example.uvanna.service.PromoService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class ScheduleTasks {

    @Autowired
    private lateinit var orderService: OrderService

    @Autowired
    private lateinit var promoService: PromoService

    @Autowired
    private lateinit var productService: ProductService

    @Scheduled(cron = "0 0 * * * *")
    fun deleteOrderBySchedule() {
        orderService.scheduleCheckForDelete()
    }

    @Scheduled(fixedRate = 24, timeUnit = TimeUnit.HOURS)
    fun checkProducts() {
        productService.checkProducts()
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
