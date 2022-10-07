package com.example.uvanna.model.common

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class Rows<T>(
    @JsonProperty("rows")
    var rows: List<T>? = null,
)