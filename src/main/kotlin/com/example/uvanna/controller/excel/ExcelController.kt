package com.example.uvanna.controller.excel

import com.example.uvanna.service.OrderService
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.io.ByteArrayOutputStream

@RestController
@CrossOrigin("*")
@RequestMapping("/api/excel")
@Tag(name = "ExcelApi", description = "Excel")
class ExcelController(private val orderService: OrderService) {

    @GetMapping("/orders")
    fun generateExcelFile(
        @RequestHeader(value = "Authorization") token: String
    ): ResponseEntity<ByteArray> {
        val byteArray = orderService.generateExcelFile(token)
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_OCTET_STREAM
        headers.setContentDispositionFormData("attachment", "orders.xlsx")
        if (byteArray != null) {
            headers.contentLength = byteArray.size.toLong()
        }
        return ResponseEntity(byteArray, headers, HttpStatus.OK)
    }


}