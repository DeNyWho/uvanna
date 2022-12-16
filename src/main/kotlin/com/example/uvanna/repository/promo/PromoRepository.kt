package com.example.uvanna.repository.promo

import com.example.uvanna.jpa.Product
import com.example.uvanna.jpa.Promo
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface PromoRepository: JpaRepository<Promo, String> {

    @Query("Select p.productsPromo from Promo p where p.id = :id")
    fun getProducts(id: String, pageable: Pageable): Page<Product>
}