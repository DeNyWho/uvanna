package com.example.uvanna.model.price

import com.fasterxml.jackson.annotation.JsonProperty

data class BuyPrice(
    @JsonProperty("value")
    val value: String = ""
)