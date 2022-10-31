package com.example.uvanna.jpa

import javax.persistence.*

@Entity
@Table(name = "CatalogFirst")
data class CatalogFirst(
    @Id
    var id: String? = "",
    var title: String? = "",
    @OneToMany
    @Column(nullable = true)
    var sub: List<CatalogSecond> = listOf(),
    @Lob
    val image: ByteArray? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CatalogFirst

        if (id != other.id) return false
        if (title != other.title) return false
        if (sub != other.sub) return false
        if (image != null) {
            if (other.image == null) return false
            if (!image.contentEquals(other.image)) return false
        } else if (other.image != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (title?.hashCode() ?: 0)
        result = 31 * result + sub.hashCode()
        result = 31 * result + (image?.contentHashCode() ?: 0)
        return result
    }
}