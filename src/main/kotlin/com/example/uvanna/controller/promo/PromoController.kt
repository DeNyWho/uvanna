package com.example.uvanna.controller.promo

import com.example.uvanna.jpa.Promo
import com.example.uvanna.model.request.payment.ProductsRequestsing
import com.example.uvanna.model.request.promo.PromoProductRequest
import com.example.uvanna.model.request.promo.PromoRequest
import com.example.uvanna.model.response.PagingResponse
import com.example.uvanna.model.response.ProductsLightResponse
import com.example.uvanna.model.response.ServiceResponse
import com.example.uvanna.service.PromoService
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
@Tag(name = "PromoApi", description = "Used to manage promo actions")
@RequestMapping("/api/promo")
class PromoController {
    
    @Autowired
    lateinit var promoService: PromoService

    @PostMapping("/create", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun createPromo(
        @RequestBody file: MultipartFile,
        title: String,
        description: String,
        dateExpired: String,
        @RequestHeader (value = "Authorization") token: String,
        response: HttpServletResponse
    ): ServiceResponse<Promo> {
        return try {
            promoService.createPromo(
                token = token,
                title = title,
                description = description,
                file = file,
                dateExpired = dateExpired
            )
        } catch (e: ChangeSetPersister.NotFoundException) {
            ServiceResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
        }
    }

    @PostMapping("/{id}/products/edit")
    fun editProductsPromo(
        @PathVariable id: String,
        @RequestBody productRequest: List<PromoProductRequest>,
        @RequestHeader (value = "Authorization") token: String,
        response: HttpServletResponse
    ): ServiceResponse<Any> {
        return try {
            promoService.addProductPromo(
                id = id,
                token = token,
                productsIds = productRequest,
            )
        } catch (e: ChangeSetPersister.NotFoundException) {
            ServiceResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
        }
    }

    @GetMapping("/{id}")
    fun getPromo(
        @PathVariable id: String,
        response: HttpServletResponse
    ): ServiceResponse<Promo>? {
        return try {
            promoService.getPromo(id)
        } catch (e: ChangeSetPersister.NotFoundException) {
            ServiceResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
        }
    }

    @GetMapping("/ids/all")
    fun getIdsPromos(
        response: HttpServletResponse
    ): ServiceResponse<String> {
        return try {
            promoService.getPromosIds()
        } catch (e: ChangeSetPersister.NotFoundException) {
            ServiceResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
        }
    }


    @GetMapping("/all")
    fun getPromos(
        @RequestParam(defaultValue = "0") pageNum: @Min(0) @Max(500) Int,
        @RequestParam(defaultValue = "48") pageSize: @Min(1) @Max(500) Int,
        response: HttpServletResponse
    ): PagingResponse<Promo> {
        return try {
            promoService.getPromos(pageNum = pageNum, pageSize = pageSize)
        } catch (e: ChangeSetPersister.NotFoundException) {
            PagingResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
        }
    }

    @DeleteMapping("/{id}")
    fun deletePromo(
        @RequestHeader (value = "Authorization") token: String,
        @PathVariable id: String,
        response: HttpServletResponse
    ): ServiceResponse<String> {
        return try {
             promoService.deletePromo(token = token, id =  id)
        } catch (e: ChangeSetPersister.NotFoundException) {
            ServiceResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
        }
    }

    @PostMapping("/{id}/edit", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun editPromo(
        @PathVariable id: String,
        @RequestHeader (value = "Authorization") token: String,
        @RequestBody file: MultipartFile,
        title: String,
        description: String,
        @RequestParam dateExpired: String,
        response: HttpServletResponse
    ): ServiceResponse<Promo> {
        return try {
            promoService.editPromo(
                id = id,
                token = token,
                title = title,
                description = description,
                file = file,
                dateExpired = dateExpired
            )
        } catch (e: ChangeSetPersister.NotFoundException) {
            ServiceResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
        }
    }

    @GetMapping("/{id}/products")
    fun getProductPromo(
        @RequestParam(defaultValue = "0") pageNum: @Min(0) @Max(500) Int,
        @RequestParam(defaultValue = "48") pageSize: @Min(1) @Max(500) Int,
        @PathVariable id: String,
        response: HttpServletResponse
    ): PagingResponse<ProductsLightResponse> {
        return try {
             promoService.getProductPromo(id =  id, page = pageNum, countCard = pageSize)
        } catch (e: ChangeSetPersister.NotFoundException) {
            PagingResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
        }
    }


}