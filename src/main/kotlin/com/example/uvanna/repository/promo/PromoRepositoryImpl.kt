package com.example.uvanna.repository.promo

import com.example.uvanna.jpa.Promo
import com.example.uvanna.model.response.PagingResponse
import com.example.uvanna.model.response.ServiceResponse
import org.springframework.web.multipart.MultipartFile

interface PromoRepositoryImpl {

    fun getPromo(id: String): ServiceResponse<Promo>
    fun getPromos(pageSize: Int, pageNum: Int): PagingResponse<Promo>
    fun addPromoWithCategory(
        title: String,
        description: String,
        file: MultipartFile,
        category: String,
        token: String
    ): ServiceResponse<Promo>

    fun addPromoWithProducts(
        title: String,
        description: String,
        file: MultipartFile,
        products: List<String>,
        token: String
    ): ServiceResponse<Promo>

    fun deletePromo(id: String, token: String): ServiceResponse<String>
}