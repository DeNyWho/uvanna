package com.example.uvanna.model.common.images

import com.fasterxml.jackson.annotation.JsonProperty

data class ImagesMeta(
    @JsonProperty("href")
    var downloadHref: String,
)