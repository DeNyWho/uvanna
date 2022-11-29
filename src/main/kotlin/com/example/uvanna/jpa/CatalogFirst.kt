package com.example.uvanna.jpa

import javax.persistence.*

@Entity
@Table(name = "CatalogFirst")
data class CatalogFirst(
    @Id
    var id: String? = "",
    var title: String? = "",
    @OneToMany(
        fetch = FetchType.EAGER,
        cascade = [CascadeType.DETACH, CascadeType.PERSIST, CascadeType.REFRESH]
    )
    @Column(nullable = true)
    var sub: MutableSet<CatalogSecond> = mutableSetOf(),
    val imageUrl: String = "",
    val level: String = "first"
){
    fun addToSecondLevel(catalog: CatalogSecond): CatalogFirst {
        sub.add(catalog)
        return this
    }
}