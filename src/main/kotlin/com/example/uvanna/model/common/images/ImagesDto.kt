package com.example.uvanna.model.common.images

import com.fasterxml.jackson.annotation.JsonProperty

data class ImagesDto(
    @JsonProperty("meta")
    val meta: ImagesMetaDto,
)