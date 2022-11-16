package com.example.uvanna.service

import com.example.uvanna.jpa.Orders
import com.example.uvanna.model.response.ServiceResponse
import com.example.uvanna.repository.orders.OrdersRepository
import com.example.uvanna.repository.orders.OrdersRepositoryImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class OrderService: OrdersRepositoryImpl {

    @Autowired
    lateinit var ordersRepository: OrdersRepository

    override fun createNewOrder(order: Orders): String {
        return try {
            ordersRepository.save(order)
            "Success"
        } catch (e: Exception){
             e.message.toString()
        }
    }

    override fun getOrders(id: String): ServiceResponse<Orders>? {
        return try {
            ServiceResponse(
                data = listOf(ordersRepository.getById(id)),
                message = "",
                status = HttpStatus.OK
            )
        } catch (e: Exception){
            ServiceResponse(
                data = listOf(ordersRepository.getById(id)),
                message = e.message.toString(),
                status = HttpStatus.BAD_GATEWAY
            )
        }
    }
}