package com.example.uvanna.service

import com.example.uvanna.jpa.Orders
import com.example.uvanna.jpa.Services
import com.example.uvanna.model.OrdersProducts
import com.example.uvanna.model.orders.OrderConverterPaid
import com.example.uvanna.model.orders.OrderRequest
import com.example.uvanna.model.orders.ServiceRequest
import com.example.uvanna.model.payment.*
import com.example.uvanna.model.response.*
import com.example.uvanna.repository.orders.OrdersProductsRepository
import com.example.uvanna.repository.orders.OrdersRepository
import com.example.uvanna.repository.orders.OrdersRepositoryImpl
import com.example.uvanna.repository.orders.ServiceRepository
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
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import javax.annotation.Resource

@Service
class OrderService: OrdersRepositoryImpl {

    @Autowired
    lateinit var ordersRepository: OrdersRepository

    @Autowired
    lateinit var ordersProductsRepository: OrdersProductsRepository

    @Autowired
    lateinit var productsRepository: ProductsRepository

    @Autowired
    lateinit var serviceRepository: ServiceRepository

    @Autowired
    lateinit var emailService: EmailService

    @Value("\${terminalKey}")
    lateinit var terminalKey: String

    @Value("\${terminalPassword}")
    lateinit var terminalPassword: String

    @Value("\${shopIDCredit}")
    lateinit var shopIDCredit: String

    @Value("\${showCaseIDCredit}")
    lateinit var showCaseIDCredit: String

    @Value("\${shopPasswordCredit}")
    lateinit var shopPasswordCredit: String

    @Resource
    private lateinit var checkUtil: CheckUtil

    @Autowired
    lateinit var fileService: FileService

    override fun addFile(id: String, files: List<MultipartFile>, token: String): ServiceResponse<String>? {
        val check = checkUtil.checkToken(token)

        return if(check) {
            return try {
                val item = ordersRepository.findById(id).get()

                println(item)

                files.forEach {
                    val file = fileService.saveFile(it)
                    item.addOrderFiles(file)
                }

                ordersRepository.deleteById(id)
                ordersRepository.save(item)

                ServiceResponse(
                    data = ordersRepository.findById(id).get().orderFiles.toList(),
                    message = "File with id = $id has been added",
                    status = HttpStatus.OK
                )
            } catch (e: Exception) {
                ServiceResponse(
                    data = listOf(),
                    message = "File with id = $id not found",
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

    }

    override fun deleteFile(id: String, files: List<String>, token: String): ServiceResponse<String> {
        val check = checkUtil.checkToken(token)

        return if(check) {
            return try {
                val item = ordersRepository.findById(id).get()

                println(item)

                files.forEach {
                    item.deleteOrderFiles(it)
                    fileService.deleteFile(it)
                }

                ordersRepository.deleteById(id)
                ordersRepository.save(item)

                ServiceResponse(
                    data = listOf(),
                    message = "File has been deleted",
                    status = HttpStatus.OK
                )
            } catch (e: Exception) {
                ServiceResponse(
                    data = listOf(),
                    message = "Something went wrong... ${e.message}",
                    status = HttpStatus.BAD_REQUEST
                )
            }
        } else {
            ServiceResponse(
                data = null,
                message = "Unexpected token",
                status = HttpStatus.UNAUTHORIZED
            )
        }
    }


    override fun changeOrderStatus(id: String, status: String, token: String): ServiceResponse<Orders> {
        return try {
            val check = checkUtil.checkToken(token)

            return if (check) {
                val order = ordersRepository.findById(id).get()

                if (
                    order.status != "Заказ не был оплачен. Он будет удален через неделю. (Если хотите оплатить этот заказ - сформируйте новый заказ)." &&
                    order.status != "Заказ находится на стадии подтверждения платежа" &&
                    order.status != "Заказ требует оплаты"
                ) {
                    println(order)

                    order.status = status

                    ordersRepository.deleteById(id)

                    ordersRepository.save(order)

                    ServiceResponse(
                        data = listOf(ordersRepository.findById(id).get()),
                        message = "Order has been edited",
                        status = HttpStatus.OK
                    )
                } else {
                    ServiceResponse(
                        data = null,
                        message = "Order not paid",
                        status = HttpStatus.BAD_REQUEST
                    )
                }
            } else {
                ServiceResponse(
                    data = null,
                    message = "Unexpected token",
                    status = HttpStatus.UNAUTHORIZED
                )
            }
        } catch (e: Exception) {
            ServiceResponse(
                data = null,
                message = "Something went wrong: ${e.message}",
                status = HttpStatus.BAD_REQUEST
            )
        }
    }

    override fun editOrder(id: String, order: OrderRequest, token: String): ServiceResponse<Orders> {
        return try {
            val check = checkUtil.checkToken(token)
            return if(check) {
                return try {
                    val orderTemp = ordersRepository.findById(id)
                    order.id = orderTemp.get().id
                    val b = mutableListOf<OrdersProducts>()
                    order.products.forEach {
                        ordersProductsRepository.deleteById(it.id)
                        val temp = ordersProductsRepository.save(
                            OrdersProducts(
                                productID = it.productID,
                                count = it.count,
                                sellPrice = it.sellPrice,
                                price = it.price
                            )
                        )
                        b.add(temp)
                    }
                    val ordering = Orders(
                        id = order.id,
                        city = orderTemp.get().city,
                        streetFull = orderTemp.get().streetFull,
                        fullName = orderTemp.get().fullName,
                        phone = orderTemp.get().phone,
                        email = orderTemp.get().email,
                        typePayment = orderTemp.get().typePayment,
                        typeDelivery = orderTemp.get().typeDelivery,
                        code = orderTemp.get().code,
                        price = order.price,
                        paymentID = orderTemp.get().paymentID,
                        paymentSuccess = orderTemp.get().paymentSuccess,
                        products = b.toMutableSet(),
                        servicesPdf = orderTemp.get().servicesPdf,
                        status = orderTemp.get().status,
                        updated = LocalDateTime.now().toString(),
                        dateCreated = orderTemp.get().dateCreated,
                        deleteTime = orderTemp.get().deleteTime,
                        emailSend = orderTemp.get().emailSend,
                        orderFiles = orderTemp.get().orderFiles,
                        utmMet = orderTemp.get().utmMet
                    )
                    ordersRepository.deleteById(id)
                    ordersRepository.save(ordering)
                    ServiceResponse(
                        data = listOf(ordering),
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

    var c: CheckStatePaymentResponse = CheckStatePaymentResponse()

    var cr: CreditInfoResponse = CreditInfoResponse()

    var l: OrderConverterPaid? = OrderConverterPaid(
        id = "",
        status = "",
        amount = Amount(),
        recipient = Recipient(),
        created_at = "",
        paid = false
    )

    override fun addServices(services: List<ServiceRequest>, token: String, id: String): ServiceResponse<Orders>? {
        val check = checkUtil.checkToken(token)
        return if (check) {
            try {
                val order = ordersRepository.findById(id).get()
                if(order.servicesPdf.size > 0 ){
                    order.removeServicesPdf()
                }

                services.forEach {
                    val service = Services(
                        serviceName = it.serviceName,
                        count = it.count,
                        price = it.price
                    )
                    val temp = serviceRepository.save(service)
                    order.addServicesPdf(temp)
                }

                ordersRepository.deleteById(order.id)
                ordersRepository.save(order)

                ServiceResponse(
                    data = listOf(order),
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
        } else {
            ServiceResponse(
                data = null,
                message = "Unexpected token",
                status = HttpStatus.UNAUTHORIZED
            )
        }

    }

    override fun editServices(services: List<ServiceRequest>, token: String, id: String): ServiceResponse<Orders>? {
        val check = checkUtil.checkToken(token)
        return if (check) {
            try {
                val order = ordersRepository.findById(id).get()

                if(order.servicesPdf.size > 0 ){
                    order.removeServicesPdf()
                }

                services.forEach {
                    val service = Services(
                        serviceName = it.serviceName,
                        count = it.count,
                        price = it.price
                    )
                    val temp = serviceRepository.save(service)
                    order.addServicesPdf(temp)
                }

                ordersRepository.findById(order.id)
                ordersRepository.save(order)

                ServiceResponse(
                    data = listOf(order),
                    message = "Success",
                    status = HttpStatus.BAD_REQUEST
                )

            } catch (e: Exception) {
                ServiceResponse(
                    data = null,
                    message = e.message.toString(),
                    status = HttpStatus.BAD_REQUEST
                )
            }
        } else {
            ServiceResponse(
                data = null,
                message = "Unexpected token",
                status = HttpStatus.UNAUTHORIZED
            )
        }

    }

    override fun deleteServices(token: String, id: String): ServiceResponse<Orders>? {
        val check = checkUtil.checkToken(token)
        return if (check) {
            try {
                val order = ordersRepository.findById(id).get()

                if(order.servicesPdf.size > 0 ){
                    order.removeServicesPdf()
                }

                ServiceResponse(
                    data = listOf(order),
                    message = "Success",
                    status = HttpStatus.BAD_REQUEST
                )

            } catch (e: Exception) {
                ServiceResponse(
                    data = null,
                    message = e.message.toString(),
                    status = HttpStatus.BAD_REQUEST
                )
            }
        } else {
            ServiceResponse(
                data = null,
                message = "Unexpected token",
                status = HttpStatus.UNAUTHORIZED
            )
        }

    }

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
                        Sort.Order(Sort.Direction.DESC, "dateCreated"),
                    )

                    "old" -> Sort.by(
                        Sort.Order(Sort.Direction.ASC, "dateCreated")
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
            checkPaymentAndSentEmail(order)
            try {
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
                        orderFiles = order.orderFiles,
                        dateCreated = order.dateCreated,
                        paymentID = order.paymentID,
                        paymentSuccess = order.typePayment,
                        products = order.products,
                        status = order.status,
                        servicesPdf = order.servicesPdf,
                        updated = SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(
                            Date.from(
                                Date().toInstant().atZone(
                                    ZoneId.of("Europe/Moscow")
                                ).toInstant()
                            )
                        ).toString(),
                        utmMet = order.utmMet
                    ),
                    products = products
                )
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
                checkPaymentAndSentEmail(order)
            }
        } catch (e: Exception) {
            println(e)
        }
    }

    override fun checkPaymentAndSentEmail(order: Orders) {
        if (order.deleteTime == null && order.paymentSuccess == "false" || order.emailSend == null || order.emailSend == false) {
            try {
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
                    val tokenList = "${terminalPassword}${order.paymentID}${terminalKey}"
                    val digest = MessageDigest.getInstance("SHA-256")
                    val hash = digest.digest(tokenList.toByteArray(StandardCharsets.UTF_8))
                    val ffd = hash.joinToString("") { "%02x".format(it) }

                    runBlocking {
                        val f = client.post {
                            headers {
                                contentType(Json)
                            }
                            setBody(
                                CheckStatePaymentRequest(
                                    terminalKey = terminalKey,
                                    paymentId = order.paymentID!!,
                                    token = ffd
                                )
                            )
                            url {
                                protocol = URLProtocol.HTTPS
                                host = "securepay.tinkoff.ru/v2/GetState"
                            }
                        }.body<CheckStatePaymentResponse>()
                        c = f
                    }

                    if ((c.status == "CONFIRMED" && order.emailSend == false) || (order.emailSend == null && c.status == "CONFIRMED")) {
                        emailService.sendOrderMessage(
                            paymentInfo = order,
                            title = "Заказ успешно оплачен",
                            template = "orderPaid"
                        )
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
                                paymentSuccess = true.toString(),
                                products = order.products,
                                dateCreated = order.dateCreated,
                                status = "Заказ успешно оплачен",
                                orderFiles = order.orderFiles,
                                updated = SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(
                                    Date.from(
                                        Date().toInstant().atZone(
                                            ZoneId.of("Europe/Moscow")
                                        ).toInstant()
                                    )
                                ).toString(),
                                deleteTime = null,
                                emailSend = true,
                                utmMet = order.utmMet
                            )
                        )
                    }

                    if ((c.status == "CANCELED" && order.emailSend == false) || (order.emailSend == null && c.status == "CANCELED") || (order.emailSend == null && c.status == "REJECTED") || (c.status == "REJECTED" && order.emailSend == false)) {
                        emailService.sendOrderMessage(
                            paymentInfo = order,
                            title = "Заказ не был оплачен",
                            template = "rejected"
                        )
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
                                paymentSuccess = false.toString(),
                                products = order.products,
                                dateCreated = order.dateCreated,
                                status = "Заказ не был оплачен. Он будет удален через неделю. (Если хотите оплатить этот заказ - сформируйте новый заказ).",
                                orderFiles = order.orderFiles,
                                updated = SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(
                                    Date.from(
                                        Date().toInstant().atZone(
                                            ZoneId.of("Europe/Moscow")
                                        ).toInstant()
                                    )
                                ).toString(),
                                deleteTime = order.deleteTime ?: LocalDate.now().plusDays(7),
                                emailSend = true,
                                utmMet = order.utmMet
                            )
                        )
                    }
                }
                if(order.typePayment == "credit") {
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

                    if(order.emailSend == null || order.emailSend == false) {
                        runBlocking {
                            val f = client.get {
                                headers {
                                    contentType(ContentType.Application.Json)
                                }
                                basicAuth(
                                    username = showCaseIDCredit,
                                    password = shopPasswordCredit,
                                )
                                url {
                                    protocol = URLProtocol.HTTPS
                                    host = "forma.tinkoff.ru/api/partners/v2/orders/${order.id}/info"
                                }
                            }.body<CreditInfoResponse>()
                            cr = f
                        }
                    }
                    if ((cr.status == "signed" && order.emailSend == false) || (order.emailSend == null && cr.status == "signed")) {
                        emailService.sendOrderMessage(
                            paymentInfo = order,
                            title = "Заказ успешно оплачен",
                            template = "orderPaid"
                        )
                        emailService.sendOrderMessageUvanna(
                            paymentInfo = order,
                            title = "Заказ успешно оплачен"
                        )
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
                                paymentSuccess = true.toString(),
                                products = order.products,
                                dateCreated = order.dateCreated,
                                status = "Заказ успешно оплачен",
                                orderFiles = order.orderFiles,
                                updated = SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(
                                    Date.from(
                                        Date().toInstant().atZone(
                                            ZoneId.of("Europe/Moscow")
                                        ).toInstant()
                                    )
                                ).toString(),
                                deleteTime = null,
                                emailSend = true,
                                utmMet = order.utmMet
                            )
                        )
                    }

                    if ((cr.status == "canceled" && order.emailSend == false) || (order.emailSend == null && cr.status == "canceled") || (order.emailSend == null && cr.status == "rejected") || (cr.status == "rejected" && order.emailSend == false)) {
                        emailService.sendOrderMessage(
                            paymentInfo = order,
                            title = "Заказ не был оплачен",
                            template = "rejected"
                        )
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
                                paymentSuccess = false.toString(),
                                products = order.products,
                                dateCreated = order.dateCreated,
                                status = "Заказ не был оплачен. Он будет удален через неделю. (Если хотите оплатить этот заказ - сформируйте новый заказ).",
                                orderFiles = order.orderFiles,
                                updated = SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(
                                    Date.from(
                                        Date().toInstant().atZone(
                                            ZoneId.of("Europe/Moscow")
                                        ).toInstant()
                                    )
                                ).toString(),
                                deleteTime = order.deleteTime ?: LocalDate.now().plusDays(7),
                                emailSend = true,
                                utmMet = order.utmMet
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                e.message
            }
        }
    }


    fun generateExcelFile(token: String): ByteArray? {
        val check = checkUtil.checkToken(token)
        if (check) {
            val orders = ordersRepository.findAll()
            val workbook = XSSFWorkbook()
            val sheet = workbook.createSheet("Orders")
            val headerRow = sheet.createRow(0)
            headerRow.createCell(0).setCellValue("Code")
            headerRow.createCell(1).setCellValue("City")
            headerRow.createCell(2).setCellValue("Street Full")
            headerRow.createCell(3).setCellValue("Full Name")
            headerRow.createCell(4).setCellValue("Phone")
            headerRow.createCell(5).setCellValue("Email")
            headerRow.createCell(6).setCellValue("Type Payment")
            headerRow.createCell(7).setCellValue("Type Delivery")
            headerRow.createCell(9).setCellValue("Price")
            headerRow.createCell(9).setCellValue("Payment ID")
            headerRow.createCell(10).setCellValue("Payment Success")
            headerRow.createCell(11).setCellValue("Status")
            headerRow.createCell(12).setCellValue("Updated")
            headerRow.createCell(13).setCellValue("Date Created")
            headerRow.createCell(14).setCellValue("Delete Time")
            headerRow.createCell(15).setCellValue("UTM Met")

            var rowNum = 1
            for (order in orders) {
                val row = sheet.createRow(rowNum++)
                row.createCell(0).setCellValue(order.code)
                row.createCell(1).setCellValue(order.city)
                row.createCell(2).setCellValue(order.streetFull)
                row.createCell(3).setCellValue(order.fullName)
                row.createCell(4).setCellValue(order.phone)
                row.createCell(5).setCellValue(order.email)
                row.createCell(6).setCellValue(order.typePayment)
                row.createCell(7).setCellValue(order.typeDelivery)
                row.createCell(8).setCellValue(order.price)
                row.createCell(9).setCellValue(order.paymentID ?: "")
                row.createCell(10).setCellValue(order.paymentSuccess ?: "")
                row.createCell(11).setCellValue(order.status)
                row.createCell(12).setCellValue(order.updated)
                row.createCell(13).setCellValue(order.dateCreated.toString())
                row.createCell(14).setCellValue(order.deleteTime?.toString() ?: "")
                row.createCell(15).setCellValue(order.utmMet)
            }

            val outputStream = ByteArrayOutputStream()
            workbook.write(outputStream)
            outputStream.close()
            return outputStream.toByteArray()
        } else return null
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