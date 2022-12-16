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

    @Query("From Product p where (:brand is null or p.brand in :brand)" +
            " and (:firstPrice is null or p.price between :firstPrice and :secondPrice)" +
            " and (:stockEmpty is null or :stockEmpty is true and p.stock = 0 or :stockEmpty is false)" +
            " and (:stockFull is null or :stockFull is true and p.stock > 0 or :stockFull is false)" +
            " and (:categoryId is null or p.thirdSub = :categoryId or p.secondSub = :categoryId)" +
            " and (:isSell is null or p.sellPrice is not null or p.sellPrice > 0)")
    fun findAllBy(
        pageable: Pageable,
        brand: List<String?>?,
        firstPrice: Int?,
        secondPrice: Int?,
        stockEmpty: Boolean?,
        stockFull: Boolean?,
        categoryId: String?,
        isSell: Boolean?
    ): Page<Product>


    @Query("select p from Product p where :productId <> p.id  order by random()")
    fun findProductsByRandom(pageable: Pageable, productId: String): Page<Product>

    @Query("select v from Product v where upper(v.title) like concat('%', upper(?1), '%')")
    fun findByTitleSearch(pageable: Pageable, @Param("title") title: String): Page<Product>


    @Query("select m from Product m where :characteristic member of m.characteristic and m.thirdSub = :level")
    fun findProductByCharacteristic(pageable: Pageable, characteristic: List<Characteristic>, level: String): Page<Product>

    @Query("select m from Product m where m.thirdSub = :level")
    fun getThirdLevelSort(level: String): List<Characteristic>

    @Query("select distinct m.brand from Product m where m.secondSub = :category or m.thirdSub = :category ")
    fun findBrands(category: String): List<String>

}