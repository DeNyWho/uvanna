package com.example.uvanna.model.product.folder

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class ProductFolderDto(
    @JsonProperty("meta")
    val meta: FolderMeta,
    @JsonProperty("updated")
    val updated: String,
    @JsonProperty("name")
    val name: String,
)