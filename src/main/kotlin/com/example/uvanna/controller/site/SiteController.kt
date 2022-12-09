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
        token: String,
        response: HttpServletResponse
    ): ServiceResponse<MainBanner>? {
        return try {
            siteService.addMainBanner(file = file, token = token)
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
        @RequestParam token: String,
        response: HttpServletResponse
    ): ServiceResponse<String> {
        return try {
            return siteService.deleteMainBanner(id = id, token = token)
        } catch (e: ChangeSetPersister.NotFoundException) {
            ServiceResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
        }
    }



}