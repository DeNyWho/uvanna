package com.example.uvanna.controller.products

import com.example.uvanna.jpa.Product
import com.example.uvanna.model.product.ProductRequest
import com.example.uvanna.model.response.ServiceResponse
import com.example.uvanna.service.ProductService
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.crossstore.ChangeSetPersister
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.security.Provider.Service
import javax.servlet.http.HttpServletResponse


@RestController
@Tag(name = "ProductsApi", description = "All about products")
@RequestMapping("/api/products/")
class ProductsController {

    private val logger = LoggerFactory.getLogger(ProductsController::class.java)

    @Autowired
    lateinit var productService: ProductService

    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun addProduct(
        @RequestParam files: List<MultipartFile>,
        product: ProductRequest,
        response: HttpServletResponse
    ): ServiceResponse<String> {
        return try {
            productService.addProduct(product, files)

            return ServiceResponse(data = listOf("Success"), status = HttpStatus.OK)
        } catch (e: ChangeSetPersister.NotFoundException) {
            ServiceResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
        }
    }

    @DeleteMapping()
    fun deleteProduct(
        @RequestParam id: String,
        response: HttpServletResponse
    ): ServiceResponse<String> {
        return try {
            productService.deleteProduct(id)

            return ServiceResponse(data = listOf("Success"), status = HttpStatus.OK)
        } catch (e: ChangeSetPersister.NotFoundException) {
            ServiceResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
        }
    }

    @GetMapping("parser")
    fun parseProducts(
        @RequestParam brand: String,
        response: HttpServletResponse
    ): ServiceResponse<String>{
        val start = System.currentTimeMillis()
        productService.parser(brand)

        val finish = System.currentTimeMillis()
        val elapsed = finish - start
        logger.info("time execution $elapsed")

        return ServiceResponse(data = listOf(elapsed.toString()), status = HttpStatus.OK)
    }




}