# Ch05 Starter — To-Do List Tugas Pertemuan 5

Starter project untuk **Tugas Pertemuan 5** (lihat [`codes/praktikum/ch05/README.md`](../ch05/README.md)
untuk materi praktikum lengkapnya: Routes, NavHost, Bottom Navigation, argument
antar layar). Proyek ini **sudah bisa di-build dan dijalankan apa adanya** —
navigasi tiga tab, Beranda → Detail, dan tombol back semuanya sudah berfungsi
persis seperti hasil praktikum. Yang tersisa hanya tiga soal Tugas Pertemuan 5,
ditandai `// TODO` di kode.

Cari komentar `// TODO` di kode untuk instruksi detail per langkah — checklist
di README ini hanya ringkasannya.

---

## Cara Membuka

1. **File → Open** → pilih folder `ch05_starter/`
2. Tunggu Gradle sync (akan mengunduh `androidx.navigation:navigation-compose`)
3. Pilih device/emulator → klik **Run ▶**
4. Coba dulu: klik artikel di Beranda → masuk Detail → tombol back → pindah
   tab Eksplorasi/Profil. Semua ini sudah jalan — titik awal kalian.

---

## To-Do List

### 1. `ExploreScreen.kt` — Grid Foto

- [ ] Ganti `Box` placeholder dengan `LazyVerticalGrid(columns = GridCells.Fixed(2))`
- [ ] Render `dummyPhotos` (sudah disiapkan di file ini) sebagai grid 2 kolom
- [ ] Beri jarak antar sel (`contentPadding`, `verticalArrangement`/`horizontalArrangement`
      = `Arrangement.spacedBy(8.dp)`)

Kerangka kode lengkap ada di komentar `TODO Pertemuan 5` di bagian atas file.

### 2. `MainScreen.kt` — Animasi Transisi Antar Layar

- [ ] Tambahkan `enterTransition` dan `exitTransition` ke keempat
      `composable(...)` (Home, Explore, Profile, Detail) di dalam `NavHost`
- [ ] Pastikan animasi terasa konsisten (mis. semua slide dari arah yang sama)

### 3. Tantangan — Deep Link

- [ ] Daftarkan `<intent-filter>` untuk skema `myapp://article/{id}` di
      `AndroidManifest.xml` → cari `TODO 3` di sana
- [ ] Tambahkan `deepLinks = listOf(navDeepLink { uriPattern = "myapp://article/{itemId}" })`
      pada `composable(Routes.Detail.route, ...)` di `MainScreen.kt` → cari `TODO 3` di sana
- [ ] Uji dengan:
      ```bash
      adb shell am start -W -a android.intent.action.VIEW \
          -d "myapp://article/3" com.example.ch05starter
      ```
      (perhatikan package name `com.example.ch05starter`, bukan `ch05` — beda
      dari contoh di README praktikum karena starter ini punya applicationId sendiri)

---

## Kriteria Selesai

Sebelum push, pastikan:

- [ ] Semua marker `TODO` di `ExploreScreen.kt`, `MainScreen.kt`, dan
      `AndroidManifest.xml` sudah diganti kode asli
      (boleh `grep -rn "TODO" app/src` untuk memastikan tidak ada yang terlewat)
- [ ] Grid foto di tab Eksplorasi tampil dan bisa di-scroll
- [ ] Perpindahan antar tab/layar terlihat ada animasinya, bukan instan
- [ ] *(Kalau mengerjakan Tantangan)* deep link berhasil membuka `DetailScreen`
      langsung dari perintah `adb` di atas

**Pengumpulan:** push ke branch `pertemuan-5` → kumpulkan link di LMS.
