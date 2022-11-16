package com.example.uvanna.service

import com.example.uvanna.model.payment.*
import com.example.uvanna.model.request.payment.PaymentRequest
import com.example.uvanna.model.response.PaymentResponse
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
import kotlinx.coroutines.runBlocking
import okhttp3.Credentials
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.UUID


@Service
class PaymentService {

    @Value("\${payment_key}")
    lateinit var paymentKey: String

    @Value("\${payment_shop}")
    lateinit var paymentShop: String

    @Autowired
    lateinit var productsRepository: ProductsRepository

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

    fun createNewPayment(ids: List<String>): Any  {
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

        var price = 0
        ids.forEach {
            price = price + productsRepository.findById(it).get().price
        }

        println(price)

        runBlocking {
            val f = client.post {
                headers {
                    contentType(ContentType.Application.Json)
                    append("Idempotence-Key", UUID.randomUUID().toString())
                    append(Authorization, Credentials.basic(paymentShop,paymentKey))
                }
                setBody(
                    PaymentRequest(
                        amount = Amount(value = price.toString(), currency = "RUB"),
                        confirmation = Confirmation(type = "redirect", return_url = "https://uvanna.store/cart/history"),
                        capture = true,
                        test = true
                    )
                )
                url {
                    protocol = URLProtocol.HTTPS
                    host = "api.yookassa.ru/v3/payments"
                }
            }
            c = f.body()
            c.metadata = null
        }
        return c
    }
}