package com.example.uvanna.controller.images

import com.example.uvanna.jpa.Image
import com.example.uvanna.model.response.ServiceResponse
import com.example.uvanna.repository.admin.AdminRepository
import com.example.uvanna.service.FileService
import com.example.uvanna.service.ImageService
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.crossstore.ChangeSetPersister
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.*
import javax.servlet.http.HttpServletResponse

@RestController
@CrossOrigin("*")
@Tag(name = "Images", description = "Images")
@RequestMapping("/images/")
class ImageController {

    @Autowired
    lateinit var imageService: ImageService

    @Autowired
    lateinit var fileService: FileService

    @PostMapping("loadImage" ,consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun loadImage(
        @RequestBody file: MultipartFile,
        token: String,
        response: HttpServletResponse
    ): ServiceResponse<String> {
        return try {
            imageService.loadImage(token, file)
        } catch (e: ChangeSetPersister.NotFoundException) {
            ServiceResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
        }
    }

    @DeleteMapping("deleteImage")
    fun deleteImage(
        @RequestParam url: String,
        @RequestHeader (value = "Authorization") token: String,
        response: HttpServletResponse
    ): ServiceResponse<String> {
        return try {
            imageService.deleteImage(token, url)
        } catch (e: ChangeSetPersister.NotFoundException) {
            ServiceResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
        }
    }

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