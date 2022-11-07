package com.example.uvanna.controller.catalog

import com.example.uvanna.jpa.CatalogSecond
import com.example.uvanna.jpa.CatalogThird
import com.example.uvanna.model.response.ServiceResponse
import com.example.uvanna.service.CatalogService
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.crossstore.ChangeSetPersister
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
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
        @RequestParam option: String,
        id: String?,
        response: HttpServletResponse
    ): ServiceResponse<Boolean> {
        return try {
            val data = catalogService.addLevel(id, file, title, option)
            return ServiceResponse(data = listOf(data), status = HttpStatus.OK)
        } catch (e: ChangeSetPersister.NotFoundException) {
            ServiceResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
        }
    }

    @DeleteMapping
    fun deleteCategory(
        @RequestParam id: String,
        response: HttpServletResponse
    ): ServiceResponse<String> {
        return try {
            catalogService.deleteCategory(id)
            return ServiceResponse(data = listOf("Success"), status = HttpStatus.OK)
        } catch (e: ChangeSetPersister.NotFoundException) {
            ServiceResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
        }
    }


    @GetMapping
    fun getCatalog(
        id: String?,
        response: HttpServletResponse
    ): ServiceResponse<Any> {
        return try {
            val data = catalogService.getLevels(id)
            return ServiceResponse(data = listOf(data), status = HttpStatus.OK)
        } catch (e: ChangeSetPersister.NotFoundException) {
            ServiceResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
        }
    }






























}