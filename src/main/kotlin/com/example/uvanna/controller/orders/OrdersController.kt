package com.example.uvanna.controller.orders

import com.example.uvanna.jpa.Orders
import com.example.uvanna.model.request.payment.PaymentDataRequest
import com.example.uvanna.model.response.PagingResponse
import com.example.uvanna.model.response.ServiceResponse
import com.example.uvanna.service.OrderService
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.crossstore.ChangeSetPersister
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletResponse
import javax.validation.constraints.Max
import javax.validation.constraints.Min

@RestController
@CrossOrigin("*")
@Tag(name = "orders", description = "orders")
@RequestMapping("/api/orders/")
class OrdersController {

    @Autowired
    lateinit var orderService: OrderService

    @PostMapping("edit/{id}")
    fun editOrder(
        @PathVariable id: String,
        orders: Orders,
        response: HttpServletResponse
    ): ServiceResponse<Any> {
        return try {
            ServiceResponse(
                data = listOf(orderService.editOrder(id, orders)),
                message = "Order has been edited",
                status = HttpStatus.OK
            )
        } catch (e: ChangeSetPersister.NotFoundException) {
            ServiceResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
        }
    }

    @GetMapping()
    fun getOrders(
        @Parameter(description = "filter = new | old | paid | no paid ")
        filter: String?,
        @RequestParam(defaultValue = "0") pageNum: @Min(0) @Max(500) Int,
        @RequestParam(defaultValue = "48") pageSize: @Min(1) @Max(500) Int,
        response: HttpServletResponse
    ): PagingResponse<Orders>? {
        return try {
             orderService.getOrdersList(filter, pageNum, pageSize)
        } catch (e: ChangeSetPersister.NotFoundException) {
            PagingResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
        }
    }

    @GetMapping("{code}")
    fun getOrder(
        @PathVariable code: String,
        response: HttpServletResponse
    ): Any {
            return orderService.getOrders(code)
//            ServiceResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
//        }
    }

//    @GetMapping("{code}")
//    fun getOrder(
//        @PathVariable code: String,
//        response: HttpServletResponse
//    ): ServiceResponse<Orders>? {
//        return try {
//            return orderService.getOrders(code)
//        } catch (e: ChangeSetPersister.NotFoundException) {
//            ServiceResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
//        }
//    }
}