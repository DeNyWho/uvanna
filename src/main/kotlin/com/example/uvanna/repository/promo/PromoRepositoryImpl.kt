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

//    fun deleteProductPromo(id: String, token: String, productsIds: List<String>): Any
    fun addProductPromo(id: String, token: String, productsIds: List<PromoProductRequest>): ServiceResponse<Any>
    fun getProductPromo(page: Int, countCard: Int, id: String): PagingResponse<ProductsLightResponse>

    fun editPromoWithProducts(
        id: String,
        title: String,
        description: String,
        file: MultipartFile,
        products: List<PromoProductRequest>,
        token: String,
        dateExpired: String
    ): ServiceResponse<Promo>

    fun addPromoWithProducts(
        title: String,
        description: String,
        file: MultipartFile,
        products: List<PromoProductRequest>,
        token: String,
        dateExpired: String
    ): ServiceResponse<Promo>
}