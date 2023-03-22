package com.example.uvanna.model.product

data class CharacteristicsRequest(
    val characteristicTitle: String = "",
    val characteristicsData: List<String> = listOf()
)