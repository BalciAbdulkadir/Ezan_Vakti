package com.example.ezan_vakti.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ezan_vakti.data.DataStoreManager
import com.example.ezan_vakti.model.PrayerTime
import com.example.ezan_vakti.network.RetrofitInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// DataStore'u kullanabilmek için ViewModel'in kurucusuna eklememiz gerekiyor.
class MainViewModel(private val dataStoreManager: DataStoreManager) : ViewModel() {

    // Bu state'ler Composable fonksiyonlar tarafından dinlenecek.
    val prayerTimes = mutableStateOf<List<PrayerTime>>(emptyList())
    val isLoading = mutableStateOf(false)
    val errorMessage = mutableStateOf("")
    val currentCity = mutableStateOf("İstanbul") // Başlangıçta ve aramalarda kullanılacak şehir.

    init {
        // ViewModel ilk ayağa kalktığında, hafızadaki son şehri yüklemeyi dene.
        // Bu, uygulama her açıldığında kullanıcının en son baktığı yeri görmesini sağlar.
        viewModelScope.launch {
            // DataStore'dan veriyi `flow.first()` ile tek seferlik çekiyoruz.
            val savedCity = dataStoreManager.getLastCity().first() ?: "istanbul" // Eğer kayıtlı şehir yoksa İstanbul'u kullan.
            getTimesByCity(savedCity)
        }
    }

    // API, "niğde" gibi Türkçe karakterli sorguları anlamıyor.
    // Bu yüzden "nigde" gibi bir formata çevirmemiz lazım.
    private fun normalizeCityName(cityName: String): String {
        val original = "ıİşŞğĞüÜöÖçÇ"
        val normalized = "iissgguuooicc"
        return cityName.map { char ->
            val index = original.indexOf(char)
            if (index >= 0) normalized[index] else char
        }.joinToString("")
    }

    fun getTimesByCity(cityName: String) {
        // Ana UI thread'ini bloklamamak için tüm network işlemleri Coroutine içinde yapılmalı.
        viewModelScope.launch {
            isLoading.value = true // Arayüzde yükleniyor animasyonu başlasın.
            errorMessage.value = "" // Önceki hataları temizle.

            try {
                // API küçük harf beklediği için, kullanıcı nasıl yazarsa yazsın küçük harfe çeviriyoruz.
                val locale = java.util.Locale("tr", "TR")
                val cleanedCityName = cityName.trim().lowercase(locale)
                val apiCityName = normalizeCityName(cleanedCityName)

                // 1. Adım: Şehir adıyla ID'sini bul.
                val locationList = RetrofitInstance.api.searchLocation(apiCityName)

                if (locationList.isNotEmpty()) {
                    // API'den şehir bulunduysa...
                    val foundCityName = cleanedCityName.replaceFirstChar { it.titlecase(locale) }
                    currentCity.value = foundCityName // Arayüzdeki başlığı güncelle.

                    // Son başarılı aramayı hafızaya kaydet.
                    dataStoreManager.saveLastCity(foundCityName)

                    // 2. Adım: Bulunan ID ile namaz vakitlerini çek.
                    val cityId = locationList[0].id
                    val times = RetrofitInstance.api.getPrayerTimes(cityId)
                    prayerTimes.value = times
                    errorMessage.value = "" // Her şey yolunda, hata mesajı olmasın.

                } else {
                    // Eğer API bu isimde bir şehir bulamadıysa, kullanıcıyı bilgilendir.
                    errorMessage.value = "'$cityName' bulunamadı! Farklı bir isimle deneyin."
                    prayerTimes.value = emptyList() // Ekranda eski veri kalmasın.
                }

            } catch (e: Exception) {
                // İnternet yoksa veya API çöktüyse bu blok çalışır.
                errorMessage.value = "Veriler alınırken bir sorun oluştu."
                prayerTimes.value = emptyList()
                e.printStackTrace() // Sorunu ayıklamak için hatayı Logcat'e yazdır.
            } finally {
                // İşlem başarılı da olsa, hata da olsa yükleniyor animasyonunu durdur.
                isLoading.value = false
            }
        }
    }
}

// ViewModel'e kurucu (constructor) üzerinden parametre (DataStoreManager gibi) geçmek istediğimizde
// bu şekilde bir Factory sınıfı oluşturmamız gerekiyor. Android sistemi, ViewModel'i nasıl
// yaratacağını bu Factory'den öğreniyor.
class MainViewModelFactory(private val dataStoreManager: DataStoreManager) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(dataStoreManager) as T
        }
        throw IllegalArgumentException("Bu Factory sadece MainViewModel içindir.")
    }
}