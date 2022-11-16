package com.example.uvanna.repository.orders

import com.example.uvanna.jpa.Orders
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OrdersRepository: JpaRepository<Orders, String> {
}