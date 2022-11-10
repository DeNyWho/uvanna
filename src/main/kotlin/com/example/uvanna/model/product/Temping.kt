package com.example.uvanna.model.product

import org.springframework.web.multipart.MultipartFile


data class Temping(
    val productRequest: ProductRequest,
    val characteristic: List<CharactRequest>,
    val files: List<MultipartFile>
)
