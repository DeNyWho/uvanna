package com.example.uvanna.repository.products

import com.example.uvanna.jpa.Characteristic
import com.example.uvanna.jpa.Product
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ProductsRepository: JpaRepository<Product, String> {

//    @Query("select m from Product m where m.brand in :brand")
//    fun findPriceBetweenAndBrand(pageable: Pageable, firstPrice: Int, secondPrice: Int, brand: List<String>): Page<Product>
  
    @Query("select v from Product v where v.brand in :brand")
    fun findBrand(pageable: Pageable, brand: List<String>):Page<Product>

    @Query("select v from Product v where upper(v.title) like concat('%', upper(?1), '%')")
    fun findByTitleSearch(@Param("title") title: String): List<Product>


    @Query("select m from Product m where (m.price between :firstPrice and :secondPrice)")
    fun findPriceBetween(pageable: Pageable, firstPrice: Int, secondPrice: Int): Page<Product>

    @Query("select m from Product m where m.brand in :brand")
    fun findProductByBrand(pageable: Pageable, brand: List<String>): Page<Product>

    @Query("select m from Product m where :characteristic member of m.characteristic and m.thirdSub = :level")
    fun findProductByCharacteristic(pageable: Pageable, characteristic: List<Characteristic>, level: String): Page<Product>

    @Query("select m from Product m where m.thirdSub = :level")
    fun getThirdLevelSort(level: String): List<Characteristic>

    @Query("select m from Product m where m.stock = 0")
    fun findProductEmptyStock(pageable: Pageable): Page<Product>

    @Query("select m from Product m where m.stock = 0 and m.price between :firstPrice and :secondPrice")
    fun findProductEmptyStockWithBetweenPrice(pageable: Pageable, firstPrice: Int, secondPrice: Int): Page<Product>

    @Query("select m from Product m where m.stock > 0")
    fun findProductFullStock(pageable: Pageable): Page<Product>

    @Query("select m from Product m where m.stock > 0 and m.price between :firstPrice and :secondPrice")
    fun findProductFullStockWithBetweenPrice(pageable: Pageable, firstPrice: Int, secondPrice: Int): Page<Product>


    @Query("select m from Product m where m.secondSub = :category or m.thirdSub = :category")
    fun findProductWithCategoryId(pageable: Pageable, category: String): Page<Product>

    @Query("select m from Product m where (m.secondSub = :category or m.thirdSub = :category) and m.price between :firstPrice and :secondPrice")
    fun findProductWithCategoryIdWithBetweenPrice(pageable: Pageable, category: String, firstPrice: Int, secondPrice: Int): Page<Product>

    @Query("select distinct m.brand from Product m where m.secondSub = :category or m.thirdSub = :category")
    fun findBrands(category: String): List<String>

}