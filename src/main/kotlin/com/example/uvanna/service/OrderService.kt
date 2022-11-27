package com.example.uvanna.service

import com.example.uvanna.jpa.Orders
import com.example.uvanna.model.payment.Amount
import com.example.uvanna.model.payment.ConfirmationWithToken
import com.example.uvanna.model.payment.Recipient
import com.example.uvanna.model.response.PaymentResponse
import com.example.uvanna.model.response.ServiceResponse
import com.example.uvanna.repository.orders.OrdersRepository
import com.example.uvanna.repository.orders.OrdersRepositoryImpl
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.Credentials
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.util.*

@Service
class OrderService: OrdersRepositoryImpl {

    @Autowired
    lateinit var ordersRepository: OrdersRepository

    override fun createNewOrder(order: Orders): String {
        return try {
            ordersRepository.save(order)
            "Success"
        } catch (e: Exception){
             e.message.toString()
        }
    }

    override fun editOrder(id: String, order: Orders): String {
        return try {
            val orderTemp = ordersRepository.findById(id)
            order.id = orderTemp.get().id
            ordersRepository.save(order)
            "Success"
        } catch (e: Exception){
             e.message.toString()
        }
    }

    @Value("\${payment_key}")
    lateinit var paymentKey: String

    @Value("\${payment_shop}")
    lateinit var paymentShop: String

    var c: PaymentResponse = PaymentResponse(
        id = "",
        status = "",
        amount = Amount(),
        recipient = Recipient(),
        created_at = "",
        confirmation = ConfirmationWithToken(),
        test = "",
        paid = "",
        refundable = ""
    )

    override fun getOrders(id: String): ServiceResponse<Orders>? {
        return try {
            val client = HttpClient() {
                expectSuccess = false

                defaultRequest {
                    contentType(ContentType.Application.Json)
                }
                install(ContentNegotiation) {
                    json()
                }
                install(Logging) {
                    logger = Logger.DEFAULT
                    level = LogLevel.ALL
                }
            }

            runBlocking {
                val order = withContext(Dispatchers.IO) {
                    ordersRepository.findByCode(id).get()
                }
                val f = client.get {
                    headers {
                        contentType(ContentType.Application.Json)
                        append(HttpHeaders.Authorization, Credentials.basic(paymentShop, paymentKey))
                    }
                    url {
                        protocol = URLProtocol.HTTPS
                        host = "api.yookassa.ru/v3/payments/${order.paymentID}"
                    }
                }
                c = f.body()
                c.metadata = null

                withContext(Dispatchers.IO) {
                    ordersRepository.save(
                        Orders(
                            id = order.id,
                            city = order.city,
                            streetFull = order.streetFull,
                            fullName = order.fullName,
                            phone = order.phone,
                            email = order.email,
                            typeDelivery = order.typeDelivery,
                            typePayment = order.typePayment,
                            paymentID = order.id,
                            paymentSuccess = c.paid,
                            code = order.code,
                            products = order.products,
                            status = if(c.paid == "false") "заказ требует оплаты" else { "Заказ успешно оплачен и уже обрабатывается!" }
                        )
                    )
                }
            }
            ServiceResponse(
                data = listOf(),
                message = "",
                status = HttpStatus.OK
            )
        } catch (e: Exception){
            ServiceResponse(
                data = listOf(ordersRepository.getById(id)),
                message = e.message.toString(),
                status = HttpStatus.BAD_GATEWAY
            )
        }
    }
}