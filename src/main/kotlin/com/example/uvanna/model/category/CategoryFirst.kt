package com.example.uvanna.model.category

import com.example.uvanna.jpa.CatalogSecond

data class CategoryFirst(
    val id: String,
    val title: String,
    val sub: MutableSet<CatalogSecond>,
    val imageUrl: String
)