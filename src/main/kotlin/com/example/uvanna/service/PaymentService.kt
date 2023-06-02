package com.example.uvanna.service

import com.example.uvanna.jpa.Orders
import com.example.uvanna.model.OrdersProducts
import com.example.uvanna.model.moysklad.counter_party_data.CounterPartyData
import com.example.uvanna.model.payment.receipt.Items
import com.example.uvanna.model.payment.receipt.Receipt
import com.example.uvanna.model.request.credit.CreditItems
import com.example.uvanna.model.request.credit.CreditRequest
import com.example.uvanna.model.request.payment.DataRequest
import com.example.uvanna.model.request.payment.PaymentDataRequest
import com.example.uvanna.model.request.payment.PaymentRequest
import com.example.uvanna.model.request.payment.ProductsRequestsing
import com.example.uvanna.model.response.CreditResponse
import com.example.uvanna.model.response.PaymentResponse
import com.example.uvanna.model.response.ServiceResponse
import com.example.uvanna.repository.orders.OrdersProductsRepository
import com.example.uvanna.repository.orders.OrdersRepository
import com.example.uvanna.repository.payment.PaymentRepositoryImpl
import com.example.uvanna.repository.products.ProductsRepository
import com.google.gson.Gson
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.kotlinx.serializer.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBodyOrNull
import org.springframework.web.reactive.function.client.awaitExchange
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*


@Service
class PaymentService : PaymentRepositoryImpl {

    @Value("\${terminalKey}")
    lateinit var terminalKey: String

    @Value("\${terminalPassword}")
    lateinit var terminalPassword: String

    @Value("\${shopIDCredit}")
    lateinit var shopIDCredit: String

    @Value("\${showCaseIDCredit}")
    lateinit var showCaseIDCredit: String

    @Autowired
    lateinit var productsRepository: ProductsRepository

    @Autowired
    lateinit var ordersProductsRepository: OrdersProductsRepository

    @Autowired
    lateinit var ordersRepository: OrdersRepository

    @Autowired
    lateinit var emailService: EmailService

    var c: PaymentResponse = PaymentResponse()

    var creditResponse: CreditResponse = CreditResponse()


    @Value("\${moysklad.username}")
    private lateinit var usernameMoySk: String

    @Value("\${moysklad.password}")
    private lateinit var passwordMoySk: String

    fun test() {
        val gson = Gson()
        val client = WebClient.builder()
            .baseUrl("https://online.moysklad.ru/api/remap/1.2")
            .defaultHeaders { headers ->
                headers.setBasicAuth(usernameMoySk, passwordMoySk)
                headers.contentType = MediaType.APPLICATION_JSON
                headers.acceptCharset = listOf(Charset.forName("UTF-8"))
            }
            .build()

        val counterpartyData = CounterPartyData(
            name = "test",
            actualAddress = "test",
            phone = "test",
            email = "test"
        )
        val b = runBlocking {
            val response = client.post()
                .uri("/entity/counterparty")
                .body(BodyInserters.fromValue(counterpartyData))
                .awaitExchange()
            val r = response.awaitBodyOrNull<String>()
            val parsedJson = gson.fromJson(r, Map::class.java)
            parsedJson["id"].toString()
        }
        val requestBody = """
        {
            "organization": {
                "meta": {
                    "href": "https://online.moysklad.ru/api/remap/1.2/entity/organization/a6396778-f0c2-11ed-0a80-079f0010c46f",
                    "type": "organization",
                    "mediaType": "application/json"
                }
            },
            "agent": {
                "meta": {
                    "href": "https://online.moysklad.ru/api/remap/1.2/entity/counterparty/3c3c9b16-00aa-11ee-0a80-046f0001ec7f",
                    "type": "counterparty",
                    "mediaType": "application/json"
                }
            }
        }
    """.trimIndent()

        val response = client.post()
            .uri("/entity/customerorder")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .body(BodyInserters.fromValue(requestBody))
            .retrieve()
            .bodyToMono(String::class.java)
            .block()

        println(response)

    }

    override fun createNewPayment(
        ordersProducts: List<ProductsRequestsing>,
        paymentDataRequest: PaymentDataRequest
    ): Any {
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
                val product = productsRepository.findById(it.product).get()
                val temp = if (product.sellPrice != null) product.sellPrice else product.price
                price = price + (temp!! * it.count)
            }
            var v =
                "${(0..9).random()}${(0..9).random()}${(0..9).random()}${(0..9).random()}${(0..9).random()}${(0..9).random()}${(0..9).random()}${(0..9).random()}-${(0..9).random()}${(0..9).random()}${(0..9).random()}${(0..9).random()}"
            while (ordersRepository.findByCode(v).isPresent) {
                v =
                    "${(0..9).random()}${(0..9).random()}${(0..9).random()}${(0..9).random()}${(0..9).random()}${(0..9).random()}${(0..9).random()}${(0..9).random()}-${(0..9).random()}${(0..9).random()}${(0..9).random()}${(0..9).random()}"
            }
            val id = UUID.randomUUID().toString()

            val items = mutableListOf<Items>()

            ordersProducts.forEach {
                val product = productsRepository.findById(it.product).get()
                items.add(
                    Items(
                        name = product.title,
                        price = "${product.price}00".toInt(),
                        amount = if (product.sellPrice == null) "${product.price * it.count}00".toInt() else "${product.sellPrice!! * it.count}00".toInt(),
                        quantity = it.count
                    )
                )
            }


            runBlocking {
                val f = client.post {
                    headers {
                        contentType(ContentType.Application.Json)
                    }
                    setBody(
                        PaymentRequest(
                            terminalKey = terminalKey,
                            description = "Покупка на сайте Uvanna.store",
                            orderID = id,
                            amount = "${price}00".toInt(),
                            successURL = "https://uvanna.store/order/orderCreated?code=$v",
                            receipt = Receipt(
                                email = paymentDataRequest.email,
                                phone = paymentDataRequest.phone,
                                items = items
                            ),
                            data = DataRequest(
                                email = paymentDataRequest.email,
                                phone = paymentDataRequest.phone
                            )
                        )
                    )
                    url {
                        protocol = URLProtocol.HTTPS
                        host = "securepay.tinkoff.ru/v2/Init"
                    }
                }.body<PaymentResponse>()

                c = f

                val r = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
                val z = SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(
                    Date.from(
                        Date().toInstant().atZone(
                            ZoneId.of("Europe/Moscow")
                        ).toInstant()
                    )
                )

                val b = mutableListOf<OrdersProducts>()
                ordersProducts.forEach {
                    b.add(
                        ordersProductsRepository.save(
                            OrdersProducts(
                                productID = it.product,
                                count = it.count,
                                sellPrice = productsRepository.findById(it.product).get().sellPrice,
                                price = productsRepository.findById(it.product).get().price
                            )
                        )
                    )
                }

                ordersProducts.forEach { productOrd ->

                }

                withContext(Dispatchers.IO) {
                    val order = Orders(
                        id = id,
                        city = paymentDataRequest.city,
                        streetFull = paymentDataRequest.streetFull,
                        fullName = paymentDataRequest.fullname,
                        phone = paymentDataRequest.phone,
                        dateCreated = LocalDateTime.parse(z, r),
                        email = paymentDataRequest.email,
                        paymentSuccess = false.toString(),
                        price = price.toDouble(),
                        products = b.toMutableSet(),
                        updated = SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(
                            Date.from(
                                Date().toInstant().atZone(
                                    ZoneId.of("Europe/Moscow")
                                ).toInstant()
                            )
                        ).toString(),
                        typeDelivery = paymentDataRequest.typeDelivery,
                        typePayment = paymentDataRequest.typePayment,
                        paymentID = c.paymentId,
                        code = v,
                        status = "заказ требует оплаты",
                        utmMet = paymentDataRequest.utmMet
                    )

                    ordersRepository.save(order)
                }
            }

            emailService.sendNewOrderMessage(paymentInfo = ordersRepository.findById(id).get())

            return c
        } else if (paymentDataRequest.typePayment == "credit") {
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
                val product = productsRepository.findById(it.product).get()
                val temp = if (product.sellPrice != null) product.sellPrice else product.price
                price = price + (temp!! * it.count)
            }
            var v =
                "${(0..9).random()}${(0..9).random()}${(0..9).random()}${(0..9).random()}${(0..9).random()}${(0..9).random()}${(0..9).random()}${(0..9).random()}-${(0..9).random()}${(0..9).random()}${(0..9).random()}${(0..9).random()}"
            while (ordersRepository.findByCode(v).isPresent) {
                v =
                    "${(0..9).random()}${(0..9).random()}${(0..9).random()}${(0..9).random()}${(0..9).random()}${(0..9).random()}${(0..9).random()}${(0..9).random()}-${(0..9).random()}${(0..9).random()}${(0..9).random()}${(0..9).random()}"
            }
            val id = UUID.randomUUID().toString()

            val items = mutableListOf<CreditItems>()

            ordersProducts.forEach {
                val product = productsRepository.findById(it.product).get()
                items.add(
                    CreditItems(
                        name = product.title,
                        price = product.price,
                        quantity = it.count
                    )
                )
            }


            runBlocking {
                val f = client.post {
                    headers {
                        contentType(ContentType.Application.Json)
                    }
                    setBody(
                        CreditRequest(
                            shopId = shopIDCredit,
                            showcaseId = showCaseIDCredit,
                            sum = price,
                            items = items,
                            successURL = "https://uvanna.store/order/orderCreated?code=$v",
                            orderID = id
                        )
                    )
                    url {
                        protocol = URLProtocol.HTTPS
                        host = "forma.tinkoff.ru/api/partners/v2/orders/create"
                    }
                }.body<CreditResponse>()

                creditResponse = f

                val r = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
                val z = SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(
                    Date.from(
                        Date().toInstant().atZone(
                            ZoneId.of("Europe/Moscow")
                        ).toInstant()
                    )
                )

                val b = mutableListOf<OrdersProducts>()
                ordersProducts.forEach {
                    b.add(
                        ordersProductsRepository.save(
                            OrdersProducts(
                                productID = it.product,
                                count = it.count,
                                sellPrice = productsRepository.findById(it.product).get().sellPrice,
                                price = productsRepository.findById(it.product).get().price
                            )
                        )
                    )
                }

                withContext(Dispatchers.IO) {
                    val order = Orders(
                        id = id,
                        city = paymentDataRequest.city,
                        streetFull = paymentDataRequest.streetFull,
                        fullName = paymentDataRequest.fullname,
                        phone = paymentDataRequest.phone,
                        dateCreated = LocalDateTime.parse(z, r),
                        email = paymentDataRequest.email,
                        paymentSuccess = false.toString(),
                        price = price.toDouble(),
                        products = b.toMutableSet(),
                        updated = SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(
                            Date.from(
                                Date().toInstant().atZone(
                                    ZoneId.of("Europe/Moscow")
                                ).toInstant()
                            )
                        ).toString(),
                        typeDelivery = paymentDataRequest.typeDelivery,
                        typePayment = paymentDataRequest.typePayment,
                        paymentID = creditResponse.id,
                        code = v,
                        status = "заказ требует подтверждение кредита от банка",
                        utmMet = paymentDataRequest.utmMet
                    )
                    ordersRepository.save(order)
                }
            }

            emailService.sendNewOrderMessage(paymentInfo = ordersRepository.findById(id).get())

            return creditResponse
        } else {
            var v =
                "${(0..9).random()}${(0..9).random()}${(0..9).random()}${(0..9).random()}${(0..9).random()}${(0..9).random()}${(0..9).random()}${(0..9).random()}-${(0..9).random()}${(0..9).random()}${(0..9).random()}${(0..9).random()}"
            while (ordersRepository.findByCode(v).isPresent) {
                v =
                    "${(0..9).random()}${(0..9).random()}${(0..9).random()}${(0..9).random()}${(0..9).random()}${(0..9).random()}${(0..9).random()}${(0..9).random()}-${(0..9).random()}${(0..9).random()}${(0..9).random()}${(0..9).random()}"
            }

            val id = UUID.randomUUID().toString()

            var price = 0.0
            ordersProducts.forEach {
                val product = productsRepository.findById(it.product).get()
                val temp = if (product.sellPrice != null) product.sellPrice else product.price
                price = price + (temp!! * it.count)
            }

            val r = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
            val z = SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(
                Date.from(
                    Date().toInstant().atZone(
                        ZoneId.of("Europe/Moscow")
                    ).toInstant()
                )
            )
            val b = mutableListOf<OrdersProducts>()
            ordersProducts.forEach {
                b.add(
                    ordersProductsRepository.save(
                        OrdersProducts(
                            productID = it.product,
                            count = it.count,
                            sellPrice = productsRepository.findById(it.product).get().sellPrice,
                            price = productsRepository.findById(it.product).get().price
                        )
                    )
                )
            }

            val vxc = Orders(
                id = id,
                city = paymentDataRequest.city,
                streetFull = paymentDataRequest.streetFull,
                fullName = paymentDataRequest.fullname,
                phone = paymentDataRequest.phone,
                email = paymentDataRequest.email,
                price = price,
                dateCreated = LocalDateTime.parse(z, r),
                paymentSuccess = false.toString(),
                updated = SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(
                    Date.from(
                        Date().toInstant().atZone(
                            ZoneId.of("Europe/Moscow")
                        ).toInstant()
                    )
                ).toString(),
                products = b.toMutableSet(),
                typeDelivery = paymentDataRequest.typeDelivery,
                typePayment = paymentDataRequest.typePayment,
                paymentID = c.paymentId,
                code = v,
                status = "Заказ сформирован",
                utmMet = paymentDataRequest.utmMet
            )

            ordersRepository.save(vxc)

            emailService.sendNewOrderMessage(paymentInfo = ordersRepository.findById(vxc.id).get())
            emailService.sendOrderMessageUvannaNal(
                paymentInfo = ordersRepository.findById(vxc.id).get(),
                title = "Новый заказ с №${vxc.code} оплата: Наличные"
            )

            return ordersRepository.findById(id)
        }
    }

    override fun getOrder(id: String): ServiceResponse<Orders>? {
        return try {
            ServiceResponse(
                data = listOf(ordersRepository.findById(id).get()),
                message = "Success",
                status = HttpStatus.OK
            )
        } catch (e: Exception) {
            ServiceResponse(
                data = null,
                message = e.message.toString(),
                status = HttpStatus.BAD_REQUEST
            )
        }
    }

}