package com.example.uvanna.jpa

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "Calls")
data class Calls(
    @Id
    val id: String = "",
    val name: String = "",
    val phone: String = ""
)