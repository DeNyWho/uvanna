package com.example.uvanna.repository.site

import com.example.uvanna.jpa.MainBanner
import com.example.uvanna.model.response.ServiceResponse
import org.springframework.stereotype.Repository
import org.springframework.web.multipart.MultipartFile

@Repository
interface SiteRepositoryImpl {

    fun getMainBanners(): ServiceResponse<MainBanner>
    fun deleteMainBanner(token: String, id: String): ServiceResponse<String>
    fun addMainBanner(file: MultipartFile, token: String): ServiceResponse<MainBanner>
}