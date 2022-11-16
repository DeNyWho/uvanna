package com.example.uvanna.controller.products

import com.example.uvanna.jpa.Characteristic
import com.example.uvanna.model.product.ProductRequest
import com.example.uvanna.model.product.ProductsLightResponse
import com.example.uvanna.model.response.ServiceResponse
import com.example.uvanna.service.ProductService
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.crossstore.ChangeSetPersister
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import javax.servlet.http.HttpServletResponse
import javax.validation.constraints.Max
import javax.validation.constraints.Min


@RestController
@CrossOrigin("*")
@Tag(name = "ProductsApi", description = "All about products")
@RequestMapping("/api/products/")
class ProductsController {

    private val logger = LoggerFactory.getLogger(ProductsController::class.java)

    @Autowired
    lateinit var productService: ProductService

    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun addProduct(
        @RequestBody files: List<MultipartFile>,
        product: ProductRequest,
        response: HttpServletResponse
    ): ServiceResponse<String> {
        return try {
            productService.addProduct(product, files,product.charactTitle , product.charactData)

            return ServiceResponse(data = listOf("Success"), status = HttpStatus.OK)
        } catch (e: ChangeSetPersister.NotFoundException) {
            ServiceResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
        }
    }

    @GetMapping("character/{id}")
    fun getCharacterSort(
        @PathVariable id: String,
        response: HttpServletResponse
    ): ServiceResponse<Characteristic>{
        return try {
            val data = productService.getCharactSort(id)

            return ServiceResponse(data = data, status = HttpStatus.OK)
        } catch (e: ChangeSetPersister.NotFoundException) {
            ServiceResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
        }
    }

    @GetMapping
    fun getProducts(
        @RequestParam(defaultValue = "1") pageNum: @Min(1) Int,
        @RequestParam(defaultValue = "48") pageSize: @Min(1) @Max(500) Int,
        brand: String? ,
        smallPrice: Int?,
        highPrice: Int?,
        @Parameter(description = "Order = brand | characteristic | stockEmpty | stockFull") order: String?,
        @Parameter(description = "Filter = expensive | cheap | new | old") filter: String?,
        characteristic: List<Characteristic>?,
        level: String?,
        response: HttpServletResponse
    ): ServiceResponse<ProductsLightResponse>? {
        return try {
            return productService.getProducts(
                countCard = pageSize,
                page = pageNum,
                brand = brand,
                smallPrice = smallPrice,
                highPrice = highPrice,
                sort = order,
                filter = filter,
                characteristic = characteristic,
                level = level
            )
        } catch (e: ChangeSetPersister.NotFoundException) {
            ServiceResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
        }
    }

    @DeleteMapping
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
        val data = productService.parser(brand)

        val finish = System.currentTimeMillis()
        val elapsed = finish - start
        logger.info("time execution $elapsed")

        return ServiceResponse(data = data, status = HttpStatus.OK)
    }




}