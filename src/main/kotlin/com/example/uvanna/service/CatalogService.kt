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

            val firstSub = catalog.sub.sortedBy { it.title }.toMutableSet()
            val secondSub = mutableListOf<CatalogSecond>()

            firstSub.forEach { catalogSecond ->
                secondSub.add(
                    CatalogSecond(
                        id = catalogSecond.id,
                        level = catalogSecond.level,
                        title = catalogSecond.title,
                        sub = catalogSecond.sub.sortedBy { it.title }.toMutableSet(),
                        imageUrl = catalogSecond.imageUrl,
                    )
                )
            }

            return CategoryFirst(
                id = catalog.id!!,
                title = catalog.title!!,
                sub = secondSub.sortedBy { it.title }.toMutableSet(),
                imageUrl = catalog.imageUrl,
                level = catalog.level
            )
        }

        if (secondCatalog) {
            val catalog = catalogSecondRepository.findById(id).get()
            val catalogTemp = catalogRepository.findUpper(catalog)
            val secondSub = mutableListOf<CatalogSecond>()
            val firstSub = catalogTemp.sub.sortedBy { it.title }.toMutableSet()

            firstSub.forEach { catalogSecond ->
                secondSub.add(
                    CatalogSecond(
                        id = catalogSecond.id,
                        level = catalogSecond.level,
                        title = catalogSecond.title,
                        sub = catalogSecond.sub.sortedBy { it.title }.toMutableSet(),
                        imageUrl = catalogSecond.imageUrl,
                    )
                )
            }

            return CategoryFirst(
                id = catalogTemp.id!!,
                title = catalogTemp.title!!,
                sub = secondSub.sortedBy { it.title }.toMutableSet(),
                imageUrl = catalogTemp.imageUrl,
                level = catalogTemp.level
            )
        }

        if (thirdCatalog) {
            val catalog = catalogThirdRepository.findById(id).get()
            val b = catalogSecondRepository.findUpper(catalog)
            return CatalogSecond(
                id = b.id,
                level = b.level,
                title = b.title,
                sub = b.sub.sortedBy { it.title }.toMutableSet(),
                imageUrl = b.imageUrl,
            )
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

                val firstSub = catalog.sub.sortedBy { it.title }.toMutableSet()
                val secondSub = mutableListOf<CatalogSecond>()

                firstSub.forEach { catalogSecond ->
                    secondSub.add(
                        CatalogSecond(
                            id = catalogSecond.id,
                            level = catalogSecond.level,
                            title = catalogSecond.title,
                            sub = catalogSecond.sub.sortedBy { it.title }.toMutableSet(),
                            imageUrl = catalogSecond.imageUrl,
                        )
                    )
                }

                return CategoryFirst(
                    id = catalog.id!!,
                    title = catalog.title!!,
                    sub = secondSub.sortedBy { it.title }.toMutableSet(),
                    imageUrl = catalog.imageUrl,
                    level = catalog.level
                )
            }

            if (secondCatalog) {
                val catalog = catalogSecondRepository.findById(id).get()

                return CategorySecond(
                    id = catalog.id!!,
                    title = catalog.title!!,
                    sub = catalog.sub.sortedBy { it.title }.toMutableSet(),
                    imageUrl = catalog.imageUrl,
                    level = catalog.level
                )
            }

            if (thirdCatalog) {

                val catalog = catalogThirdRepository.findById(id).get()

                return CategoryThird(
                    id = catalog.id!!,
                    title = catalog.title!!,
                    level = catalog.level,
                    imageUrl = catalog.imageUrl
                )
            }
        } else {
            val catalogsTemp = catalogRepository.findAll()
            val catalogs = mutableListOf<CatalogFirst>()

            catalogsTemp.forEach { catalogFirst ->
                val secondSub = mutableListOf<CatalogSecond>()
                catalogFirst.sub.forEach { catalogSecond ->
                    secondSub.add(
                        CatalogSecond(
                            id = catalogSecond.id,
                            level = catalogSecond.level,
                            title = catalogSecond.title,
                            sub = catalogSecond.sub.sortedBy { it.title }.toMutableSet(),
                            imageUrl = catalogSecond.imageUrl,
                        )
                    )
                }
                catalogs.add(
                    CatalogFirst(
                        id = catalogFirst.id,
                        level = catalogFirst.level,
                        imageUrl = catalogFirst.imageUrl,
                        sub = secondSub.sortedBy { it.title }.toMutableSet(),
                        title = catalogFirst.title
                    )
                )
            }
            return catalogs.sortedBy { it.title }
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

                        fileService.deleteByUrl(catalog.imageUrl)

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

                        val second = catalogSecondRepository.findById(id).get()
                        val firstCatalog = catalogRepository.findUpper(second)

                        firstCatalog.deleteFromSecondLevel(second)

                        val catalog = catalogSecondRepository.findById(id).get()
                        catalogSecondRepository.deleteById(id)
                        catalog.sub.forEach { item ->
                            catalogThirdRepository.deleteById(item.id!!)
                        }

                        fileService.deleteByUrl(catalog.imageUrl)

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
                        val third = catalogThirdRepository.findById(id).get()
                        val secondCatalog = catalogSecondRepository.findUpper(third)

                        secondCatalog.deleteFromThirdLevel(third)

                        catalogThirdRepository.deleteById(id)

                        fileService.deleteByUrl(third.imageUrl)

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
                        message = "Category with id = $id not found",
                        status = HttpStatus.NOT_FOUND
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

    override fun swapCategory(categoryId: String, swapToCategory: String, token: String): ServiceResponse<Any> {
        val check = checkUtil.checkToken(token)
        return if (check) {
            val firstCatalog = catalogRepository.findById(categoryId).isPresent
            val secondCatalog = catalogSecondRepository.findById(categoryId).isPresent
            val thirdCatalog = catalogThirdRepository.findById(categoryId).isPresent

            if (firstCatalog) {
                return ServiceResponse(
                    data = null,
                    message = "the 1st level was introduced",
                    status = HttpStatus.BAD_REQUEST
                )
            }

            if(thirdCatalog){
                val catalog = catalogThirdRepository.findById(categoryId).get()
                val catalogUpper = catalogSecondRepository.findUpper(catalog)
                val catalogUpperUpper = catalogRepository.findUpper(catalogUpper)
                val swapFirst = catalogRepository.findById(swapToCategory).isPresent
                val swapSecond = catalogSecondRepository.findById(swapToCategory).isPresent

                if(swapFirst) {
                    val products = productsRepository.findAllByThirdCategory(catalog.id!!)
                    catalogUpper.deleteFromThirdLevel(catalog)
                    val swapCategory = catalogRepository.findById(swapToCategory).get()

                    val final = CatalogSecond(
                        id = catalog.id,
                        title = catalog.title,
                        imageUrl = catalog.imageUrl,
                        sub = mutableSetOf(),
                        level = "second"
                    )

                    swapCategory.addToSecondLevel(final)

                    products.forEach {
                        productsRepository.deleteById(it.id)
                        println(it.images)
                        val product = it
                        product.firstSub = swapCategory.id!!
                        product.secondSub = catalog.id!!
                        productsRepository.save(product)
                    }

                    catalogThirdRepository.deleteById(catalog.id!!)

                    catalogSecondRepository.save(final)
                    catalogSecondRepository.save(catalogUpper)
                    catalogRepository.save(swapCategory)
                    catalogRepository.save(catalogUpperUpper)

                    return ServiceResponse(
                        data = listOf("Catalog with id = $categoryId swap to id = $swapToCategory"),
                        message = "Success",
                        status = HttpStatus.OK
                    )
                }

                if(swapSecond){
                    val products = productsRepository.findAllByThirdCategory(catalog.id!!)
                    catalogUpper.deleteFromThirdLevel(catalog)
                    val swapCategory = catalogSecondRepository.findById(swapToCategory).get()
                    val swapUpper = catalogRepository.findUpper(swapCategory)

                    products.forEach {
                        productsRepository.deleteById(it.id)
                        println(it.images)
                        val product = it
                        product.secondSub = swapCategory.id!!
                        product.thirdSub = catalog.id!!
                        productsRepository.save(product)
                    }

                    val final = CatalogThird(
                        id = catalog.id,
                        title = catalog.title,
                        imageUrl = catalog.imageUrl,
                        level = "third"
                    )
                    swapCategory.addToThirdLevel(final)

                    catalogSecondRepository.save(swapCategory)
                    catalogSecondRepository.save(catalogUpper)
                    catalogRepository.save(catalogUpperUpper)
                    catalogRepository.save(swapUpper)

                    return ServiceResponse(
                        data = listOf("Catalog with id = $categoryId swap to id = $swapToCategory"),
                        message = "Success",
                        status = HttpStatus.OK
                    )
                }
            }

            if(secondCatalog){
                val catalog = catalogSecondRepository.findById(categoryId).get()
                val catalogUpper = catalogRepository.findUpper(catalog)
                val products = productsRepository.findAllBySecondCategory(catalog.id!!)
                val swapFirst = catalogRepository.findById(swapToCategory).isPresent
                val swapSecond = catalogSecondRepository.findById(swapToCategory).isPresent

                if(swapFirst) {
                    catalogUpper.deleteFromSecondLevel(catalog)
                    val swapCategory = catalogRepository.findById(swapToCategory).get()

                    swapCategory.addToSecondLevel(catalog)

                    products.forEach {
                        productsRepository.deleteById(it.id)
                        println(it.images)
                        val product = it
                        product.firstSub = swapCategory.id!!
                        product.secondSub = catalog.id!!
                        productsRepository.save(product)
                    }
                    catalogSecondRepository.deleteById(catalog.id!!)

                    catalogSecondRepository.save(catalog)
                    catalogRepository.save(swapCategory)
                    catalogRepository.save(catalogUpper)
                    return ServiceResponse(
                        data = listOf("Catalog with id = $categoryId swap to id = $swapToCategory"),
                        message = "Success",
                        status = HttpStatus.OK
                    )
                }

                if(swapSecond){
                    catalogUpper.deleteFromSecondLevel(catalog)
                    val swapCategory = catalogSecondRepository.findById(swapToCategory).get()
                    val swapUpper = catalogRepository.findUpper(swapCategory)

                    val final = CatalogThird(
                        id = catalog.id,
                        title = catalog.title,
                        imageUrl = catalog.imageUrl,
                        level = "third"
                    )
                    swapCategory.addToThirdLevel(final)

                    products.forEach {
                        productsRepository.deleteById(it.id)
                        println(it.images)
                        val product = it
                        product.secondSub = swapCategory.id!!
                        product.thirdSub = catalog.id!!
                        productsRepository.save(product)
                    }
                    catalogSecondRepository.deleteById(catalog.id!!)

                    catalogThirdRepository.save(final)
                    catalogSecondRepository.save(swapCategory)
                    catalogRepository.save(catalogUpper)
                    catalogRepository.save(swapUpper)
                    return ServiceResponse(
                        data = listOf("Catalog with id = $categoryId swap to id = $swapToCategory"),
                        message = "Success",
                        status = HttpStatus.OK
                    )
                }
            }

            ServiceResponse(
                data = null,
                message = "Message: $firstCatalog | $secondCatalog | $thirdCatalog",
                status = HttpStatus.UNAUTHORIZED
            )
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
                var count = 0
                val catalogFirst = catalogRepository.findById(id).isPresent
                val catalogSecond = catalogSecondRepository.findById(id).isPresent
                val catalogThird = catalogThirdRepository.findById(id).isPresent

                if (catalogFirst) {
                    try {
                        val temp = catalogRepository.findById(id).get()
                        catalogRepository.deleteById(id)

                        fileService.deleteByUrl(temp.imageUrl)

                        val catalog = CatalogFirst(
                            id = id,
                            title = title,
                            sub = temp.sub,
                            imageUrl = fileService.save(file),
                            level = temp.level
                        )

                        catalogRepository.save(catalog)

                        count = count + 1
                    } catch (e: Exception) {
                        println(e.message)
                    }
                }

                if (catalogSecond) {
                    try {
                        val temp = catalogSecondRepository.findById(id).get()

                        val first = catalogRepository.findUpper(temp)
                        first.deleteFromSecondLevel(temp)

                        fileService.deleteByUrl(temp.imageUrl)

                        val catalog = CatalogSecond(
                            id = id,
                            title = title,
                            sub = temp.sub,
                            imageUrl = fileService.save(file),
                            level = temp.level
                        )

                        first.addToSecondLevel(catalogSecondRepository.save(catalog))

                        catalogRepository.deleteById(first.id!!)

                        catalogSecondRepository.deleteById(id)

                        catalogSecondRepository.save(catalog)
                        catalogRepository.save(first)

                        count = count + 1
                    } catch (e: Exception) {
                        println(e.message)
                    }
                }

                if (catalogThird) {
                    try {
                        val temp = catalogThirdRepository.findById(id).get()

                        val second = catalogSecondRepository.findUpper(temp)
                        val first = catalogRepository.findUpper(second)

                        second.deleteFromThirdLevel(temp)

                        fileService.deleteByUrl(temp.imageUrl)

                        val catalog = CatalogThird(
                            id = id,
                            title = title,
                            imageUrl = fileService.save(file),
                            level = temp.level
                        )

                        second.addToThirdLevel(catalogThirdRepository.save(catalog))

                        catalogRepository.deleteById(first.id!!)
                        catalogSecondRepository.deleteById(second.id!!)
                        catalogThirdRepository.deleteById(id)

                        catalogThirdRepository.save(catalog)
                        catalogSecondRepository.save(second)
                        catalogRepository.save(first)

                        count = count + 1
                    } catch (e: Exception){
                        println(e.message)
                    }
                }

                if(count > 0 ){
                    ServiceResponse(
                        data = null,
                        message = "Category with id = $id has been edited",
                        status = HttpStatus.OK
                    )
                } else {
                    ServiceResponse(
                        data = null,
                        message = "Something went wrong...",
                        status = HttpStatus.NOT_FOUND
                    )
                }
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
    ) {
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