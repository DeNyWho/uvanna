package com.example.uvanna.service

import com.example.uvanna.jpa.CatalogFirst
import com.example.uvanna.repository.catalog.CatalogRepository
import com.example.uvanna.repository.catalog.CatalogRepositoryImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

@Service
class CatalogService: CatalogRepositoryImpl {

    @Autowired
    private lateinit var catalogRepository: CatalogRepository

    override fun addFirstLevel(file: MultipartFile, title: String, sub: List<String>): ResponseEntity<ByteArray> {
        var id = UUID.randomUUID().toString()
        println("ID = $id")
        var catalog = catalogRepository.findById(id).isEmpty
        while (catalog){
            id = UUID.randomUUID().toString()
            catalog = catalogRepository.findById(id).isPresent
        }
        catalogRepository.save(
            CatalogFirst(
                id = id,
                title = title,
                image = file.bytes,
            )
        )
        val image = catalogRepository.getById(id).image

        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(MediaType.IMAGE_JPEG_VALUE))
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"${System.currentTimeMillis()}\"")
            .body(image)
    }

}