package com.example.ezan_vakti

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels // `by viewModels()` delegesi için bu şart.
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.* // remember, mutableStateOf, vb. için lazım.
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ezan_vakti.viewmodel.MainViewModel
// Bazen Android Studio bu importları otomatik eklemiyor, elle eklemek gerekebiliyor.
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

class MainActivity : ComponentActivity() {
    // viewModels delegesi sayesinde ViewModel'i kolayca oluşturuyoruz.
    // Bu yapı, ekran döndürme gibi durumlarda ViewModel'in hayatta kalmasını sağlar.
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EzanVaktiEkrani(viewModel = viewModel)
        }
        // Uygulama ilk açıldığında ViewModel zaten son şehri yüklüyor,
        // o yüzden burada tekrar çağırmamıza gerek kalmadı.
    }
}

// Projenin genel renk paletini buradan yönetebiliriz.
val IslamicGreen = Color(0xFF1B5E20)
val SoftGreen = Color(0xFF4CAF50)
val GoldColor = Color(0xFFFFD700)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EzanVaktiEkrani(viewModel: MainViewModel) {
    // ViewModel'deki state'leri `by` ile bağlayarak kodu daha temiz hale getiriyoruz.
    // Artık `.value` yazmamıza gerek kalmıyor.
    val namazVakitleri by viewModel.prayerTimes
    val yukleniyor by viewModel.isLoading
    val hataMesaji by viewModel.errorMessage

    // Arama kutusunun metnini tutacak state. Başlangıçta son aranan şehri gösterir.
    var searchText by remember { mutableStateOf(viewModel.currentCity.value) }

    // ViewModel'deki şehir (örneğin DataStore'dan yüklendiğinde) değişirse,
    // arama kutusundaki metni de güncelliyoruz. Böylece arayüz hep senkronize kalır.
    LaunchedEffect(viewModel.currentCity.value) {
        searchText = viewModel.currentCity.value
    }

    // API'den gelen listenin sadece ilk elemanını, yani bugünün vaktini alıyoruz.
    val bugununVakti = namazVakitleri.firstOrNull()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(colors = listOf(SoftGreen, Color.White)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- ARAMA KUTUSU ---
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    label = { Text("Şehir Ara") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = IslamicGreen,
                        focusedLabelColor = IslamicGreen,
                        cursorColor = IslamicGreen
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { viewModel.getTimesByCity(searchText) },
                    colors = ButtonDefaults.buttonColors(containerColor = IslamicGreen)
                ) {
                    Text("Getir")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- İÇERİK GÖSTERİM ALANI ---
            if (yukleniyor) {
                // Veri yüklenirken dönen bir progress indicator göster.
                CircularProgressIndicator(color = IslamicGreen)
            } else if (hataMesaji.isNotEmpty()) {
                // Eğer bir hata oluştuysa, kullanıcıyı bilgilendir.
                Text(
                    text = hataMesaji,
                    color = Color.Red,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp)
                )
            } else if (bugununVakti != null) {
                // Veri başarıyla geldiyse, vakitleri göster.
                val displayCity = viewModel.currentCity.value.uppercase(java.util.Locale("tr", "TR"))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = displayCity,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = IslamicGreen
                    )
                    Text(
                        text = bugununVakti.date ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.DarkGray
                    )

                    Spacer(modifier = Modifier.height(30.dp))

                    VakitlerKarti(bugununVakti = bugununVakti)
                }
            }
        }
    }
}

@Composable
fun VakitlerKarti(bugununVakti: com.example.ezan_vakti.model.PrayerTime) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Bu kartın içeriği şimdilik statik, ileride dinamik hale getirilebilir.
        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            elevation = CardDefaults.cardElevation(10.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Bir Sonraki Vakte Kalan", color = Color.Gray, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text("02 : 45 : 12", fontSize = 48.sp, fontWeight = FontWeight.Bold, color = IslamicGreen)
                Text("saat   dakika   saniye", fontSize = 12.sp, color = Color.LightGray)
                Spacer(modifier = Modifier.height(16.dp))
                LinearProgressIndicator(
                    progress = 0.6f, // Örnek bir ilerleme değeri
                    modifier = Modifier.fillMaxWidth().height(6.dp),
                    color = GoldColor,
                    trackColor = Color.LightGray.copy(alpha = 0.3f)
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Vakitleri ızgara düzeninde gösteren bölüm.
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                VakitItem(icon = Icons.Outlined.WbTwilight, ad = "İmsak", saat = bugununVakti.fajr)
                VakitItem(icon = Icons.Outlined.WbSunny, ad = "Güneş", saat = bugununVakti.sun)
                VakitItem(icon = Icons.Filled.WbSunny, ad = "Öğle", saat = bugununVakti.dhuhr)
            }
            Spacer(modifier = Modifier.height(20.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                VakitItem(icon = Icons.Outlined.WbCloudy, ad = "İkindi", saat = bugununVakti.asr)
                VakitItem(icon = Icons.Outlined.NightsStay, ad = "Akşam", saat = bugununVakti.maghrib)
                VakitItem(icon = Icons.Filled.DarkMode, ad = "Yatsı", saat = bugununVakti.isha)
            }
        }
    }
}

// Her bir namaz vaktini temsil eden küçük kutucuk.
@Composable
fun VakitItem(icon: androidx.compose.ui.graphics.vector.ImageVector, ad: String, saat: String?) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(imageVector = icon, contentDescription = ad, tint = Color.Gray, modifier = Modifier.size(28.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = ad, fontSize = 12.sp, color = Color.Gray)
        Text(text = saat ?: "--:--", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = IslamicGreen)
    }
}
