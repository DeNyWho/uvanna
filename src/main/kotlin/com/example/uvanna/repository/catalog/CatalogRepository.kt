package com.example.uvanna.repository.catalog

import com.example.uvanna.jpa.CatalogFirst
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CatalogRepository: JpaRepository<CatalogFirst, String> {
}