package com.example.uvanna.service

import com.example.uvanna.jpa.Promo
import com.example.uvanna.model.request.promo.PromoProductRequest
import com.example.uvanna.model.response.PagingResponse
import com.example.uvanna.model.response.ProductsLightResponse
import com.example.uvanna.model.response.ServiceResponse
import com.example.uvanna.repository.admin.AdminRepository
import com.example.uvanna.repository.products.ProductsRepository
import com.example.uvanna.repository.promo.PromoRepository
import com.example.uvanna.repository.promo.PromoRepositoryImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDate
import java.util.*

@Service
class PromoService: PromoRepositoryImpl {

    @Autowired
    lateinit var adminRepository: AdminRepository

    @Autowired
    lateinit var promoRepository: PromoRepository

    @Autowired
    lateinit var productsRepository: ProductsRepository

    @Autowired
    lateinit var fileService: FileService

    override fun createPromo(
        title: String,
        description: String,
        file: MultipartFile,
        token: String,
        dateExpired: String
    ): ServiceResponse<Promo>{
        return try {
            val check = checkToken(token)

            if(check) {
                val imageUrl = fileService.save(file)

                val id = UUID.randomUUID().toString()

                val item = Promo(
                    id = id,
                    title = title,
                    description = description,
                    imageUrl = imageUrl,
                    dateExpired = dateExpired,
                    dateCreated = LocalDate.now(),
                )

                promoRepository.save(item)

                ServiceResponse(
                    data = listOf(promoRepository.findById(item.id).get()),
                    message = "Promo has been created",
                    status = HttpStatus.OK
                )
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

    override fun addProductsToPromo(
        id: String,
        token: String,
        products: List<PromoProductRequest>
    ): ServiceResponse<Promo> {
        return try {
            val check = checkToken(token)

            if(check) {

                val promo = promoRepository.findById(id).get()

                products.forEach {
                    val product = productsRepository.findById(it.product).get()
                    product.percent = it.percent
                    product.sellPrice = product.price * (it.percent /100)
                    productsRepository.save(product)
                    promo.addPromoProducts(productsRepository.findById(it.product).get())
                }

                promoRepository.save(promo)

                ServiceResponse(
                    data = listOf(promoRepository.findById(promo.id).get()),
                    message = "Products has been added to promo with id = $id",
                    status = HttpStatus.OK
                )
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

    override fun editPromo(
        id: String,
        title: String,
        description: String,
        file: MultipartFile,
        token: String,
        dateExpired: String
    ): ServiceResponse<Promo>{
        return try {
            val check = checkToken(token)

            if(check) {
                val imageUrl = fileService.save(file)

                val tempPromo = promoRepository.findById(id).get()

                val item = Promo(
                    id = id,
                    title = title,
                    description = description,
                    imageUrl = imageUrl,
                    dateExpired = dateExpired,
                    dateCreated = LocalDate.now(),
                    productsPromo = tempPromo.productsPromo
                )

                promoRepository.deleteById(id)
                promoRepository.save(item)

                ServiceResponse(
                    data = listOf(promoRepository.findById(item.id).get()),
                    message = "Promo has been edited",
                    status = HttpStatus.OK
                )
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



    override fun getPromo(id: String): ServiceResponse<Promo>{
        return try {
            ServiceResponse(
                data = listOf(promoRepository.findById(id).get()),
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

    override fun getPromos(pageSize: Int, pageNum: Int): PagingResponse<Promo> {
        return try {
            val sort = Sort.by(
                Sort.Order(Sort.Direction.ASC, "dateExpired"),
            )
            val pageable: Pageable = PageRequest.of(pageNum, pageSize, sort)
            val statePage: Page<Promo> = promoRepository.findAll(pageable)

            val light = mutableListOf<Promo>()
            statePage.content.forEach {
                light.add(
                    Promo(
                        id = it.id,
                        title = it.title,
                        description = it.description,
                        dateExpired = it.dateExpired,
                        imageUrl = it.imageUrl,
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

    override fun addProductPromo(id: String, token: String, productsIds: List<PromoProductRequest>): ServiceResponse<Any> {
        val check = checkToken(token)
        return if(check) {
            try {
                val promo = promoRepository.findById(id).get()
                return try {
                    promo.deleteAllPromoProducts()

                    productsIds.forEach {
                        val product = productsRepository.findById(it.product).get()
                        product.percent = it.percent
                        product.sellPrice = product.price - (product.price * (it.percent.toDouble() / 100.00)).toInt()
                        promo.addPromoProducts(productsRepository.findById(it.product).get())
                    }
                    promoRepository.deleteById(id)
                    promoRepository.save(promo)

                    ServiceResponse(
                        data = listOf(),
                        message = "Products promo has been edited",
                        status = HttpStatus.OK
                    )
                } catch (e: Exception){
                    ServiceResponse(
                        data = listOf(),
                        message = "Something went wrong: ${e.message}",
                        status = HttpStatus.NOT_FOUND
                    )
                }
            } catch (e: Exception) {
                ServiceResponse(
                    data = listOf(),
                    message = "Promo with id = $id not found",
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

    override fun deletePromo(id: String, token: String): ServiceResponse<String>{
        val check = checkToken(token)
        return if(check) {
            return try {
                val temp = promoRepository.findById(id).get()
                fileService.deleteByUrl(temp.imageUrl)
                promoRepository.deleteById(id)
                ServiceResponse(
                    data = listOf(),
                    message = "Promo with id = $id has been deleted",
                    status = HttpStatus.OK
                )
            } catch (e: Exception) {
                ServiceResponse(
                    data = listOf(),
                    message = "Promo with id = $id not found",
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

    override fun getProductPromo(page: Int, countCard: Int, id: String): PagingResponse<ProductsLightResponse> {
        return try {
            val pageable: Pageable = PageRequest.of(page, countCard)
            val statePage = promoRepository.getProducts(id, pageable)
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
                message = "Something went wrong: ${e.message}",
                status = HttpStatus.BAD_REQUEST
            )
        }
    }

    fun checkToken(token: String): Boolean {
        val token = adminRepository.findAdminTokenByToken(token)

        return token != null
    }

}