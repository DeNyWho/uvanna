package com.example.uvanna.model.common.price

import com.fasterxml.jackson.annotation.JsonProperty

data class BuyPrice(
    @JsonProperty("value")
    val value: String = ""
)