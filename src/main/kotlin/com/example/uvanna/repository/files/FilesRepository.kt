package com.example.uvanna.repository.files

import com.example.uvanna.jpa.Files
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FilesRepository: JpaRepository<Files, String>