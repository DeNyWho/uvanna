package com.example.uvanna.jpa

import javax.persistence.*

@Entity
@Table(name = "CharacteristicProduct")
data class CharacteristicProduct(
    @Id
    var id: String? = "",
    @ElementCollection
    @JoinTable(name = "charact_title")
    val title: List<String> = ArrayList<String>(),
    @ElementCollection
    @JoinTable(name = "charact_value")
    val value: List<String> = ArrayList<String>(),

)