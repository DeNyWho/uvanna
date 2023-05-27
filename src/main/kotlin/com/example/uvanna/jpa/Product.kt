@file:UseSerializers(LocalDateTimeSerializer::class)

package com.example.uvanna.jpa

import com.example.uvanna.util.LocalDateTimeSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import org.hibernate.annotations.LazyCollection
import org.hibernate.annotations.LazyCollectionOption
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "product")
@Serializable
data class Product(
    @Id
    var id: String = UUID.randomUUID().toString(),
    @LazyCollection(LazyCollectionOption.FALSE)
    @ElementCollection(fetch = FetchType.EAGER)
    var images: List<String> = mutableListOf(),
    val updated: LocalDateTime = LocalDateTime.now(),
    val title: String = "",
    @OneToMany(cascade = [CascadeType.ALL])
    var characteristic: List<Characteristic> = mutableListOf(),
    val brand: String = "",
    var firstSub: String = "",
    var secondSub: String = "",
    var thirdSub: String = "",
    var price: Int = 0,
    @Column(nullable = true)
    var sellPrice: Int? = null,
    var stock: Int = 0,
    @Column(nullable = true)
    var percent: Int? = null,
    @Column(nullable = true)
    var archive: Boolean? = false,
    @Column(nullable = true)
    val popularity: Boolean? = null
)