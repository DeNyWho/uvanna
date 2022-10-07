package com.example.uvanna.service

import com.example.uvanna.model.common.Rows
import com.example.uvanna.model.common.images.Images
import com.example.uvanna.model.common.images.ImagesMeta
import com.example.uvanna.model.product.Product
import com.example.uvanna.model.product.ProductDto
import com.example.uvanna.model.product.folder.ProductFolder
import com.example.uvanna.model.product.folder.ProductFolderDto
import com.example.uvanna.repository.ProductsRepository
import com.example.uvanna.util.Constants.Groups
import com.example.uvanna.util.Constants.ProductByGroup
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToFlux
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Flux
import java.nio.file.Files
import java.nio.file.Paths

@Service
class ProductService: ProductsRepository {

    @Autowired
    lateinit var webClient: WebClient

    fun getProductFolder(): List<ProductFolder> {
        val data =  webClient
            .get()
            .uri(Groups)
            .retrieve()
            .bodyToMono<Rows<ProductFolderDto>>()
            .block()
        val list = mutableListOf<ProductFolder>()
        data?.rows?.forEach {
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

    fun getProductsByFolder(id: String): List<Product>{
        val data = webClient
            .get()
            .uri("$ProductByGroup$id")
            .retrieve()
            .bodyToMono<Rows<ProductDto>>()
            .block()

        val list = mutableListOf<Product>()
        data?.rows?.forEach {
            list.add(
                Product(
                    image = getImageFromDownloadLink(it.imagesDto.meta.href),
                    updated = it.updated,
                    name = it.name,
                    group = it.group,
                    minPrice = when(it.minPrice.value.length){
                        3 -> it.minPrice.value.dropLast(2)
                        4 -> it.minPrice.value.dropLast(3)
                        else -> it.minPrice.value.dropLast(4) }.toInt(),
                    salePrice = when(it.salePrices[0].value.length){
                        3 -> it.salePrices[0].value.dropLast(2)
                        4 -> it.salePrices[0].value.dropLast(3)
                        else -> it.salePrices[0].value.dropLast(4) }.toInt(),
                    buyPrice = when(it.buyPrice.value.length){
                        3 -> it.buyPrice.value.dropLast(2)
                        4 -> it.buyPrice.value.dropLast(3)
                        else -> it.buyPrice.value.dropLast(4) }.toInt(),
                    stock = it.stock.dropLast(2).toInt(),
                )
            )
        }
        list.forEach {
            val link = it.image
            val file = webClient.get()
                .uri(link)
                .retrieve()
                .bodyToFlux<DataBuffer>()
            store(file)
        }

        return list.toList()
    }

    fun store(file: Flux<DataBuffer>) {
        val root = Paths.get("images")
//        Files.copy()
//        Files.copy(file.inputStream, root.resolve(file.originalFilename))
    }

    fun getImageFromDownloadLink(link: String): String {
        val image = webClient
            .get()
            .uri(link.drop(41))
            .retrieve()
            .bodyToMono<Rows<Images>>()
            .block()
        if(image?.rows?.size!! < 1){
            image.rows = listOf(Images(meta = ImagesMeta("Empty")))
        }
        return image.rows?.get(0)?.meta?.downloadHref ?: ""
    }

}