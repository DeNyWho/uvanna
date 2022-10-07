package com.example.uvanna.service

import com.example.uvanna.model.product.Rows
import com.example.uvanna.model.product.folder.ProductFolder
import com.example.uvanna.repository.ProductsRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono

@Service
class ProductService: ProductsRepository {

    @Autowired
    lateinit var webClient: WebClient

    fun getProductFolder(): List<ProductFolder> {
        val data =  webClient
            .get()
            .uri("entity/productfolder")
            .retrieve()
            .bodyToMono<Rows>()
            .block()
        val list = mutableListOf<ProductFolder>()
        data?.productFolder?.forEach {
            list.add(
                ProductFolder(
                id = it.meta.href.drop(62),
                updated = it.updated,
                name = it.name,
                )
            )
        }
        return list.toList()
    }

}