package com.example.uvanna.jpa

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table


@Entity
@Table(name = "AdminToken")
data class AdminToken(
    @Id
    var id: String? = "",
    val token: String = "",
)