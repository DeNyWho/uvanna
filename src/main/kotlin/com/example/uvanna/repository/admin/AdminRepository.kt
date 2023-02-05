package com.example.uvanna.repository.admin

import com.example.uvanna.jpa.Admins
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface AdminRepository: JpaRepository<Admins, String> {

    @Query("Select t from Admins t where t.token = :token")
    fun findAdminTokenByToken(token: String): Admins?

    @Query("Select t from Admins t where t.password = :password and t.login = :login")
    fun findAdminByLoginAndPassword(login: String, password: String): Admins?
}