package com.example.uvanna.jpa

import javax.persistence.*

@Entity
@Table(name = "Files")
data class Files(
    @Id
    var id: String? = "",

    @Lob
    @Column(name = "file")
    var file: ByteArray? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Files

        if (id != other.id) return false
        if (file != null) {
            if (other.file == null) return false
            if (!file.contentEquals(other.file)) return false
        } else if (other.file != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (file?.contentHashCode() ?: 0)
        return result
    }
}