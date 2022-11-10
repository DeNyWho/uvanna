package com.example.uvanna.repository.catalog

import com.example.uvanna.jpa.CatalogFirst
import com.example.uvanna.jpa.CatalogSecond
import com.example.uvanna.jpa.CatalogThird
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface CatalogRepository: JpaRepository<CatalogFirst, String> {
    @Query("select m from CatalogFirst m where :catalog member of m.sub")
    fun findUpper(catalog: CatalogSecond): CatalogFirst
}

@Repository
interface CatalogSecondRepository: JpaRepository<CatalogSecond, String> {
    @Query("select m from CatalogSecond m where :catalog member of m.sub")
    fun findUpper(catalog: CatalogThird): CatalogSecond
}

@Repository
interface CatalogThirdRepository: JpaRepository<CatalogThird, String> {
}