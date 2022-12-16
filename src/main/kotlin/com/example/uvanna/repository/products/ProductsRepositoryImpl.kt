package com.example.uvanna.repository.products

import com.example.uvanna.jpa.Characteristic
import com.example.uvanna.jpa.Product
import com.example.uvanna.model.product.Brands
import com.example.uvanna.model.request.product.ProductRequest
import com.example.uvanna.model.response.PagingResponse
import com.example.uvanna.model.response.ProductLighterResponse
import com.example.uvanna.model.response.ProductsLightResponse
import com.example.uvanna.model.response.ServiceResponse
import org.springframework.stereotype.Repository
import org.springframework.web.multipart.MultipartFile
import javax.validation.constraints.Max
import javax.validation.constraints.Min

@Repository
interface ProductsRepositoryImpl {


    fun getCharactSort(level: String): List<Characteristic>

    fun getProduct(id: String): ServiceResponse<Product>?

    fun getBrands(id: String): ServiceResponse<String>?

    fun findProduct(
        searchQuery: String,
        pageNum: @Min(value = 0.toLong()) @Max(value = 500.toLong()) Int,
        pageSize: @Min(value = 1.toLong()) @Max(value = 500.toLong()) Int
    ): ServiceResponse<ProductLighterResponse>?

    fun editProduct(
        id: String,
        characteristic: List<String>,
        data: List<String>,
        files: List<MultipartFile>,
        token: String,
        product: ProductRequest
    ): ServiceResponse<Product>?

    fun addProduct(
        product: ProductRequest,
        files: List<MultipartFile>,
        characteristic: List<String>,
        token: String,
        data: List<String>
    ): ServiceResponse<Product>?

    fun deleteProduct(token: String, id: String): ServiceResponse<String>

    fun getProducts(
        countCard: Int,
        page: Int,
        brand: Brands?,
        smallPrice: Int?,
        highPrice: Int?,
        filter: String?,
        categoryId: String?,
        stockEmpty: Boolean?,
        stockFull: Boolean?,
        isSellByPromo: Boolean?
    ): PagingResponse<ProductsLightResponse>?

    fun getProductRandom(
        countCard: Int,
        page: Int,
        filter: String?,
        productId: String?
    ): PagingResponse<ProductsLightResponse>?
}