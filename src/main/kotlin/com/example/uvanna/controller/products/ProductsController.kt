package com.example.uvanna.controller.products

import com.example.uvanna.model.product.Product
import com.example.uvanna.model.product.folder.ProductFolder
import com.example.uvanna.model.response.ServiceResponse
import com.example.uvanna.service.ProductService
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletResponse


@RestController
@Tag(name = "ProductsApi", description = "All about products")
@RequestMapping("/api/products/")
class ProductsController {

    @Autowired
    lateinit var productService: ProductService

    @GetMapping("getProductFolders")
    fun getProductFolders(
        response: HttpServletResponse
    ): ServiceResponse<ProductFolder> {
        val data = productService.getProductFolder()
        return ServiceResponse(data = data, HttpStatus.OK)
    }

    @GetMapping("getProductsByFolder")
    fun getProductsByFolder(
        @RequestParam id: String,
        response: HttpServletResponse
    ): ServiceResponse<Product> {
        val data = productService.getProductsByFolder(id)
        return ServiceResponse(data = data, HttpStatus.OK)
    }




}