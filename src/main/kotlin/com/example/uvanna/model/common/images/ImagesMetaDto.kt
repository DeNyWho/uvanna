package com.example.uvanna.model.common.images

import com.fasterxml.jackson.annotation.JsonProperty

data class ImagesMetaDto(
    @JsonProperty("href")
    val href: String = "",
)