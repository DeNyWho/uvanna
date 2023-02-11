package com.example.uvanna.controller.files

import com.example.uvanna.jpa.Files
import com.example.uvanna.service.FileService
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@CrossOrigin("*")
@Tag(name = "Files", description = "Files")
@RequestMapping("/api/files/")
class FilesController {

    @Autowired
    lateinit var fileService: FileService

    @GetMapping("{id}")
    fun getFile(@PathVariable id: String): ResponseEntity<ByteArray?>? {
        val fileEntityOptional: Optional<Files> = fileService.getFile(id)
        if (!fileEntityOptional.isPresent) {
            return ResponseEntity.notFound()
                .build()
        }

        val fileEntity = fileEntityOptional.get()
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileEntity.id + "\"")
            .contentType(MediaType.valueOf(MediaType.APPLICATION_PDF_VALUE))
            .body(fileEntity.file)
    }
}

