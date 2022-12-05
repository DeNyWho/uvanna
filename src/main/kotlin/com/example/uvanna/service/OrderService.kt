package com.example.uvanna.service

import com.example.uvanna.jpa.Orders
import com.example.uvanna.model.orders.OrderConverter
import com.example.uvanna.model.payment.Amount
import com.example.uvanna.model.payment.ConfirmationRedirect
import com.example.uvanna.model.payment.Recipient
import com.example.uvanna.model.response.PagingResponse
import com.example.uvanna.model.response.ServiceResponse
import com.example.uvanna.repository.orders.OrdersRepository
import com.example.uvanna.repository.orders.OrdersRepositoryImpl
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.ContentType.Application.Json
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.json.Json
import okhttp3.Credentials
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.util.*

@Service
class OrderService: OrdersRepositoryImpl {

    @Autowired
    lateinit var ordersRepository: OrdersRepository

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

    var c: OrderConverter = OrderConverter(
        id = "",
        status = "",
        amount = Amount(),
        recipient = Recipient(),
        created_at = "",
        test = true,
        paid = false,
        refundable = false,
        confirmation = ConfirmationRedirect()
    )

    override fun getOrdersList(
        filter: String?,
        pageNum: Int,
        pageSize: Int
    ): PagingResponse<Orders>? {
        return try {
            val sort = when (filter) {
                "new" -> Sort.by(
                    Sort.Order(Sort.Direction.DESC, "updated"),
                )

                "old" -> Sort.by(
                    Sort.Order(Sort.Direction.ASC, "updated")
                )

                else -> null
            }

            val pageable: Pageable =
                if (sort != null) PageRequest.of(pageNum, pageSize, sort) else PageRequest.of(pageNum, pageSize)

            val statePage: Page<Orders> = when (filter) {
                "paid" -> {
                    ordersRepository.findByPaidStatus(pageable, "true")
                }

                "no paid" -> {
                    ordersRepository.findByPaidStatus(pageable, "false")
                }

                else -> ordersRepository.findAll(pageable)
            }
            val temp = mutableListOf<Orders>()

            statePage.content.forEach {
                temp.add(it)
            }
            PagingResponse(
                data = temp,
                totalElements = statePage.totalElements,
                totalPages = statePage.totalPages,
                message = "Success",
                status = HttpStatus.OK
            )
        } catch (e: Exception) {
            PagingResponse(
                data = null,
                message = e.message.toString(),
                status = HttpStatus.BAD_REQUEST
            )
        }
    }

    override fun getOrders(id: String): Any {
//        return try {
            val client = HttpClient() {
                expectSuccess = false

                defaultRequest {
                    contentType(Json)
                }
                install(ContentNegotiation) {
                    json()
                }
                install(Logging) {
                    logger = Logger.DEFAULT
                    level = LogLevel.ALL
                }
            }
        var zxc:  HttpResponse? = null
            runBlocking {
                val order = withContext(Dispatchers.IO) {
                    ordersRepository.findByCode(id).get()
                }
                var v = 0

                val f = client.get {
                    headers {
                        contentType(Json)
                        append("Idempotence-Key", UUID.randomUUID().toString())
                        append(HttpHeaders.Authorization, Credentials.basic(paymentShop, paymentKey))
                    }
                    url {
                        protocol = URLProtocol.HTTPS
                        host = "api.yookassa.ru/v3/payments/${order.paymentID}"
                    }
                }
                c = f.body()
            }
            println(c)
            return c

//                c = f.body()

//                withContext(Dispatchers.IO) {
//                    ordersRepository.save(
//                        Orders(
//                            id = order.id,
//                            city = order.city,
//                            streetFull = order.streetFull,
//                            fullName = order.fullName,
//                            phone = order.phone,
//                            email = order.email,
//                            typeDelivery = order.typeDelivery,
//                            typePayment = order.typePayment,
//                            paymentID = order.id,
//                            paymentSuccess = c.paid.toString(),
//                            code = order.code,
//                            products = order.products,
//                            status = if(!c.paid) "заказ требует оплаты" else { "Заказ успешно оплачен и уже обрабатывается!" }
//                        )
//                    )
//                }
//            }
//            ServiceResponse(
//                data = listOf(ordersRepository.findByCode(code = id).get()),
//                message = "",
//                status = HttpStatus.OK
//            )
//        } catch (e: Exception){
//            ServiceResponse(
//                data = listOf(ordersRepository.findByCode(code = id).get()),
//                message = e.message.toString(),
//                status = HttpStatus.BAD_GATEWAY
//            )
//        }
    }
}