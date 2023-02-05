package com.example.uvanna.jpa

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "Admins")
data class Admins(
    @Id
    var id: String = "",
    val login: String = "",
    val password: String = "",
    val token: String = ""

)