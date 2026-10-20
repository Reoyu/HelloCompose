# Ch09 Starter — KabarKini dengan NewsAPI

Titik awal Praktikum Pertemuan 9. Aplikasi berita **KabarKini** sudah berjalan:
headline per kategori, pencarian (dengan debounce), gambar dengan Coil, serta
state loading / error + **Coba Lagi** / kosong. Mengetuk artikel langsung membuka
browser. Jadi **Tugas 1 sudah ada**. Yang harus kamu kerjakan adalah **Tugas 2–4**,
yang ditandai di kode dengan komentar `// TODO [Tx.y]`.

> Jawaban lengkap ada di `praktikum/ch09`. Kerjakan dulu sendiri, baru
> bandingkan. Jangan menyalin sebelum semua TODO selesai.

---

## Langkah Pertama: API Key

1. Daftar gratis di <https://newsapi.org/register> dan salin API key-mu.
2. Buka `local.properties` dan isi baris terakhirnya:
   ```properties
   NEWS_API_KEY=isi_api_key_kamu
   ```
3. Build ulang aplikasinya. Key dimasukkan ke `BuildConfig` saat build.

`local.properties` sudah ada di `.gitignore`, jadi **jangan** memindahkan key ke
file lain yang ikut ter-commit. Tanpa key, aplikasi tetap berjalan, tetapi
menampilkan *"API key tidak valid atau belum diisi…"* (HTTP 401).

---

## TODO — Wajib Diselesaikan Mahasiswa

**Menemukan semua TODO:** Android Studio → **View → Tool Windows → TODO**
(`⌘6` di macOS, `Alt+6` di Windows/Linux). Hapus komentar `TODO` setelah selesai,
sehingga daftar itu **kosong** saat kamu selesai.

> Sebelum mengerjakan Tugas 2–4, pahami dulu alur Tugas 1:
> `NewsApiService → NewsRepositoryImpl (Result) → NewsViewModel → NewsRoute/NewsScreen`,
> beserta perannya `ArticleDto.toDomain()` dan `Throwable.toUserMessage()`.

### Tugas 2 — Layar detail artikel

| ID | File | Yang harus dikerjakan |
|---|---|---|
| [ ] **T2.1** | `data/NewsRepository.kt` | `getArticle(url)` mengembalikan artikel yang pernah dimuat (sementara simpan di `Map` di memori; setelah Tugas 4, ambil dari Room) |
| [ ] **T2.2** | `Routes.kt`, `KabarKiniApp.kt` | Route `detail/{articleUrl}` dengan `Uri.encode(url)`; daftarkan `composable(...)` dengan `navArgument` bertipe `StringType` |
| [ ] **T2.3** | `ui/detail/ArticleDetailViewModel.kt` | Muat artikel lewat `repository.getArticle(articleUrl)`; jika tidak ada, tampilkan `"Artikel tidak ditemukan"` |
| [ ] **T2.4** | `ui/detail/ArticleDetailRoute.kt`, `ArticleDetailScreen.kt` *(baru)* | Gambar, sumber · waktu, judul, "Oleh …", deskripsi, potongan isi, tombol **Baca Selengkapnya** (`openUrl`) |
| [ ] **T2.5** | `KabarKiniApp.kt` | Ketuk kartu → **navigasi ke detail**, bukan langsung ke browser |

### Tugas 3 — Pull-to-refresh

| ID | File | Yang harus dikerjakan |
|---|---|---|
| [ ] **T3.1** | `ui/news/NewsViewModel.kt` | `refresh()`: muat ulang dengan `isRefreshing = true` dan `isLoading = false`, sehingga daftar lama **tetap tampil** |
| [ ] **T3.2** | `ui/news/NewsScreen.kt` | Parameter `onRefresh`; bungkus konten dengan `PullToRefreshBox`; layar error/kosong juga harus bisa ditarik (lihat petunjuk di `ScrollableCenter`) |
| [ ] **T3.3** | `ui/news/NewsRoute.kt` | Teruskan `viewModel::refresh` |

`PullToRefreshBox` ada di `androidx.compose.material3.pulltorefresh` dan perlu
`@OptIn(ExperimentalMaterial3Api::class)`. Compose BOM proyek ini (`2024.09.00`)
sudah mendukungnya.

### Tugas 4 (Tantangan) — Room + Retrofit: mode offline

| ID | File | Yang harus dikerjakan |
|---|---|---|
| [ ] **T4.1** | `data/local/ArticleEntity.kt` *(baru)* | Tabel `articles`: `category`, `url`, `title`, `author`, `description`, `content`, `imageUrl`, `publishedAt`, `sourceName`, `position`, `cachedAt`. Primary key **gabungan** `(category, url)`, karena satu artikel bisa muncul di beberapa kategori |
| [ ] **T4.2** | `data/local/ArticleDao.kt` *(baru)* | `getArticles(category)` urut `position`; `getArticleByUrl(url)`; `searchCached(query)` (judul/deskripsi `LIKE`); `insertAll`; `deleteCategory`; `replaceCategory` dengan `@Transaction` (karena itu DAO dibuat `abstract class`) |
| [ ] **T4.3** | `data/local/NewsDatabase.kt` *(baru)* | `@Database(version = 1, exportSchema = true)` + singleton `getInstance(context)` (pola Ch07) |
| [ ] **T4.4** | `data/mapper/ArticleMapper.kt` | `Article.toEntity(category, position, cachedAt)` dan `ArticleEntity.toDomain()` |
| [ ] **T4.5** | `data/NewsRepository.kt` | Berhasil → simpan ke Room; gagal → kembalikan cache dengan `OfflineInfo(cachedAt, alasan)`; cache kosong → `Result.failure`. Hasil pencarian disimpan dengan kunci `"__search__"`; pencarian offline mencari di seluruh cache. `getArticle()` membaca Room |
| [ ] **T4.6** | `di/DefaultAppContainer.kt` | Buat `NewsDatabase` dan teruskan `articleDao()` ke repository |
| [ ] **T4.7** | `ui/news/NewsViewModel.kt` | `offline: OfflineInfo?` di UiState, diisi dari `feed.offline` |
| [ ] **T4.8** | `ui/news/NewsScreen.kt` | Banner **"Menampilkan data offline · diperbarui 2 jam lalu"** + alasannya (`formatRelativeTime` sudah tersedia di `util/`) |

Room dan KSP **sudah** terpasang di `build.gradle.kts`, termasuk folder ekspor
skema `app/schemas/`. Kamu tidak perlu mengubah Gradle.

### Aturan yang harus dipatuhi

- `NewsScreen`, `ArticleDetailScreen`, dan `ArticleCard` **tidak boleh** mengimpor
  `ViewModel` atau `Repository`. Mereka hanya menerima state dan callback.
- ViewModel **tidak boleh** memanggil Retrofit atau DAO langsung. Semua lewat `NewsRepository`.
- Navigasi hanya mengirim **URL** artikel, bukan objek `Article`.
- Jangan menghapus debounce pencarian: kuota NewsAPI gratis hanya **100 request/hari**.
- `CancellationException` tidak boleh diubah menjadi "data offline". Lempar ulang,
  seperti yang sudah dilakukan `fetch()`.
- **API key tidak boleh ada di kode yang di-push.**

### Membaca hasil tes

```bash
./gradlew testDebugUnitTest
```

| Berkas tes | Kondisi awal | Artinya |
|---|---|---|
| `NewsViewModelTest` (6), `NewsApiServiceTest` (4), `NetworkErrorTest` (5), `ArticleMapperTest` (3), `IsoDateTest` (3), `RelativeTimeTest` (7) | **hijau** | perilaku Tugas 1; harus tetap hijau saat kamu menambah fitur |
| `ArticleDetailViewModelTest` (2) | **merah** | hijau setelah **T2.3** selesai |
| `NewsRefreshTest` (1) | **merah** | hijau setelah **T3.1** selesai |

Tes ViewModel memakai `FakeNewsRepository`, sedangkan `NewsApiServiceTest` memakai
Retrofit sungguhan dengan server palsu (`MockWebServer`), sehingga tidak butuh
internet maupun API key. Untuk Tugas 4, tambahkan tes repository dengan
`FakeNewsApiService` (sudah ada) dan `FakeArticleDao` buatanmu. Contoh lengkapnya
ada di `praktikum/ch09` (`NewsRepositoryImplTest`).

### Dianggap selesai jika semua ini benar

Jalankan aplikasi di emulator, lalu periksa satu per satu:

**Tugas 2**
- [ ] Ketuk artikel → layar detail menampilkan gambar, judul, penulis, dan deskripsi.
- [ ] **Baca Selengkapnya** membuka artikel di browser; tombol kembali kembali ke daftar.
- [ ] Artikel dari hasil **pencarian** juga bisa dibuka di layar detail.

**Tugas 3**
- [ ] Tarik daftar ke bawah → indikator refresh muncul, daftar lama tetap terlihat, lalu diperbarui.
- [ ] Layar error (mis. mode pesawat) juga bisa ditarik untuk mencoba lagi.

**Tugas 4**
- [ ] Buka aplikasi saat online, lalu aktifkan mode pesawat dan tarik daftar →
      berita tetap tampil dengan banner "Menampilkan data offline · diperbarui …".
- [ ] Saat offline, kategori yang **belum pernah** dibuka menampilkan layar error, bukan daftar kosong.
- [ ] Pencarian saat offline menemukan artikel dari cache.
- [ ] Tutup aplikasi total, buka lagi dalam mode pesawat → cache masih ada.

**Kode**
- [ ] Jendela TODO kosong, `./gradlew assembleDebug` sukses, dan `./gradlew testDebugUnitTest` hijau semua.
- [ ] `git grep` dengan API key-mu tidak menemukan apa pun.

**Pengumpulan:** push ke branch `pertemuan-9` **tanpa API key** → kumpulkan link
di LMS sebelum pertemuan berikutnya.

---

## Cara Membuka di Android Studio

1. **File → Open** → pilih folder `praktikum/ch09_starter/`
2. Isi `NEWS_API_KEY` di `local.properties` (lihat di atas).
3. Tunggu Gradle sync → pilih device/emulator → klik **Run ▶**

> **Versi compiler sama dengan Ch03–Ch07** — Gradle 8.6, AGP 8.3.2, Kotlin 1.9.23,
> KSP `1.9.23-1.0.20`, plugin serialization `1.9.23`. Library: Retrofit 2.11.0,
> OkHttp 4.12.0, kotlinx-serialization-json 1.6.3, Coil 3.0.4, Room 2.6.1.

Materi konsep (HTTP, Retrofit, DTO vs domain model, `Result`, Coil) ada di buku
Bab 9 dan slide `ch09`. Penjelasan tiap lapisan dan perbedaan dengan listing buku
ada di `praktikum/ch09/README.md`.

---

## Run the code

```bash
cd /Users/dhareva/Books/2026_MP/codes/praktikum/ch09_starter
./gradlew installDebug
adb shell am start -n com.example.ch09/.MainActivity
```
