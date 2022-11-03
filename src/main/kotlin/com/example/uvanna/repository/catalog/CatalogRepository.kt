package com.example.uvanna.repository.catalog

import com.example.uvanna.jpa.CatalogFirst
import com.example.uvanna.jpa.CatalogSecond
import com.example.uvanna.jpa.CatalogThird
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CatalogRepository: JpaRepository<CatalogFirst, String> {
}

@Repository
interface CatalogSecondRepository: JpaRepository<CatalogSecond, String> {
}

@Repository
interface CatalogThirdRepository: JpaRepository<CatalogThird, String> {
}