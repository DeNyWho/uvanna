package com.example.uvanna.controller.site

import com.example.uvanna.jpa.MainBanner
import com.example.uvanna.jpa.Product
import com.example.uvanna.jpa.Promo
import com.example.uvanna.model.response.PagingResponse
import com.example.uvanna.model.response.PromoLightResponse
import com.example.uvanna.model.response.ServiceResponse
import com.example.uvanna.service.SiteService
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
@Tag(name = "SiteApi", description = "Used to manage site elements")
@RequestMapping("/api/site/")
class SiteController {

    @Autowired
    lateinit var siteService: SiteService

    @PostMapping("MainBanner", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun addMainBanner(
        @RequestBody file: MultipartFile,
        response: HttpServletResponse
    ): ServiceResponse<MainBanner>? {
        return try {
            siteService.addMainBanner(file = file)
        } catch (e: ChangeSetPersister.NotFoundException) {
            ServiceResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
        }
    }

    @GetMapping("MainBanners")
    fun getMainBanners(
        response: HttpServletResponse
    ): ServiceResponse<MainBanner>? {
        return try {
            siteService.getMainBanners()
        } catch (e: ChangeSetPersister.NotFoundException) {
            ServiceResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
        }
    }


    @DeleteMapping("MainBanner")
    fun deleteMainBanner(
        @RequestParam id: String,
        response: HttpServletResponse
    ): ServiceResponse<String> {
        return try {
            siteService.deleteMainBanner(id)

            return ServiceResponse(
                data = null,
                message = "MainBanner with id = $id has been deleted",
                status = HttpStatus.OK
            )
        } catch (e: ChangeSetPersister.NotFoundException) {
            ServiceResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
        }
    }

    @PostMapping("promo", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun addPromo(
        @RequestBody file: MultipartFile,
        title: String,
        description: String,
        response: HttpServletResponse
    ): ServiceResponse<Promo> {
        return try {
            siteService.addPromo(title = title, description = description, file = file)
        } catch (e: ChangeSetPersister.NotFoundException) {
            ServiceResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
        }
    }

    @GetMapping("promo/{id}")
    fun getPromo(
        @PathVariable id: String,
        response: HttpServletResponse
    ): ServiceResponse<Promo>? {
        return try {
            siteService.getPromo(id)
        } catch (e: ChangeSetPersister.NotFoundException) {
            ServiceResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
        }
    }


    @GetMapping("promo/all")
    fun getPromos(
        @RequestParam(defaultValue = "0") pageNum: @Min(0) @Max(500) Int,
        @RequestParam(defaultValue = "48") pageSize: @Min(1) @Max(500) Int,
        response: HttpServletResponse
    ): PagingResponse<PromoLightResponse> {
        return try {
            siteService.getPromos(pageNum = pageNum, pageSize = pageSize)
        } catch (e: ChangeSetPersister.NotFoundException) {
            PagingResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
        }
    }

    @DeleteMapping("promo/{id}")
    fun deletePromo(
        @PathVariable id: String,
        response: HttpServletResponse
    ): ServiceResponse<String> {
        return try {
            siteService.deletePromo(id)

            return ServiceResponse(
                data = null,
                message = "Promo with id = $id has been deleted",
                status = HttpStatus.OK
            )
        } catch (e: ChangeSetPersister.NotFoundException) {
            ServiceResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
        }
    }



}