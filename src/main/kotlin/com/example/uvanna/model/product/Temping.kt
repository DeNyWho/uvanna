package com.example.uvanna.model.product

import com.example.uvanna.model.request.product.CharactRequest
import com.example.uvanna.model.request.product.ProductRequest
import org.springframework.web.multipart.MultipartFile


data class Temping(
    val productRequest: ProductRequest,
    val characteristic: List<CharactRequest>,
    val files: List<MultipartFile>
)
