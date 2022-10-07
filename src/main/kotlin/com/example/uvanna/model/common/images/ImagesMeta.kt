package com.example.uvanna.model.common.images

import com.fasterxml.jackson.annotation.JsonProperty

data class ImagesMeta(
    @JsonProperty("downloadHref")
    var downloadHref: String,
)