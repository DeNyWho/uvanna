package com.example.uvanna.repository.payment

import com.example.uvanna.jpa.Orders
import com.example.uvanna.model.request.payment.PaymentDataRequest
import com.example.uvanna.model.request.payment.ProductsRequestsing
import com.example.uvanna.model.response.ServiceResponse
import org.springframework.stereotype.Repository

@Repository
interface PaymentRepositoryImpl {

    fun getOrder(id: String): ServiceResponse<Orders>?

    fun createNewPayment(ordersProducts: List<ProductsRequestsing>, paymentDataRequest: PaymentDataRequest): Any
}