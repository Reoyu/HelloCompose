# Ch10 Starter — Refactor KabarKini ke Hilt

Titik awal Praktikum Pertemuan 10. Isinya aplikasi berita **KabarKini** dari Ch09,
lengkap dan berjalan: headline per kategori, pencarian, layar detail,
pull-to-refresh, dan mode offline Room. Semua dependency masih dirakit
**manual** lewat `AppContainer` dan `viewModelFactory`.

Tugasmu: **mengganti manual DI dengan Hilt** tanpa mengubah perilaku aplikasi
sedikit pun, lalu menulis tes dengan MockK dan `@TestInstallIn`. Semua bagian
yang harus dikerjakan ditandai di kode dengan komentar `// TODO [Tx.y]`.

> Jawaban lengkap ada di `praktikum/ch10`. Kerjakan dulu sendiri, baru
> bandingkan. Jangan menyalin sebelum semua TODO selesai.

---

## Yang Sudah Disiapkan

- **Gradle sudah lengkap.** Plugin `com.google.dagger.hilt.android` 2.51.1,
  `hilt-android` + `ksp(hilt-android-compiler)`, `hilt-navigation-compose`, MockK,
  dan dependensi uji Hilt untuk `androidTest`. Kamu tidak perlu mengubah Gradle,
  tetapi **bacalah** `build.gradle.kts` dan `app/build.gradle.kts`, karena bagian
  Hilt ditandai komentar *Chapter 10*.
- **`util/Clock.kt`.** Di Ch09, repository menerima lambda `clock: () -> Long`.
  Hilt tidak bisa meng-inject lambda, jadi lambda itu sudah diganti interface `Clock`.
- **`data/remote/NetworkFactory.kt`.** Fungsi `createOkHttpClient()` dan
  `createNewsApiService()`. Module Hilt-mu tinggal memanggilnya.
- **`ArticleDetailViewModel` sudah menerima `SavedStateHandle`**, bukan
  `articleUrl` langsung. Navigation otomatis memasukkan argumen route ke sana,
  dan Hilt juga bisa meng-inject `SavedStateHandle`. Jadi constructor-nya
  tidak perlu diubah, cukup diberi anotasi.
- **Uji instrumented:** `HiltTestRunner`, `FakeNewsRepository`, dan
  `NewsAppHiltTest` sudah ada di `app/src/androidTest/`.

---

## Langkah Pertama: API Key

Salin `NEWS_API_KEY` dari `local.properties` Ch09 ke `local.properties` proyek
ini. Daftar gratis di <https://newsapi.org/register> jika belum punya.
Tanpa key, aplikasi tetap berjalan, tetapi menampilkan
*"API key tidak valid atau belum diisi…"*. Tugas 3 dan 4 **tidak** membutuhkan key.

---

## TODO — Wajib Diselesaikan Mahasiswa

**Menemukan semua TODO:** Android Studio → **View → Tool Windows → TODO**
(`⌘6` di macOS, `Alt+6` di Windows/Linux). Hapus komentar `TODO` setelah selesai,
sehingga daftar itu **kosong** saat kamu selesai.

> **Mulai dari `di/DefaultAppContainer.kt`.** File ini adalah "peta" refactor:
> setiap bagiannya diberi TODO yang menyebut module Hilt tujuannya.

### Tugas 1 — Refactor ke Hilt, hapus `AppContainer` dan factory manual

| ID | File | Yang harus dikerjakan |
|---|---|---|
| [ ] **T1.1** | `MainApplication.kt` | `@HiltAndroidApp`; hapus `container` dan isi `onCreate()` |
| [ ] **T1.2** | `MainActivity.kt` | `@AndroidEntryPoint` |
| [ ] **T1.3** | `di/NetworkModule.kt` *(baru)* | `object` + `@Module @InstallIn(SingletonComponent::class)`; `@Provides @Singleton` untuk `Json`, `OkHttpClient`, `NewsApiService` (pakai fungsi di `NetworkFactory.kt` dan `BuildConfig.NEWS_API_KEY`) |
| [ ] **T1.4** | `di/AppModule.kt` *(baru)* | `@Provides fun provideClock(): Clock = Clock { System.currentTimeMillis() }` |
| [ ] **T1.5** | `data/NewsRepository.kt` | `NewsRepositoryImpl @Inject constructor(...)` |
| [ ] **T1.6** | `ui/news/NewsViewModel.kt`, `ui/detail/ArticleDetailViewModel.kt` | `@HiltViewModel` + `@Inject constructor` |
| [ ] **T1.7** | `ui/news/NewsRoute.kt`, `ui/detail/ArticleDetailRoute.kt` | Ganti `viewModel(factory = viewModelFactory { ... })` dengan `hiltViewModel()` |
| [ ] **T1.8** | `di/AppContainer.kt`, `di/DefaultAppContainer.kt` | **Hapus** kedua file |

### Tugas 2 — `DatabaseModule` dan `RepositoryModule`

| ID | File | Yang harus dikerjakan |
|---|---|---|
| [ ] **T2.1** | `di/DatabaseModule.kt` *(baru)*, `data/local/NewsDatabase.kt` | `@Provides @Singleton NewsDatabase` dengan `@ApplicationContext context` + `Room.databaseBuilder(..., NewsDatabase.NAME)`; `@Provides ArticleDao`. Lalu **hapus** `getInstance()` dan `INSTANCE` |
| [ ] **T2.2** | `di/RepositoryModule.kt` *(baru)* | `abstract class` dengan `@Binds @Singleton abstract fun bindNewsRepository(impl: NewsRepositoryImpl): NewsRepository` |

Nama class module (`NetworkModule`, `DatabaseModule`, `RepositoryModule`,
`AppModule`) **harus persis** seperti di atas dan berada di package
`com.example.ch10.di`, karena `HiltSetupTest` memeriksanya.

### Tugas 3 — Dua unit test dengan MockK

| ID | File | Yang harus dikerjakan |
|---|---|---|
| [ ] **T3.1** | `test/.../ui/news/NewsViewModelMockKTest.kt` | Skenario **sukses**: `coEvery { ... } returns Result.success(...)`, periksa state, dan `coVerify(exactly = 1)` |
| [ ] **T3.2** | file yang sama | Skenario **gagal**: `Result.failure(IOException())` → daftar kosong + *"Tidak ada koneksi internet."* |

Petunjuk lengkap ada di komentar file tersebut. Uji unit **tidak memakai Hilt**:
cukup `NewsViewModel(repository)`.

### Tugas 4 (Tantangan) — `@TestInstallIn`

| ID | File | Yang harus dikerjakan |
|---|---|---|
| [ ] **T4.1** | `androidTest/.../di/FakeRepositoryModule.kt` *(baru)* | `@Module @TestInstallIn(components = [SingletonComponent::class], replaces = [RepositoryModule::class])` yang mem-`@Binds` `FakeNewsRepository` sebagai `NewsRepository` |

`NewsAppHiltTest` tidak perlu diubah. Ketiga tesnya hijau setelah Tugas 1, 2,
dan T4.1 selesai.

### Aturan yang harus dipatuhi

- Perilaku aplikasi **tidak boleh berubah**. Tes lama (`NewsViewModelTest`,
  `NewsRepositoryImplTest`, dll.) harus tetap hijau.
- **Jangan menulis `@Inject constructor` di class library** (Retrofit, OkHttp,
  Room). Gunakan `@Provides` di module.
- Interface (`NewsRepository`) diikat dengan **`@Binds`**, bukan `@Provides`.
- Setelah selesai, **tidak boleh ada** `AppContainer`, `container`, `viewModelFactory`,
  atau `createSavedStateHandle()` di `app/src/main`.
- **API key tidak boleh ada di kode yang di-push.**

### Membaca hasil tes

```bash
./gradlew testDebugUnitTest            # tanpa emulator & internet
./gradlew connectedDebugAndroidTest    # Tugas 4, emulator harus menyala
```

| Berkas tes | Kondisi awal | Artinya |
|---|---|---|
| `NewsViewModelTest` (8), `NewsRepositoryImplTest` (8), `ArticleDetailViewModelTest` (2), `NewsApiServiceTest` (4), `NetworkErrorTest` (5), `ArticleMapperTest` (4), `IsoDateTest` (3), `RelativeTimeTest` (7) | **hijau** (41) | perilaku Ch09; harus **tetap hijau** setelah refactor |
| `HiltSetupTest` (7) | **merah** | hijau setelah **Tugas 1 + 2** selesai |
| `NewsViewModelMockKTest` (2) | **merah** | hijau setelah **T3.1** dan **T3.2** |
| `NewsAppHiltTest` (3, androidTest) | **merah** | hijau setelah Tugas 1, 2, dan **T4.1** |

`HiltSetupTest` memeriksa class yang **di-generate Hilt** saat kompilasi, misalnya
`Hilt_MainActivity` untuk `@AndroidEntryPoint`. Jika satu tesnya merah, pesan
tesnya menyebut bagian mana yang belum ada.

### Error Hilt yang sering muncul

| Pesan | Penyebab |
|---|---|
| `[Dagger/MissingBinding] NewsRepository cannot be provided without an @Provides-annotated method` | `RepositoryModule` (T2.2) belum ada, atau `@InstallIn` lupa |
| `[Dagger/MissingBinding] ArticleDao cannot be provided ...` | `DatabaseModule` (T2.1) belum ada |
| `[Dagger/MissingBinding] Clock cannot be provided ...` | `AppModule` (T1.4) belum ada |
| `Given component holder class MainActivity does not implement ... GeneratedComponent` (runtime) | `@AndroidEntryPoint` (T1.2) lupa |
| `Cannot create an instance of class NewsViewModel` (runtime) | `@HiltViewModel` (T1.6) lupa, atau masih memakai `viewModel()` biasa |
| `Hilt Activity must be attached to an @HiltAndroidApp Application` (runtime) | `@HiltAndroidApp` (T1.1) lupa |

Selama refactor, build **pasti gagal untuk sementara**: misalnya setelah T1.1,
`NewsRoute` masih memanggil `app.container`. Begitu ViewModel diberi
`@HiltViewModel` (T1.6), Hilt memeriksa semua dependency-nya saat kompilasi.
Build baru sukses lagi setelah semua module (T1.3, T1.4, T2.1, T2.2),
`@Inject constructor` (T1.5), dan `hiltViewModel()` (T1.7) ada. Jadi kerjakan
Tugas 1 dan 2 sekaligus, baru build.

### Dianggap selesai jika semua ini benar

- [ ] Aplikasi berjalan **persis seperti sebelumnya**: headline, ganti kategori,
      pencarian, ketuk artikel → detail, pull-to-refresh, mode offline.
- [ ] Jendela TODO kosong; `di/` hanya berisi keempat module.
- [ ] `./gradlew testDebugUnitTest` hijau semua (**51** tes).
- [ ] `./gradlew connectedDebugAndroidTest` hijau semua (**3** tes).
- [ ] `git grep` dengan API key-mu tidak menemukan apa pun.

**Pengumpulan:** push ke branch `pertemuan-10` **tanpa API key** → kumpulkan link
di LMS sebelum pertemuan berikutnya.

---

## Cara Membuka di Android Studio

1. **File → Open** → pilih folder `praktikum/ch10_starter/`
2. Isi `NEWS_API_KEY` di `local.properties` (lihat di atas).
3. Tunggu Gradle sync → pilih device/emulator → klik **Run ▶**

> **Versi compiler sama dengan Ch03–Ch09**: Gradle 8.6, AGP 8.3.2, Kotlin 1.9.23,
> KSP `1.9.23-1.0.20`. Ditambah Hilt 2.51.1, hilt-navigation-compose 1.2.0, MockK 1.13.10.

Materi konsep (`@Inject`, `@Module`, `@Provides` vs `@Binds`, scope) ada di buku
Bab 10 dan slide `ch10`. Penjelasan tiap langkah dan perbedaan dengan listing
buku ada di `praktikum/ch10/README.md`.

---

## Run the code

```bash
cd /Users/dhareva/Books/2026_MP/codes/praktikum/ch10_starter
./gradlew installDebug
adb shell am start -n com.example.ch10/.MainActivity
```
