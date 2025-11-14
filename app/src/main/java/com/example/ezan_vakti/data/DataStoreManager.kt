package com.example.ezan_vakti.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// DataStore'u Context üzerinden bir extension olarak oluşturuyoruz.
// Bu, "preferences" adında bir dosya oluşturup verileri oraya kaydedecek.
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

// Bu sınıf, DataStore ile ilgili tüm okuma ve yazma işlemlerini bir araya topluyor.
// ViewModel gibi yerlerden bu sınıfı kullanarak veriye erişeceğiz.
class DataStoreManager(context: Context) {

    private val dataStore = context.dataStore

    // Bu "companion object" içinde, kaydedeceğimiz veriler için anahtarlar tanımlıyoruz.
    // Tıpkı bir veritabanı sütunu veya SharedPreferences anahtarı gibi düşünebiliriz.
    companion object {
        // Son aranan şehri saklamak için kullanılacak anahtar.
        val LAST_CITY_KEY = stringPreferencesKey("last_city")
    }

    // Son aranan şehri DataStore'a kaydeder.
    // `suspend` olması önemli, çünkü DataStore I/O işlemi yapar ve ana thread'i bloklamamalı.
    suspend fun saveLastCity(city: String) {
        dataStore.edit {
            it[LAST_CITY_KEY] = city
        }
    }

    // Kayıtlı olan son şehri geri döndürür.
    // Değer değiştiğinde otomatik olarak yeni değeri alabilmek için Flow olarak döndürüyoruz.
    // Flow, Coroutine'ler ile birlikte çalışan, zamanla değişebilen bir veri akışıdır.
    fun getLastCity(): Flow<String?> {
        return dataStore.data.map {
            it[LAST_CITY_KEY]
        }
    }
}