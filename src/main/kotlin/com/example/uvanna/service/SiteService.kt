package com.example.uvanna.service

import com.example.uvanna.jpa.MainBanner
import com.example.uvanna.jpa.Product
import com.example.uvanna.jpa.Promo
import com.example.uvanna.model.product.ProductsLightResponse
import com.example.uvanna.model.response.PagingResponse
import com.example.uvanna.model.response.PromoLightResponse
import com.example.uvanna.model.response.ServiceResponse
import com.example.uvanna.repository.site.mainBanner.MainBannerRepository
import com.example.uvanna.repository.site.SiteRepositoryImpl
import com.example.uvanna.repository.site.promo.PromoRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import javax.validation.constraints.Max
import javax.validation.constraints.Min

@Service
class SiteService: SiteRepositoryImpl {

    @Autowired
    lateinit var mainBannerRepository: MainBannerRepository

    @Autowired
    lateinit var promoRepository: PromoRepository

    @Autowired
    lateinit var fileService: FileService

    override fun addMainBanner(file: MultipartFile): ServiceResponse<MainBanner>{
        return try {
            val imageUrl = fileService.save(file)

            val item = MainBanner(
                id = UUID.randomUUID().toString(),
                imageUrl = imageUrl
            )

            mainBannerRepository.save(item)

            ServiceResponse(
                data = listOf(mainBannerRepository.findById(item.id).get()),
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

    override fun getMainBanners():ServiceResponse<MainBanner>{
        return try {
            ServiceResponse(
                data = mainBannerRepository.findAll(),
                message = "Success",
                status = HttpStatus.OK
            )
        } catch (e: Exception) {
            ServiceResponse(
                data = null,
                message = "Something went wrong: ${e.message}",
                status = HttpStatus.BAD_REQUEST
            )
        }
    }

    fun deleteMainBanner(id: String){
        mainBannerRepository.deleteById(id)
    }

    override fun addPromo(title: String, description: String, file: MultipartFile): ServiceResponse<Promo>{
        return try {
            val imageUrl = fileService.save(file)

            val item = Promo(
                id = UUID.randomUUID().toString(),
                title = title,
                description = description,
                imageUrl = imageUrl,
                date = LocalDate.now()
            )

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

    override fun getPromos(pageSize: Int, pageNum: Int): PagingResponse<PromoLightResponse> {
        return try {
            val sort = Sort.by(
                Sort.Order(Sort.Direction.DESC, "date"),
            )
            val pageable: Pageable = PageRequest.of(pageNum, pageSize, sort)
            val statePage: Page<Promo> = promoRepository.findAll(pageable)

            val light = mutableListOf<PromoLightResponse>()
            statePage.content.forEach {
                light.add(
                    PromoLightResponse(
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