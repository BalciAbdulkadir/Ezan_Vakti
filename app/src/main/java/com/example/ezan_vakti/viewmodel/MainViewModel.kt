package com.example.ezan_vakti.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ezan_vakti.data.DataStoreManager
import com.example.ezan_vakti.model.PrayerTime
import com.example.ezan_vakti.network.RetrofitInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// ViewModel değil, AndroidViewModel
class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val dataStoreManager = DataStoreManager(application) // Hafıza

    val prayerTimes = mutableStateOf<List<PrayerTime>>(emptyList())
    val isLoading = mutableStateOf(false)
    val errorMessage = mutableStateOf("")
    val currentCity = mutableStateOf("Istanbul") // Kayıtlı şehri tutan değişken

    init {
        loadLastCity() // Uygulama açılırken hafızayı yükle
    }

    private fun loadLastCity() {
        viewModelScope.launch {
            val savedCity = dataStoreManager.getCity.first()
            currentCity.value = savedCity
            getTimesByCity(savedCity, isAutoLoad = true) // Otomatik veri çek
        }
    }

    private fun normalizeCityName(cityName: String): String {
        val original = "ıİşŞğĞüÜöÖçÇ"
        val normalized = "iissgguuooicc"
        return cityName.map { char ->
            val index = original.indexOf(char)
            if (index >= 0) normalized[index] else char
        }.joinToString("")
    }

    fun getTimesByCity(cityName: String, isAutoLoad: Boolean = false) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = ""

            try {
                val locale = java.util.Locale("tr", "TR")
                val duzeltilmisSehir = cityName.trim().lowercase(locale)
                val apiCityName = normalizeCityName(duzeltilmisSehir)

                val locationList = RetrofitInstance.api.searchLocation(apiCityName)

                if (locationList.isNotEmpty()) {
                    val cityId = locationList[0].id
                    val times = RetrofitInstance.api.getPrayerTimes(cityId) // ApiService'teki ad
                    prayerTimes.value = times
                    errorMessage.value = ""

                    // Sadece kullanıcı butona bastıysa kaydet
                    if (!isAutoLoad) {
                        dataStoreManager.saveCity(duzeltilmisSehir)
                        currentCity.value = duzeltilmisSehir
                    }
                } else {
                    errorMessage.value = "'${cityName}' bulunamadı!"
                    prayerTimes.value = emptyList()
                }
            } catch (e: Exception) {
                errorMessage.value = "Hata: İnternet bağlantınızı kontrol edin."
                prayerTimes.value = emptyList()
                e.printStackTrace()
            } finally {
                isLoading.value = false
            }
        }
    }
}