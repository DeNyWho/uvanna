package com.example.uvanna.controller.orders

import com.example.uvanna.jpa.Orders
import com.example.uvanna.model.response.ServiceResponse
import com.example.uvanna.service.OrderService
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.crossstore.ChangeSetPersister
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletResponse

@RestController
@CrossOrigin("*")
@Tag(name = "orders", description = "orders")
@RequestMapping("/api/orders/")
class OrdersController {

    @Autowired
    lateinit var orderService: OrderService

    @PostMapping
    fun newOrder(
        @RequestBody orders: Orders,
        response: HttpServletResponse
    ): ServiceResponse<Any> {
        return try {
            ServiceResponse(
                data = listOf(orderService.createNewOrder(orders)),
                message = "Order has been created",
                status = HttpStatus.OK
            )
        } catch (e: ChangeSetPersister.NotFoundException) {
            ServiceResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
        }
    }

    @GetMapping("{id}")
    fun getOrder(
        @PathVariable id: String,
        response: HttpServletResponse
    ): ServiceResponse<Orders>? {
        return try {
            return orderService.getOrders(id)
        } catch (e: ChangeSetPersister.NotFoundException) {
            ServiceResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
        }
    }
}