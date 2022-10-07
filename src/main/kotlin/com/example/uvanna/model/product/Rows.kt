package com.example.uvanna.model.product

import com.example.uvanna.model.product.folder.ProductFolderDto
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class Rows(
    @JsonProperty("rows")
    val productFolder: List<ProductFolderDto>,
)