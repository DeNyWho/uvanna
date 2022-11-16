package com.example.uvanna.repository.products

import com.example.uvanna.jpa.Characteristic
import com.example.uvanna.jpa.Product
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ProductsRepository: JpaRepository<Product, String> {
    @Query("select m from Product m where m.price between :firstPrice and :secondPrice")
    fun findPriceBetween(pageable: Pageable, firstPrice: Int, secondPrice: Int): Page<Product>

    @Query("select m from Product m where ( m.price between :firstPrice and :secondPrice ) and m.brand = :brand")
    fun findPriceBetweenAndBrand(pageable: Pageable, firstPrice: Int, secondPrice: Int, brand: String): Page<Product>

    fun findProductByBrand(pageable: Pageable, brand: String): Page<Product>

    @Query("select m from Product m where :characteristic member of m.characteristic and m.thirdSub = :level")
    fun findProductByCharacteristic(pageable: Pageable, characteristic: List<Characteristic>, level: String): Page<Product>

    @Query("select m from Product m where m.thirdSub = :level")
    fun getThirdLevelSort(level: String): List<Characteristic>

    @Query("select m from Product m where m.stock = 0")
    fun findProductEmptyStock(pageable: Pageable): Page<Product>

    @Query("select m from Product m where m.stock > 0")
    fun findProductFullStock(pageable: Pageable): Page<Product>

}