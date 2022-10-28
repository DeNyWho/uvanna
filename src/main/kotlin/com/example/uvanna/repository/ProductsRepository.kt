package com.example.uvanna.repository

import com.example.uvanna.model.product.detail.ProductDetail
import com.example.uvanna.model.product.main.Product
import com.example.uvanna.model.product.folder.ProductFolder
import org.springframework.stereotype.Repository

@Repository
interface ProductsRepository {

    fun getProductsByFolder(id: String, pageNum: Int, pageSize: Int): List<Product>
    fun getProductFolder(): List<ProductFolder>
    fun getProduct(id: String): List<Product>
    fun parser(brand: String)
}