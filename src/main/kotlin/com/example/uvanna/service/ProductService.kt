package com.example.uvanna.service

import com.example.uvanna.jpa.Characteristic
import com.example.uvanna.jpa.Product
import com.example.uvanna.model.product.ProductRequest
import com.example.uvanna.model.product.ProductsLightResponse
import com.example.uvanna.repository.products.ProductsRepository
import com.example.uvanna.repository.products.ProductsRepositoryImpl
import com.example.uvanna.util.OS.*
import com.example.uvanna.util.getOSU
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
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

    override fun getProducts(countCard: Int, page: Int, brand: String?, smallPrice: Int?, sort: String?, highPrice: Int?): List<ProductsLightResponse>{
        return sortQuery(
            page = page,
            countCard = countCard,
            brand = brand,
            smallPrice = smallPrice,
            highPrice = highPrice,
            order = sort
        )
    }

    fun sortQuery(countCard: Int, page: Int, brand: String?, smallPrice: Int?, highPrice: Int?, order: String? ): List<ProductsLightResponse> {
        val sort = when(order){
            "expensive" -> Sort.by(
                Sort.Order(Sort.Direction.DESC, "price"),
            )
            "cheap" -> Sort.by(
                Sort.Order(Sort.Direction.ASC, "price")
            )
            else -> null
        }

        val pageable: Pageable = if(sort != null ) PageRequest.of(page, countCard, sort) else PageRequest.of(page, countCard)
        val statePage: Page<Product> = if(smallPrice != null && highPrice != null) {
            if(brand != null) {
                productsRepository.findPriceBetweenAndBrand(pageable, smallPrice, highPrice, brand)
            } else {
                productsRepository.findPriceBetween(pageable, smallPrice, highPrice)
            }
        } else {
            if(brand != null) {
                productsRepository.findProductByBrand(pageable, brand)
            } else {
                productsRepository.findAll(pageable)
            }
        }

        val light = mutableListOf<ProductsLightResponse>()

        statePage.content.forEach {
            light.add(
                ProductsLightResponse(
                    id = it.id,
                    title = it.title,
                    imageUrls = it.images,
                    price = it.price,
                    stock = it.stock
                )
            )
        }

        return light
    }

    override fun deleteProduct(id: String) {
        productsRepository.deleteById(id)
    }



    override fun parser(brand: String): List<String> {
        val driver = setWebDriver("https://santehnika-online.ru/brands/$brand/")
        val urls = mutableListOf<String>()
        println(driver.pageSource)
        val tempUrls = driver.findElements(By.xpath("//*[@class=\"AeMeQv_tStmde8Cdk9QF text--sm\"]"))
        println(tempUrls.size)
        tempUrls.forEach {
            println(it)
            urls.add(it.findElement(By.tagName("a")).getAttribute("href"))
        }
        println(urls)
        return urls
    }

    fun setWebDriver(url: String): WebDriver {
        val pathDriver: String = when (getOSU()) {
            // Loaded from here https://chromedriver.storage.googleapis.com/index.html?path=101.0.4951.41/
            WINDOWS -> "_win32_101.exe"
            LINUX -> "_linux64_101"
            MAC -> "_mac64_101"
            else -> throw Exception("Unknown operating system!")
        }
        System.setProperty("webdriver.chrome.driver", "driver/chromedriver$pathDriver");
        val options = ChromeOptions()
        options.addArguments("--headless")
        val driver = ChromeDriver(options)
        try {
            driver.get(url);
        } catch (e: Exception) {
            println(e.message)

            throw Exception(e.localizedMessage)
        }
        return driver
    }


















}