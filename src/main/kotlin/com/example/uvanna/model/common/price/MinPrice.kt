package com.example.uvanna.model.common.price

import com.fasterxml.jackson.annotation.JsonProperty

data class MinPrice(
    @JsonProperty("value")
    val value: String
)