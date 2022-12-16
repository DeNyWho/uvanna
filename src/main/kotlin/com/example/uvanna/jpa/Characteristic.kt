package com.example.uvanna.jpa

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "characteristic")
data class Characteristic(
    @Id
    var id: String = "",
    val title: String = "",
    val data: String = ""
)
