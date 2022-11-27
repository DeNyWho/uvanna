package com.example.uvanna.controller.payment

import com.example.uvanna.jpa.Orders
import com.example.uvanna.model.OrdersProducts
import com.example.uvanna.model.request.payment.PaymentDataRequest
import com.example.uvanna.model.request.payment.PaymentProductRequest
import com.example.uvanna.model.response.ServiceResponse
import com.example.uvanna.service.PaymentService
import io.swagger.v3.oas.annotations.Parameter
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
        @RequestParam ids: List<PaymentProductRequest>,
        @Parameter(description = "typePayment = nal | beznal typeDelivery poka tolko: pickup")
        @RequestParam paymentDataRequest: PaymentDataRequest,
        response: HttpServletResponse
    ): ServiceResponse<Any> {
        return try {
            ServiceResponse(
                data = listOf(paymentService.createNewPayment(ids, paymentDataRequest)),
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