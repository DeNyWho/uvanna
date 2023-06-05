package com.example.uvanna.model.moysklad.counter_party_data

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.serialization.Serializable

data class CounterPartyData(
    val name: String,
    val actualAddress: String = "",
    val phone: String = "",
    val email :String = "",
)

data class CounterpartyResponse(
    @JsonProperty("rows") val rows: List<Counterparty>
)

data class Counterparty(
    @JsonProperty("id") val id: String
)