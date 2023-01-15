package com.example.uvanna.jpa

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "MainBanner")
data class MainBanner(
    @Id
    var id: String = "",
    val imagePCUrl: String? = null,
    val imageMobileUrl: String? = null,
)