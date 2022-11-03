package com.example.uvanna.controller.images

import com.example.uvanna.jpa.Image
import com.example.uvanna.service.FileService
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@Tag(name = "Images", description = "Images")
@RequestMapping("/images/")
class ImageController {

    @Autowired
    lateinit var fileService: FileService


    @GetMapping("{id}")
    fun getFile(@PathVariable id: String): ResponseEntity<ByteArray?>? {
        val fileEntityOptional: Optional<Image> = fileService.getFile(id)
        if (!fileEntityOptional.isPresent) {
            return ResponseEntity.notFound()
                .build()
        }

        val fileEntity = fileEntityOptional.get()
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileEntity.id + "\"")
            .contentType(MediaType.valueOf(MediaType.IMAGE_PNG_VALUE))
            .body(fileEntity.image)
    }
}