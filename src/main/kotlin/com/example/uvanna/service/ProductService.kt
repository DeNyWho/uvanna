package com.example.uvanna.service

import com.example.uvanna.jpa.Image
import com.example.uvanna.jpa.Product
import com.example.uvanna.model.common.Rows
import com.example.uvanna.model.common.images.Images
import com.example.uvanna.model.common.images.ImagesMeta
import com.example.uvanna.repository.ProductsRepository
import com.example.uvanna.repository.ProductsRepositoryImpl
import com.example.uvanna.util.Constants.parserSuite
import it.skrape.core.document
import it.skrape.fetcher.HttpFetcher
import it.skrape.fetcher.response
import it.skrape.fetcher.skrape
import it.skrape.selects.eachHref
import it.skrape.selects.eachText
import it.skrape.selects.html5.a
import it.skrape.selects.html5.div
import it.skrape.selects.html5.span
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono


@Service
class ProductService: ProductsRepositoryImpl {

    @Autowired
    lateinit var productsRepository: ProductsRepository

    @Autowired
    lateinit var webClient: WebClient

    override fun addProduct(): Product {

    }

    fun addImage(id: Long,file: MultipartFile){
        val product = productsRepository.findById(id).orElseThrow()
        val imageTemp = mutableListOf<Image>()
        imageTemp.addAll(product.images)
        imageTemp.add(Image(image = file.bytes))
        product.images = imageTemp.toList()
        productsRepository.save(product)
    }

//    override fun getProduct(id: String): List<Product> {
//
//        val data = webClient
//            .get()
//            .uri(Constants.ProductAssortment + id)
//            .retrieve()
//            .bodyToMono<Rows<ProductDto>>()
//            .block()
//
//        val list = mutableListOf<Product>()
//        data?.rows?.forEach {
//            var images = mutableListOf<String>()
//            getImageFromDownloadLink(it.imagesDto.meta.href)?.forEach {
//                webClient
//                    .get()
//                    .uri(it.meta.toString().drop(65) )
//                    .exchange()
//                    .map {
//                        images.addAll(it.headers().header("Location") )
//                    }
//                    .block()
//            }
//            list.add(
//                Product(
//                    id = it.id,
//                    image = images,
//                    updated = it.updated,
//                    name = it.name,
//                    group = it.group,
//                    minPrice = try {
//                        when (it.minPrice.value.length) {
//                            3 -> it.minPrice.value.dropLast(2)
//                            4 -> it.minPrice.value.dropLast(3)
//                            else -> it.minPrice.value.dropLast(4)
//                        }.toInt()
//                    } catch (e: Exception) {
//                        0
//                    },
//                    salePrice = try {
//                        when (it.salePrices[0].value.length) {
//                            3 -> it.salePrices[0].value.dropLast(2)
//                            4 -> it.salePrices[0].value.dropLast(3)
//                            else -> it.salePrices[0].value.dropLast(4)
//                        }.toInt()
//                    } catch (e: Exception) {
//                        0
//                    },
//                    buyPrice = try {
//                        when (it.buyPrice.value.length) {
//                            3 -> it.buyPrice.value.dropLast(2)
//                            4 -> it.buyPrice.value.dropLast(3)
//                            else -> it.buyPrice.value.dropLast(4)
//                        }.toInt()
//                    } catch (e: Exception) {
//                        0
//                    },
//                    stock = it.stock.dropLast(2).toInt(),
//                )
//            )
//        }
//
//
//        return list.toList()
//    }
//
//
//    override fun getProductFolder(): List<ProductFolder> {
//        val data =  webClient
//            .get()
//            .uri(Groups)
//            .retrieve()
//            .bodyToMono<Rows<ProductFolderDto>>()
//            .block()
//        val list = mutableListOf<ProductFolder>()
//        data?.rows?.forEach {
//            var stock = 0
//            val temp = webClient
//                .get()
//                .uri("$ProductByGroup${it.meta.href.drop(62)}")
//                .retrieve()
//                .bodyToMono<Rows<ProductDto>>()
//                .block()
//
//            temp?.rows?.forEach {
//                if(it.stock.dropLast(2).toInt() > 0){
//                    stock += it.stock.dropLast(2).toInt()
//                }
//            }
//
//            list.add(
//                ProductFolder(
//                    id = it.meta.href.drop(62),
//                    updated = it.updated,
//                    name = it.name,
//                    stock = stock
//                )
//            )
//        }
//        return list.toList()
//    }
//
//    override fun getProductsByFolder(
//        id: String,
//        pageNum: Int,
//        pageSize: Int
//    ): List<Product> {
//        val data = webClient
//            .get()
//            .uri("$ProductByGroup$id&limit=$pageSize&offset=$pageNum")
//            .retrieve()
//            .bodyToMono<Rows<ProductDto>>()
//            .block()
//
//        val list = mutableListOf<Product>()
//        data?.rows?.forEach {
//            var images = mutableListOf<String>()
//            getImageFromDownloadLink(it.imagesDto.meta.href)?.forEach {
//                webClient
//                    .get()
//                    .uri(it.meta.toString().drop(65) )
//                    .exchange()
//                    .map {
//                        images.addAll(it.headers().header("Location") )
//                    }
//                    .block()
//            }
//            list.add(
//                Product(
//                    id = it.id,
//                    image = images.take(3),
//                    updated = it.updated,
//                    name = it.name,
//                    group = it.group,
//                    minPrice = try {
//                        when (it.minPrice.value.length) {
//                            3 -> it.minPrice.value.dropLast(2)
//                            4 -> it.minPrice.value.dropLast(3)
//                            else -> it.minPrice.value.dropLast(4)
//                        }.toInt()
//                    } catch (e: Exception) {
//                        0
//                    },
//                    salePrice = try {
//                        when (it.salePrices[0].value.length) {
//                            3 -> it.salePrices[0].value.dropLast(2)
//                            4 -> it.salePrices[0].value.dropLast(3)
//                            else -> it.salePrices[0].value.dropLast(4)
//                        }.toInt()
//                    } catch (e: Exception) {
//                        0
//                    },
//                    buyPrice = try {
//                        when (it.buyPrice.value.length) {
//                            3 -> it.buyPrice.value.dropLast(2)
//                            4 -> it.buyPrice.value.dropLast(3)
//                            else -> it.buyPrice.value.dropLast(4)
//                        }.toInt()
//                    } catch (e: Exception) {
//                        0
//                    },
//                    stock = it.stock.dropLast(2).toInt(),
//                )
//            )
//        }
//
//        return list.toList()
//    }

    override fun parser(brand: String){
        var maxId = 0
        var pageSize = 0
        val urls = mutableListOf<String>()
        val images = mutableListOf<String>()
        val titles = mutableListOf<String>()

        skrape(HttpFetcher) {
            request {
                url = "$parserSuite$brand/"
            }
            response {
                document.a {
                    withClass = "img.pos_rel"
                    urls.addAll(findAll { return@findAll eachHref }.map { "${parserSuite.dropLast(1)}$it" })
                }
            }
        }
        var pagesBoolean: Boolean

        for (i in 0 until 1) {
            val link = urls[i]
            var price = 0
            var title = ""
            val characteristicNaming = mutableListOf<String>()
            val characteristicValue = mutableListOf<String>()
            println(link)
            pagesBoolean = false
            while (!pagesBoolean) {
                try {
                    skrape(HttpFetcher) {
                        request {
                            url = link
                            timeout = 400_000
                        }
                        response {
                            document.div {
                                withClass = "price"
                                price = findFirst { return@findFirst text }.dropLast(3).replace(" ", "").toInt()
                            }
                            document.div {
                                withClass = "des.des1"
                                val temp = findFirst { return@findFirst text }
                                val from = temp.indexOf(".")
                                title = temp.substring(0, from)
                            }
                            document.span {
                                withClass = "left"
                                characteristicNaming.addAll(findAll { return@findAll eachText })
                            }
                            document.div {
                                withClass = "val"
                                characteristicValue.addAll(findAll { return@findAll eachText })
                            }
                        }
                    }

                    println(characteristicNaming)
                    println(characteristicNaming.distinct().toList().find { it == "Производитель:" })
                    println(characteristicNaming.toList().filter {  it == "Производитель:" })
                    println(characteristicNaming.distinct().toList().indexOf("Коллекция:"))
                    println(characteristicNaming.distinct().toList().indexOf("Артикул:"))
                    println(characteristicNaming.distinct().toList().indexOf("Цвет:"))
                    println(characteristicNaming.distinct().toList().indexOf("Материал:"))
                    println(characteristicNaming.distinct().toList().indexOf("Ширина:"))
                    println(characteristicNaming.distinct().toList().indexOf("Высота:"))
                    println(characteristicNaming.distinct().toList().indexOf("Длина:"))
                    println(characteristicNaming.distinct().toList().indexOf("Глубина:"))
                    println(characteristicNaming.distinct().toList().indexOf("Объем:"))
                    println(characteristicNaming.distinct().toList().indexOf("Вес:"))
                    println(characteristicNaming.distinct().toList().indexOf("Гарантийный срок:"))



                    
                    pagesBoolean = true
                } catch (e: Exception) {
                    println(e.message)
                    pagesBoolean = false
                }
            }
        }
    }



    fun getImageFromDownloadLink(link: String): List<Images>? {
        val image = webClient
            .get()
            .uri(link.drop(41))
            .retrieve()
            .bodyToMono<Rows<Images>>()
            .block()
        if(image?.rows?.size!! < 1){
            image.rows = listOf(Images(meta = ImagesMeta("Empty")))
        }
        return image.rows
    }

}