package com.example.uvanna.controller.products

import com.example.uvanna.model.product.detail.ProductDetail
import com.example.uvanna.model.product.main.Product
import com.example.uvanna.model.product.folder.ProductFolder
import com.example.uvanna.model.response.ServiceResponse
import com.example.uvanna.service.ProductService
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.crossstore.ChangeSetPersister
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletResponse
import javax.validation.constraints.Max
import javax.validation.constraints.Min


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
        return try {
            val data = productService.getProductFolder()
            return ServiceResponse(data = data, status = HttpStatus.OK)
        } catch (e: ChangeSetPersister.NotFoundException) {
            ServiceResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
        }
    }

    @GetMapping("getProductsByFolder")
    fun getProductsByFolder(
        @RequestParam(defaultValue = "0") pageNum: @Min(0) Int,
        @RequestParam(defaultValue = "16") pageSize: @Min(1) @Max(48) Int,
        @RequestParam id: String,
        response: HttpServletResponse
    ): ServiceResponse<Product> {
        return try {
            val data = productService.getProductsByFolder(id, pageNum, pageSize)
            return ServiceResponse(data = data, status = HttpStatus.OK)
        } catch (e: ChangeSetPersister.NotFoundException) {
            ServiceResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
        }
    }

    @GetMapping("{id}")
    fun getProductDetail(
        @PathVariable id: String,
        response: HttpServletResponse
    ): ServiceResponse<ProductDetail> {
        return try {
        val data = productService.getProduct(id)
            return ServiceResponse(data = data, status = HttpStatus.OK)
        } catch (e: ChangeSetPersister.NotFoundException) {
            ServiceResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
        }
    }




}