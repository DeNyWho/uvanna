package com.example.uvanna.service

import com.example.uvanna.jpa.Admins
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


    fun checkToken(token: String): ServiceResponse<Admins> {
        val pgToken = adminRepository.findAdminTokenByToken(token)
        return if (token == pgToken?.token) {
            ServiceResponse(
                data = listOf(pgToken),
                message = "Success",
                status = HttpStatus.OK
            )
        } else {
            ServiceResponse(
                data = null,
                message = "The token is not accepted",
                status = HttpStatus.BAD_REQUEST
            )
        }
    }

    fun generateToken(login: String, password: String): ServiceResponse<Admins> {
        try {
            val admin = adminRepository.findAdminByLoginAndPassword(login = login, password = password)

            adminRepository.deleteById(admin!!.id)
            val token = UUID.randomUUID().toString()
            val adminFinal = adminRepository.save(
                Admins(
                    id = admin.id,
                    password = admin.password,
                    login = admin.login,
                    token = token,
                    type = admin.type
                )
            )

            return ServiceResponse(
                data = listOf(adminFinal),
                message = "Success",
                status = HttpStatus.OK
            )
        } catch (e: Exception) {
            return ServiceResponse(
                data = null,
                message = "Somthing went wrong....",
                status = HttpStatus.UNAUTHORIZED
            )
        }
    }
}