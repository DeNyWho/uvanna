package com.example.uvanna.repository.site

import com.example.uvanna.jpa.Blog
import com.example.uvanna.jpa.Product
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface BlogRepository: JpaRepository<Blog, String> {

    override fun findAll(pageable: Pageable): Page<Blog>

    @Query("select b from Blog b where :id <> b.id  order by random()")
    fun findBlogsByRandom(pageable: Pageable, id: String): Page<Blog>
}