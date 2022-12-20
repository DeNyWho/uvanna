package com.example.uvanna.repository.orders

import com.example.uvanna.model.OrdersProducts
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OrdersProductsRepository: JpaRepository<OrdersProducts, String>