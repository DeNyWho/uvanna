package com.example.uvanna.repository.products

import com.example.uvanna.jpa.Characteristic
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CharacteristicRepository: JpaRepository<Characteristic, String> {
}