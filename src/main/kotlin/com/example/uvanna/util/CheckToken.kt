package com.example.uvanna.util

import com.example.uvanna.repository.admin.AdminRepository
import org.springframework.beans.factory.annotation.Autowired


@Autowired
private lateinit var adminRepository: AdminRepository

fun checkToken(token: String): Boolean {
    val actualToken = adminRepository.findAdminTokenByToken(token)
    return actualToken != null
}