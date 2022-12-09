package com.example.uvanna.repository.promo

import com.example.uvanna.jpa.Promo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface PromoRepository: JpaRepository<Promo, String> {

}