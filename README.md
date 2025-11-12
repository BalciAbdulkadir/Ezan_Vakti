# 🕌 Ezan Vakti (Prayer Times) - Android MVP

Modern Android teknolojileri kullanılarak geliştirilmiş, minimalist ve performans odaklı Ezan Vakti uygulaması.

Bu proje, kullanıcıların seçtiği konuma göre Diyanet uyumlu namaz vakitlerini anlık olarak görüntüler. **MVVM (Model-View-ViewModel)** mimarisi ve **Jetpack Compose** arayüz kiti ile geliştirilmiştir.

## 📱 Ekran Görüntüleri

*(Ekran görüntüleri yakında eklenecektir)*

## 🛠️ Kullanılan Teknolojiler ve Kütüphaneler

Bu proje, modern Android geliştirme standartlarına uygun olarak hazırlanmıştır:

* **Dil:** [Kotlin](https://kotlinlang.org/) (%100)
* **Arayüz (UI):** [Jetpack Compose](https://developer.android.com/jetpack/compose) (XML kullanılmadı)
* **Mimari:** MVVM (Model - View - ViewModel)
* **Ağ İstekleri (Networking):** [Retrofit 2](https://square.github.io/retrofit/) & OkHttp
* **Veri Dönüştürme:** Gson
* **Eşzamanlılık (Concurrency):** Kotlin Coroutines & Scope Functions
* **Minimum SDK:** API 24 (Android 7.0)

## 🚀 Özellikler

* **Şehir Arama:** Kullanıcı herhangi bir il veya ilçe ismini girerek arama yapabilir.
* **Akıllı Türkçe Karakter Desteği:** "izmir", "uskudar", "ÇANKIRI" gibi farklı yazımları otomatik algılar ve düzeltir.
* **Günlük Vakitler:** İmsak, Güneş, Öğle, İkindi, Akşam ve Yatsı vakitlerini listeler.
* **Hata Yönetimi:** İnternet bağlantısı veya API sorunlarında kullanıcıyı bilgilendirir.

## 🔌 API Kaynağı

Veriler, Diyanet İşleri Başkanlığı verilerini sağlayan açık kaynaklı bir servis üzerinden çekilmektedir:
* Base URL: `https://prayertimes.api.abdus.dev/`

## ⚙️ Kurulum ve Çalıştırma

Projeyi yerel makinenizde çalıştırmak için:

1.  Bu repoyu klonlayın:
    ```bash
    git clone [https://github.com/KULLANICI_ADIN/ezan_vakti.git](https://github.com/KULLANICI_ADIN/ezan_vakti.git)
    ```
2.  **Android Studio**'yu açın ve `File > Open` diyerek klasörü seçin.
3.  Gradle senkronizasyonunun bitmesini bekleyin.
4.  Emülatör veya fiziksel cihazınızı seçerek **Run (▶️)** tuşuna basın.

## 🗺️ Yol Haritası (Roadmap)

Bu sürüm **MVP (Minimum Viable Product)** aşamasındadır. Gelecek güncellemeler için planlanan özellikler:

- [ ] Şık ve modern arayüz (UI) geliştirmeleri
- [ ] Konum (GPS) ile otomatik şehir bulma
- [ ] Vakit gelince bildirim gönderme (AlarmManager)
- [ ] Widget desteği
- [ ] **[GİZLİ]** İnovatif özellik (Geliştirme aşamasında)

## 🤝 Katkıda Bulunma

Pull request'ler kabul edilir. Büyük değişiklikler için lütfen önce tartışma başlatınız.

## 📄 Lisans

[MIT](https://choosealicense.com/licenses/mit/)