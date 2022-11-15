package com.example.uvanna.repository.products

import com.example.uvanna.jpa.Characteristic
import com.example.uvanna.model.product.ProductRequest
import com.example.uvanna.model.product.ProductsLightResponse
import org.springframework.stereotype.Repository
import org.springframework.web.multipart.MultipartFile

@Repository
interface ProductsRepositoryImpl {

    fun parser(brand: String): List<String>

    fun deleteProduct(id: String)

    fun addProduct(
        product: ProductRequest,
        files: List<MultipartFile>,
        characteristic: List<String>,
        data: List<String>
    )


    fun getProducts(
        countCard: Int,
        page: Int,
        brand: String?,
        smallPrice: Int?,
        sort: String?,
        filter: String?,
        highPrice: Int?,
        characteristic: List<Characteristic>?
    ): List<ProductsLightResponse>
}