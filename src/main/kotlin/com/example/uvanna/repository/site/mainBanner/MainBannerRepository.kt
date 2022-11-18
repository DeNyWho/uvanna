package com.example.uvanna.repository.site.mainBanner

import com.example.uvanna.jpa.MainBanner
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MainBannerRepository: JpaRepository<MainBanner, String> {
}