package com.example.ezan_vakti.network

import com.example.ezan_vakti.model.Location
import com.example.ezan_vakti.model.PrayerTime
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    // Şehir arama fonksiyonu (Örn: istanbul yazınca ID'sini bulacak)
    // Adres: https://prayertimes.api.abdus.dev/api/diyanet/search?q=istanbul
    @GET("api/diyanet/search")
    suspend fun searchLocation(@Query("q") query: String): List<Location>

    // Namaz vakitlerini çekme fonksiyonu (ID ile)
    // Adres: https://prayertimes.api.abdus.dev/api/diyanet/prayertimes?location_id=1234
    @GET("api/diyanet/prayertimes")
    suspend fun getPrayerTimes(@Query("location_id") locationId: Int): List<PrayerTime>
}