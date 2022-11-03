package com.example.uvanna.service

import com.example.uvanna.jpa.CatalogFirst
import com.example.uvanna.jpa.CatalogSecond
import com.example.uvanna.jpa.CatalogThird
import com.example.uvanna.model.category.CategoryFirst
import com.example.uvanna.model.category.CategorySecond
import com.example.uvanna.model.category.CategoryThird
import com.example.uvanna.repository.catalog.CatalogRepository
import com.example.uvanna.repository.catalog.CatalogRepositoryImpl
import com.example.uvanna.repository.catalog.CatalogSecondRepository
import com.example.uvanna.repository.catalog.CatalogThirdRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.*


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


    override fun getLevels(id: String?): Any {
        if(id != null) {
            val firstCatalog = catalogRepository.findById(id).isEmpty
            val secondCatalog = catalogSecondRepository.findById(id).isEmpty
            val thirdCatalog = catalogThirdRepository.findById(id).isEmpty

            if (!firstCatalog) {
                val catalog = catalogRepository.findById(id).get()

                return CategoryFirst(
                    id = catalog.id!!,
                    title = catalog.title!!,
                    sub = catalog.sub,
                    imageUrl = catalog.imageUrl
                )
            }

            if (!secondCatalog) {
                val catalog = catalogSecondRepository.findById(id).get()

                return CategorySecond(
                    id = catalog.id!!,
                    title = catalog.title!!,
                    sub = catalog.sub,
                    imageUrl = catalog.imageUrl
                )
            }

            if (!thirdCatalog) {

                val catalog = catalogThirdRepository.findById(id).get()

                return CategoryThird(id = catalog.id!!, title = catalog.title!!)
            }
        } else {
            return catalogRepository.findAll()
        }

        return false
    }

    override fun addLevel(
        id: String?,
        file: MultipartFile,
        title: String,
        option: String
    ): Boolean {
        return try {
            when (option) {
                "first" -> addFirstLevel(file, title)
                "second" -> addSecondLevel(id!!, file, title)
                "third" -> addThirdLevel(id!!, title)
            }
            true
        } catch (e: Exception){
            false
        }
    }

    fun addFirstLevel(file: MultipartFile, title: String) {
        var id = UUID.randomUUID().toString()
        var catalog = catalogRepository.findById(id).isEmpty

        while (catalog){
            id = UUID.randomUUID().toString()
            catalog = catalogRepository.findById(id).isEmpty
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

        val b = catalogRepository.findById(id).orElseThrow().addToSecondLevel(
            second
        )
        catalogRepository.save(b)
        println("ZXC = ${catalogRepository.findById(id)}")
    }
    fun addThirdLevel(
        id: String,
        title: String
    ) {
        val thirdId = UUID.randomUUID().toString()

        val third = catalogThirdRepository.save(
            CatalogThird(
                id = thirdId,
                title = title
            )
        )

        val b = catalogSecondRepository.findById(id).orElseThrow().addToThirdLevel(
            third
        )
        catalogSecondRepository.save(b)
    }



}