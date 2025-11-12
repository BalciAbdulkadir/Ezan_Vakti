package com.example.ezan_vakti.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ezan_vakti.model.PrayerTime
import com.example.ezan_vakti.network.RetrofitInstance
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    // Ekranda göstereceğimiz namaz vakitleri listesi (Başlangıçta boş)
    val prayerTimes = mutableStateOf<List<PrayerTime>>(emptyList())

    // Yükleniyor simgesi göstermek için bir durum (Başlangıçta false)
    val isLoading = mutableStateOf(false)

    // Hata olursa mesajı göstermek için
    val errorMessage = mutableStateOf("")

    // Şehir adına göre verileri çeken asıl fonksiyon


    fun getTimesByCity(cityName: String) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = ""

            try {
                // DÜZELTME: Gelen şehri Türkçe kurallarına göre düzeltiyoruz.
                // Örn: "izmir" -> "İzmir", "şarkışla" -> "Şarkışla"
                val locale = java.util.Locale("tr", "TR")
                val duzeltilmisSehir = cityName.trim().lowercase(locale).replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(locale) else it.toString()
                }

                // Logcat'e yazdıralım (Aşağıda anlatacağım)
                println("Aranan Şehir (Orijinal): $cityName")
                println("Aranan Şehir (Düzeltilmiş): $duzeltilmisSehir")

                // 1. Adım: Arama yap
                val locationList = RetrofitInstance.api.searchLocation(duzeltilmisSehir)

                if (locationList.isNotEmpty()) {
                    val cityId = locationList[0].id
                    println("Bulunan Şehir ID: $cityId") // ID'yi buldu mu görelim

                    // 2. Adım: Vakitleri çek
                    val times = RetrofitInstance.api.getPrayerTimes(cityId)
                    prayerTimes.value = times
                } else {
                    // Eğer liste boş geldiyse
                    errorMessage.value = "$duzeltilmisSehir bulunamadı! Tam adını yazmayı deneyin (örn: Üsküdar)."
                    println("HATA: Şehir listesi boş döndü.")
                }

            } catch (e: Exception) {
                errorMessage.value = "Hata: ${e.localizedMessage}"
                e.printStackTrace() // Hatayı Logcat'e yazdır
            } finally {
                isLoading.value = false
            }
        }
    }
}