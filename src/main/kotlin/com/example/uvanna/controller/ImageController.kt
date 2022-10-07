package com.example.uvanna.controller

import com.example.uvanna.model.product.Product
import com.example.uvanna.model.product.folder.ProductFolder
import com.example.uvanna.model.response.ServiceResponse
import com.example.uvanna.service.ProductService
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletResponse

@RestController
@Tag(name = "ImagesApi", description = "All about images")
@RequestMapping("/api/images/")
class ImageController {

    @GetMapping("{id}")
    fun getProductFolders(
        @PathVariable id: String,
        response: HttpServletResponse,
    ) {

    }




}