package com.example.uvanna.model.moysklad.counter_party_data

import kotlinx.serialization.Serializable

data class CounterPartyData(
    val name: String,
    val actualAddress: String = "",
    val phone: String = "",
    val email :String = "",
)
