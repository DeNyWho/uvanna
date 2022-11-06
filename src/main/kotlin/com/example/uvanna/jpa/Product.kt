package com.example.uvanna.jpa

import com.example.uvanna.model.product.Characteristic
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "product")
data class Product(
    @Id
    var id: String = UUID.randomUUID().toString(),
    @ElementCollection
    var images: List<String> = listOf(),
    var updated: String = "",
    val title: String = "",
    @OneToMany
    val characteristic: List<Characteristic> = listOf(),
    val brand: String = "",
    val firstSub: String = "",
    val secondSub: String = "",
    val thirdSub: String = "",
    val price: Int = 0,
    val stock: Int = 0,
)