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
    @OneToMany(
        fetch = FetchType.EAGER,
        cascade = [CascadeType.DETACH, CascadeType.PERSIST, CascadeType.REFRESH]
    )
    @Column(nullable = true)
    var productsPromo: MutableSet<Product> = mutableSetOf()
) {
    fun deleteAllPromoProducts(): Promo {
        productsPromo.forEach {
            it.sellPrice = null
            it.percent = null
        }
        productsPromo.clear()
        return this
    }
    fun addPromoProducts(products: Product): Promo {
        productsPromo.add(products)
        return this
    }

    fun deletePromoProducts(product: Product): Promo {
        productsPromo.remove(product)
        return this
    }
}