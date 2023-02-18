package com.example.uvanna.util

import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

fun <T> List<T>.toPage(pageable: Pageable) = PageImpl(this, pageable, this.size.toLong())