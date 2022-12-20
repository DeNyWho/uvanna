package com.example.uvanna.jpa

import com.example.uvanna.model.OrdersProducts
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "orders")
data class Orders(
    @Id
    var id: String = UUID.randomUUID().toString(),
    var city: String = "",
    var streetFull: String = "",
    var fullName: String = "",
    var phone: String = "",
    val email: String = "",
    val typePayment: String = "",
    val typeDelivery: String = "",
    val code: String = "",
    val price: Double = 0.0,
    @Column(nullable = true)
    val paymentID: String? = null,
    @Column(nullable = true)
    val paymentSuccess: String? = null,
    @OneToMany(
        fetch = FetchType.EAGER,
        cascade = [CascadeType.DETACH, CascadeType.PERSIST, CascadeType.REFRESH]
    )
    @Column(nullable = true)
    val products: MutableSet<OrdersProducts> = mutableSetOf<OrdersProducts>(),
    val status: String = "",
    val updated: String = "",
    @Column(nullable = true)
    val deleteTime: LocalDate? = null,
    @Column(nullable = true)
    val emailSend: Boolean? = null
) {
    fun addProducts(product: OrdersProducts): Orders {
        products.add(product)
        return this
    }
}