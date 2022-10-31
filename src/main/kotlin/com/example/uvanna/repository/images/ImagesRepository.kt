package com.example.uvanna.repository.images

import com.example.uvanna.jpa.Image
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ImagesRepository: JpaRepository<Image, String > {
}