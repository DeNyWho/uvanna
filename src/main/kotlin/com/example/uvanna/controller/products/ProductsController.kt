package com.example.uvanna.controller.products

import com.example.uvanna.jpa.Product
import com.example.uvanna.model.response.ServiceResponse
import com.example.uvanna.service.ProductService
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.crossstore.ChangeSetPersister
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
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
        @RequestParam file: MultipartFile,
        response: HttpServletResponse
    ): ServiceResponse<Product>{
        return try {
            val data = productService.addProduct()

            return ServiceResponse(data = listOf(data), status = HttpStatus.OK)
        } catch (e: ChangeSetPersister.NotFoundException) {
            ServiceResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
        }
    }

//    @GetMapping("getProductFolders")
//    fun getProductFolders(
//        response: HttpServletResponse
//    ): ServiceResponse<ProductFolder> {
//        return try {
//            val data = productService.getProductFolder()
//            return ServiceResponse(data = data, status = HttpStatus.OK)
//        } catch (e: ChangeSetPersister.NotFoundException) {
//            ServiceResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
//        }
//    }
//
//    @GetMapping("getProductsByFolder")
//    fun getProductsByFolder(
//        @RequestParam(defaultValue = "0") pageNum: @Min(0) Int,
//        @RequestParam(defaultValue = "16") pageSize: @Min(1) @Max(48) Int,
//        @RequestParam id: String,
//
//        response: HttpServletResponse
//    ): ServiceResponse<Product> {
//        return try {
//            val data = productService.getProductsByFolder(id, pageNum, pageSize)
//            return ServiceResponse(data = data, status = HttpStatus.OK)
//        } catch (e: ChangeSetPersister.NotFoundException) {
//            ServiceResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
//        }
//    }
//
//    @GetMapping("{id}")
//    fun getProductDetail(
//        @PathVariable id: String,
//        response: HttpServletResponse
//    ): ServiceResponse<Product> {
//        return try {
//        val data = productService.getProduct(id)
//            return ServiceResponse(data = data, status = HttpStatus.OK)
//        } catch (e: ChangeSetPersister.NotFoundException) {
//            ServiceResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
//        }
//    }

    @GetMapping("parser")
    fun parseProducts(
        @RequestParam brand: String,
        response: HttpServletResponse
    ): ServiceResponse<String>{
        val start = System.currentTimeMillis()
        val data = productService.parser(brand)

        val finish = System.currentTimeMillis()
        val elapsed = finish - start
        logger.info("time execution $elapsed")

        return ServiceResponse(data = listOf(elapsed.toString()), status = HttpStatus.OK)
    }




}