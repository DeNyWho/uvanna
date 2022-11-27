package com.example.uvanna.jpa

import com.example.uvanna.model.OrdersProducts
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.OneToMany
import javax.persistence.Table

@Entity
@Table(name = "orders")
data class Orders(
    @Id
    var id: String = "",
    var city: String = "",
    var streetFull: String = "",
    var fullName: String = "",
    var phone: String = "",
    val email: String = "",
    val typePayment: String = "",
    val typeDelivery: String = "",
    val code: String = "",
    @Column(nullable = true)
    val paymentID: String? = null,
    @Column(nullable = true)
    val paymentSuccess: String? = null,
    @OneToMany
    val products: List<OrdersProducts> = listOf(),
    val status: String = "",
    val updated: String = ""
)