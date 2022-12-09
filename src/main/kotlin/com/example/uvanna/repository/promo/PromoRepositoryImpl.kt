package com.example.uvanna.repository.promo

import com.example.uvanna.jpa.Promo
import com.example.uvanna.model.response.PagingResponse
import com.example.uvanna.model.response.ServiceResponse
import org.springframework.web.multipart.MultipartFile

interface PromoRepositoryImpl {

    fun addPromo(title: String, description: String, file: MultipartFile): ServiceResponse<Promo>
    fun getPromo(id: String): ServiceResponse<Promo>
    fun getPromos(pageSize: Int, pageNum: Int): PagingResponse<Promo>
    fun addPromoWithCategory(
        title: String,
        description: String,
        file: MultipartFile,
        category: String
    ): ServiceResponse<Promo>
}