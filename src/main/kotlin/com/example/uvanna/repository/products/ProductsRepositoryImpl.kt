package com.example.uvanna.repository.products

import com.example.uvanna.model.product.ProductRequest
import org.springframework.stereotype.Repository
import org.springframework.web.multipart.MultipartFile

@Repository
interface ProductsRepositoryImpl {

    fun parser(brand: String)
    fun deleteProduct(id: String)
    fun addProduct(
        product: ProductRequest,
        files: List<MultipartFile>,
        characteristic: List<String>,
        data: List<String>
    )
}