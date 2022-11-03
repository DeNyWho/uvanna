package com.example.uvanna.model.category

import com.example.uvanna.jpa.CatalogThird

data class CategorySecond(
    val id: String,
    val title: String,
    val sub: MutableSet<CatalogThird>,
    val imageUrl: String?,
)