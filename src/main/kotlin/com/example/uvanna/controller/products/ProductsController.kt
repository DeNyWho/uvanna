package com.example.uvanna.controller.products

import com.example.uvanna.jpa.Characteristic
import com.example.uvanna.jpa.Product
import com.example.uvanna.model.product.Brands
import com.example.uvanna.model.request.product.ProductRequest
import com.example.uvanna.model.response.PagingResponse
import com.example.uvanna.model.response.ProductLighterResponse
import com.example.uvanna.model.response.ProductsLightResponse
import com.example.uvanna.model.response.ServiceResponse
import com.example.uvanna.service.ProductService
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
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


    @Autowired
    lateinit var productService: ProductService

    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun addProduct(
        @RequestBody files: List<MultipartFile>,
        product: ProductRequest,
        @RequestHeader (value = "Authorization") token: String,
        response: HttpServletResponse
    ): ServiceResponse<Product>? {
        return try {
            productService.addProduct(product = product, files = files, characteristic = product.charactTitle, data = product.charactData, token = token)
        } catch (e: ChangeSetPersister.NotFoundException) {
            ServiceResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
        }
    }

    @PostMapping("edit/{id}", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun editProducts(
        @PathVariable id: String,
        @RequestBody files: List<MultipartFile>,
        product: ProductRequest,
        @RequestHeader (value = "Authorization") token: String,
        response: HttpServletResponse
    ): ServiceResponse<Product>? {
        return try {
            productService.editProduct(
                id = id,
                product = product,
                files = files,
                characteristic = product.charactTitle,
                data = product.charactData,
                token = token,
            )
        } catch (e: ChangeSetPersister.NotFoundException) {
            ServiceResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
        }
    }

    @GetMapping("character/{id}")
    fun getCharacterSort(
        @PathVariable id: String,
        response: HttpServletResponse
    ): ServiceResponse<Characteristic> {
        return try {
            ServiceResponse(data = productService.getCharactSort(id), status = HttpStatus.OK)
        } catch (e: ChangeSetPersister.NotFoundException) {
            ServiceResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
        }
    }

    @GetMapping("{id}")
    fun getProduct(
        @PathVariable id: String,
        response: HttpServletResponse
    ): ServiceResponse<Product>? {
        return try {
            productService.getProduct(id)
        } catch (e: ChangeSetPersister.NotFoundException) {
            ServiceResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
        }
    }

    @GetMapping("brands")
    fun getBrandsByCategory(
        categoryId: String,
        response: HttpServletResponse
    ): ServiceResponse<String>? {
        return try {
            productService.getBrands(categoryId)
        } catch (e: ChangeSetPersister.NotFoundException) {
            ServiceResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
        }
    }

    @GetMapping("search")
    fun searchProduct(
        searchQuery: String,
        @RequestParam(defaultValue = "0") pageNum: @Min(0) @Max(500) Int,
        @RequestParam(defaultValue = "48") pageSize: @Min(1) @Max(500) Int,
        response: HttpServletResponse
    ): ServiceResponse<ProductLighterResponse>? {
        return try {
            productService.findProduct(searchQuery, pageNum, pageSize)
        } catch (e: ChangeSetPersister.NotFoundException) {
            ServiceResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
        }
    }

    @GetMapping()
    fun getProducts(
        @RequestParam(defaultValue = "0") pageNum: @Min(0) @Max(500) Int,
        @RequestParam(defaultValue = "48") pageSize: @Min(1) @Max(500) Int,
        brands: Brands?,
        smallPrice: Int?,
        highPrice: Int?,
        stockEmpty: Boolean?,
        stockFull: Boolean?,
        isSell: Boolean?,
        @Parameter(description = "Filter = expensive | cheap | new | old") filter: String?,
        categoryId: String?,
        productId: String?,
        response: HttpServletResponse
    ): PagingResponse<ProductsLightResponse>? {
        return try {
            productService.getProducts (
                countCard = pageSize,
                page = pageNum,
                brand = brands,
                smallPrice = smallPrice,
                highPrice = highPrice,
                filter = filter,
                stockEmpty = stockEmpty,
                stockFull = stockFull,
                categoryId = categoryId,
                isSellByPromo = isSell
            )
        } catch (e: ChangeSetPersister.NotFoundException) {
            PagingResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
        }
    }

    @GetMapping("random")
    fun getProductsRandom(
        @RequestParam(defaultValue = "0") pageNum: @Min(0) @Max(500) Int,
        @RequestParam(defaultValue = "48") pageSize: @Min(1) @Max(500) Int,
        @Parameter(description = "Filter = expensive | cheap | new | old") filter: String?,
        productId: String?,
        response: HttpServletResponse
    ): PagingResponse<ProductsLightResponse>? {
        return try {
            productService.getProductRandom(
                countCard = pageSize,
                page = pageNum,
                filter = filter,
                productId = productId
            )
        } catch (e: ChangeSetPersister.NotFoundException) {
            PagingResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
        }
    }

    @DeleteMapping
    fun deleteProduct(
        @RequestParam id: String,
        @RequestHeader (value = "Authorization") token: String,
        response: HttpServletResponse
    ): ServiceResponse<String> {
        return try {
            return productService.deleteProduct(id = id, token = token)
        } catch (e: ChangeSetPersister.NotFoundException) {
            ServiceResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
        }
    }


}