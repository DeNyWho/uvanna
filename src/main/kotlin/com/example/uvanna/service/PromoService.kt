package com.example.uvanna.service

import com.example.uvanna.repository.promo.PromoRepositoryImpl
import com.example.uvanna.jpa.Promo
import com.example.uvanna.model.response.PagingResponse
import com.example.uvanna.model.response.ServiceResponse
import com.example.uvanna.repository.catalog.CatalogRepository
import com.example.uvanna.repository.catalog.CatalogSecondRepository
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
    lateinit var promoRepository: PromoRepository

    @Autowired
    lateinit var catalogRepository: CatalogRepository

    @Autowired
    lateinit var catalogSecondRepository: CatalogSecondRepository

    @Autowired
    lateinit var fileService: FileService

    fun addPromoWithProducts(title: String, description: String, file: MultipartFile, products: List<String>): ServiceResponse<Promo>{
        return try {
            val imageUrl = fileService.save(file)

            val item = Promo(
                id = UUID.randomUUID().toString(),
                title = title,
                description = description,
                imageUrl = imageUrl,
                date = LocalDate.now(),
            )



            val b = promoRepository.findById(id).get().addPromoProducts(
                second
            )
            catalogRepository.save(b)

            promoRepository.save(item)




            ServiceResponse(
                data = listOf(promoRepository.findById(item.id).get()),
                message = "Product has been created",
                status = HttpStatus.OK
            )
        } catch (e: Exception){
            ServiceResponse(
                data = null,
                message = "Something went wrong: ${e.message}",
                status = HttpStatus.BAD_REQUEST
            )
        }
    }

    override fun addPromoWithCategory(title: String, description: String, file: MultipartFile, category: String): ServiceResponse<Promo>{

        val id = UUID.randomUUID().toString()



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

    override fun getPromo(id: String): ServiceResponse<Promo>{
        return try {
            ServiceResponse(
                data = listOf(promoRepository.getById(id)),
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
                        date = it.date,
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

    fun deletePromo(id: String){
        promoRepository.deleteById(id)
    }

}