package com.example.uvanna.repository.orders

import com.example.uvanna.jpa.Services
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ServiceRepository: JpaRepository<Services, String>