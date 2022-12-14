package com.example.uvanna.service

import com.example.uvanna.jpa.Orders
import com.example.uvanna.model.orders.OrderConverterNeedPaid
import com.example.uvanna.model.orders.OrderConverterPaid
import com.example.uvanna.model.payment.*
import com.example.uvanna.model.payment.receipt.Customer
import com.example.uvanna.model.payment.receipt.Items
import com.example.uvanna.model.request.payment.ReceiptRequest
import com.example.uvanna.model.response.*
import com.example.uvanna.repository.orders.OrdersRepository
import com.example.uvanna.repository.orders.OrdersRepositoryImpl
import com.example.uvanna.repository.products.ProductsRepository
import com.example.uvanna.util.CheckUtil
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.ContentType.Application.Json
import io.ktor.serialization.kotlinx.cbor.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
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
import java.time.LocalDate
import java.util.*
import javax.annotation.Resource

@Service
class OrderService: OrdersRepositoryImpl {

    @Autowired
    lateinit var ordersRepository: OrdersRepository

    @Autowired
    lateinit var productsRepository: ProductsRepository

    @Autowired
    lateinit var emailService: EmailService

    @Value("\${payment_key}")
    lateinit var paymentKey: String

    @Value("\${payment_shop}")
    lateinit var paymentShop: String

    @Resource
    private lateinit var checkUtil: CheckUtil

    override fun editOrder(id: String, order: Orders, token: String): ServiceResponse<Orders> {
        return try {
            val check = checkUtil.checkToken(token)
            return if(check) {
                return try {
                    val orderTemp = ordersRepository.findById(id)
                    order.id = orderTemp.get().id
                    ordersRepository.deleteById(id)
                    ordersRepository.save(order)
                    ServiceResponse(
                        data = listOf(),
                        message = "Order with id = $id has been edited",
                        status = HttpStatus.OK
                    )
                } catch (e: Exception) {
                    ServiceResponse(
                        data = listOf(),
                        message = "Order with id = $id not found",
                        status = HttpStatus.NOT_FOUND
                    )
                }
            } else {
                ServiceResponse(
                    data = null,
                    message = "Unexpected token",
                    status = HttpStatus.UNAUTHORIZED
                )
            }
        } catch (e: Exception){
            ServiceResponse(
                data = null,
                message = "Something went wrong: ${e.message}",
                status = HttpStatus.BAD_REQUEST
            )
        }
    }

    var c: OrderConverterNeedPaid? = OrderConverterNeedPaid(
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

    var l: OrderConverterPaid? = OrderConverterPaid(
        id = "",
        status = "",
        amount = Amount(),
        recipient = Recipient(),
        created_at = "",
        test = true,
        paid = false
    )

    override fun getOrdersList(
        filter: String?,
        pageNum: Int,
        pageSize: Int,
        token: String
    ): PagingResponse<Orders>? {
        val check = checkUtil.checkToken(token)
        return if (check) {
            try {
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
        } else {
            PagingResponse(
                data = null,
                message = "Unexpected token",
                status = HttpStatus.UNAUTHORIZED
            )
        }
    }

    override fun getOrders(id: String): Any {
        return try {
            val order = ordersRepository.findByCode(id).get()
            try {
                return if (order.typePayment == "beznal") {
                    val client = HttpClient {
                        expectSuccess = false

                        defaultRequest {
                            contentType(Json)
                        }
                        install(ContentNegotiation) {
                            json(Json {
                                prettyPrint = true
                                isLenient = true
                                ignoreUnknownKeys = true
                            })
                        }
                        install(Logging) {
                            logger = Logger.DEFAULT
                            level = LogLevel.ALL
                        }
                    }

                    val products = mutableListOf<ProductsWithCount>()

                    order.products.forEach {
                        val temp = productsRepository.findById(it.productID).get()
                        products.add(
                            ProductsWithCount(
                                product = temp,
                                count = it.count
                            )
                        )
                    }

                    runBlocking {
                        try {
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
                            }.body<OrderConverterNeedPaid>()
                            c = f
                            l = null
                        } catch (e: Exception) {
                            c = null
                            try {
                                val request = client.get {
                                    headers {
                                        contentType(Json)
                                        append("Idempotence-Key", UUID.randomUUID().toString())
                                        append(HttpHeaders.Authorization, Credentials.basic(paymentShop, paymentKey))
                                    }
                                    url {
                                        protocol = URLProtocol.HTTPS
                                        host = "api.yookassa.ru/v3/payments/${order.paymentID}"
                                    }
                                }.body<OrderConverterPaid>()
                                l = request
                            } catch (e: Exception) {
                                println("Error = ${e.message}")
                            }
                        }
                    }

                    if (c != null) {
                        ordersRepository.deleteById(order.id)
                        ordersRepository.save(
                            Orders(
                                id = order.id,
                                city = order.city,
                                streetFull = order.streetFull,
                                fullName = order.fullName,
                                phone = order.phone,
                                email = order.email,
                                typePayment = order.typePayment,
                                typeDelivery = order.typeDelivery,
                                code = order.code,
                                price = order.price,
                                paymentID = order.paymentID,
                                paymentSuccess = c!!.paid.toString(),
                                products = order.products,
                                status = when (c!!.status) {
                                    "succeeded" -> "?????????? ?????????????? ??????????????"
                                    "canceled" -> "?????????? ???? ?????? ??????????????. ???? ?????????? ???????????? ?????????? ????????????. (???????? ???????????? ???????????????? ???????? ?????????? - ?????????????????????? ?????????? ??????????)."
                                    "waiting_for_capture" -> "?????????? ?????????????????? ???? ???????????? ?????????????????????????? ??????????????"
                                    else -> "?????????? ?????????????? ????????????"
                                },
                                updated = LocalDate.now().toString(),
                                deleteTime = if (c!!.status == "canceled") {
                                    if (order.deleteTime == null)
                                        LocalDate.now().plusDays(7) else order.deleteTime
                                } else order.deleteTime
                            )
                        )

                        OrderFullResponsePaid(
                            order = Orders(
                                id = order.id,
                                city = order.city,
                                streetFull = order.streetFull,
                                fullName = order.fullName,
                                phone = order.phone,
                                email = order.email,
                                typePayment = order.typePayment,
                                typeDelivery = order.typeDelivery,
                                code = order.code,
                                price = order.price,
                                paymentID = order.paymentID,
                                paymentSuccess = c!!.paid.toString(),
                                products = order.products,
                                status = when (c!!.status) {
                                    "succeeded" -> "?????????? ?????????????? ??????????????"
                                    "canceled" -> "?????????? ???? ?????? ??????????????. ???? ?????????? ???????????? ?????????? ????????????. (???????? ???????????? ???????????????? ???????? ?????????? - ?????????????????????? ?????????? ??????????)."
                                    "waiting_for_capture" -> "?????????? ?????????????????? ???? ???????????? ?????????????????????????? ??????????????"
                                    else -> "?????????? ?????????????? ????????????"
                                },
                                updated = LocalDate.now().toString(),
                                deleteTime = if (c!!.status == "canceled") {
                                    if (order.deleteTime == null)
                                        LocalDate.now().plusDays(7) else order.deleteTime
                                } else order.deleteTime
                            ),
                            orderConverterNeedPaid = c!!,
                            products = products
                        )
                    } else if (l != null) {
                        ordersRepository.deleteById(order.id)
                        ordersRepository.save(
                            Orders(
                                id = order.id,
                                city = order.city,
                                streetFull = order.streetFull,
                                fullName = order.fullName,
                                phone = order.phone,
                                email = order.email,
                                price = order.price,
                                typePayment = order.typePayment,
                                typeDelivery = order.typeDelivery,
                                code = order.code,
                                paymentID = order.paymentID,
                                paymentSuccess = l!!.paid.toString(),
                                products = order.products,
                                status = when (l!!.status) {
                                    "succeeded" -> "?????????? ?????????????? ??????????????"
                                    "canceled" -> "?????????? ???? ?????? ??????????????. ???? ?????????? ???????????? ?????????? ????????????. (???????? ???????????? ???????????????? ???????? ?????????? - ?????????????????????? ?????????? ??????????)."
                                    "waiting_for_capture" -> "?????????? ?????????????????? ???? ???????????? ?????????????????????????? ??????????????"
                                    else -> "?????????? ?????????????? ????????????"
                                },
                                updated = LocalDate.now().toString(),
                                deleteTime = if (l!!.status == "canceled") {
                                    if (order.deleteTime == null)
                                        LocalDate.now().plusDays(7) else order.deleteTime
                                } else order.deleteTime
                            )
                        )

                        OrderFullResponseNoPaid(
                            order = Orders(
                                id = order.id,
                                city = order.city,
                                streetFull = order.streetFull,
                                fullName = order.fullName,
                                phone = order.phone,
                                email = order.email,
                                typePayment = order.typePayment,
                                typeDelivery = order.typeDelivery,
                                code = order.code,
                                price = order.price,
                                paymentID = order.paymentID,
                                paymentSuccess = l!!.paid.toString(),
                                products = order.products,
                                status = when (l!!.status) {
                                    "succeeded" -> "?????????? ?????????????? ??????????????"
                                    "canceled" -> "?????????? ???? ?????? ??????????????. ???? ?????????? ???????????? ?????????? ????????????. (???????? ???????????? ???????????????? ???????? ?????????? - ?????????????????????? ?????????? ??????????)."
                                    "waiting_for_capture" -> "?????????? ?????????????????? ???? ???????????? ?????????????????????????? ??????????????"
                                    else -> "?????????? ?????????????? ????????????"
                                },
                                updated = LocalDate.now().toString(),
                                deleteTime = if (l!!.status == "canceled") {
                                    if (order.deleteTime == null)
                                        LocalDate.now().plusDays(7) else order.deleteTime
                                } else order.deleteTime
                            ),
                            orderConverterPaid = l!!,
                            products = products
                        )
                    } else {
                        ServiceResponse<Any>(
                            data = listOf(),
                            message = "Something went wrong...",
                            status = HttpStatus.NOT_FOUND
                        )
                    }
                } else {
                    val products = mutableListOf<ProductsWithCount>()

                    order.products.forEach {
                        val temp = productsRepository.findById(it.productID).get()
                        products.add(
                            ProductsWithCount(
                                product = temp,
                                count = it.count
                            )
                        )
                    }
                    OrderSmallResponse(
                        order = Orders(
                            id = order.id,
                            city = order.city,
                            streetFull = order.streetFull,
                            fullName = order.fullName,
                            phone = order.phone,
                            price = order.price,
                            email = order.email,
                            typePayment = order.typePayment,
                            typeDelivery = order.typeDelivery,
                            code = order.code,
                            paymentID = order.paymentID,
                            paymentSuccess = order.typePayment,
                            products = order.products,
                            status = "?????????? ??????????????????????",
                            updated = LocalDate.now().toString()
                        ),
                        products = products
                    )
                }
            } catch (e: Exception) {
                ServiceResponse(
                    data = listOf(e),
                    message = "Order with code = $id not found",
                    status = HttpStatus.NOT_FOUND
                )
            }
        } catch (e: Exception) {
            ServiceResponse<Any>(
                data = listOf(),
                message = "Something went wrong... ${e.message}",
                status = HttpStatus.NOT_FOUND
            )
        }
    }

    override fun scheduleCheckForMessage() {
        try {
            val orders = ordersRepository.findAll()

            orders.forEach { order ->
                println(order)
                if (order.deleteTime == null && order.paymentSuccess == "false") {
                    if (order.typePayment == "beznal") {
                        val client = HttpClient {
                            expectSuccess = false

                            defaultRequest {
                                contentType(Json)
                            }
                            install(ContentNegotiation) {
                                json(Json {
                                    prettyPrint = true
                                    isLenient = true
                                    ignoreUnknownKeys = true
                                })
                            }
                            install(Logging) {
                                logger = Logger.DEFAULT
                                level = LogLevel.ALL
                            }
                        }

                        runBlocking {
                            try {
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
                                }.body<OrderConverterNeedPaid>()
                                c = f
                                l = null
                            } catch (e: Exception) {
                                c = null
                                try {
                                    val request = client.get {
                                        headers {
                                            contentType(Json)
                                            append("Idempotence-Key", UUID.randomUUID().toString())
                                            append(
                                                HttpHeaders.Authorization,
                                                Credentials.basic(paymentShop, paymentKey)
                                            )
                                        }
                                        url {
                                            protocol = URLProtocol.HTTPS
                                            host = "api.yookassa.ru/v3/payments/${order.paymentID}"
                                        }
                                    }.body<OrderConverterPaid>()
                                    l = request
                                } catch (e: Exception) {
                                    println("Error = ${e.message}")
                                }
                            }
                        }

                        if (c != null && c!!.status != "pending" && c!!.status != "waiting_for_capture") {
                            ordersRepository.deleteById(order.id)
                            if ((c!!.status == "succeeded" && order.emailSend == false) || (order.emailSend == null && c!!.status == "succeeded")) {
                                emailService.sendOrderMessage(
                                    paymentInfo = order,
                                    title = "?????????? ?????????????? ??????????????",
                                    template = "orderPaid"
                                )

                                val items = mutableListOf<Items>()

                                order.products.forEach {
                                    val product = productsRepository.findById(it.productID).get()
                                    items.add(
                                        Items(
                                            description = product.title,
                                            amount = Amount(
                                                value = if (product.sellPrice == null) "${product.price}" else "${product.sellPrice}",
                                                currency = "RUB"
                                            ),
                                            quantity = "${it.count}",
                                            vatCode = 1
                                        )
                                    )
                                }

                                runBlocking {
                                    client.post {
                                        headers {
                                            contentType(ContentType.Application.Json)
                                            append("Idempotence-Key", UUID.randomUUID().toString())
                                            append(
                                                HttpHeaders.Authorization,
                                                Credentials.basic(paymentShop, paymentKey)
                                            )
                                        }
                                        setBody(
                                            ReceiptRequest(
                                                customer = Customer(
                                                    email = order.email,
                                                    phone = order.phone,
                                                ),
                                                items = items,
                                                settlements = listOf(
                                                    Settlements(
                                                        amount = Amount(
                                                            value = order.price.toInt().toString(),
                                                            currency = "RUB"
                                                        )
                                                    )
                                                )
                                            )
                                        )
                                        url {
                                            protocol = URLProtocol.HTTPS
                                            host = "api.yookassa.ru/v3/receipts"
                                        }
                                    }
                                }
                            } else if ((c!!.status == "canceled" && order.emailSend == false) || (order.emailSend == null && c!!.status == "canceled")) {
                                emailService.sendOrderMessage(
                                    paymentInfo = order,
                                    title = "?????????? ???? ?????? ??????????????",
                                    template = "rejected"
                                )
                            }
                            ordersRepository.save(
                                Orders(
                                    id = order.id,
                                    city = order.city,
                                    streetFull = order.streetFull,
                                    fullName = order.fullName,
                                    phone = order.phone,
                                    email = order.email,
                                    typePayment = order.typePayment,
                                    typeDelivery = order.typeDelivery,
                                    code = order.code,
                                    price = order.price,
                                    paymentID = order.paymentID,
                                    paymentSuccess = c!!.paid.toString(),
                                    products = order.products,
                                    status = when (c!!.status) {
                                        "succeeded" -> "?????????? ?????????????? ??????????????"
                                        "canceled" -> "?????????? ???? ?????? ??????????????. ???? ?????????? ???????????? ?????????? ????????????. (???????? ???????????? ???????????????? ???????? ?????????? - ?????????????????????? ?????????? ??????????)."
                                        "waiting_for_capture" -> "?????????? ?????????????????? ???? ???????????? ?????????????????????????? ??????????????"
                                        else -> "?????????? ?????????????? ????????????"
                                    },
                                    updated = LocalDate.now().toString(),
                                    deleteTime = if (c!!.status == "canceled") {
                                        if (order.deleteTime == null)
                                            LocalDate.now().plusDays(7) else order.deleteTime
                                    } else order.deleteTime,
                                    emailSend = true
                                )
                            )
                        }



                        if (l != null && l!!.status != "pending" && l!!.status != "waiting_for_capture") {
                            ordersRepository.deleteById(order.id)
                            if ((l!!.status == "succeeded" && order.emailSend == false) || (l!!.status == "succeeded" && order.emailSend == null)) {
                                emailService.sendOrderMessage(
                                    paymentInfo = order,
                                    title = "?????????? ?????????????? ??????????????",
                                    template = "orderPaid"
                                )
                                val items = mutableListOf<Items>()

                                order.products.forEach {
                                    val product = productsRepository.findById(it.productID).get()
                                    items.add(
                                        Items(
                                            description = product.title,
                                            amount = Amount(
                                                value = if (product.sellPrice == null) "${product.price}" else "${product.sellPrice}",
                                                currency = "RUB"
                                            ),
                                            quantity = "${it.count}",
                                            vatCode = 1
                                        )
                                    )
                                }

                                runBlocking {
                                    client.post {
                                        headers {
                                            contentType(ContentType.Application.Json)
                                            append("Idempotence-Key", UUID.randomUUID().toString())
                                            append(
                                                HttpHeaders.Authorization,
                                                Credentials.basic(paymentShop, paymentKey)
                                            )
                                        }
                                        setBody(
                                            ReceiptRequest(
                                                paymentId = "${order.paymentID}",
                                                type = "payment",
                                                customer = Customer(
                                                    email = order.email,
                                                    phone = order.phone,
                                                ),
                                                items = items,
                                                settlements = listOf(
                                                    Settlements(
                                                        type = "cashless",
                                                        amount = Amount(
                                                            value = order.price.toInt().toString(),
                                                            currency = "RUB"
                                                        )
                                                    )
                                                ),
                                                send = true
                                            )
                                        )
                                        url {
                                            protocol = URLProtocol.HTTPS
                                            host = "api.yookassa.ru/v3/receipts"
                                        }
                                    }
                                }
                            } else if ((l!!.status == "canceled" && order.emailSend == false) || (order.emailSend == null && l!!.status == "canceled")) {
                                emailService.sendOrderMessage(
                                    paymentInfo = order,
                                    title = "?????????? ???? ?????? ??????????????",
                                    template = "rejected"
                                )
                            }
                            ordersRepository.save(
                                Orders(
                                    id = order.id,
                                    city = order.city,
                                    streetFull = order.streetFull,
                                    fullName = order.fullName,
                                    phone = order.phone,
                                    email = order.email,
                                    typePayment = order.typePayment,
                                    typeDelivery = order.typeDelivery,
                                    code = order.code,
                                    price = order.price,
                                    paymentID = order.paymentID,
                                    paymentSuccess = l!!.paid.toString(),
                                    products = order.products,
                                    status = when (l!!.status) {
                                        "succeeded" -> "?????????? ?????????????? ??????????????"
                                        "canceled" -> "?????????? ???? ?????? ??????????????. ???? ?????????? ???????????? ?????????? ????????????. (???????? ???????????? ???????????????? ???????? ?????????? - ?????????????????????? ?????????? ??????????)."
                                        "waiting_for_capture" -> "?????????? ?????????????????? ???? ???????????? ?????????????????????????? ??????????????"
                                        else -> "?????????? ?????????????? ????????????"
                                    },
                                    updated = LocalDate.now().toString(),
                                    deleteTime = if (l!!.status == "canceled") {
                                        if (order.deleteTime == null)
                                            LocalDate.now().plusDays(7) else order.deleteTime
                                    } else order.deleteTime,
                                    emailSend = true
                                )
                            )
                        }
                    }
                }
            }
        } catch (e: Exception) {
            println(e)
        }
    }

    override fun scheduleCheckForDelete(){
        val orders = ordersRepository.findAll()

        orders.forEach {
            if(it.deleteTime == LocalDate.now()) {
                ordersRepository.deleteById(it.id)
            }
        }
    }
}