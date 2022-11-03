package com.example.uvanna.service

import com.example.uvanna.jpa.Image
import com.example.uvanna.repository.image.ImageRepository
import org.htmlunit.org.apache.http.entity.FileEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.*


@Service
class FileService {

    @Autowired
    private lateinit var imageRepository: ImageRepository

    fun save(file: MultipartFile): String {
        var id = UUID.randomUUID().toString()
        var catalog = imageRepository.findById(id).isEmpty
        while (catalog){
            id = UUID.randomUUID().toString()
            catalog = imageRepository.findById(id).isPresent
        }
        imageRepository.save(Image(
            id = id,
            image = file.bytes
        ))
        return "http://localhost:12500/images/$id"
    }

    fun getFile(id: String): Optional<Image> {
        return imageRepository.findById(id)
    }

}