package com.example.uvanna.repository.catalog

import com.example.uvanna.jpa.CatalogSecond
import com.example.uvanna.jpa.CatalogThird
import org.springframework.stereotype.Repository
import org.springframework.web.multipart.MultipartFile

@Repository
interface CatalogRepositoryImpl {

    fun addLevel(id: String?, file: MultipartFile, title: String, option: String): Boolean
    fun getLevels(id: String?): Any
    fun deleteCategory(id: String)
    fun getUpperLevels(id: String): Any
}