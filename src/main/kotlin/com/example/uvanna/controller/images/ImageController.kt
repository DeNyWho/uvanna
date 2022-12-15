package com.example.uvanna.controller.images

import com.example.uvanna.jpa.Image
import com.example.uvanna.model.response.ServiceResponse
import com.example.uvanna.service.FileService
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
    lateinit var fileService: FileService

    @PostMapping("loadImage" ,consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun loadImage(
        @RequestBody files: MultipartFile,
        @RequestHeader (value = "Authorization") token: String,
        response: HttpServletResponse
    ): ServiceResponse<String> {
        return try {
            val v = if(token != "vzxcxzasfdadfa") "vv" else fileService.save(files)
            ServiceResponse(data = listOf(v),status = HttpStatus.NOT_FOUND, message = "")
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