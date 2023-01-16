package com.example.uvanna.service

import com.example.uvanna.jpa.Files
import com.example.uvanna.jpa.Image
import com.example.uvanna.repository.files.FilesRepository
import com.example.uvanna.repository.image.ImageRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.*


@Service
class FileService {

    @Autowired
    private lateinit var imageRepository: ImageRepository

    @Autowired
    private lateinit var filesRepository: FilesRepository

    @Value("\${host_url}")
    lateinit var host: String

    fun deleteFile(url: String) {
        filesRepository.deleteById(url.replaceRange(0..27, ""))
    }

    fun deleteByUrl(url: String?) {
        imageRepository.deleteById(url?.replaceRange(0..28, "")!!)
    }

    fun save(file: MultipartFile): String {
        val id = UUID.randomUUID().toString()
        imageRepository.save(Image(
            id = id,
            image = file.bytes
        ))
        return "$host/images/$id"
    }

    fun saveFile(file: MultipartFile): String {
        val id = UUID.randomUUID().toString()
        filesRepository.save(
            Files(
                id = id,
                file = file.bytes
            )
        )
        return "$host/files/$id"
    }

    fun getFile(id: String): Optional<Files> {
        return filesRepository.findById(id)
    }

    fun getImage(id: String): Optional<Image> {
        return imageRepository.findById(id)
    }

}