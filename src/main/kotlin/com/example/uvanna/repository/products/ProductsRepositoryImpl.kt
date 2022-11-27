package com.example.uvanna.repository.products

import com.example.uvanna.jpa.Characteristic
import com.example.uvanna.jpa.Product
import com.example.uvanna.model.product.Brands
import com.example.uvanna.model.request.product.ProductRequest
import com.example.uvanna.model.response.ProductsLightResponse
import com.example.uvanna.model.response.PagingResponse
import com.example.uvanna.model.response.ProductLighterResponse
import com.example.uvanna.model.response.ServiceResponse
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
    ): ServiceResponse<Product>?

//
//    fun getProducts(
//        countCard: Int,
//        page: Int,
//        brand: String?,
//        smallPrice: Int?,
//        sort: String?,
//        filter: String?,
//        highPrice: Int?,
//        characteristic: List<Characteristic>?,
//        level: String?
//    ): ServiceResponse<ProductsLightResponse>?

    fun getCharactSort(level: String): List<Characteristic>

    fun getProduct(id: String): ServiceResponse<Product>?

    fun getBrands(id: String): ServiceResponse<String>?
    fun getProducts(
        countCard: Int,
        page: Int,
        brand: Brands?,
        smallPrice: Int?,
        highPrice: Int?,
        order: String?,
        filter: String?,
        level: String?,
        categoryId: String?
    ): PagingResponse<ProductsLightResponse>?

    fun findProduct(searchQuery: String): ServiceResponse<ProductLighterResponse>?
}