package com.example.uvanna.service

import com.example.uvanna.jpa.Product
import com.example.uvanna.repository.image.ImageRepository
import com.example.uvanna.repository.products.ProductsRepository
import com.example.uvanna.repository.products.ProductsRepositoryImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


@Service
class ProductService: ProductsRepositoryImpl {

    @Autowired
    lateinit var productsRepository: ProductsRepository

    @Autowired
    lateinit var imageRepository: ImageRepository

    override fun addProduct(): Product {

        val product = Product(
        )

        return product
    }


    override fun parser(brand: String){
        val urls = mutableListOf<String>()



//        skrape(HttpFetcher) {
//            request {
//                url = "$parserSuiteBrands$brand/"
//            }
//            response {
//                document.a {
//                    withClass = "ok_TLOmB1GkfFNQ4v5vr.noL7tyaY_1TGqOqzUFmj"
//                    urls.addAll(findAll { return@findAll eachHref })
//                }
//            }
//        }
//
//        urls.forEach {
//            val imagesLink = mutableListOf<String>()
//            val images = mutableListOf<Image>()
//            var price = 0
//            var title = ""
//            var firstLevel = ""
//            var secondLevel = ""
//            var thirdLevel = ""
//            var titleAbout = mutableListOf<String>()
//            var valueAbout = mutableListOf<String>()
//
//            skrape(HttpFetcher) {
//                request {
//                    url = it
//                }
//                response {
//                    document.a {
//                        withClass = "BkQv6L988JGqsYjSgwN8"
//                        imagesLink.addAll(findAll { return@findAll eachSrc })
//                    }
//                    document.span {
//                        withClass = "b-price__price-core"
//                        price = findFirst { return@findFirst text }.toInt()
//                    }
//                    document.h1 {
//                        withClass = "tCkWijBo2HB3Eq5mlQJJ"
//                        title = findFirst { return@findFirst text }
//                    }
//                    document.div {
//                        withClass = "Z988dsoYKeTynCoRKNbj"
//                        titleAbout.addAll(findAll { return@findAll eachText })
//                    }
//                    document.div {
//                        withClass = "NKU9hzVbfqHy3RwbwjJA"
//                        valueAbout.addAll(findAll { return@findAll eachText })
//                    }
//                }
//            }
//
//            imagesLink.forEach {
//                val file = webClient
//                    .get()
//                    .uri(it)
//                    .accept(MediaType.IMAGE_PNG)
//                    .accept(MediaType.IMAGE_JPEG)
//                    .accept(MediaType.IMAGE_GIF)
//                    .retrieve()
//                    .bodyToMono(ByteArray::class.java)
//                    .block()
//                var imageId = UUID.randomUUID().toString()
//                var present = imagesRepository.findById(imageId).isPresent
//                while(present){
//                    imageId = UUID.randomUUID().toString()
//                    present = imagesRepository.findById(imageId).isPresent
//                }
//                images.add(Image(id = imageId, image = file))
//            }
//
//
//        }
    }


}