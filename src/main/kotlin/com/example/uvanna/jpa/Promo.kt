package com.example.uvanna.jpa

import java.time.LocalDate
import javax.persistence.*

@Entity
@Table(name = "Promo")
data class Promo(
    @Id
    var id: String = "",
    var title: String? = "",
    val description: String? = "",
    val imageUrl: String? = null,
    val dateCreated: LocalDate? = null,
    val dateExpired: String? = null,
    @OneToMany(
        fetch = FetchType.EAGER,
        cascade = [CascadeType.DETACH, CascadeType.PERSIST, CascadeType.REFRESH]
    )
    @Column(nullable = true)
    var productsPromo: MutableSet<Product> = mutableSetOf()
) {
    fun addPromoProducts(products: Product): Promo {
        productsPromo.add(products)
        return this
    }
}