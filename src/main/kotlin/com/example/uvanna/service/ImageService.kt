package com.example.uvanna.service

import com.example.uvanna.model.response.ServiceResponse
import com.example.uvanna.repository.admin.AdminRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class ImageService {

    @Autowired
    lateinit var fileService: FileService

    @Autowired
    lateinit var adminRepository: AdminRepository

    fun deleteImage(token: String, url: String): ServiceResponse<String> {
        val check = checkToken(token)
        return if(check) {
            try {
                fileService.deleteByUrl(url)
                ServiceResponse(
                    data = listOf(),
                    message = "Success",
                    status = HttpStatus.OK
                )
            } catch (e: Exception) {
                ServiceResponse(
                    data = listOf(),
                    message = "Something went wrong... ( ${e.message} )",
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



    fun loadImage(token: String, file: MultipartFile): ServiceResponse<String>{
        val check = checkToken(token)
        return if(check) {
            try {
                ServiceResponse(
                    data = listOf(fileService.save(file)),
                    message = "Success",
                    status = HttpStatus.OK
                )
            } catch (e: Exception) {
                ServiceResponse(
                    data = listOf(),
                    message = "Something went wrong... ( ${e.message} )",
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