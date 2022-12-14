package com.example.uvanna.repository.promo

import com.example.uvanna.jpa.Promo
import com.example.uvanna.model.request.promo.PromoProductRequest
import com.example.uvanna.model.response.PagingResponse
import com.example.uvanna.model.response.ProductsLightResponse
import com.example.uvanna.model.response.ServiceResponse
import org.springframework.web.multipart.MultipartFile

interface PromoRepositoryImpl {

    fun getPromo(id: String): ServiceResponse<Promo>

    fun getPromos(pageSize: Int, pageNum: Int): PagingResponse<Promo>

    fun deletePromo(id: String, token: String): ServiceResponse<String>

    fun addProductPromo(id: String, token: String, productsIds: List<PromoProductRequest>): ServiceResponse<Any>

    fun getProductPromo(page: Int, countCard: Int, id: String): PagingResponse<ProductsLightResponse>

    fun createPromo(
        title: String,
        description: String,
        file: MultipartFile,
        token: String,
        dateExpired: String
    ): ServiceResponse<Promo>

    fun addProductsToPromo(id: String, token: String, products: List<PromoProductRequest>): ServiceResponse<Promo>

    fun editPromo(
        id: String,
        title: String,
        description: String,
        file: MultipartFile,
        token: String,
        dateExpired: String
    ): ServiceResponse<Promo>

    fun getPromosIds(): ServiceResponse<String>
    fun scheduleCheckForDelete()
}