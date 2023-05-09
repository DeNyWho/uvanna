package com.example.uvanna.repository.products

import com.example.uvanna.jpa.ProductBrands
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface BrandsRepository: JpaRepository<ProductBrands, String> {

    fun findByTitle(title: String): Optional<ProductBrands>
}