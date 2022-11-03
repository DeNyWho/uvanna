package com.example.uvanna.model

data class Temp(
    val image: ByteArray,
    val text: String,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Temp

        if (!image.contentEquals(other.image)) return false
        if (text != other.text) return false

        return true
    }

    override fun hashCode(): Int {
        var result = image.contentHashCode()
        result = 31 * result + text.hashCode()
        return result
    }
}
