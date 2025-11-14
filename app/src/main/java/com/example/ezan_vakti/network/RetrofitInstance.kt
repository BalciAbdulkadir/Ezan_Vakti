package com.example.ezan_vakti.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Retrofit objesini projenin her yerinden kolayca erişilebilecek
// tek bir yerden yönetmek için bu "object"i kullanıyoruz (Singleton pattern).
object RetrofitInstance {

    // API'nin ana URL'i. Bütün endpointler bu adresten sonra gelir.
    private const val BASE_URL = "https://prayertimes.api.abdus.dev/"

    // `by lazy` kullanarak, Retrofit objesinin sadece ihtiyaç duyulduğunda
    // (yani ilk defa çağrıldığında) bir kere oluşturulmasını sağlıyoruz.
    // Bu, uygulama başlarken gereksiz yere kaynak harcamasını önler.
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL) // API'nin ana adresini belirtiyoruz.
            .addConverterFactory(GsonConverterFactory.create()) // Gelen JSON verilerini Kotlin objelerine (data class) çevirmek için.
            .build()
    }

    // ApiService arayüzünü kullanarak API çağrıları yapacak olan asıl nesne.
    // Arayüzdeki fonksiyonları (searchLocation, getPrayerTimes) bu nesne üzerinden çağıracağız.
    val api: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}