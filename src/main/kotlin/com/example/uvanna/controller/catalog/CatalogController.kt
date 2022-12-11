package com.example.uvanna.controller.catalog

import com.example.uvanna.model.response.ServiceResponse
import com.example.uvanna.service.CatalogService
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.crossstore.ChangeSetPersister
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import javax.servlet.http.HttpServletResponse

@RestController
@CrossOrigin("*")
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
        @RequestParam id: String?,
        @RequestHeader(value = "Authorization") token: String,
        response: HttpServletResponse
    ): ServiceResponse<String> {
        return try {
            catalogService.addLevel(id = id, file = file, title = title, option = option, token = token)
        } catch (e: ChangeSetPersister.NotFoundException) {
            ServiceResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
        }
    }

    @DeleteMapping
    fun deleteCategory(
        @RequestParam id: String,
        @RequestHeader (value = "Authorization") token: String,
        response: HttpServletResponse
    ): ServiceResponse<String> {
        return try {
            catalogService.deleteCategory(id = id, token = token)
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
            return ServiceResponse(
                data = listOf(catalogService.getLevels(id)),
                message = "Success",
                status = HttpStatus.OK
            )
        } catch (e: ChangeSetPersister.NotFoundException) {
            ServiceResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
        }
    }

    @GetMapping("upper/{id}")
    fun getCatalogUpper(
        @PathVariable id: String,
        response: HttpServletResponse
    ): ServiceResponse<Any> {
        return try {
            return ServiceResponse(
                data = listOf(catalogService.getUpperLevels(id)),
                message = "Success",
                status = HttpStatus.OK
            )
        } catch (e: ChangeSetPersister.NotFoundException) {
            ServiceResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
        }
    }






}