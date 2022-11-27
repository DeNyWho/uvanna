package com.example.uvanna.repository.orders

import com.example.uvanna.jpa.Orders
import com.example.uvanna.model.response.ServiceResponse
import org.springframework.stereotype.Repository

@Repository
interface OrdersRepositoryImpl {


    fun createNewOrder(order: Orders): String
    fun getOrders(id: String): ServiceResponse<Orders>?
    fun editOrder(id: String, order: Orders): String
}