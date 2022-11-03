package com.example.uvanna.repository.image

import com.example.uvanna.jpa.Image
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ImageRepository: JpaRepository<Image, String> {
}