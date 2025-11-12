package com.example.ezan_vakti

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ezan_vakti.model.PrayerTime
import com.example.ezan_vakti.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    // ViewModel'i burada başlatıyoruz
    private val viewModel = MainViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Uygulamanın ana teması
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    EzanVaktiEkrani(viewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EzanVaktiEkrani(viewModel: MainViewModel) {
    // Kullanıcının yazdığı şehir ismini tutan değişken
    var sehirIsmi by remember { mutableStateOf("Istanbul") }

    // ViewModel'den gelen verileri dinliyoruz
    val namazVakitleri = viewModel.prayerTimes.value
    val yukleniyor = viewModel.isLoading.value
    val hataMesaji = viewModel.errorMessage.value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- Başlık ---
        Text(
            text = "Ezan Vakti",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- Arama Kutusu ve Buton ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = sehirIsmi,
                onValueChange = { sehirIsmi = it },
                label = { Text("Şehir Girin (Örn: Ankara)") },
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(onClick = {
                // Butona basınca ViewModel'e "Git bul" diyoruz
                viewModel.getTimesByCity(sehirIsmi)
            }) {
                Text("Bul")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- Durum Göstergeleri ---
        if (yukleniyor) {
            CircularProgressIndicator() // Dönen yükleniyor çemberi
        }

        if (hataMesaji.isNotEmpty()) {
            Text(text = hataMesaji, color = Color.Red)
        }

        // --- Liste (Namaz Vakitleri) ---
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(namazVakitleri) { vakit ->
                NamazVaktiKarti(vakit)
            }
        }
    }
}

@Composable
fun NamazVaktiKarti(vakit: PrayerTime) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Tarih: ${vakit.date}", fontWeight = FontWeight.Bold)
            Divider(modifier = Modifier.padding(vertical = 8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                VakitSatiri("İmsak", vakit.fajr)
                VakitSatiri("Güneş", vakit.sun)
                VakitSatiri("Öğle", vakit.dhuhr)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                VakitSatiri("İkindi", vakit.asr)
                VakitSatiri("Akşam", vakit.maghrib)
                VakitSatiri("Yatsı", vakit.isha)
            }
        }
    }
}

@Composable
fun VakitSatiri(isim: String, saat: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = isim, fontSize = 12.sp, color = Color.Gray)
        Text(text = saat, fontSize = 16.sp, fontWeight = FontWeight.Medium)
    }
}