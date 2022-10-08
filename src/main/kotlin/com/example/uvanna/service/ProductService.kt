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
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import javax.validation.constraints.Max
import javax.validation.constraints.Min


@Service
class ProductService: ProductsRepository {

    @Autowired
    lateinit var webClient: WebClient

    @Autowired
    lateinit var getRedirectLink: String

    @Value("\${token}")
    lateinit var token: String

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

    fun getProductsByFolder(
        id: String,
        pageNum: Int,
        pageSize: Int
    ): List<Product> {
        val data = webClient
            .get()
            .uri("$ProductByGroup$id&limit=$pageSize&offset=$pageNum")
            .retrieve()
            .bodyToMono<Rows<ProductDto>>()
            .block()

        val list = mutableListOf<Product>()
        data?.rows?.forEach {
            var images = "Empty"
            if(getImageFromDownloadLink(it.imagesDto.meta.href).drop(41).length > 8) {
                webClient
                    .get()
                    .uri(getImageFromDownloadLink(it.imagesDto.meta.href).drop(41))
                    .exchange()
                    .map {
                        images = it.headers().header("Location")[0]
                    }
                    .block()
            }
            list.add(
                Product(
                    image = images,
                    updated = it.updated,
                    name = it.name,
                    group = it.group,
                    minPrice = when (it.minPrice.value.length) {
                        3 -> it.minPrice.value.dropLast(2)
                        4 -> it.minPrice.value.dropLast(3)
                        else -> it.minPrice.value.dropLast(4)
                    }.toInt(),
                    salePrice = when (it.salePrices[0].value.length) {
                        3 -> it.salePrices[0].value.dropLast(2)
                        4 -> it.salePrices[0].value.dropLast(3)
                        else -> it.salePrices[0].value.dropLast(4)
                    }.toInt(),
                    buyPrice = when (it.buyPrice.value.length) {
                        3 -> it.buyPrice.value.dropLast(2)
                        4 -> it.buyPrice.value.dropLast(3)
                        else -> it.buyPrice.value.dropLast(4)
                    }.toInt(),
                    stock = it.stock.dropLast(2).toInt(),
                )
            )
        }

        return list.toList()
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