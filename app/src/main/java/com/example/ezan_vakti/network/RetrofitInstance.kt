package com.example.ezan_vakti.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    // API'nin ana adresi (Sonunda / olması zorunludur)
    private const val BASE_URL = "https://prayertimes.api.abdus.dev/"

    // Retrofit nesnesini oluşturuyoruz
    // 'by lazy' demek: Uygulama açılır açılmaz değil, ilk ihtiyaç duyulduğunda oluştur demek (Performans için)
    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // Gelen veriyi (JSON) otomatik çevir
            .build()
            .create(ApiService::class.java)
    }
}