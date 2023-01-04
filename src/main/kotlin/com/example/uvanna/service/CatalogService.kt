package com.example.uvanna.service

import com.example.uvanna.jpa.CatalogFirst
import com.example.uvanna.jpa.CatalogSecond
import com.example.uvanna.jpa.CatalogThird
import com.example.uvanna.model.category.CategoryFirst
import com.example.uvanna.model.category.CategorySecond
import com.example.uvanna.model.category.CategoryThird
import com.example.uvanna.model.response.ServiceResponse
import com.example.uvanna.repository.catalog.CatalogRepository
import com.example.uvanna.repository.catalog.CatalogRepositoryImpl
import com.example.uvanna.repository.catalog.CatalogSecondRepository
import com.example.uvanna.repository.catalog.CatalogThirdRepository
import com.example.uvanna.repository.products.ProductsRepository
import com.example.uvanna.util.CheckUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.*
import javax.annotation.Resource


@Service
class CatalogService: CatalogRepositoryImpl {

    @Autowired
    private lateinit var catalogRepository: CatalogRepository

    @Autowired
    private lateinit var catalogSecondRepository: CatalogSecondRepository

    @Autowired
    private lateinit var catalogThirdRepository: CatalogThirdRepository

    @Autowired
    private lateinit var fileService: FileService

    @Autowired
    private lateinit var productsRepository: ProductsRepository

    @Resource
    private lateinit var checkUtil: CheckUtil

    override fun getUpperLevels(id: String): Any {
        val firstCatalog = catalogRepository.findById(id).isPresent
        val secondCatalog = catalogSecondRepository.findById(id).isPresent
        val thirdCatalog = catalogThirdRepository.findById(id).isPresent

        if (firstCatalog) {
            val catalog = catalogRepository.findById(id).get()

            return CategoryFirst(
                id = catalog.id!!,
                title = catalog.title!!,
                sub = catalog.sub,
                imageUrl = catalog.imageUrl,
                level = catalog.level
            )
        }

        if (secondCatalog) {
            val catalog = catalogSecondRepository.findById(id).get()
            return catalogRepository.findUpper(catalog)
        }

        if (thirdCatalog) {
            val catalog = catalogThirdRepository.findById(id).get()
            return catalogSecondRepository.findUpper(catalog)
        }

        return false
    }


    override fun getLevels(id: String?): Any {
        if (id != null) {
            val firstCatalog = catalogRepository.findById(id).isPresent
            val secondCatalog = catalogSecondRepository.findById(id).isPresent
            val thirdCatalog = catalogThirdRepository.findById(id).isPresent

            if (firstCatalog) {
                val catalog = catalogRepository.findById(id).get()

                return CategoryFirst(
                    id = catalog.id!!,
                    title = catalog.title!!,
                    sub = catalog.sub,
                    imageUrl = catalog.imageUrl,
                    level = catalog.level
                )
            }

            if (secondCatalog) {
                val catalog = catalogSecondRepository.findById(id).get()

                return CategorySecond(
                    id = catalog.id!!,
                    title = catalog.title!!,
                    sub = catalog.sub,
                    imageUrl = catalog.imageUrl,
                    level = catalog.level
                )
            }

            if (thirdCatalog) {

                val catalog = catalogThirdRepository.findById(id).get()

                return CategoryThird(id = catalog.id!!, title = catalog.title!!, level = catalog.level)
            }
        } else {
            return catalogRepository.findAll()
        }

        return false
    }

    override fun deleteCategory(id: String, token: String): ServiceResponse<String> {
        val check = checkUtil.checkToken(token)
        return if (check) {
            return try {
                var count = 0
                val catalogFirst = catalogRepository.findById(id).isPresent
                val catalogSecond = catalogSecondRepository.findById(id).isPresent
                val catalogThird = catalogThirdRepository.findById(id).isPresent

                if (catalogFirst) {
                    try {
                        val catalog = catalogRepository.findById(id).get()
                        catalogRepository.deleteById(id)
                        catalog.sub.forEach { secondItem ->
                            catalogSecondRepository.deleteById(secondItem.id!!)
                            secondItem.sub.forEach { thirdItem ->
                                catalogThirdRepository.deleteById(thirdItem.id!!)
                            }
                        }
                        val temp = productsRepository.findAllByCategories(category = catalog.id!!)

                        temp.forEach {
                            productsRepository.deleteById(it.id)
                        }

                        count = count + 1
                    } catch (e: Exception) {
                        println(e.message)
                    }
                }

                if (catalogSecond) {
                    try {
                        val catalog = catalogSecondRepository.findById(id).get()
                        catalogSecondRepository.deleteById(id)
                        catalog.sub.forEach { item ->
                            catalogThirdRepository.deleteById(item.id!!)
                        }
                        val temp = productsRepository.findAllByCategories(category = catalog.id!!)

                        temp.forEach {
                            productsRepository.deleteById(it.id)
                        }

                        count = count + 1
                    } catch (e: Exception) {
                        println(e.message)
                    }
                }

                if (catalogThird) {
                    try {
                        catalogThirdRepository.deleteById(id)
                        val temp = productsRepository.findAllByCategories(category = id)

                        temp.forEach {
                            productsRepository.deleteById(it.id)
                        }

                        count = count + 1
                    } catch (e: Exception){
                        println(e.message)
                    }
                }
                if(count > 0){
                    ServiceResponse(
                        data = listOf(),
                        message = "Category with id = $id has been deleted",
                        status = HttpStatus.OK
                    )
                } else {
                    ServiceResponse(
                        data = listOf(),
                        message = "Category with id = $id has been deleted",
                        status = HttpStatus.OK
                    )
                }

            } catch (e: Exception) {
                ServiceResponse(
                    data = listOf(),
                    message = "Category with id = $id not found",
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
    override fun edit(id: String, file: MultipartFile, title: String, token: String): ServiceResponse<Any> {
        val check = checkUtil.checkToken(token)
        return if (check) {
            return try {
                var option: String? = null
                val catalogFirst = catalogRepository.findById(id).isPresent
                val catalogSecond = catalogSecondRepository.findById(id).isPresent
                val catalogThird = catalogThirdRepository.findById(id).isPresent

                if (catalogFirst) {
                    try {
                        val catalog = catalogRepository.findById(id).get()
                        option = catalog.level
                    } catch (e: Exception) {
                        println(e.message)
                    }
                }

                if (catalogSecond) {
                    try {
                        val catalog = catalogSecondRepository.findById(id).get()
                        option = catalog.level
                    } catch (e: Exception) {
                        println(e.message)
                    }
                }

                if (catalogThird) {
                    try {
                        val catalog = catalogThirdRepository.findById(id).get()
                        option = catalog.level
                    } catch (e: Exception){
                        println(e.message)
                    }
                }

                when (option) {
                    "first" -> addFirstLevel(file, title)
                    "second" -> addSecondLevel(id, file, title)
                    "third" -> addThirdLevel(id, title, file)
                }
                ServiceResponse(
                    data = null,
                    message = "Category with id = $id has been deleted",
                    status = HttpStatus.OK
                )
            } catch (e: Exception) {
                ServiceResponse(
                    data = null,
                    message = "Category with id = $id not found",
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

    override fun addLevel(
        id: String?,
        file: MultipartFile,
        title: String,
        option: String,
        token: String,
    ): ServiceResponse<String> {
        val check = checkUtil.checkToken(token)
        return if (check) {
            return try {
                when (option) {
                    "first" -> addFirstLevel(file, title)
                    "second" -> addSecondLevel(id!!, file, title)
                    "third" -> addThirdLevel(id!!, title, file)
                }
                ServiceResponse(
                    data = null,
                    message = "Category with id = $id has been deleted",
                    status = HttpStatus.OK
                )
            } catch (e: Exception) {
                ServiceResponse(
                    data = null,
                    message = "Category with id = $id not found",
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

    fun addFirstLevel(file: MultipartFile, title: String) {
        var id = UUID.randomUUID().toString()
        var catalog = catalogRepository.findById(id).isPresent

        while (catalog){
            id = UUID.randomUUID().toString()
            catalog = catalogRepository.findById(id).isPresent
        }

        catalogRepository.save(
            CatalogFirst(
                id = id,
                title = title,
                imageUrl = fileService.save(file),
            )
        )
    }

    fun addSecondLevel(
        id: String,
        file: MultipartFile,
        title: String
    ) {
        val secondId = UUID.randomUUID().toString()

        val second = catalogSecondRepository.save(
            CatalogSecond(
            id = secondId,
            title = title,
            imageUrl = fileService.save(file)
            )
        )

        val b = catalogRepository.findById(id).get().addToSecondLevel(
            second
        )
        catalogRepository.save(b)
    }

    fun addThirdLevel(
        id: String,
        title: String,
        file: MultipartFile,
    ){
        val thirdId = UUID.randomUUID().toString()

        val third = catalogThirdRepository.save(
            CatalogThird(
                id = thirdId,
                title = title,
                imageUrl = fileService.save(file)
            )
        )

        val b = catalogSecondRepository.findById(id).get().addToThirdLevel(
            third
        )
        catalogSecondRepository.save(b)
    }





}