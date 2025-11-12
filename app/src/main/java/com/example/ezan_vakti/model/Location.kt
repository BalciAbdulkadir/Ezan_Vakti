package com.example.ezan_vakti.model // Burası senin paket adınla aynı olmalı

data class Location(
    val id: Int,
    val country: String,
    val city: String,
    val region: String // İlçe veya bölge
)