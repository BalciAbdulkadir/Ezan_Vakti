package com.example.ezan_vakti.model

import com.google.gson.annotations.SerializedName

// Bu data class, şehir arandığında API'den dönen JSON objesini temsil ediyor.
// Örnek: { "id": 9541, "name": "İstanbul" }
data class Location(

    // @SerializedName notasyonu, JSON'daki "id" alanını, Kotlin'deki "id" değişkeniyle eşleştirir.
    @SerializedName("id")
    val id: Int,

    // JSON'daki "name" alanını, Kotlin'deki "name" değişkeniyle eşleştirir.
    @SerializedName("name")
    val name: String
)