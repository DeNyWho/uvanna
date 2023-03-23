package com.example.uvanna.service

import com.example.uvanna.jpa.Characteristic
import com.example.uvanna.jpa.Product
import com.example.uvanna.jpa.ProductBrands
import com.example.uvanna.jpa.TemplateCharact
import com.example.uvanna.model.PercentageList
import com.example.uvanna.model.product.Brands
import com.example.uvanna.model.product.Charss
import com.example.uvanna.model.product.Filters
import com.example.uvanna.model.request.product.ProductRequest
import com.example.uvanna.model.response.PagingResponse
import com.example.uvanna.model.response.ProductLighterResponse
import com.example.uvanna.model.response.ProductsLightResponse
import com.example.uvanna.model.response.ServiceResponse
import com.example.uvanna.repository.products.BrandsRepository
import com.example.uvanna.repository.products.ProductsRepository
import com.example.uvanna.repository.products.ProductsRepositoryImpl
import com.example.uvanna.repository.products.TemplateCharactRepository
import com.example.uvanna.util.CheckUtil
import com.example.uvanna.util.toPage
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
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import javax.annotation.Resource


@Service
class ProductService: ProductsRepositoryImpl {

    @Autowired
    lateinit var templateCharactRepository: TemplateCharactRepository

    @Autowired
    lateinit var productsRepository: ProductsRepository

    @Autowired
    lateinit var brandsRepository: BrandsRepository

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

                    val v = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
                    val z = SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(
                        Date.from(
                            Date().toInstant().atZone(
                                ZoneId.of("Europe/Moscow")
                            ).toInstant()
                        )
                    )

                    val item = Product(
                        id = id,
                        images = imagesUrl,
                        title = product.title,
                        updated = LocalDateTime.parse(z, v ),
                        characteristic = charact,
                        firstSub = product.firstSub,
                        secondSub = product.secondSub,
                        thirdSub = product.thirdSub,
                        stock = product.stock,
                        brand = product.brand,
                        sellPrice = product.sellPrice,
                        price = product.price,
                        archive = product.archive
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

                    val v = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
                    val z = SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(
                        Date.from(
                            Date().toInstant().atZone(
                                ZoneId.of("Europe/Moscow")
                            ).toInstant()
                        )
                    )

                    val item = Product(
                        id = UUID.randomUUID().toString(),
                        images = imagesUrl,
                        title = product.title,
                        updated = LocalDateTime.parse(z, v ),
                        characteristic = charact,
                        firstSub = product.firstSub,
                        secondSub = product.secondSub,
                        thirdSub = product.thirdSub,
                        stock = product.stock,
                        brand = product.brand,
                        price = product.price,
                        sellPrice = null,
                        archive = product.archive
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

    override fun deleteBrandById(id: String, token: String): ServiceResponse<String> {
        val check = checkUtil.checkToken(token)

        return if(check) {
            return try {
                brandsRepository.deleteById(id)

                ServiceResponse(
                    data = listOf(),
                    message = "Brand with id = $id has been deleted",
                    status = HttpStatus.OK
                )
            } catch (e: Exception) {
                ServiceResponse(
                    data = listOf(),
                    message = "Brand with id = $id not found",
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

    override fun getAllBrands(): ServiceResponse<ProductBrands>? {
        return try {

            val brands = brandsRepository.findAll().toList()

            ServiceResponse(
                data = brands,
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

    override fun createBrand(title: String, token: String): ServiceResponse<ProductBrands>? {
        return try {
            val check = checkUtil.checkToken(token)
            return if(check) {
                return try {
                    val item = ProductBrands(
                        id = UUID.randomUUID().toString(),
                        title = title
                    )

                    brandsRepository.save(item)

                    ServiceResponse(
                        data = listOf(brandsRepository.findById(item.id).get()),
                        message = "Brand has been created",
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
                        sellPrice = product.sellPrice,
                        archive = product.archive
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
                        sellPrice = it.sellPrice,
                        archive = it.archive
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
        searchQuery: String?,
        characteristics: Pair<List<String>?, List<String>?>
    ): PagingResponse<ProductsLightResponse>? {
        var fka = Pair<List<String>?, List<String>?>(first = null, second = null)
        if(characteristics.first != null) {
            val first = mutableListOf<String>()
            characteristics.first!!.forEachIndexed { index, s ->
                val t = "${s[s.length - 2]}${s.last()}"
                when {
                    t == "см" && s.length < 4 -> {
                        try {
                            first[index - 1] = "${first[index - 1]} см"
                        } catch (e: Exception) {
                            first.add(s.replace(",",""))
                        }
                    }
                    t == "мм" && s.length < 4 -> {
                        try {
                            first[index - 1] = "${first[index - 1]} мм"
                        } catch (e: Exception) {
                            first.add(s.replace(",",""))
                        }
                    }
                    else -> first.add(s.replace(",",""))
                }
            }
            fka = Pair(first = first, second = characteristics.second)
        }
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

                "string" -> null

                else -> null
            }

            var pageable: Pageable =
                if (sort != null) PageRequest.of(page, countCard, sort) else PageRequest.of(page, countCard)

            var maxPricePage = if(characteristics.first == null) {
                productsRepository.getMaxPrice(
                    brand = brand?.brand,
                    stockEmpty = stockEmpty,
                    stockFull = stockFull,
                    categoryId = categoryId,
                    isSell = isSellByPromo,
                    searchQuery = searchQuery
                )
            } else {
                0
            }
            val statePage: Page<Product> = if(characteristics.first == null){
                productsRepository.findAllBy(
                    pageable = pageable,
                    brand = brand?.brand,
                    firstPrice = smallPrice,
                    secondPrice = highPrice,
                    stockEmpty = stockEmpty,
                    stockFull = stockFull,
                    categoryId = categoryId,
                    isSell = isSellByPromo,
                    searchQuery = searchQuery
                )
            } else {
                pageable = if (sort != null) PageRequest.of(page, 32765, sort) else PageRequest.of(page, 32765)
                val products = productsRepository.findAllBy(
                    pageable = pageable,
                    brand = brand?.brand,
                    firstPrice = smallPrice,
                    secondPrice = highPrice,
                    stockEmpty = stockEmpty,
                    stockFull = stockFull,
                    categoryId = categoryId,
                    isSell = isSellByPromo,
                    searchQuery = searchQuery
                ).content
                val tc = mutableListOf<Charss>()
                fka.first!!.forEachIndexed { index, it ->
                    tc.add(
                        Charss(
                            title = it,
                            data = fka.second!![index]
                        )
                    )
                }
                val maxPercent = mutableListOf<PercentageList>()
                val p = mutableListOf<Product>()
                products.forEachIndexed { index, product ->
                    val b = mutableListOf<Charss>()
                    product.characteristic.forEach {
                        b.add(
                            Charss(
                                data = it.data,
                                title = it.title.replace(",", "")
                            )
                        )
                    }
                    val firstSet = HashSet(b)
                    val secondSet = HashSet(tc)
                    firstSet.retainAll(secondSet)
                    maxPercent.add(PercentageList(firstSet.size, index))
                }
                maxPercent.sortBy { it.size }
                maxPercent.forEach {
                    if(tc.size == it.size){
                        p.add(products[it.index])
                    }
                }
                maxPricePage = p.maxOf { it.price }
                p.toPage(pageable)
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
                        sellPrice = it.sellPrice,
                        archive = it.archive
                    )
                )
            }

            return PagingResponse(
                data = light,
                totalElements = statePage.totalElements,
                totalPages = statePage.totalPages,
                maxPrice = maxPricePage,
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
                        sellPrice = it.sellPrice,
                        archive = it.archive
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
                stock = stock,
                percent = temp.percent,
                archive = temp.archive
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


    override fun changeProductArchive(id: String, archive: Boolean, token: String): ServiceResponse<Product>? {
        return try {
            val temp = productsRepository.findById(id).get()

            println(temp.images)

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
                stock = temp.stock,
                percent = temp.percent,
                archive = archive
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

    override fun addTemplateCharact(id: String, token: String, charact: List<String>): ServiceResponse<TemplateCharact> {
        val check = checkUtil.checkToken(token)

        return if(check) {
            return try {
                val item = TemplateCharact(
                    categoryId = id,
                    charact = charact
                )

                templateCharactRepository.save(item)

                ServiceResponse(
                    data = listOf(templateCharactRepository.findById(id).get()),
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
        } else {
            ServiceResponse(
                data = null,
                message = "Unexpected token",
                status = HttpStatus.UNAUTHORIZED
            )
        }
    }

    override fun editTemplateCharact(id: String, token: String, charact: List<String>): ServiceResponse<TemplateCharact> {
        val check = checkUtil.checkToken(token)

        return if (check) {
            return try {
                val item = TemplateCharact(
                    categoryId = id,
                    charact = charact
                )
                templateCharactRepository.deleteById(id)
                templateCharactRepository.save(item)

                ServiceResponse(
                    data = listOf(templateCharactRepository.findById(id).get()),
                    message = "Template with category id = $id has been edited",
                    status = HttpStatus.OK
                )
            } catch (e: Exception) {
                ServiceResponse(
                    data = null,
                    message = e.message.toString(),
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
    }

    override fun deleteTemplateCharact(id: String, token: String): ServiceResponse<TemplateCharact> {
        val check = checkUtil.checkToken(token)

        return if(check) {
            return try {
                templateCharactRepository.deleteById(id)
                ServiceResponse(
                    data = listOf(),
                    message = "Template with category id = $id has been deleted",
                    status = HttpStatus.OK
                )
            } catch (e: Exception) {
                ServiceResponse(
                    data = listOf(),
                    message = "Template with category id = $id not found",
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

    override fun getTemplateCharact(): ServiceResponse<TemplateCharact> {
        return try {
            val items = templateCharactRepository.findAll().toList()

            ServiceResponse(
                data = items,
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

    override fun getFilters(categoryId: String): ServiceResponse<Filters> {
        return try {
            val chars = templateCharactRepository.findById(categoryId).get()

            val filters = mutableListOf<Filters>()

            val products = productsRepository.findAllByCategories(categoryId)

            chars.charact?.forEach { categoryCharacteristic ->
                val tempFilters = mutableListOf<String>()
                products.forEach { product ->
                    println("WWW ${product.id}")
                    product.characteristic.forEach { characteristic ->
                        if(characteristic.title.replace(",","") == categoryCharacteristic.replace(",","")) {
                            tempFilters.add(characteristic.data)
                            println(characteristic.title)
                            println(characteristic.data)
                        }
                    }
                }
                filters.add(
                    Filters(
                        title = categoryCharacteristic,
                        data = tempFilters.distinct()
                    )
                )
            }
            ServiceResponse(
                data = filters,
                message = "Success",
                status = HttpStatus.OK
            )
        } catch (e: Exception) {
            ServiceResponse(
                data = listOf(),
                message = "Category with id = $categoryId not found",
                status = HttpStatus.NOT_FOUND
            )
        }
    }

    override fun getTemplateCharactById(id: String): ServiceResponse<TemplateCharact> {
        return try {
            val item = templateCharactRepository.findById(id).get()

            ServiceResponse(
                data = listOf(item),
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

}