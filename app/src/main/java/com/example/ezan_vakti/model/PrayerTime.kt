package com.example.ezan_vakti.model

data class PrayerTime(
    val date: String,    // Tarih
    val fajr: String,    // İmsak
    val sun: String,     // Güneş
    val dhuhr: String,   // Öğle
    val asr: String,     // İkindi
    val maghrib: String, // Akşam
    val isha: String     // Yatsı
)