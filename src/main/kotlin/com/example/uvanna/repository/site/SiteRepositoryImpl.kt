package com.example.uvanna.repository.site

import com.example.uvanna.jpa.MainBanner
import com.example.uvanna.jpa.Promo
import com.example.uvanna.model.response.PagingResponse
import com.example.uvanna.model.response.PromoLightResponse
import com.example.uvanna.model.response.ServiceResponse
import org.springframework.stereotype.Repository
import org.springframework.web.multipart.MultipartFile
import javax.validation.constraints.Max
import javax.validation.constraints.Min

@Repository
interface SiteRepositoryImpl {

    fun addMainBanner(file: MultipartFile): ServiceResponse<MainBanner>
    fun addPromo(title: String, description: String, file: MultipartFile): ServiceResponse<Promo>
    fun getMainBanners(): ServiceResponse<MainBanner>
    fun getPromos(
        pageSize: @Min(value = 1.toLong()) @Max(value = 500.toLong()) Int,
        pageNum: @Min(value = 0.toLong()) @Max(value = 500.toLong()) Int
    ): PagingResponse<PromoLightResponse>

    fun getPromo(id: String): ServiceResponse<Promo>
}