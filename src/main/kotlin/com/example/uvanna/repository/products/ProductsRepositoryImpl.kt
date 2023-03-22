package com.example.uvanna.repository.products

import com.example.uvanna.jpa.Product
import com.example.uvanna.jpa.ProductBrands
import com.example.uvanna.jpa.TemplateCharact
import com.example.uvanna.model.product.Brands
import com.example.uvanna.model.product.Filters
import com.example.uvanna.model.request.product.ProductRequest
import com.example.uvanna.model.response.PagingResponse
import com.example.uvanna.model.response.ProductLighterResponse
import com.example.uvanna.model.response.ProductsLightResponse
import com.example.uvanna.model.response.ServiceResponse
import org.springframework.stereotype.Repository
import org.springframework.web.multipart.MultipartFile
import javax.validation.constraints.Max
import javax.validation.constraints.Min

@Repository
interface ProductsRepositoryImpl {


    fun getProduct(id: String): ServiceResponse<Product>?

    fun getBrands(id: String): ServiceResponse<String>?

    fun findProduct(
        searchQuery: String,
        pageNum: @Min(value = 0.toLong()) @Max(value = 500.toLong()) Int,
        pageSize: @Min(value = 1.toLong()) @Max(value = 500.toLong()) Int
    ): ServiceResponse<ProductLighterResponse>?

    fun editProduct(
        id: String,
        characteristic: List<String>,
        data: List<String>,
        files: List<MultipartFile>,
        token: String,
        product: ProductRequest
    ): ServiceResponse<Product>?

    fun addProduct(
        product: ProductRequest,
        files: List<MultipartFile>,
        characteristic: List<String>,
        token: String,
        data: List<String>
    ): ServiceResponse<Product>?

    fun deleteProduct(token: String, id: String): ServiceResponse<String>


    fun getProductRandom(
        countCard: Int,
        page: Int,
        filter: String?,
        productId: String?
    ): PagingResponse<ProductsLightResponse>?

    fun getProductsByIds(ids: List<String>): ServiceResponse<ProductLighterResponse>
    fun addProductStock(id: String, stock: Int, token: String): ServiceResponse<Product>?
    fun getProductsIds(): ServiceResponse<String>
    fun createBrand(title: String, token: String): ServiceResponse<ProductBrands>?
    fun getAllBrands(): ServiceResponse<ProductBrands>?
    fun deleteBrandById(id: String, token: String): ServiceResponse<String>
    fun addTemplateCharact(id: String, token: String, charact: List<String>): ServiceResponse<TemplateCharact>
    fun deleteTemplateCharact(id: String, token: String): ServiceResponse<TemplateCharact>
    fun editTemplateCharact(id: String, token: String, charact: List<String>): ServiceResponse<TemplateCharact>
    fun getTemplateCharact(): ServiceResponse<TemplateCharact>
    fun getTemplateCharactById(id: String): ServiceResponse<TemplateCharact>
    fun changeProductArchive(id: String, archive: Boolean, token: String): ServiceResponse<Product>?
    fun getFilters(categoryId: String): ServiceResponse<Filters>
    fun getProducts(
        countCard: Int,
        page: Int,
        brand: Brands?,
        smallPrice: Int?,
        highPrice: Int?,
        filter: String?,
        categoryId: String?,
        stockEmpty: Boolean?,
        stockFull: Boolean?,
        isSellByPromo: Boolean?,
        searchQuery: String?,
        characteristics: Pair<List<String>?, List<String>?>
    ): PagingResponse<ProductsLightResponse>?
}