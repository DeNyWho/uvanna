package com.example.uvanna.repository.calls

import com.example.uvanna.jpa.Calls
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CallsRepository: JpaRepository<Calls, String>