package com.example.uvanna.jpa

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.OneToMany
import javax.persistence.Table

@Entity
@Table(name = "product")
data class Product(
    @Id
    val id: Long? = 0,
    @OneToMany
    var images: List<Image> = listOf(Image()),
    val updated: String = "",
    val name: String = "",
    val group: String = "",
    val salePrice: Int = 0,
    val buyPrice: Int = 0,
    val stock: Int = 0,
)