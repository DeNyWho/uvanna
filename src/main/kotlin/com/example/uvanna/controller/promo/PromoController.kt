package com.example.uvanna.controller.promo

import com.example.uvanna.jpa.Promo
import com.example.uvanna.model.response.PagingResponse
import com.example.uvanna.model.response.ServiceResponse
import com.example.uvanna.service.PromoService
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.crossstore.ChangeSetPersister
import org.springframework.http.HttpStatus
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

    @PostMapping("products")
    fun addPromoWithProducts(
        @RequestBody file: MultipartFile,
        title: String,
        description: String,
        productIDS: List<String>,
        @RequestParam token: String,
        response: HttpServletResponse
    ): ServiceResponse<Promo> {
        return try {
            promoService.addPromoWithProducts(token = token, title = title, description = description, file = file, products = productIDS)
        } catch (e: ChangeSetPersister.NotFoundException){
            ServiceResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
        }
    }

    @PostMapping("category")
    fun addPromoWithCategory(
        @RequestBody file: MultipartFile,
        title: String,
        description: String,
        category: String,
        @RequestParam token: String,
        response: HttpServletResponse
    ): ServiceResponse<Promo> {
        return try {
            promoService.addPromoWithCategory(token = token, title = title, description = description, file = file, category = category)
        } catch (e: ChangeSetPersister.NotFoundException){
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
        @RequestParam token: String,
        @PathVariable id: String,
        response: HttpServletResponse
    ): ServiceResponse<String> {
        return try {
             promoService.deletePromo(token = token, id =  id)
        } catch (e: ChangeSetPersister.NotFoundException) {
            ServiceResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
        }
    }
}