package com.example.uvanna.repository.products

import com.example.uvanna.jpa.Product
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductsRepository: JpaRepository<Product, String> {
}