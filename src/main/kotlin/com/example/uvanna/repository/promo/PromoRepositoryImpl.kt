package com.example.uvanna.repository.promo

import com.example.uvanna.jpa.Promo
import com.example.uvanna.model.request.promo.PromoProductRequest
import com.example.uvanna.model.response.PagingResponse
import com.example.uvanna.model.response.ServiceResponse
import org.springframework.web.multipart.MultipartFile

interface PromoRepositoryImpl {

    fun getPromo(id: String): ServiceResponse<Promo>
    fun getPromos(pageSize: Int, pageNum: Int): PagingResponse<Promo>

    fun deletePromo(id: String, token: String): ServiceResponse<String>
    fun addPromoWithProducts(
        title: String,
        description: String,
        file: MultipartFile,
        products: List<PromoProductRequest>,
        token: String,
        number: Int?,
        percent: Int?,
        dateExpired: String
    ): ServiceResponse<Promo>
}