package com.example.uvanna.model.product.folder

import com.fasterxml.jackson.annotation.JsonProperty

data class FolderMeta(
    @JsonProperty("href")
    val href: String = "",
)