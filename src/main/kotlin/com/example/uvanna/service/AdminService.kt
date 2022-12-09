package com.example.uvanna.service

import com.example.uvanna.jpa.AdminToken
import com.example.uvanna.model.response.ServiceResponse
import com.example.uvanna.repository.admin.AdminRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.util.*

@Service
class AdminService {

    @Autowired
    lateinit var adminRepository: AdminRepository

    @Value("\${admin_login}")
    lateinit var adminLogin: String

    @Value("\${admin_password}")
    lateinit var adminPassword: String

    fun generateToken(login: String, password: String): ServiceResponse<String> {
        if(password == adminPassword && login == adminLogin) {
            adminRepository.deleteAll()
            val token = UUID.randomUUID().toString()
            adminRepository.save(
                AdminToken(
                    id = UUID.randomUUID().toString(),
                    token = token
                )
            )
            return ServiceResponse(
                data = listOf(token),
                message = "Success",
                status = HttpStatus.OK
            )
        } else {
            return ServiceResponse(
                data = null,
                message = "Somthing went wrong....",
                status = HttpStatus.UNAUTHORIZED
            )
        }
    }
}