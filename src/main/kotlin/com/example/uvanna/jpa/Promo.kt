package com.example.uvanna.jpa

import java.time.LocalDate
import javax.persistence.*

@Entity
@Table(name = "Promo")
data class Promo(
    @Id
    var id: String = "",
    var title: String? = "",
    @Column(columnDefinition = "TEXT")
    val description: String? = "",
    val imageUrl: String? = null,
    val dateCreated: LocalDate? = null,
    val dateExpired: LocalDate? = null,
    @ElementCollection
    @Column(nullable = true)
    var productsPromo: MutableSet<String> = mutableSetOf(),
    @Column(nullable = true)
    var isEnd: Boolean? = null
) {
    fun deleteAllPromoProducts(): Promo {
        productsPromo.clear()
        return this
    }

    fun addPromoProducts(productId: String): Promo {
        productsPromo.add(productId)
        return this
    }

    fun deletePromoProducts(productId: String): Promo {
        productsPromo.remove(productId)
        return this
    }
}