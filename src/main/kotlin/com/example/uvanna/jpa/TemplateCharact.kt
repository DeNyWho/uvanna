package com.example.uvanna.jpa

import javax.persistence.*

@Entity
@Table(name = "Template_charact")
data class TemplateCharact(
    @Id
    var categoryId: String = "",
    @Column(nullable = true)
    @ElementCollection
    var charact: List<String>? = mutableListOf(),
)
