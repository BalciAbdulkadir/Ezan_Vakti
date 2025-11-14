package com.example.ezan_vakti

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ezan_vakti.model.PrayerTime
import com.example.ezan_vakti.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.Locale

// Tema Renkleri
val IslamicGreen = Color(0xFF1B5E20) // Koyu Yeşil
val SoftGreen = Color(0xFF4CAF50)    // Açık Yeşil (Gradyan için)
val GoldColor = Color(0xFFFFD700)    // Altın Rengi (Sayaç çizgisi)

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                EzanVaktiEkrani(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EzanVaktiEkrani(viewModel: MainViewModel) {

    val namazVakitleri by viewModel.prayerTimes
    val yukleniyor by viewModel.isLoading
    val hataMesaji by viewModel.errorMessage

    // Arama kutusundaki metni, ViewModel'deki 'currentCity' ile senkronize ediyoruz
    var sehirIsmi by remember { mutableStateOf(viewModel.currentCity.value) }

    // Hafızadan veri gelince arama kutusunu güncelle
    LaunchedEffect(viewModel.currentCity.value) {
        sehirIsmi = viewModel.currentCity.value
    }

    // API'den gelen 30 günlük listenin sadece ilkini (bugünü) alıyoruz
    val bugununVakti = namazVakitleri.firstOrNull()

    // --- ANA KAPLAYICI (Box) ---
    // Arka plana yeşil gradyan (renk geçişi) DEĞİŞTİRİLECEK!!!!!!!!
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(SoftGreen, Color(0xFFE8F5E9), Color.White),
                    startY = 0f,
                    endY = 2000f // Geçişin uzunluğu
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ÜST KISIM: Şehir ve Tarih
            Text(
                text = viewModel.currentCity.value.uppercase(Locale("tr", "TR")),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = IslamicGreen
            )

            Text(
                text = formatTarih(bugununVakti?.date), // Tarihi güzelleştirdik
                style = MaterialTheme.typography.bodyMedium,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Geri Sayım Kartı
            GeriSayimKarti(bugununVakti)

            Spacer(modifier = Modifier.height(24.dp))

            //  Vakitler Izgarası
            if (bugununVakti != null) {
                VakitIzgarasi(bugununVakti = bugununVakti)
            } else if (yukleniyor) {
                CircularProgressIndicator(color = IslamicGreen)
            }

            // Arama kutusunu en alta alıyoruz
            Spacer(modifier = Modifier.weight(1f)) // Kalan tüm boşluğu ittir

            OutlinedTextField(
                value = sehirIsmi,
                onValueChange = { sehirIsmi = it },
                label = { Text("Şehir Değiştir") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = { viewModel.getTimesByCity(sehirIsmi) }) {
                        Icon(Icons.Outlined.Search, contentDescription = "Ara", tint = IslamicGreen)
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = IslamicGreen,
                    focusedLabelColor = IslamicGreen,
                    cursorColor = IslamicGreen
                )
            )
            if (hataMesaji.isNotEmpty()) {
                Text(text = hataMesaji, color = Color.Red, modifier = Modifier.padding(top = 8.dp))
            }
        }
    }
}

@Composable
fun GeriSayimKarti(bugununVakti: PrayerTime?) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 24.dp, horizontal = 16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "YATSININ ÇIKMASINA", // Burası da dinamik olacak
                color = Color.Gray,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Burası şimdilik Statik
            Text(
                text = "06 : 59 : 45",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = IslamicGreen,
                textAlign = TextAlign.Center
            )

            // saniye saniye saniye yazısı
            Row(modifier = Modifier.fillMaxWidth(0.8f), horizontalArrangement = Arrangement.SpaceAround) {
                Text("saat", fontSize = 12.sp, color = Color.LightGray)
                Text("dakika", fontSize = 12.sp, color = Color.LightGray)
                Text("saniye", fontSize = 12.sp, color = Color.LightGray)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // İlerleme Çubuğu (Sarı Çizgi)
            LinearProgressIndicator(
                progress = 0.3f, // %30 dolu
                modifier = Modifier.fillMaxWidth().height(8.dp).padding(horizontal = 16.dp),
                color = GoldColor,
                trackColor = Color.Gray.copy(alpha = 0.2f)
            )
        }
    }
}

@Composable
fun VakitIzgarasi(bugununVakti: PrayerTime) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            //ikonları kullanıyoruz
            VakitItem(icon = Icons.Outlined.WbTwilight, ad = "İmsak", saat = bugununVakti.fajr ?: "--:--")
            VakitItem(icon = Icons.Outlined.WbSunny, ad = "Güneş", saat = bugununVakti.sun ?: "--:--")
            VakitItem(icon = Icons.Filled.WbSunny, ad = "Öğle", saat = bugununVakti.dhuhr ?: "--:--")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            VakitItem(icon = Icons.Outlined.WbCloudy, ad = "İkindi", saat = bugununVakti.asr ?: "--:--")
            VakitItem(icon = Icons.Outlined.NightsStay, ad = "Akşam", saat = bugununVakti.maghrib ?: "--:--")
            VakitItem(icon = Icons.Filled.DarkMode, ad = "Yatsı", saat = bugununVakti.isha ?: "--:--")
        }
    }
}


@Composable
fun VakitItem(icon: ImageVector, ad: String, saat: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(80.dp) // Hepsini hizalamak için
    ) {
        Icon(
            imageVector = icon,
            contentDescription = ad,
            tint = Color.Gray,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = ad, fontSize = 13.sp, color = Color.Gray)
        Text(
            text = saat,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = IslamicGreen
        )
    }
}

// API'den gelen "2025-11-14T00:00:00" tarihini "14 Kasım 2025" yapar
fun formatTarih(tarih: String?): String {
    if (tarih == null) return "Tarih yükleniyor..."
    return try {
        val apiFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val hedefFormat = SimpleDateFormat("dd MMMM yyyy", Locale("tr", "TR"))
        val dateObj = apiFormat.parse(tarih)
        hedefFormat.format(dateObj!!)
    } catch (e: Exception) {
        "Tarih formatlanamadı"
    }
}