package com.example.uvanna.jpa

import java.util.UUID
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table


@Entity
@Table(name = "Services")
data class Services (
    @Id
    var id: String = UUID.randomUUID().toString(),
    var serviceName: String = "",
    val count: Int = 0,
    val price: Int = 0
)