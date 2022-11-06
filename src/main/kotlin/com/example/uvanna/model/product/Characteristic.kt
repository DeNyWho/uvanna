package com.example.uvanna.model.product

import java.util.UUID
import javax.persistence.*

@Entity
@Table(name = "characteristic")
data class Characteristic(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val data: String = ""
)
