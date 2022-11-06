package com.example.uvanna.service

import com.example.uvanna.jpa.Product
import com.example.uvanna.model.product.Characteristic
import com.example.uvanna.model.product.ProductRequest
import com.example.uvanna.repository.products.ProductsRepository
import com.example.uvanna.repository.products.ProductsRepositoryImpl
import it.skrape.core.document
import it.skrape.fetcher.HttpFetcher
import it.skrape.fetcher.response
import it.skrape.fetcher.skrape
import it.skrape.selects.eachHref
import it.skrape.selects.eachSrc
import it.skrape.selects.eachText
import it.skrape.selects.html5.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.multipart.MultipartFile
import java.text.SimpleDateFormat
import java.util.*


@Service
class ProductService: ProductsRepositoryImpl {

    @Autowired
    lateinit var productsRepository: ProductsRepository

    @Autowired
    private lateinit var fileService: FileService


    private var restTemplate: RestTemplate? = null

    @Autowired
    fun downloadController(builder: RestTemplateBuilder) {
        this.restTemplate = builder.build()
    }

    private var pagesBoolean = false

    override fun addProduct(product: ProductRequest, files: List<MultipartFile>) {
        val charact = mutableListOf<Characteristic>()
        product.characteristic.forEach {
            charact.add(
                Characteristic(title = it.title, data = it.data)
            )
        }
        val item = Product(
            title = product.title,
            characteristic = charact,
            firstSub = product.firstSub,
            secondSub = product.secondSub,
            thirdSub = product.thirdSub,
            stock = product.stock,
            price = product.price,
        )
        item.id = UUID.randomUUID().toString()
        val imagesUrl = mutableListOf<String>()
        files.forEach {
            imagesUrl.add(fileService.save(it))
        }
        item.updated = SimpleDateFormat("dd/M/yyyy hh:mm:ss").format(Date()).toString()
        item.images = imagesUrl

        productsRepository.save(item)
    }

    override fun deleteProduct(id: String) {
        productsRepository.deleteById(id)
    }


    override fun parser(brand: String){
        val urls = mutableListOf<String>()

        while (!pagesBoolean) {
            try {
                skrape(HttpFetcher) {
                    request {
                        url = "https://santehnika-online.ru/brands/1marka/"
                        timeout = 400_000
                    }
                    response {
                        document.a {
                            withClass = "AeMeQv_tStmde8Cdk9QF.text--sm"
                            urls.addAll(findAll { return@findAll eachHref })
                        }
                    }
                }
                pagesBoolean = true
            } catch (e: Exception) {
                print(e.message)
                pagesBoolean = false
            }
        }
        println(urls)

        urls.forEach {
            val imagesLink = mutableListOf<String>()
            val images = mutableListOf<String>()
            var price = 0
            val stock = 0
            var title = ""
            val characteristic = mutableListOf<Characteristic>()
            var firstLevel = ""
            var secondLevel = ""
            var thirdLevel = ""
            val titleAbout = mutableListOf<String>()
            val valueAbout = mutableListOf<String>()

            skrape(HttpFetcher) {
                request {
                    url = it
                }
                response {
                    document.a {
                        withClass = "BkQv6L988JGqsYjSgwN8"
                        imagesLink.addAll(findAll { return@findAll eachSrc })
                    }
                    document.span {
                        withClass = "b-price__price-core"
                        price = findFirst { return@findFirst text }.toInt()
                    }
                    document.h1 {
                        withClass = "tCkWijBo2HB3Eq5mlQJJ"
                        title = findFirst { return@findFirst text }
                    }
                    document.div {
                        withClass = "Z988dsoYKeTynCoRKNbj"
                        titleAbout.addAll(findAll { return@findAll eachText })
                    }
                    document.div {
                        withClass = "NKU9hzVbfqHy3RwbwjJA"
                        valueAbout.addAll(findAll { return@findAll eachText })
                    }
                    document.li {
                        withClass = "jmvOd90U5JV_J_6Gklbq"
                        val listing = findAll{ eachText }
                        secondLevel = listing[2]
                        thirdLevel = listing[3]
                    }
                }
            }

            imagesLink.forEach {
                val imageBytes: ByteArray = restTemplate!!.getForObject(it, ByteArray::class.java)!!
                images.add(fileService.saveBytes(imageBytes))
            }

            titleAbout.forEachIndexed { index, s ->
                characteristic.add(Characteristic(title = titleAbout[index], data = valueAbout[index]))
            }


            productsRepository.save(
                Product(
                    images = images,
                    updated = SimpleDateFormat("dd/M/yyyy hh:mm:ss").format(Date()).toString(),
                    title = title,
                    characteristic = characteristic,
                    brand = brand,
                    firstSub = firstLevel,
                    secondSub = secondLevel,
                    thirdSub = thirdLevel,
                    price = price,
                    stock = stock
                )
            )

        }
    }


}