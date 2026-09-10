# Ch04 Starter — To-Do List Tugas Pertemuan 4

Starter project untuk **Tugas Pertemuan 4** (lihat [`codes/praktikum/ch04/README.md`](../ch04/README.md)
untuk materi praktikum lengkapnya). Proyek ini **sudah bisa di-build dan
dijalankan apa adanya** — tiga tab kosong/setengah-jadi dengan marker
`// TODO` persis di titik yang harus kalian isi. Tidak perlu membuat
proyek baru dari nol; cukup buka folder ini di Android Studio dan mulai dari
checklist di bawah.

Cari komentar `// TODO` di kode untuk instruksi detail per langkah — checklist
di README ini hanya ringkasannya.

---

## Cara Membuka

1. **File → Open** → pilih folder `ch04_starter/`
2. Tunggu Gradle sync
3. Pilih tab di bottom navigation (BMI / Suhu / Registrasi) → klik **Run ▶**

---

## To-Do List

### 1. `BmiScreen.kt` — lengkapi Kalkulator BMI

- [ ] **1a.** Tabel interpretasi BMI di bawah kartu hasil (4 baris:
      Kurus / Normal / Gemuk / Obesitas + rentang nilainya)
      → cari `TODO 1a` (ada dua: composable `InterpretasiBmiTable()` yang
      masih di-comment, dan titik pemanggilannya)
- [ ] **1b.** Tombol **Reset** di sebelah tombol "Hitung BMI" yang
      mengembalikan berat → 60 kg, tinggi → 165 cm, dan menyembunyikan
      hasil lagi → cari `TODO 1b`

### 2. `SuhuScreen.kt` — Kalkulator Konversi Suhu

- [ ] **2a.** Parse input Celsius (`String`) jadi `Float` dengan aman
      (`toFloatOrNull()`) → cari `TODO 2a`
- [ ] **2b.** Hitung `fahrenheit` (`C × 9/5 + 32`) dan `kelvin`
      (`C + 273.15`) memakai `derivedStateOf`, mengikuti pola `bmi` di
      `BmiScreen.kt` → cari `TODO 2b`
- [ ] *(Tantangan, opsional)* Buat ketiga field (Celsius/Fahrenheit/Kelvin)
      bisa diketik bebas, saling mengisi otomatis, **tanpa** circular
      update — baca catatan lengkap di komentar TODO paling atas file ini

### 3. `RegistrasiScreen.kt` — Form Registrasi

- [ ] **3a.** Validasi `nama` tidak boleh kosong
- [ ] **3b.** Validasi `email` mengandung karakter `@`
- [ ] **3c.** Validasi `password` minimal 8 karakter
- [ ] **3d.** Validasi `konfirmasiPassword` sama persis dengan `password`
- [ ] **3e.** Gabungkan 3a–3d jadi satu `derivedStateOf<Boolean>`
      bernama `isFormValid`, pasang ke parameter `enabled` tombol **Daftar**
      → cari `TODO 3e` untuk kerangka kodenya

### 4. Tantangan — Snackbar dengan `LaunchedEffect`

- [ ] Saat tombol **Hitung BMI** ditekan, tampilkan `Snackbar` berisi
      kategori BMI (mis. "Berat Badan Normal") selama 3 detik, memakai
      `LaunchedEffect` yang bereaksi terhadap perubahan kategori — **bukan**
      memanggil `showSnackbar()` langsung di dalam `onClick`
      → cari `TODO 4` di `BmiScreen.kt` (ada dua titik: siapkan
      `SnackbarHostState`, lalu picu efeknya)

---

## Kriteria Selesai

Sebelum push, pastikan:

- [ ] Semua marker `TODO` di ketiga file sudah diganti kode asli (boleh
      `grep -rn "TODO" app/src` untuk memastikan tidak ada yang terlewat)
- [ ] App berjalan tanpa crash di ketiga tab
- [ ] Rotasi layar tidak menghilangkan input yang sedang diisi (`rememberSaveable`
      sudah dipasang di starter — jangan dihapus)
- [ ] Tombol Daftar di RegistrasiScreen benar-benar disabled sampai form valid

**Pengumpulan:** push ke branch `pertemuan-4` → kumpulkan link di LMS
sebelum pertemuan berikutnya.
