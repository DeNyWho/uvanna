package com.example.uvanna.service

import com.example.uvanna.model.response.ServiceResponse
import com.example.uvanna.util.CheckUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import javax.annotation.Resource

@Service
class ImageService {

    @Autowired
    lateinit var fileService: FileService

    @Resource
    private lateinit var checkUtil: CheckUtil

    fun deleteImage(token: String, url: String): ServiceResponse<String> {
        val check = checkUtil.checkToken(token)
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
        val check = checkUtil.checkToken(token)
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

}