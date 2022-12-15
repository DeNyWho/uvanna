package com.example.uvanna.service

import com.example.uvanna.jpa.Characteristic
import com.example.uvanna.jpa.Product
import com.example.uvanna.model.product.Brands
import com.example.uvanna.model.request.product.ProductRequest
import com.example.uvanna.model.response.ProductsLightResponse
import com.example.uvanna.model.response.PagingResponse
import com.example.uvanna.model.response.ProductLighterResponse
import com.example.uvanna.model.response.ServiceResponse
import com.example.uvanna.repository.admin.AdminRepository
import com.example.uvanna.repository.products.ProductsRepository
import com.example.uvanna.repository.products.ProductsRepositoryImpl
import com.example.uvanna.util.OS.*
import com.example.uvanna.util.getOSU
import org.openqa.selenium.By
import org.openqa.selenium.Proxy
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.multipart.MultipartFile
import java.text.SimpleDateFormat
import java.util.*
import javax.validation.constraints.Max
import javax.validation.constraints.Min


@Service
class ProductService: ProductsRepositoryImpl {

    @Autowired
    lateinit var adminRepository: AdminRepository

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


    override fun editProduct(
        id: String,
        characteristic: List<String>,
        data: List<String>,
        files: List<MultipartFile>,
        token: String,
        product: ProductRequest
    ): ServiceResponse<Product>? {
        return try {
            val check = checkToken(token)
            return if(check) {
                return try {
                    val charact = mutableListOf<Characteristic>()
                    characteristic.forEachIndexed { index, s ->
                        charact.add(
                            Characteristic(
                                id = UUID.randomUUID().toString(),
                                title = characteristic[index],
                                data = data[index]
                            )
                        )
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
                    ServiceResponse(
                        data = listOf(productsRepository.findById(item.id).get()),
                        message = "Product has been edited",
                        status = HttpStatus.OK
                    )
                } catch (e: Exception) {
                    ServiceResponse(
                        data = null,
                        message = "Something went wrong: ${e.message}",
                        status = HttpStatus.BAD_REQUEST
                    )
                }
            } else {
                ServiceResponse(
                    data = null,
                    message = "Unexpected token",
                    status = HttpStatus.UNAUTHORIZED
                )
            }
        } catch (e: Exception){
            ServiceResponse(
                data = null,
                message = "Something went wrong: ${e.message}",
                status = HttpStatus.BAD_REQUEST
            )
        }
    }


    override fun addProduct(
        product: ProductRequest,
        files: List<MultipartFile>,
        characteristic: List<String>,
        token: String,
        data: List<String>
    ): ServiceResponse<Product>? {
        return try {
            val check = checkToken(token)
            return if(check) {
                return try {
                    val charact = mutableListOf<Characteristic>()
                    characteristic.forEachIndexed { index, s ->
                        charact.add(
                            Characteristic(
                                id = UUID.randomUUID().toString(),
                                title = characteristic[index],
                                data = data[index]
                            )
                        )
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
                        sellPrice = null
                    )

                    productsRepository.save(item)
                    ServiceResponse(
                        data = listOf(productsRepository.findById(item.id).get()),
                        message = "Product has been created",
                        status = HttpStatus.OK
                    )
                } catch (e: Exception) {
                    ServiceResponse(
                        data = null,
                        message = "Something went wrong: ${e.message}",
                        status = HttpStatus.BAD_REQUEST
                    )
                }
            } else {
                ServiceResponse(
                    data = null,
                    message = "Unexpected token",
                    status = HttpStatus.UNAUTHORIZED
                )
            }

        } catch (e: Exception){
            ServiceResponse(
                data = null,
                message = "Something went wrong: ${e.message}",
                status = HttpStatus.BAD_REQUEST
            )
        }
    }

    override fun getBrands(id: String): ServiceResponse<String>? {
        return try {
            ServiceResponse(
                data = productsRepository.findBrands(id),
                message = "Success",
                status = HttpStatus.OK
            )
        } catch (e: Exception){
            ServiceResponse(
                data = null,
                message = e.message.toString(),
                status = HttpStatus.BAD_REQUEST
            )
        }
    }

    override fun getProduct(id: String): ServiceResponse<Product>? {
        return try {
            ServiceResponse(
                data = listOf(productsRepository.getById(id)),
                message = "Success",
                status = HttpStatus.OK
            )
        } catch (e: Exception){
            ServiceResponse(
                data = null,
                message = e.message.toString(),
                status = HttpStatus.BAD_REQUEST
            )
        }
    }

    override fun findProduct(
        searchQuery: String,
        pageNum: Int,
        pageSize: Int
    ): ServiceResponse<ProductLighterResponse>? {
        return try {
            val light = mutableListOf<ProductLighterResponse>()
            val pageable: Pageable =PageRequest.of(pageNum, pageSize)

            productsRepository.findByTitleSearch(pageable, searchQuery).forEach {
                light.add(
                    ProductLighterResponse(
                        id = it.id,
                        title = it.title,
                        imageUrls = it.images,
                        price = it.price,
                        sellPrice = it.sellPrice
                    )
                )
            }
            ServiceResponse(
                data = light,
                message = "Success",
                status = HttpStatus.OK
            )
        } catch (e: Exception) {
            ServiceResponse(
                data = null,
                message = e.message.toString(),
                status = HttpStatus.BAD_REQUEST
            )
        }
    }

    override fun getCharactSort(level: String): List<Characteristic> {
        return productsRepository.getThirdLevelSort(level)
    }

    override fun getProducts(
        countCard: Int,
        page: Int,
        brand: Brands?,
        smallPrice: Int?,
        highPrice: Int?,
        order: String?,
        filter: String?,
        level: String?,
        categoryId: String?
    ): PagingResponse<ProductsLightResponse>? {
        return try {
            val sort = when (filter) {
                "expensive" -> Sort.by(
                    Sort.Order(Sort.Direction.DESC, "price"),
                )
                "cheap" -> Sort.by(
                    Sort.Order(Sort.Direction.ASC, "price")
                )
                "new" -> Sort.by(
                    Sort.Order(Sort.Direction.DESC, "updated")
                )
                else -> null
            }

            val pageable: Pageable =
                if (sort != null) PageRequest.of(page, countCard, sort) else PageRequest.of(page, countCard)
            val statePage: Page<Product> = if (smallPrice != null && highPrice != null) {
                when (order) {
                    "brand" -> productsRepository.findBrand(pageable, brand!!.brand)
//                    "brand" -> productsRepository.findPriceBetweenAndBrand(pageable, smallPrice, highPrice, brand!!.brand)
//                "characteristic" -> productsRepository.findProductByCharacteristic(pageable, characteristic!!, level!!)
                    "stockEmpty" -> productsRepository.findProductEmptyStockWithBetweenPrice(pageable, smallPrice, highPrice)
                    "stockFull" -> productsRepository.findProductFullStockWithBetweenPrice(pageable, smallPrice, highPrice)
                    else -> productsRepository.findPriceBetween(pageable, smallPrice, highPrice)
                }
                if (categoryId != null) {
                    productsRepository.findProductWithCategoryIdWithBetweenPrice(pageable, categoryId, smallPrice, highPrice)
                } else productsRepository.findPriceBetween(pageable, smallPrice, highPrice)
            } else {
                when (order) {
                    "brand" -> productsRepository.findProductByBrand(pageable, brand!!.brand)
//                "characteristic" -> productsRepository.findProductByCharacteristic(pageable, characteristic!!, level!!)
                    "stockEmpty" -> productsRepository.findProductEmptyStock(pageable)
                    "stockFull" -> productsRepository.findProductFullStock(pageable)
                    else -> productsRepository.findAll(pageable)
                }
                if (categoryId != null) {
                    productsRepository.findProductWithCategoryId(pageable, categoryId)
                } else productsRepository.findAll(pageable)
            }

            val light = mutableListOf<ProductsLightResponse>()

            statePage.content.forEach {
                light.add(
                    ProductsLightResponse(
                        id = it.id,
                        title = it.title,
                        imageUrls = it.images,
                        price = it.price,
                        stock = it.stock,
                        sellPrice = it.sellPrice
                    )
                )
            }
            PagingResponse(
                data = light,
                totalElements = statePage.totalElements,
                totalPages = statePage.totalPages,
                message = "Success",
                status = HttpStatus.OK
            )
        } catch (e: Exception){
            PagingResponse(
                data = null,
                message = e.message.toString(),
                status = HttpStatus.BAD_REQUEST
            )
        }
    }

    override fun deleteProduct(token: String, id: String): ServiceResponse<String>{
        val check = checkToken(token)
        return if(check) {
            return try {
                productsRepository.deleteById(id)
                ServiceResponse(
                    data = listOf(),
                    message = "Product with id = $id has been deleted",
                    status = HttpStatus.OK
                )
            } catch (e: Exception) {
                ServiceResponse(
                    data = listOf(),
                    message = "Product with id = $id not found",
                    status = HttpStatus.NOT_FOUND
                )
            }
        } else {
            ServiceResponse(
                data = null,
                message = "Unexpected token",
                status = HttpStatus.UNAUTHORIZED
            )
        }
    }

    fun checkToken(token: String): Boolean {
        val token = adminRepository.findAdminTokenByToken(token)

        return token != null
    }

}