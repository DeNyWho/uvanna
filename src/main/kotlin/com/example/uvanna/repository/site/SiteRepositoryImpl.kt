package com.example.uvanna.repository.site

import com.example.uvanna.jpa.Blog
import com.example.uvanna.jpa.MainBanner
import com.example.uvanna.model.request.site.BlogRequest
import com.example.uvanna.model.response.BlogLighterResponse
import com.example.uvanna.model.response.PagingBlogResponse
import com.example.uvanna.model.response.ServiceResponse
import org.springframework.stereotype.Repository
import org.springframework.web.multipart.MultipartFile

@Repository
interface SiteRepositoryImpl {

    fun getBlogById(id: String): ServiceResponse<Blog>
    fun deleteBlog(token: String, id: String): ServiceResponse<String>
    fun addBlog(
        mainImage: MultipartFile,
        subImages: List<MultipartFile>?,
        title: String,
        description: String,
        token: String
    ): ServiceResponse<Blog>

    fun getBlogs(filter: String?, pageNum: Int, pageSize: Int): PagingBlogResponse<BlogLighterResponse>
    fun getBlogsRelative(id: String, pageNum: Int, pageSize: Int): PagingBlogResponse<BlogLighterResponse>
    fun editBlog(
        id: String,
        mainImage: MultipartFile,
        subImages: List<MultipartFile>?,
        title: String,
        description: String,
        token: String
    ): ServiceResponse<Blog>?

    fun getBlogsIds(): ServiceResponse<String>
}