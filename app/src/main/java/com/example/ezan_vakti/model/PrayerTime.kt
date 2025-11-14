package com.example.ezan_vakti.model

import com.google.gson.annotations.SerializedName

// Bu data class, bir şehre ait günlük namaz vakitlerini temsil eder.
// API'den gelen JSON dizisindeki her bir obje bu yapıya dönüştürülür.
data class PrayerTime(
    // JSON'daki alan adıyla Kotlin'deki değişken adları farklıysa
    // @SerializedName kullanarak eşleştirme yaparız.
    // Örneğin JSON'da "Imsak" olarak gelen veri, bizde "fajr" değişkenine atanır.

    @SerializedName("Imsak")
    val fajr: String, // İmsak vaktini tutar.

    @SerializedName("Gunes")
    val sun: String, // Güneşin doğuş vaktini tutar.

    @SerializedName("Ogle")
    val dhuhr: String, // Öğle vaktini tutar.

    @SerializedName("Ikindi")
    val asr: String, // İkindi vaktini tutar.

    @SerializedName("Aksam")
    val maghrib: String, // Akşam vaktini tutar.

    @SerializedName("Yatsi")
    val isha: String, // Yatsı vaktini tutar.

    @SerializedName("MiladiTarihKisa")
    val date: String // Vakitlerin ait olduğu tarihi tutar (Örn: "15.05.2024").
)