package com.example.uvanna.service

import com.example.uvanna.jpa.Orders
import com.example.uvanna.model.OrdersProducts
import com.example.uvanna.model.payment.Amount
import com.example.uvanna.model.payment.Confirmation
import com.example.uvanna.model.payment.ConfirmationWithToken
import com.example.uvanna.model.payment.Recipient
import com.example.uvanna.model.request.payment.PaymentDataRequest
import com.example.uvanna.model.request.payment.PaymentRequest
import com.example.uvanna.model.request.payment.ProductsRequestsing
import com.example.uvanna.model.response.PaymentResponse
import com.example.uvanna.model.response.ServiceResponse
import com.example.uvanna.repository.orders.OrdersRepository
import com.example.uvanna.repository.payment.PaymentRepositoryImpl
import com.example.uvanna.repository.products.ProductsRepository
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.http.HttpHeaders.Authorization
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.Credentials
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat
import java.util.*


@Service
class PaymentService: PaymentRepositoryImpl {

    @Value("\${payment_key}")
    lateinit var paymentKey: String

    @Value("\${payment_shop}")
    lateinit var paymentShop: String

    @Autowired
    lateinit var productsRepository: ProductsRepository

    @Autowired
    lateinit var ordersRepository: OrdersRepository

    @Autowired
    lateinit var emailService: EmailService

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



    override fun createNewPayment(ordersProducts: List<ProductsRequestsing>, paymentDataRequest: PaymentDataRequest): Any  {
        if (paymentDataRequest.typePayment == "beznal") {
            val client = HttpClient {
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

            var price = 0
            ordersProducts.forEach {
                println("ZXC = ${productsRepository.findById(it.product).get().price}")
                price = price + productsRepository.findById(it.product).get().price
            }
            var v =
                "${(0..9).random()}${(0..9).random()}${(0..9).random()}${(0..9).random()}${(0..9).random()}${(0..9).random()}${(0..9).random()}${(0..9).random()}-${(0..9).random()}${(0..9).random()}${(0..9).random()}${(0..9).random()}"
            while (ordersRepository.findByCode(v).isPresent) {
                v =
                    "${(0..9).random()}${(0..9).random()}${(0..9).random()}${(0..9).random()}${(0..9).random()}${(0..9).random()}${(0..9).random()}${(0..9).random()}-${(0..9).random()}${(0..9).random()}${(0..9).random()}${(0..9).random()}"
            }
            val id = UUID.randomUUID().toString()

            runBlocking {
                val f = client.post {
                    headers {
                        contentType(ContentType.Application.Json)
                        append("Idempotence-Key", UUID.randomUUID().toString())
                        append(Authorization, Credentials.basic(paymentShop, paymentKey))
                    }
                    setBody(
                        PaymentRequest(
                            amount = Amount(value = price.toString(), currency = "RUB"),
                            confirmation = Confirmation(
                                type = "redirect",
                                return_url = "https://uvanna.store/order/orderCreated?code=$v"
                            ),
                            capture = true,
                            test = true
                        )
                    )
                    url {
                        protocol = URLProtocol.HTTPS
                        host = "api.yookassa.ru/v3/payments"
                    }
                }
                val orderProducts = mutableListOf<OrdersProducts>()
                ordersProducts.forEach{
                    orderProducts.add(
                        OrdersProducts(
                        productID = it.product,
                        count = it.count.toInt()
                    )
                    )
                }
                c = f.body()
                c.metadata = null
                withContext(Dispatchers.IO) {
                   ordersRepository.save(
                        Orders(
                            id = id,
                            city = paymentDataRequest.city,
                            streetFull = paymentDataRequest.streetFull,
                            fullName = paymentDataRequest.fullname,
                            phone = paymentDataRequest.phone,
                            email = paymentDataRequest.email,
                            updated = SimpleDateFormat("dd/M/yyyy hh:mm:ss").format(Date()).toString(),
                            typeDelivery = paymentDataRequest.typeDelivery,
                            typePayment = paymentDataRequest.typePayment,
                            paymentID = c.id,
                            code = v,
                            status = "заказ требует оплаты"
                        )
                    )
                    orderProducts.forEach {
                        ordersRepository.findById(id).get().addProducts(
                            OrdersProducts(
                                productID = it.productID,
                                count = it.count
                            )
                        )
                    }
                }
            }

            val order = ordersRepository.findById(id).get()

            emailService.sendNewOrderMessage(paymentInfo = order)
            return c
        } else {
            var v =
                "${(0..9).random()}${(0..9).random()}${(0..9).random()}${(0..9).random()}${(0..9).random()}${(0..9).random()}${(0..9).random()}${(0..9).random()}-${(0..9).random()}${(0..9).random()}${(0..9).random()}${(0..9).random()}"
            while (ordersRepository.findByCode(v).isPresent) {
                v =
                    "${(0..9).random()}${(0..9).random()}${(0..9).random()}${(0..9).random()}${(0..9).random()}${(0..9).random()}${(0..9).random()}${(0..9).random()}-${(0..9).random()}${(0..9).random()}${(0..9).random()}${(0..9).random()}"
            }
            val id = UUID.randomUUID().toString()

            val orderProducts = mutableListOf<OrdersProducts>()
            ordersProducts.forEach{
                orderProducts.add(
                    OrdersProducts(
                        productID = it.product,
                        count = it.count
                    )
                )
            }
            ordersRepository.save(
                Orders(
                    id = id,
                    city = paymentDataRequest.city,
                    streetFull = paymentDataRequest.streetFull,
                    fullName = paymentDataRequest.fullname,
                    phone = paymentDataRequest.phone,
                    email = paymentDataRequest.email,
                    updated = SimpleDateFormat("dd/M/yyyy hh:mm:ss").format(Date()).toString(),
                    typeDelivery = paymentDataRequest.typeDelivery,
                    typePayment = paymentDataRequest.typePayment,
                    paymentID = c.id,
                    code = v,
                    status = "Заказ сформирован"
                )
            )
            orderProducts.forEach {
                ordersRepository.findById(id).get().addProducts(
                    OrdersProducts(
                        productID = it.productID,
                        count = it.count
                    )
                )
            }

            val order = ordersRepository.findById(id).get()

            emailService.sendNewOrderMessage(paymentInfo = order)

            return ordersRepository.findById(id)
        }
    }

    override fun getOrder(id: String): ServiceResponse<Orders>? {
        return try {
            ServiceResponse(
                data = listOf(ordersRepository.getById(id)),
                message = "Success",
                status = HttpStatus.OK
            )
        } catch (e: Exception){
            ServiceResponse(
                data = null,
                message = e.message.toString(),
                status = HttpStatus.BAD_REQUEST
            )
        }
    }

}