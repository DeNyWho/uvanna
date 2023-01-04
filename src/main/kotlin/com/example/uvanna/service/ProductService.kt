package com.example.uvanna.service

import com.example.uvanna.jpa.Characteristic
import com.example.uvanna.jpa.Product
import com.example.uvanna.model.product.Brands
import com.example.uvanna.model.request.product.ProductRequest
import com.example.uvanna.model.response.PagingResponse
import com.example.uvanna.model.response.ProductLighterResponse
import com.example.uvanna.model.response.ProductsLightResponse
import com.example.uvanna.model.response.ServiceResponse
import com.example.uvanna.repository.products.ProductsRepository
import com.example.uvanna.repository.products.ProductsRepositoryImpl
import com.example.uvanna.repository.promo.PromoRepository
import com.example.uvanna.util.CheckUtil
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
import javax.annotation.Resource


@Service
class ProductService: ProductsRepositoryImpl {

    @Autowired
    lateinit var productsRepository: ProductsRepository

    @Autowired
    private lateinit var fileService: FileService

    @Resource
    private lateinit var checkUtil: CheckUtil


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
            val check = checkUtil.checkToken(token)
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
                        id = id,
                        images = imagesUrl,
                        title = product.title,
                        updated = SimpleDateFormat("dd/M/yyyy hh:mm:ss").format(Date()).toString(),
                        characteristic = charact,
                        firstSub = product.firstSub,
                        secondSub = product.secondSub,
                        thirdSub = product.thirdSub,
                        stock = product.stock,
                        brand = product.brand,
                        sellPrice = product.sellPrice,
                        price = product.price,
                    )

                    productsRepository.deleteById(id)
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
            val check = checkUtil.checkToken(token)
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
                        firstSub = product.firstSub,
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

    override fun getProductsIds(): ServiceResponse<String> {
        return try {
            val ids = mutableListOf<String>()

            val products = productsRepository.findAll()

            products.forEach {
                ids.add(it.id)
            }

            ServiceResponse(
                data = ids,
                message = "Success",
                status = HttpStatus.OK
            )
        } catch (e: Exception) {
            ServiceResponse(
                data = listOf(),
                message = "Something went wrong... ${e.message}",
                status = HttpStatus.BAD_REQUEST
            )
        }
    }

    override fun getProductsByIds(ids: List<String>): ServiceResponse<ProductLighterResponse> {
        return try {
            val light = mutableListOf<ProductLighterResponse>()
            ids.forEach {
                val product = productsRepository.findById(it).get()
                light.add(
                    ProductLighterResponse(
                        id = product.id,
                        title = product.title,
                        imageUrls = product.images,
                        price = product.price,
                        sellPrice = product.sellPrice
                    )
                )
            }
            ServiceResponse(
                data = light,
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
                data = listOf(productsRepository.findById(id).get()),
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
        filter: String?,
        categoryId: String?,
        stockEmpty: Boolean?,
        stockFull: Boolean?,
        isSellByPromo: Boolean?,
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
            val statePage: Page<Product> = productsRepository.findAllBy(
                pageable = pageable,
                brand = brand?.brand,
                firstPrice = smallPrice,
                secondPrice = highPrice,
                stockEmpty = stockEmpty,
                stockFull = stockFull,
                categoryId = categoryId,
                isSell = isSellByPromo
            )

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
                maxPrice = statePage.content.maxOf { it.price },
                message = "Success",
                status = HttpStatus.OK
            )
        } catch (e: Exception) {
            PagingResponse(
                data = null,
                message = e.message.toString(),
                status = HttpStatus.BAD_REQUEST,
                maxPrice = null
            )
        }
    }

    override fun getProductRandom(
        countCard: Int,
        page: Int,
        filter: String?,
        productId: String?,
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
            val statePage: Page<Product> = productsRepository.findProductsByRandom(pageable, productId!!)

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
        } catch (e: Exception) {
            PagingResponse(
                data = null,
                message = e.message.toString(),
                status = HttpStatus.BAD_REQUEST
            )
        }
    }

    override fun deleteProduct(token: String, id: String): ServiceResponse<String>{
        val check = checkUtil.checkToken(token)

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

    override fun addProductStock(id: String, stock: Int, token: String): ServiceResponse<Product>? {
        return try {
            val temp = productsRepository.findById(id).get()

            println("ZXC ${temp.images}")

            val product = Product(
                id = id,
                images = temp.images,
                updated = temp.updated,
                title = temp.title,
                characteristic = temp.characteristic,
                brand = temp.brand,
                firstSub = temp.firstSub,
                secondSub = temp.secondSub,
                thirdSub = temp.thirdSub,
                price = temp.price,
                sellPrice = temp.sellPrice,
                stock = temp.stock + stock,
                percent = temp.percent
            )

            productsRepository.deleteById(id)

            productsRepository.save(product)

            ServiceResponse(
                data = listOf(productsRepository.findById(id).get()),
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

}