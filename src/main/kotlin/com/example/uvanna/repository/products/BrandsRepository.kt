package com.example.uvanna.repository.products

import com.example.uvanna.jpa.ProductBrands
import org.springframework.data.jpa.repository.JpaRepository

interface BrandsRepository: JpaRepository<ProductBrands, String>