package com.example.uvanna.jpa

import javax.persistence.*

@Entity
@Table(name = "characteristic")
data class Characteristic(
    @Id
    var id: String = "",
    val title: String = "",
    val data: String = ""
)
