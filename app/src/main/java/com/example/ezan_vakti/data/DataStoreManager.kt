package com.example.ezan_vakti.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

// 'user_prefs' adında bir veritabanı dosyası oluşturuyoruz
private val Context.dataStore by preferencesDataStore(name = "user_prefs")

class DataStoreManager(private val context: Context) {

    // Kaydedeceğimiz verinin anahtar kelimesi
    companion object {
        val CITY_KEY = stringPreferencesKey("CITY_KEY")
    }

    // Şehri kaydetme fonksiyonu
    suspend fun saveCity(city: String) {
        context.dataStore.edit { preferences ->
            preferences[CITY_KEY] = city
        }
    }

    // Kayıtlı şehri okuma fonksiyonu
    // Akış (Flow) olarak okur, veri değiştiğinde anında haber verir
    val getCity: kotlinx.coroutines.flow.Flow<String> = context.dataStore.data
        .map { preferences ->
            // Eğer hafızada bir şehir yoksa, varsayılan olarak "Istanbul" döner
            preferences[CITY_KEY] ?: "Istanbul"
        }
}