package com.example.uvanna.controller.site

import com.example.uvanna.jpa.Blog
import com.example.uvanna.model.request.site.BlogRequest
import com.example.uvanna.model.response.BlogLighterResponse
import com.example.uvanna.model.response.PagingBlogResponse
import com.example.uvanna.model.response.ServiceResponse
import com.example.uvanna.service.SiteService
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
@Tag(name = "SiteApi", description = "Used to manage site elements")
@RequestMapping("/api/site/")
class SiteController {

    @Autowired
    lateinit var siteService: SiteService

    @PostMapping("blog", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun addBlog(
        @RequestPart("image")  image: MultipartFile,
        @RequestPart("subImages")subImages: List<MultipartFile>?,
        title: String,
        @RequestPart("description") description: String,
        @RequestHeader (value = "Authorization") token: String,
        response: HttpServletResponse
    ): ServiceResponse<Blog>? {
        return try {
            siteService.addBlog(
                mainImage = image,
                subImages = subImages,
                title = title,
                description = description,
                token = token
            )
        } catch (e: ChangeSetPersister.NotFoundException) {
            ServiceResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
        }
    }

    @GetMapping("blog/")
    fun getAllBlogs(
        @Parameter(description = "old | new", required = false) filter: String?,
        @RequestParam(defaultValue = "0") pageNum: @Min(0) @Max(500) Int,
        @RequestParam(defaultValue = "48") pageSize: @Min(1) @Max(500) Int,
        response: HttpServletResponse
    ): PagingBlogResponse<BlogLighterResponse>? {
        return try {
            siteService.getBlogs(filter, pageNum, pageSize)
        } catch (e: ChangeSetPersister.NotFoundException) {
            PagingBlogResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
        }
    }

    @GetMapping("blog/ids/all")
    fun getIdsBlogs(
        response: HttpServletResponse
    ): ServiceResponse<String> {
        return try {
            siteService.getBlogsIds()
        } catch (e: ChangeSetPersister.NotFoundException) {
            ServiceResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
        }
    }

    @PostMapping("blog/{id}/edit", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun editBlog(
        @PathVariable id: String,
        @RequestPart("image")  image: MultipartFile,
        @RequestPart("subImages")subImages: List<MultipartFile>?,
        title: String,
        @RequestPart("description") description: String,
        @RequestHeader (value = "Authorization") token: String,
        response: HttpServletResponse
    ): ServiceResponse<Blog>? {
        return try {
            siteService.editBlog(
                id = id,
                mainImage = image,
                subImages = subImages,
                title = title,
                description = description,
                token = token
            )
        } catch (e: ChangeSetPersister.NotFoundException) {
            ServiceResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
        }
    }
    @GetMapping("blog/relative")
    fun getBlogsRelative(
        @RequestParam id: String,
        @RequestParam(defaultValue = "0") pageNum: @Min(0) @Max(500) Int,
        @RequestParam(defaultValue = "48") pageSize: @Min(1) @Max(500) Int,
        response: HttpServletResponse
    ): PagingBlogResponse<BlogLighterResponse>? {
        return try {
            siteService.getBlogsRelative(id, pageNum, pageSize)
        } catch (e: ChangeSetPersister.NotFoundException) {
            PagingBlogResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
        }
    }

    @GetMapping("blog/{id}")
    fun getBlogById(
        @PathVariable id: String,
        response: HttpServletResponse,
    ): ServiceResponse<Blog> {
        return try {
            siteService.getBlogById(id)
        } catch (e: ChangeSetPersister.NotFoundException) {
            ServiceResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
        }
    }

    @DeleteMapping("blog/{id}")
    fun deleteBlog(
        @PathVariable id: String,
        @RequestHeader (value = "Authorization") token: String,
        response: HttpServletResponse
    ): ServiceResponse<String> {
        return try {
            siteService.deleteBlog(token = token, id = id)
        } catch (e: ChangeSetPersister.NotFoundException) {
            ServiceResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
        }
    }

}