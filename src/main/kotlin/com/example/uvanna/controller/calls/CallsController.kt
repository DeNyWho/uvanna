package com.example.uvanna.controller.calls

import com.example.uvanna.jpa.Calls
import com.example.uvanna.model.request.call.CallRequest
import com.example.uvanna.model.response.PagingResponse
import com.example.uvanna.model.response.ServiceResponse
import com.example.uvanna.service.AdminService
import com.example.uvanna.service.CallsService
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.crossstore.ChangeSetPersister
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletResponse

@RestController
@CrossOrigin("*")
@Tag(name = "CallsApi", description = "Calls")
@RequestMapping("/api/calls/")
class CallsController {

    @Autowired
    lateinit var callsService: CallsService

    @PostMapping("")
    fun addCall(
        @RequestParam token: String,
        call: CallRequest,
        response: HttpServletResponse
    ): ServiceResponse<Calls> {
        return try {
            callsService.addCall(token, call)

        } catch (e: ChangeSetPersister.NotFoundException){
            ServiceResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
        }
    }

    @PostMapping("")
    fun deleteCall(
        @RequestParam token: String,
        callId: String,
        response: HttpServletResponse
    ): ServiceResponse<Any> {
        return try {
            callsService.deleteCall(token, callId)

        } catch (e: ChangeSetPersister.NotFoundException){
            ServiceResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
        }
    }

    @PostMapping("")
    fun getCalls(
        countCard: Int,
        page: Int,
        filter: String,
        response: HttpServletResponse
    ): PagingResponse<Any> {
        return try {
            callsService.getCalls(countCard, page, filter )

        } catch (e: ChangeSetPersister.NotFoundException){
            PagingResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
        }
    }
}