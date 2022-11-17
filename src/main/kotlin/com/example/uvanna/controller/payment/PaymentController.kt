package com.example.uvanna.controller.payment

import com.example.uvanna.model.response.ServiceResponse
import com.example.uvanna.service.PaymentService
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.crossstore.ChangeSetPersister
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
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
        @RequestParam ids: List<String>,
        response: HttpServletResponse
    ): ServiceResponse<Any> {
        return try {
            ServiceResponse(
                data = listOf(paymentService.createNewPayment(ids)),
                message = "Payment has been created",
                status = HttpStatus.OK
            )
        } catch (e: ChangeSetPersister.NotFoundException) {
            ServiceResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
        }
    }
}