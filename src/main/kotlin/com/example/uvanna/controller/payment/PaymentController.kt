package com.example.uvanna.controller.payment

import com.example.uvanna.jpa.Orders
import com.example.uvanna.model.request.payment.PaymentDataRequest
import com.example.uvanna.model.request.payment.ProductsRequestsing
import com.example.uvanna.model.response.ServiceResponse
import com.example.uvanna.service.PaymentService
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.crossstore.ChangeSetPersister
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletResponse

@RestController
@CrossOrigin("*")
@Tag(name = "payment", description = "payment")
@RequestMapping("/api/payment/")
class PaymentController {

    @Autowired
    lateinit var paymentService: PaymentService

    @PostMapping
    fun newPayment(
        @RequestBody paymentProductRequest: List<ProductsRequestsing>,
        city: String,
        streetFull: String,
        fullname: String,
        phone: String,
        email: String,
        typePayment: String,
        typeDelivery: String,
        response: HttpServletResponse
    ): ServiceResponse<Any> {
        return try {
            val payment = PaymentDataRequest(
                city = city,
                streetFull = streetFull,
                fullname = fullname,
                phone = phone,
                email = email,
                typePayment = typePayment,
                typeDelivery = typeDelivery,
            )
            ServiceResponse(
                data = listOf(paymentService.createNewPayment(paymentProductRequest, payment)),
                message = "Payment has been created",
                status = HttpStatus.OK
            )
        } catch (e: ChangeSetPersister.NotFoundException) {
            ServiceResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
        }
    }

    @GetMapping("{id}")
    fun getPayment(
        @PathVariable id: String,
        response: HttpServletResponse
    ): ServiceResponse<Orders>? {
        return try {
            paymentService.getOrder(id)
        } catch (e: ChangeSetPersister.NotFoundException) {
            ServiceResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
        }
    }
}