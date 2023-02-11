package com.example.uvanna.controller.products

import com.example.uvanna.jpa.Characteristic
import com.example.uvanna.jpa.Product
import com.example.uvanna.jpa.ProductBrands
import com.example.uvanna.jpa.TemplateCharact
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

    @PostMapping("stock/{id}")
    fun addProductStock(
        @PathVariable id: String,
        stock: Int,
        @RequestHeader (value = "Authorization") token: String,
        response: HttpServletResponse
    ): ServiceResponse<Product>? {
        return try {
            productService.addProductStock(id = id, stock = stock, token = token)
        } catch (e: ChangeSetPersister.NotFoundException) {
            ServiceResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
        }
    }

    @PostMapping("archive/{id}")
    fun addProductArchive(
        @PathVariable id: String,
        archive: Boolean,
        @RequestHeader (value = "Authorization") token: String,
        response: HttpServletResponse
    ): ServiceResponse<Product>? {
        return try {
            productService.changeProductArchive(id = id, archive = archive, token = token)
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

    @GetMapping("ids/all")
    fun getIdsProducts(
        response: HttpServletResponse
    ): ServiceResponse<String> {
        return try {
            productService.getProductsIds()
        } catch (e: ChangeSetPersister.NotFoundException) {
            ServiceResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
        }
    }

    @GetMapping("listIds")
    fun getProducts(
        @RequestParam products: List<String>,
        response: HttpServletResponse
    ): ServiceResponse<ProductLighterResponse> {
        return try {
            productService.getProductsByIds(products)
        } catch (e: ChangeSetPersister.NotFoundException) {
            ServiceResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
        }
    }

    @PostMapping("character/template")
    fun addTemplate(
        @RequestParam categoryId: String,
        @RequestHeader (value = "Authorization") token: String,
        @RequestBody charact: List<String>,
        response: HttpServletResponse
    ): ServiceResponse<TemplateCharact> {
        return try {
            productService.addTemplateCharact(id = categoryId, token = token, charact = charact)
        } catch (e: ChangeSetPersister.NotFoundException) {
            ServiceResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
        }
    }

    @GetMapping("character/template/{id}")
    fun getCharacter(
        @PathVariable id: String,
        response: HttpServletResponse
    ): ServiceResponse<TemplateCharact> {
        return try {
            productService.getTemplateCharactById(id)
        } catch (e: ChangeSetPersister.NotFoundException) {
            ServiceResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
        }
    }

    @GetMapping("character/template/")
    fun getTemplate(
        response: HttpServletResponse
    ): ServiceResponse<TemplateCharact> {
        return try {
            productService.getTemplateCharact()
        } catch (e: ChangeSetPersister.NotFoundException) {
            ServiceResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
        }
    }

    @PostMapping("character/{id}/template")
    fun editTemplate(
        @PathVariable id: String,
        @RequestHeader (value = "Authorization") token: String,
        @RequestBody charact: List<String>,
        response: HttpServletResponse
    ): ServiceResponse<TemplateCharact> {
        return try {
            productService.editTemplateCharact(id = id, token = token, charact = charact)
        } catch (e: ChangeSetPersister.NotFoundException) {
            ServiceResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
        }
    }

    @DeleteMapping("character/{id}/template")
    fun deleteTemplate(
        @PathVariable id: String,
        @RequestHeader (value = "Authorization") token: String,
        response: HttpServletResponse
    ): ServiceResponse<TemplateCharact> {
        return try {
            productService.deleteTemplateCharact(id = id, token = token)
        } catch (e: ChangeSetPersister.NotFoundException) {
            ServiceResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
        }
    }

    @DeleteMapping("brands/{id}/delete")
    fun deleteBrandById(
        @PathVariable id: String,
        @RequestHeader (value = "Authorization") token: String,
        response: HttpServletResponse
    ): ServiceResponse<String> {
        return try {
            productService.deleteBrandById(id = id, token = token)
        } catch (e: ChangeSetPersister.NotFoundException) {
            ServiceResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
        }
    }

    @GetMapping("brands/all")
    fun getAllBrands(
        response: HttpServletResponse
    ): ServiceResponse<ProductBrands>? {
        return try {
            productService.getAllBrands()
        } catch (e: ChangeSetPersister.NotFoundException) {
            ServiceResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
        }
    }

    @PostMapping("brands/create")
    fun addNewBrands(
        title: String,
        @RequestHeader (value = "Authorization") token: String,
        response: HttpServletResponse
    ): ServiceResponse<ProductBrands>? {
        return try {
            productService.createBrand(title = title, token = token)
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
        searchQuery: String?,
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
                isSellByPromo = isSell,
                searchQuery = searchQuery
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