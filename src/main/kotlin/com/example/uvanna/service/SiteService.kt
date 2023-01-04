package com.example.uvanna.service

import com.example.uvanna.jpa.Blog
import com.example.uvanna.model.response.*
import com.example.uvanna.repository.site.BlogRepository
import com.example.uvanna.repository.site.SiteRepositoryImpl
import com.example.uvanna.util.CheckUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import javax.annotation.Resource

@Service
class SiteService: SiteRepositoryImpl {

    @Autowired
    lateinit var blogRepository: BlogRepository

    @Autowired
    lateinit var fileService: FileService

    @Resource
    private lateinit var checkUtil: CheckUtil

    override fun addBlog(mainImage: MultipartFile, subImages: List<MultipartFile>?, title: String, description: String, token: String): ServiceResponse<Blog>{
        return try {
            val check = checkUtil.checkToken(token)

            return if(check) {
                return try {
                    val imageUrl = fileService.save(mainImage)

                    val tempSubImages = mutableListOf<String>()

                    subImages?.forEach {
                        tempSubImages.add(fileService.save(it))
                    }

                    val item = Blog(
                        id = UUID.randomUUID().toString(),
                        mainImage = imageUrl,
                        title = title,
                        description = description,
                        subImages = if(tempSubImages.isNotEmpty()) tempSubImages.toList() else null,
                        dateCreated = LocalDateTime.now()
                    )

                    blogRepository.save(item)

                    ServiceResponse(
                        data = listOf(blogRepository.findById(item.id).get()),
                        message = "Blog has been created",
                        status = HttpStatus.OK
                    )
                } catch (e: Exception) {
                    ServiceResponse(
                        data = null,
                        message = "Something went wrong: ${e.message}",
                        status = HttpStatus.BAD_REQUEST
                    )
                }
            } else {
                ServiceResponse(
                    data = null,
                    message = "Unexpected token",
                    status = HttpStatus.UNAUTHORIZED
                )
            }
        } catch (e: Exception){
            ServiceResponse(
                data = null,
                message = "Something went wrong: ${e.message}",
                status = HttpStatus.BAD_REQUEST
            )
        }
    }

    override fun editBlog(id: String, mainImage: MultipartFile, subImages: List<MultipartFile>?, title: String, description: String, token: String): ServiceResponse<Blog>? {
        return try {
            val check = checkUtil.checkToken(token)

            return if(check) {
                return try {
                    val prevBlog = blogRepository.findById(id).get()
                    fileService.deleteByUrl(prevBlog.mainImage)

                    try {
                        prevBlog.subImages!!.forEach {
                            fileService.deleteByUrl(it)
                        }
                    } catch (e: Exception){}

                    blogRepository.deleteById(id)
                    val imageUrl = fileService.save(mainImage)

                    val tempSubImages = mutableListOf<String>()

                    subImages?.forEach {
                        tempSubImages.add(fileService.save(it))
                    }

                    val item = Blog(
                        id = UUID.randomUUID().toString(),
                        mainImage = imageUrl,
                        title = title,
                        description = description,
                        subImages = if(tempSubImages.isNotEmpty()) tempSubImages.toList() else null,
                        dateCreated = LocalDateTime.now()
                    )

                    blogRepository.save(item)

                    ServiceResponse(
                        data = listOf(blogRepository.findById(item.id).get()),
                        message = "Blog has been edited",
                        status = HttpStatus.OK
                    )
                } catch (e: Exception) {
                    ServiceResponse(
                        data = null,
                        message = "Something went wrong: ${e.message}",
                        status = HttpStatus.BAD_REQUEST
                    )
                }
            } else {
                ServiceResponse(
                    data = null,
                    message = "Unexpected token",
                    status = HttpStatus.UNAUTHORIZED
                )
            }
        } catch (e: Exception){
            ServiceResponse(
                data = null,
                message = "Something went wrong: ${e.message}",
                status = HttpStatus.BAD_REQUEST
            )
        }
    }

    override fun getBlogsRelative(id: String, pageNum: Int, pageSize: Int): PagingBlogResponse<BlogLighterResponse> {
        return try {
            val pageable: Pageable = PageRequest.of(pageNum, pageSize)
            val statePage: Page<Blog> = blogRepository.findBlogsByRandom(pageable, id)

            val light = mutableListOf<BlogLighterResponse>()

            statePage.content.forEach {
                light.add(
                    BlogLighterResponse(
                        id = it.id,
                        title = it.title,
                        image = it.mainImage,
                        dateCreated = it.dateCreated
                    )
                )
            }
            PagingBlogResponse(
                data = light,
                totalElements = statePage.totalElements,
                totalPages = statePage.totalPages,
                message = "Success",
                status = HttpStatus.OK
            )
        } catch (e: Exception) {
            PagingBlogResponse(
                data = null,
                message = e.message.toString(),
                status = HttpStatus.BAD_REQUEST
            )
        }
    }

    override fun getBlogsIds(): ServiceResponse<String> {
        return try {
            val ids = mutableListOf<String>()

            val blogs = blogRepository.findAll()

            blogs.forEach {
                ids.add(it.id)
            }

            ServiceResponse(
                data = ids,
                message = "Success",
                status = HttpStatus.OK
            )
        } catch (e: Exception) {
            ServiceResponse(
                data = listOf(),
                message = "Something went wrong... ${e.message}",
                status = HttpStatus.BAD_REQUEST
            )
        }
    }

    override fun getBlogs(filter: String?, pageNum: Int, pageSize: Int): PagingBlogResponse<BlogLighterResponse> {
        return try {
            val sort = when (filter) {
                "old" -> Sort.by(
                    Sort.Order(Sort.Direction.ASC, "dateCreated"),
                )
                "new" -> Sort.by(
                    Sort.Order(Sort.Direction.DESC, "dateCreated")
                )

                else -> null
            }
            val pageable: Pageable =
                if (sort != null) PageRequest.of(pageNum, pageSize, sort) else PageRequest.of(pageNum, pageSize)

            val statePage: Page<Blog> = blogRepository.findAll(pageable)

            val light = mutableListOf<BlogLighterResponse>()

            statePage.content.forEach {
                light.add(
                    BlogLighterResponse(
                        id = it.id,
                        title = it.title,
                        image = it.mainImage,
                        dateCreated = it.dateCreated
                    )
                )
            }

            PagingBlogResponse(
                data = light,
                totalElements = statePage.totalElements,
                totalPages = statePage.totalPages,
                message = "Success",
                status = HttpStatus.OK
            )
        } catch (e: Exception) {
            PagingBlogResponse(
                data = null,
                message = "Something went wrong: ${e.message}",
                status = HttpStatus.BAD_REQUEST
            )
        }
    }

    override fun getBlogById(id: String): ServiceResponse<Blog> {
        return try {
            ServiceResponse(
                data = listOf(blogRepository.findById(id).get()),
                message = "Success",
                status = HttpStatus.OK
            )
        } catch (e: Exception) {
            ServiceResponse(
                data = null,
                message = "Something went wrong: ${e.message}",
                status = HttpStatus.BAD_REQUEST
            )
        }
    }

    override fun deleteBlog(token: String, id: String): ServiceResponse<String>{
        val check = checkUtil.checkToken(token)

        return if(check) {
            return try {
                val temp = blogRepository.findById(id).get()

                if(temp.subImages?.isNotEmpty() == true){
                    temp.subImages?.forEach {
                        fileService.deleteByUrl(it)
                    }
                }

                fileService.deleteByUrl(temp.mainImage)

                blogRepository.deleteById(id)

                ServiceResponse(
                    data = listOf(),
                    message = "Blog with id = $id has been deleted",
                    status = HttpStatus.OK
                )
            } catch (e: Exception) {
                ServiceResponse(
                    data = listOf(),
                    message = "Blog with id = $id not found",
                    status = HttpStatus.NOT_FOUND
                )
            }
        } else {
            ServiceResponse(
                data = null,
                message = "Unexpected token",
                status = HttpStatus.UNAUTHORIZED
            )
        }
    }


}