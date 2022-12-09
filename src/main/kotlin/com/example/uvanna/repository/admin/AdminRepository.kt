package com.example.uvanna.repository.admin

import com.example.uvanna.jpa.AdminToken
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface AdminRepository: JpaRepository<AdminToken, String> {

    @Query("Select t from AdminToken t where t.token = :token")
    fun findAdminTokenByToken(token: String): AdminToken?
}