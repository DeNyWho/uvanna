package com.example.uvanna.jpa

import java.time.LocalDate
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.ElementCollection
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "Blog")
data class Blog(
    @Id
    var id: String = "",
    val mainImage: String = "",
    @Column(nullable = true)
    @ElementCollection
    var subImages: List<String>? = mutableListOf(),
    val title: String = "",
    @Column(columnDefinition = "TEXT")
    val description: String = "",
    val dateCreated: LocalDateTime? = null,
)