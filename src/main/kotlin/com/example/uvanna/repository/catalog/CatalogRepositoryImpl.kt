package com.example.uvanna.repository.catalog

import com.example.uvanna.model.response.ServiceResponse
import org.springframework.stereotype.Repository
import org.springframework.web.multipart.MultipartFile

@Repository
interface CatalogRepositoryImpl {

    fun getLevels(id: String?): Any
    fun getUpperLevels(id: String): Any
    fun deleteCategory(id: String, token: String): ServiceResponse<String>
    fun addLevel(
        id: String?,
        file: MultipartFile,
        title: String,
        option: String,
        token: String
    ): ServiceResponse<String>
}