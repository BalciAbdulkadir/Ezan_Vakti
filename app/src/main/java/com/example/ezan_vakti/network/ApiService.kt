package com.example.ezan_vakti.network

import com.example.ezan_vakti.model.Location
import com.example.ezan_vakti.model.PrayerTime
import retrofit2.http.GET
import retrofit2.http.Query

// Retrofit bu arayüzü kullanarak API çağrılarını yapacak.
// Her bir fonksiyon, API'nin bir endpoint'ine (uç noktasına) karşılık geliyor.
interface ApiService {

    // Bu fonksiyon, kullanıcı "istanbul" gibi bir arama yaptığında,
    // API'den bu şehre ait ID'yi ve diğer bilgileri getirmek için kullanılıyor.
    // Örnek URL: https://prayertimes.api.abdus.dev/api/diyanet/search?q=istanbul
    @GET("api/diyanet/search")
    suspend fun searchLocation(@Query("q") query: String): List<Location>

    // Şehir ID'sini bulduktan sonra, bu fonksiyon ile o ID'ye ait günlük namaz vakitlerini çekiyoruz.
    // Örnek URL: https://prayertimes.api.abdus.dev/api/diyanet/prayertimes?location_id=9541
    @GET("api/diyanet/prayertimes")
    suspend fun getPrayerTimes(@Query("location_id") locationId: Int): List<PrayerTime>
}