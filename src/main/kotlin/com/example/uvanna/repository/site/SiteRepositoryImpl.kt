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

    fun getMainBanners(): ServiceResponse<MainBanner>
    fun deleteMainBanner(token: String, id: String): ServiceResponse<String>
    fun addMainBanner(file: MultipartFile, token: String): ServiceResponse<MainBanner>
}