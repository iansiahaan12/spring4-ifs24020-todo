# Spring v4.0 Starter Proyek

## Logs

### 29-10-2025

- Melakukan inisialisasi proyek
- Menambahkan method 'sayHello()' pada HomeController
- menambahkan pengujian untuk method 'sayHello()'

### Update dari logs

- Menambahkan endpoint informasiNim/{nim} untuk decoding informasi NIM
- Menambahkan endpoint perolehanNilai/{strBase64} untuk perhitungan nilai akademik
- Menambahkan endpoint perbedaanL/{strBase64} untuk analisis pattern L pada matriks
- Menambahkan endpoint palingTer/{strBase64} untuk analisis frekuensi dan statistik data numerik
- Implementasi sistem konversi nilai ke huruf mutu dengan ambang batas yang terdefinisi
- Penambahan kamus program studi untuk decoding NIM
- Validasi input Base64 dan handling exception yang komprehensif

## Syntax

### Melakukan Instal Ulang Kebutuhan Paket

command: `mvn clean install`

### Menjalankan Aplikasi

Command: `mvn spring-boot:run`

URL: http://localhost:8080

### Menjalankan Test Covertage

command: `./mvnw test jacoco:report`

command-check: `./mvnw clean test jacoco:check`

update
