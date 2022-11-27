package com.example.uvanna.repository.orders

import com.example.uvanna.jpa.Orders
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort.Order
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface OrdersRepository: JpaRepository<Orders, String> {

    @Query("select v from Orders v where v.paymentSuccess = :paid")
    fun findByPaidStatus(pageable: Pageable, paid: String): Page<Orders>

    @Query("select m from Orders m where m.code = :code")
    fun findByCode(code: String): Optional<Orders>
}