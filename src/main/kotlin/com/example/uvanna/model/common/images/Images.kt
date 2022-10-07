package com.example.uvanna.model.common.images

import com.fasterxml.jackson.annotation.JsonProperty

data class Images(
    @JsonProperty("meta")
    val meta: ImagesMeta,
)