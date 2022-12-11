package com.example.uvanna.service

import com.example.uvanna.repository.promo.PromoRepositoryImpl
import com.example.uvanna.jpa.Promo
import com.example.uvanna.model.request.promo.PromoProductRequest
import com.example.uvanna.model.response.PagingResponse
import com.example.uvanna.model.response.ServiceResponse
import com.example.uvanna.repository.admin.AdminRepository
import com.example.uvanna.repository.products.ProductsRepository
import com.example.uvanna.repository.promo.PromoRepository
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

    override fun addPromoWithProducts(
        title: String,
        description: String,
        file: MultipartFile,
        products: List<PromoProductRequest>,
        token: String,
        number: Int?,
        percent: Int?,
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

                products.forEach {
                    val product = productsRepository.findById(it.productId).get()
                    val calculatedPercent = if(percent == null) {
                        val temp = (( product.price - number!! ) / product.price ).toString()
                        "${temp[2]}${temp[3]}".toInt()
                    } else {
                        null
                    }
                    product.percent = if(percent != null ) it.percent else calculatedPercent
                    product.sellPrice = if(percent == null) product.price - number!! else product.price * (percent/100)
                    productsRepository.save(product)
                    item.addPromoProducts(productsRepository.findById(it.productId).get())
                }

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
                Sort.Order(Sort.Direction.DESC, "date"),
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

    override fun deletePromo(id: String, token: String): ServiceResponse<String>{
        val check = checkToken(token)
        return if(check) {
            return try {
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

    fun checkToken(token: String): Boolean {
        val token = adminRepository.findAdminTokenByToken(token)

        return token != null
    }

}