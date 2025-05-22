
📱 LatteProje - Tarama Modülü
Bu modül, Android tabanlı bir görüntü işleme uygulamasının kamera ile ürün tarama ve hatalı/sağlam ürün tespiti yapan bölümüdür. Gerçek zamanlı kamera görüntüsü üzerinden TensorFlow Lite (TFLite) kullanılarak sınıflandırma yapılır. Sınıflandırma sonucunda ürün hatalıysa veritabanına kaydedilir, sağlamsa ayrı bir listeye alınır.

🚀 Özellikler
📷 Canlı kamera görüntüsü ile analiz

🧠 TensorFlow Lite modeliyle ürün sınıflandırması (Hatalı / Sağlam)

🗃 SQLite veritabanına kayıt (tarih ve saat ile)

✅ Kamera izni yönetimi

🔁 Gerçek zamanlı görüntü işleme (128x128 çözünürlükte)

🔄 Otomatik dönüş düzeltmesi (image rotation)

🛠 Kullanılan Teknolojiler
Android CameraX API

TensorFlow Lite (model.tflite)

Kotlin

Bitmap ve ByteBuffer işleme

SQLite veritabanı

Live image analysis (ImageProxy → Bitmap)

🧠 Model Yapısı
Modeliniz model.tflite dosyası olarak assets/ klasöründe yer almalıdır ve 128x128 boyutunda, 3 kanallı RGB görüntüleri giriş olarak beklemelidir.

Çıkış:

FloatArray(2) — 2 sınıf için softmax sonuçları.

output[0]: Hatalı Ürün skoru

output[1]: Sağlam Ürün skoru

📂 Dosya Yapısı
pgsql
Kopyala
Düzenle
TaramaActivity.kt           # Kamera görüntüsü alır ve model ile tahmin yapar
HataliUrunDatabase.kt       # Hatalı ürünlerin SQLite'a kaydedilmesi
UrunlerDatabase.kt          # Sağlam ürünlerin SQLite'a kaydedilmesi
model.tflite                # TensorFlow Lite model dosyası (assets içinde)
ActivityTaramaBinding       # XML layout için View Binding
📸 Uygulama Akışı
Uygulama başlatıldığında kamera izni istenir.

Kamera açılır ve görüntü alınır.

Görüntü ImageProxy nesnesinden Bitmap’e dönüştürülür.

Bitmap TensorFlow Lite modeli ile analiz edilir.

Sonuç:

Hatalıysa HataliUrunDatabase’e kayıt edilir.

Sağlamsa UrunlerDatabase’e kayıt edilir.

Kullanıcıya durum (Hatalı Ürün / Sağlam Ürün) gösterilir.

🧪 Model Girdisi ve Ön İşleme
Görüntü 128x128 boyutuna yeniden boyutlandırılır.

RGB değerleri [0,1] aralığına normalize edilir.

Float32 ByteBuffer formatında modele iletilir.

📦 UrunlerActivity – Sağlam Ürün Listeleme Ekranı
UrunlerActivity, sağlam olarak sınıflandırılmış ürünlerin veritabanından listelendiği ve yönetildiği ekrandır. Bu sayfa, kullanıcıya toplam sağlam ürün sayısını, liste halinde kayıt tarihlerini gösterir ve listeyi sıralama/temizleme işlemlerini gerçekleştirmesine olanak tanır.

🔧 Özellikler
📋 Sağlam Ürün Listesi Gösterimi:
SQLite veritabanından (UrunlerDatabase) çekilen sağlam ürün kayıtları, RecyclerView aracılığıyla ekranda sıralı biçimde görüntülenir.

🔃 Sıralama Desteği:

Artan sıralama (varsayılan – en eski kayıt en üstte)

Azalan sıralama (en yeni kayıt en üstte)

Kullanıcı sıralama düğmelerini kullanarak görünümü değiştirebilir.

🗑️ Tüm Kayıtları Temizleme:
Sil butonuna basıldığında veritabanındaki tüm sağlam ürün kayıtları temizlenir.

🔢 Toplam Ürün Sayısı Gösterimi:
Sayfanın üst kısmında toplam sağlam ürün sayısı yer alır.

🔙 Geri Dönüş:
Sol üstteki geri butonuyla MainActivity sayfasına dönüş yapılır.

🗃️ Kullanılan Bileşenler
RecyclerView – Liste görünümü için

UrunlerAdapter – RecyclerView adaptörü

UrunlerDatabase – Veritabanı işlemleri (kayıt listeleme, silme, sayma)

ActivityUrunlerBinding – View binding ile erişim

💡 Notlar
Liste verileri "1. 2025-05-22 14:35:02" formatında sıralı şekilde gösterilir.

Sıralama butonları dinamik olarak görünürlük değiştirir (örn: "Sırala" görünüyorsa "Ters Sırala" gizlenir).

Sayfa açıldığında veriler varsayılan olarak artan şekilde sıralanır (ASC).

📁 İlgili Dosyalar
UrunlerDatabase.kt – Veritabanı işlemleri (listeleme, temizleme, sayma)

UrunlerAdapter.kt – RecyclerView adaptörü

MainActivity.kt – Geri dönüş yapılan ana sayfa


https://github.com/user-attachments/assets/cdf9f6ca-0877-41ca-9828-7c452f639655

https://github.com/user-attachments/assets/6836d313-e7ee-4689-bbd5-374ccca243b1

![urunler](https://github.com/user-attachments/assets/70d436d7-d491-4716-afa7-e6f557a3152b)
![hatali urunler](https://github.com/user-attachments/assets/19073d3b-d89b-494c-8e04-4d5f7c8cc800)

