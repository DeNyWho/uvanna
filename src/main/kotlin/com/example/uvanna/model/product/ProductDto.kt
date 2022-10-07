package com.example.uvanna.model.product

import com.example.uvanna.model.common.images.ImagesDto
import com.example.uvanna.model.common.price.BuyPrice
import com.example.uvanna.model.common.price.MinPrice
import com.example.uvanna.model.common.price.SalePrices
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class ProductDto(
//    @JsonProperty
//    val externalCode: String,
    @JsonProperty("images")
    val imagesDto: ImagesDto,
    @JsonProperty("updated")
    val updated: String,
    @JsonProperty("name")
    val name: String,
    @JsonProperty("pathName")
    val group: String,
    @JsonProperty("minPrice")
    val minPrice: MinPrice,
    @JsonProperty("salePrices")
    val salePrices: List<SalePrices>,
    @JsonProperty("buyPrice")
    val buyPrice: BuyPrice,
    @JsonProperty("stock")
    val stock: String,
)