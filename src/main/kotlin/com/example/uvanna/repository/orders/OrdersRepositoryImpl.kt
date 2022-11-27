package com.example.uvanna.repository.orders

import com.example.uvanna.jpa.Orders
import com.example.uvanna.model.response.PagingResponse
import com.example.uvanna.model.response.ServiceResponse
import org.springframework.stereotype.Repository
import javax.validation.constraints.Max
import javax.validation.constraints.Min

@Repository
interface OrdersRepositoryImpl {
    fun getOrders(id: String): ServiceResponse<Orders>?
    fun editOrder(id: String, order: Orders): String
    fun getOrdersList(
        filter: String?,
        pageNum: @Min(value = 0.toLong()) @Max(value = 500.toLong()) Int,
        pageSize: @Min(value = 1.toLong()) @Max(value = 500.toLong()) Int
    ): PagingResponse<Orders>?
}