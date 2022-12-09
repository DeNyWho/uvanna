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
    fun getMainBanners(): ServiceResponse<MainBanner>
}