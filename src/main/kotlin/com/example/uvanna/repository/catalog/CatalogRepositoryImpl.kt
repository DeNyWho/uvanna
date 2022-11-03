package com.example.uvanna.repository.catalog

import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Repository
import org.springframework.web.multipart.MultipartFile

@Repository
interface CatalogRepositoryImpl {

    fun addFirstLevel(file: MultipartFile, title: String, sub: List<String>): String
}