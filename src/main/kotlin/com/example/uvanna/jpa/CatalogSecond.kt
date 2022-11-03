package com.example.uvanna.jpa

import javax.persistence.*

@Entity
@Table(name = "CatalogSecond")
data class CatalogSecond(
    @Id
    var id: String? = "",
    var title: String? = "",
    @OneToMany(
        fetch = FetchType.EAGER,
        cascade = [CascadeType.DETACH, CascadeType.PERSIST, CascadeType.REFRESH]
    )
    @Column(nullable = true)
    var sub: MutableSet<CatalogThird> = mutableSetOf(),
    val imageUrl: String? = null
){
    fun addToThirdLevel(catalog: CatalogThird): CatalogSecond {
        sub.add(catalog)
        return this
    }
}