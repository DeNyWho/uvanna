package com.example.uvanna.service

import com.example.uvanna.controller.products.ProductsController
import com.example.uvanna.jpa.Product
import com.example.uvanna.jpa.Characteristic
import com.example.uvanna.model.product.ProductRequest
import com.example.uvanna.repository.products.CharacteristicRepository
import com.example.uvanna.repository.products.ProductsRepository
import com.example.uvanna.repository.products.ProductsRepositoryImpl
import it.skrape.core.document
import it.skrape.fetcher.HttpFetcher
import it.skrape.fetcher.response
import it.skrape.fetcher.skrape
import it.skrape.selects.eachHref
import it.skrape.selects.html5.a
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.multipart.MultipartFile
import java.text.SimpleDateFormat
import java.util.*


@Service
class ProductService: ProductsRepositoryImpl {

    private val logger = LoggerFactory.getLogger(ProductsController::class.java)

    @Autowired
    lateinit var productsRepository: ProductsRepository

    @Autowired
    lateinit var characteristicRepository: CharacteristicRepository

    @Autowired
    private lateinit var fileService: FileService


    private var restTemplate: RestTemplate? = null

    @Autowired
    fun downloadController(builder: RestTemplateBuilder) {
        this.restTemplate = builder.build()
    }

    private var pagesBoolean = false

    override fun addProduct(
        product: ProductRequest,
        files: List<MultipartFile>,
        characteristic: List<String>,
        data: List<String>
    ) {
        val charact = mutableListOf<Characteristic>()
        characteristic.forEachIndexed { index, s ->
            charact.add(Characteristic(id = UUID.randomUUID().toString(), title = characteristic[index], data = data[index] ))
        }
        val imagesUrl = mutableListOf<String>()
        files.forEach {
            imagesUrl.add(fileService.save(it))
        }

        val item = Product(
            id = UUID.randomUUID().toString(),
            images = imagesUrl,
            title = product.title,
            updated = SimpleDateFormat("dd/M/yyyy hh:mm:ss").format(Date()).toString(),
            characteristic = charact,
            secondSub = product.secondSub,
            thirdSub = product.thirdSub,
            stock = product.stock,
            brand = product.brand,
            price = product.price,
        )


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
                        url = "https://santehnika-online.ru/brands/$brand/"
                        timeout = 400_000
                    }
                    response {
                        println(this.document)
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
//        println(urls)

//        urls.forEach {
//            val imagesLink = mutableListOf<String>()
//            val images = mutableListOf<String>()
//            var price = 0
//            val stock = 0
//            var title = ""
//            val characteristic = mutableListOf<Characteristic>()
//            var secondLevel = ""
//            var thirdLevel = ""
//            val titleAbout = mutableListOf<String>()
//            val valueAbout = mutableListOf<String>()
//
//            while (!pagesBoolean) {
//                try {
//                    skrape(HttpFetcher) {
//                        request {
//                            url = it
//                        }
//                        response {
//                            document.a {
//                                withClass = "BkQv6L988JGqsYjSgwN8"
//                                imagesLink.addAll(findAll { return@findAll eachSrc })
//                            }
//                            document.span {
//                                withClass = "b-price__price-core"
//                                price = findFirst { return@findFirst text }.toInt()
//                                logger.info(price.toString())
//                            }
//                            document.h1 {
//                                withClass = "tCkWijBo2HB3Eq5mlQJJ"
//                                title = findFirst { return@findFirst text }
//                            }
//                            document.div {
//                                withClass = "Z988dsoYKeTynCoRKNbj"
//                                titleAbout.addAll(findAll { return@findAll eachText })
//                            }
//                            document.div {
//                                withClass = "NKU9hzVbfqHy3RwbwjJA"
//                                valueAbout.addAll(findAll { return@findAll eachText })
//                            }
//                            document.li {
//                                withClass = "jmvOd90U5JV_J_6Gklbq"
//                                val listing = findAll{ return@findAll eachText }
//                                logger.info("ZXC LIST = $listing")
//                                secondLevel = listing[2]
//                                thirdLevel = listing[3]
//                            }
//                        }
//                    }
//                    pagesBoolean = true
//                } catch (e: Exception) {
//                    print(e.message)
//                    pagesBoolean = false
//                }
//            }
//
//            imagesLink.forEach {
//                val imageBytes: ByteArray = restTemplate!!.getForObject(it, ByteArray::class.java)!!
//                images.add(fileService.saveBytes(imageBytes))
//            }
//
//            titleAbout.forEachIndexed { index, s ->
//                characteristic.add(Characteristic(title = titleAbout[index], data = valueAbout[index]))
//            }
//            logger.info("ZXC = ${
//                Product(
//                    images = images,
//                    updated = SimpleDateFormat("dd/M/yyyy hh:mm:ss").format(Date()).toString(),
//                    title = title,
//                    characteristic = characteristic,
//                    brand = brand,
//                    secondSub = secondLevel,
//                    thirdSub = thirdLevel,
//                    price = price,
//                    stock = stock
//                )
//            }")
//
//
//            productsRepository.save(
//                Product(
//                    images = images,
//                    updated = SimpleDateFormat("dd/M/yyyy hh:mm:ss").format(Date()).toString(),
//                    title = title,
//                    characteristic = characteristic,
//                    brand = brand,
//                    secondSub = secondLevel,
//                    thirdSub = thirdLevel,
//                    price = price,
//                    stock = stock
//                )
//            )
//
//        }
    }


}