package com.example.uvanna.model

import java.util.*
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "ordersProducts")
data class OrdersProducts (
    @Id
    var id: String = UUID.randomUUID().toString(),
    val productID: String = "",
    val count: Int = 0
)