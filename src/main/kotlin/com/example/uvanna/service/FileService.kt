package com.example.uvanna.service

import com.example.uvanna.jpa.Image
import com.example.uvanna.repository.image.ImageRepository
import org.htmlunit.org.apache.http.entity.FileEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.*


@Service
class FileService {

    @Autowired
    private lateinit var imageRepository: ImageRepository

    @Value("\${host_url}")
    lateinit var host: String

    fun save(file: MultipartFile): String {
        val id = UUID.randomUUID().toString()
        imageRepository.save(Image(
            id = id,
            image = file.bytes
        ))
        return "$host/images/$id"
    }

    fun saveBytes(file: ByteArray): String {
        val id = UUID.randomUUID().toString()
        imageRepository.save(
            Image(
                id = id,
                image = file
            )
        )
        return "$host/images/$id"
    }

    fun getFile(id: String): Optional<Image> {
        return imageRepository.findById(id)
    }

}