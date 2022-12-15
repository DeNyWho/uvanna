package com.example.uvanna.controller.admin

import com.example.uvanna.model.response.ServiceResponse
import com.example.uvanna.service.AdminService
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.crossstore.ChangeSetPersister
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletResponse

@RestController
@CrossOrigin("*")
@Tag(name = "AdminApi", description = "Admin")
@RequestMapping("/api/admin/")
class AdminController {

    @Autowired
    lateinit var adminService: AdminService

    @PostMapping("generateToken")
    fun generateToken(
        @RequestParam login: String,
        @RequestParam password: String,
        response: HttpServletResponse
    ): ServiceResponse<String> {
        return try {
            adminService.generateToken(login = login, password = password)

        } catch (e: ChangeSetPersister.NotFoundException){
            ServiceResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
        }
    }

    @GetMapping("checkToken")
    fun checkToken(
        @RequestParam token: String,
        response: HttpServletResponse
    ): ServiceResponse<String>{
        return try {
            adminService.checkToken(token)

        } catch (e: ChangeSetPersister.NotFoundException){
            ServiceResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
        }
    }
}