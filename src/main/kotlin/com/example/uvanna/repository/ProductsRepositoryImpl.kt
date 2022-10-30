package com.example.uvanna.repository

import com.example.uvanna.jpa.Product
import org.springframework.stereotype.Repository

@Repository
interface ProductsRepositoryImpl {

    //    fun getProductsByFolder(id: String, pageNum: Int, pageSize: Int): List<Product>
//    fun getProductFolder(): List<ProductFolder>
//    fun getProduct(id: String): List<Product>
    fun parser(brand: String)
    fun addProduct(): Product
}