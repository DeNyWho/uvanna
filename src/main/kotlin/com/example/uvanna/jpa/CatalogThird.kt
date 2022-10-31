package com.example.uvanna.jpa

import javax.persistence.*

@Entity
@Table(name = "CatalogThird")
data class CatalogThird(
    @Id
    var id: String? = "",
    var title: String? = "",
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CatalogThird

        if (id != other.id) return false
        if (title != other.title) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (title?.hashCode() ?: 0)
        return result
    }
}