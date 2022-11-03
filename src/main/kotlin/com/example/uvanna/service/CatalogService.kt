package com.example.uvanna.service

import com.example.uvanna.jpa.CatalogFirst
import com.example.uvanna.repository.catalog.CatalogRepository
import com.example.uvanna.repository.catalog.CatalogRepositoryImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.util.*


@Service
class CatalogService: CatalogRepositoryImpl {

    @Autowired
    private lateinit var catalogRepository: CatalogRepository

    @Autowired
    private lateinit var fileService: FileService

    override fun addFirstLevel(file: MultipartFile, title: String, sub: List<String>): String {
        var id = UUID.randomUUID().toString()
        println("ID = $id")
        var catalog = catalogRepository.findById(id).isEmpty
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


        return "wtfzxc"
    }



}