package com.example.uvanna.controller.catalog

import com.example.uvanna.model.response.ServiceResponse
import com.example.uvanna.service.CatalogService
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import javax.servlet.http.HttpServletResponse

@RestController
@Tag(name = "CatalogApi", description = "All about catalog")
@RequestMapping("/api/catalog/")
class CatalogController {

    @Autowired
    lateinit var catalogService: CatalogService

    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun addCategory(
        @RequestParam file: MultipartFile,
        @RequestParam title: String,
        @RequestParam sub: List<String>,
        response: HttpServletResponse
    ): ResponseEntity<ByteArray> {
            return catalogService.addFirstLevel(file, title, sub)

//            return ServiceResponse(data = listOf(data), status = HttpStatus.OK)
    }
}