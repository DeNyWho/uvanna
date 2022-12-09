package com.example.uvanna.service

import com.example.uvanna.jpa.MainBanner
import com.example.uvanna.jpa.Promo
import com.example.uvanna.model.response.PagingResponse
import com.example.uvanna.model.response.ServiceResponse
import com.example.uvanna.repository.site.mainBanner.MainBannerRepository
import com.example.uvanna.repository.site.SiteRepositoryImpl
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
class SiteService: SiteRepositoryImpl {

    @Autowired
    lateinit var mainBannerRepository: MainBannerRepository

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

}