package com.example.uvanna.jpa

import java.util.*
import javax.persistence.*

@Entity
@Table(name = "product")
data class Product(
    @Id
    var id: String = UUID.randomUUID().toString(),
    @ElementCollection
    var images: List<String> = mutableListOf(),
    var updated: String = "",
    val title: String = "",
    @OneToMany(cascade = [CascadeType.ALL])
    var characteristic: List<Characteristic> = mutableListOf(),
    val brand: String = "",
    val secondSub: String = "",
    val thirdSub: String = "",
    val price: Int = 0,
    @Column(nullable = true)
    var sellPrice: Int? = 0,
    val stock: Int = 0,
    @Column(nullable = true)
    var percent: Int? = null,
)