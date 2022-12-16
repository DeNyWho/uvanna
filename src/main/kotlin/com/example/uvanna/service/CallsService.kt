package com.example.uvanna.service

import com.example.uvanna.jpa.Calls
import com.example.uvanna.model.request.call.CallRequest
import com.example.uvanna.model.response.PagingResponse
import com.example.uvanna.model.response.ServiceResponse
import com.example.uvanna.repository.admin.AdminRepository
import com.example.uvanna.repository.calls.CallsRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.util.*

@Service
class CallsService {

    @Autowired
    private lateinit var callsRepository: CallsRepository
    @Autowired
    private lateinit var adminRepository: AdminRepository

    fun addCall(call: CallRequest): ServiceResponse<Calls> {
        return try {

            val id = UUID.randomUUID().toString()

            callsRepository.save(
                Calls(
                    id = id,
                    name = call.name,
                    date = call.date,
                    phone = call.phone
                )
            )
            ServiceResponse(
                data = listOf(callsRepository.findById(id).get()),
                message = "Call has been created",
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

    fun deleteCall(token: String, id: String): ServiceResponse<Any> {
        val check = checkToken(token)
        return if(check) {
            try {
                callsRepository.deleteById(id)
                ServiceResponse(
                    data = listOf(),
                    message = "Call with id = $id has been deleted",
                    status = HttpStatus.OK
                )
            } catch (e: Exception) {
                ServiceResponse(
                    data = listOf(),
                    message = "Call with id = $id not found",
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


    fun getCalls(countCard: Int, page: Int, filter: String): PagingResponse<Any> {
        return try {
            val sort = when (filter) {
                "new" -> Sort.by(
                    Sort.Order(Sort.Direction.DESC, "date"),
                )

                "old" -> Sort.by(
                    Sort.Order(Sort.Direction.ASC, "date")
                )

                else -> null
            }

            val pageable: Pageable =
                if (sort != null) PageRequest.of(page, countCard, sort) else PageRequest.of(page, countCard)
            val statePage: Page<Calls> = callsRepository.findAll(pageable)
            PagingResponse(
                data = statePage.content,
                totalPages = statePage.totalPages,
                totalElements = statePage.totalElements,
                message = "Success",
                status = HttpStatus.OK
            )
        } catch (e: Exception) {
            PagingResponse(
                data = null,
                totalPages = 0,
                totalElements = 0,
                message = "Something went wrong",
                status = HttpStatus.OK
            )

        }
    }


    fun checkToken(token: String): Boolean {
        val token = adminRepository.findAdminTokenByToken(token)

        return token != null
    }
}