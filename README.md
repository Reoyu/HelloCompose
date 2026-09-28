# Ch06 — Architecture: ViewModel & MVVM

Praktikum me-refactor aplikasi artikel dari Ch05 agar layar **Home**
memakai pola **MVVM**: `ViewModel`, `StateFlow`, `UiState`, `Repository`,
dan manual dependency injection — sebelum masuk ke Hilt di chapter
berikutnya.

---

## TODO — Wajib Diselesaikan Mahasiswa

Starter ini berhenti **tepat sebelum Tugas 2–4**: Home sudah memakai MVVM
(Langkah 1–6 di bawah sudah jadi), tetapi belum ada pencarian, *empty state*,
dan `ProfileViewModel`. Semua yang harus kamu kerjakan ditandai di kode dengan
komentar `// TODO [Tx.y]`, dan nomornya sama dengan tabel di bawah.

**Menemukan semua TODO:** Android Studio → **View → Tool Windows → TODO**
(`⌘6` di macOS, `Alt+6` di Windows/Linux). Setiap baris yang tampil adalah satu
pekerjaan; hapus komentar `TODO`-nya setelah selesai, sehingga daftar itu
kosong saat kamu selesai.

> Tugas 1 (refactor Home ke MVVM) sudah ada di starter — pahami dulu tiap
> lapisannya, lalu kerjakan Tugas 2–4 berurutan.

### Tugas 2 — Pencarian artikel

| ID | File | Yang harus dikerjakan |
|---|---|---|
| [ ] **T2.1** | `home/HomeUiState.kt` | Tambah field `query: String = ""` |
| [ ] **T2.2** | `home/HomeViewModel.kt` | Simpan daftar lengkap di `allArticles` (privat, bukan di UiState); saat `refresh()` sukses, tampilkan hasil yang sudah difilter dengan query yang sedang aktif |
| [ ] **T2.3** | `home/HomeViewModel.kt` | Buat `onQueryChange(newQuery)` + fungsi privat `filter(...)`: cocok jika **judul atau kategori** mengandung query, **tidak peka huruf besar/kecil**, query kosong = semua artikel |
| [ ] **T2.4** | `home/HomeScreen.kt` | Tambah parameter `onQueryChange` dan `SearchField` (`OutlinedTextField`) di atas daftar; `TextField` hanya **melaporkan** teks, tombol hapus (ikon Clear) muncul saat query tidak kosong |
| [ ] **T2.5** | `home/HomeRoute.kt` | Sambungkan `onQueryChange = viewModel::onQueryChange` |

### Tugas 3 — Empty result state

| ID | File | Yang harus dikerjakan |
|---|---|---|
| [ ] **T3.1** | `home/HomeUiState.kt` | Properti turunan `isEmptyResult` (getter, **bukan field baru**): `!isLoading && errorMessage == null && articles.isEmpty()` |
| [ ] **T3.2** | `home/HomeScreen.kt` | Cabang `uiState.isEmptyResult -> EmptyResultContent(...)` di antara cabang error dan daftar; pesan **menyebut kata kunci** yang dicari |

### Tugas 4 (Tantangan) — `ProfileViewModel`

| ID | File | Yang harus dikerjakan |
|---|---|---|
| [ ] **T4.1** | `profile/ProfileRepository.kt` *(baru)* | `UserProfile`, interface `ProfileRepository` (sinkron, tanpa `suspend`), `FakeProfileRepository` |
| [ ] **T4.2** | `profile/ProfileUiState.kt` *(baru)* | `username` + `notificationsEnabled` dalam **satu** data class |
| [ ] **T4.3** | `profile/ProfileViewModel.kt` *(baru)* | `StateFlow` + event `onUsernameChange()` dan `onToggleNotification()` |
| [ ] **T4.4** | `profile/ProfileViewModelFactory.kt` *(baru)* | Pola sama dengan `HomeViewModelFactory` |
| [ ] **T4.5** | `profile/ProfileRoute.kt` *(baru)* + `ProfileScreen` | Pindahkan `ProfileScreen` ke package `profile`, terima `(uiState, onUsernameChange, onToggleNotification)`; tampilkan `OutlinedTextField` nama dan `Switch` notifikasi |
| [ ] **T4.6** | `di/AppContainer.kt` | Tambah `profileRepository` di interface dan `DefaultAppContainer` |
| [ ] **T4.7** | `MainScreen.kt` | Ganti `ProfileScreen()` menjadi `ProfileRoute()` |

### Aturan yang harus dipatuhi

- `HomeScreen` dan `ProfileScreen` **tidak boleh** mengimpor apa pun dari `ViewModel` atau `Repository` — keduanya hanya menerima state dan callback.
- Logika filter berada di `HomeViewModel`, **bukan** di composable.
- `NavController` tidak pernah diberikan ke ViewModel.
- Jangan menambah field `isEmpty` terpisah di `HomeUiState`; gunakan properti turunan.

### Dianggap selesai jika semua ini benar

Jalankan aplikasinya, lalu cek satu per satu:

**Home (Tugas 2 + 3)**
- [ ] Muncul kolom pencarian di atas daftar artikel.
- [ ] Ketik `compose` → tepat **2** artikel tampil: *Compose Basics* dan *Navigation Compose* (cocok ke judul).
- [ ] Ketik `DATABASE` (huruf besar) → artikel kategori *Database* tetap ketemu (tidak peka huruf besar/kecil, cocok ke **kategori**).
- [ ] Ketik `zzz` → muncul pesan yang menyebut `"zzz"`, **bukan** layar kosong, dan tombol **Coba Lagi** tidak muncul (ini bukan error).
- [ ] Ikon **✕** menghapus query dan semua artikel kembali tampil.
- [ ] Buka ulang aplikasi, lalu **saat spinner masih berputar** (1–2 detik pertama) langsung ketik `basics` → begitu data tiba, daftar sudah terfilter (query tidak tertimpa hasil `refresh()`).
- [ ] Klik artikel hasil pencarian → Detail terbuka; kembali → query masih terisi.

**Profil (Tugas 4)**
- [ ] Layar Profil menampilkan nama dan status notifikasi dari `FakeProfileRepository`.
- [ ] Ubah nama, lalu **putar layar** → nama **tidak reset** (bukti state hidup di ViewModel).
- [ ] Toggle notifikasi → teks status berubah (aktif/nonaktif).
- [ ] Pindah tab lalu kembali → nilai tetap.

**Kode**
- [ ] Jendela **TODO** di Android Studio kosong (tidak ada `TODO [T...]` tersisa).
- [ ] `./gradlew assembleDebug` sukses tanpa error.

---

## Hasil Akhir

Struktur yang sama seperti Ch05 (tiga tab Bottom Navigation), tapi layar
**Beranda** kini punya tiga lapisan:

| Layer | Isi |
|---|---|
| Data | `Article`, `ArticleRepository`, `FakeArticleRepository` |
| Presentation | `HomeUiState`, `HomeViewModel` |
| View | `HomeRoute` (terhubung ke ViewModel) + `HomeScreen` (murni presentasional) |

Saat aplikasi dibuka: indikator loading muncul → data dimuat lewat
`viewModelScope.launch` → daftar artikel tampil. Jika gagal, tampil pesan
error + tombol **Coba Lagi**. Detail artikel dan Bottom Navigation tetap
bekerja seperti Ch05.

---

## Prasyarat

- Sudah menyelesaikan Ch05 (Navigation & Multi-Screen App)
- Android Studio Hedgehog (2023.1.1) atau lebih baru
- Android SDK API 34, JDK 17

> **Versi compiler sama dengan Ch03–Ch05** — Gradle 8.6, AGP 8.3.2, Kotlin 1.9.23.

---

## Cara Membuka di Android Studio

1. **File → Open** → pilih folder `praktikum/ch06_starter/`
2. Tunggu Gradle sync — dependency baru yang diunduh:
   `androidx.lifecycle:lifecycle-viewmodel-compose` dan
   `androidx.lifecycle:lifecycle-runtime-compose`
3. Pilih device/emulator → klik **Run ▶**

---

## Langkah 1 — Tambahkan Dependency ViewModel Compose

Buka `app/build.gradle.kts`, pastikan baris berikut ada di blok `dependencies`:

```kotlin
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
```

Dua dependency ini menyediakan `viewModel()` composable function dan
`collectAsStateWithLifecycle()` — cara aman mengumpulkan `StateFlow` yang
menyesuaikan lifecycle layar.

---

## Langkah 2 — Buat Model dan Repository (`data/`)

`Article` sekarang punya field `category`, dan aksesnya dibungkus lewat
interface agar `ViewModel` tidak tahu datanya dari mana:

```kotlin
data class Article(
    val id: Int,
    val title: String,
    val category: String,
    val excerpt: String
)

interface ArticleRepository {
    suspend fun getArticles(): List<Article>
    fun getCachedArticles(): List<Article>
}

class FakeArticleRepository : ArticleRepository {
    private val cached = listOf(/* ... */)

    override suspend fun getArticles(): List<Article> {
        delay(1200) // simulasi latensi jaringan
        return cached
    }

    override fun getCachedArticles(): List<Article> = cached
}
```

`getArticles()` bersifat `suspend` (dipanggil dari ViewModel saat load),
sedangkan `getCachedArticles()` sinkron — dipakai `DetailScreen` untuk
mencari artikel by id tanpa perlu ViewModel sendiri.

---

## Langkah 3 — Buat Manual DI Container (`di/AppContainer.kt`)

Sebelum Hilt hadir di chapter berikutnya, dependency dirakit manual di
satu tempat:

```kotlin
interface AppContainer {
    val articleRepository: ArticleRepository
}

class DefaultAppContainer : AppContainer {
    override val articleRepository: ArticleRepository by lazy {
        FakeArticleRepository()
    }
}
```

`MainApplication.kt` menyimpan container ini dan didaftarkan di
`AndroidManifest.xml` lewat `android:name=".MainApplication"`:

```kotlin
class MainApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer()
    }
}
```

---

## Langkah 4 — Buat `HomeUiState` dan `HomeViewModel` (`home/`)

`HomeUiState` adalah *single source of truth* layar Home — loading,
daftar artikel, dan pesan error dibungkus dalam satu data class:

```kotlin
data class HomeUiState(
    val isLoading: Boolean = false,
    val articles: List<Article> = emptyList(),
    val errorMessage: String? = null
)
```

`HomeViewModel` menyimpan state itu di `MutableStateFlow` dan memuat data
lewat `viewModelScope.launch` saat `init`:

```kotlin
class HomeViewModel(
    private val repository: ArticleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init { refresh() }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            runCatching { repository.getArticles() }
                .onSuccess { articles ->
                    _uiState.update { it.copy(isLoading = false, articles = articles) }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = error.message ?: "Gagal memuat artikel")
                    }
                }
        }
    }
}
```

`HomeViewModelFactory` menyuntikkan `repository` ke `HomeViewModel` karena
constructor-nya punya parameter — `ViewModel` bawaan hanya bisa dibuat
otomatis untuk constructor kosong.

---

## Langkah 5 — Pisahkan `HomeRoute` dan `HomeScreen`

`HomeRoute` adalah satu-satunya bagian yang tahu soal `ViewModel` dan
`Context`; `HomeScreen` murni menerima state + callback sehingga mudah
di-preview dan di-test:

```kotlin
@Composable
fun HomeRoute(onArticleClick: (Int) -> Unit) {
    val app = LocalContext.current.applicationContext as MainApplication
    val viewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(app.container.articleRepository)
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeScreen(
        uiState = uiState,
        onArticleClick = onArticleClick,
        onRetry = viewModel::refresh
    )
}

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onArticleClick: (Int) -> Unit,
    onRetry: () -> Unit
) {
    when {
        uiState.isLoading -> LoadingContent()
        uiState.errorMessage != null -> ErrorContent(uiState.errorMessage, onRetry)
        else -> ArticleList(uiState.articles, onArticleClick)
    }
}
```

---

## Langkah 6 — Wire di `MainScreen.kt`

`composable(Routes.Home.route)` sekarang memanggil `HomeRoute(...)`,
bukan `HomeScreen(navController)` seperti di Ch05 — `NavController` tidak
pernah diberikan ke ViewModel, navigasi tetap dipicu dari UI lewat event
`onArticleClick`:

```kotlin
composable(Routes.Home.route) {
    HomeRoute(
        onArticleClick = { id ->
            navController.navigate(Routes.Detail.createRoute(id))
        }
    )
}
```

`DetailScreen` juga dipecah jadi `DetailRoute` (mengambil artikel dari
`app.container.articleRepository.getCachedArticles()`) dan `DetailScreen`
(murni presentasional, menerima `Article?`).

Jalankan aplikasi → indikator loading tampil sesaat → daftar artikel
muncul → klik artikel → masuk ke Detail. Pindah tab tetap berfungsi
seperti Ch05.

---

## Checklist Praktikum

- Saat aplikasi dibuka, indikator loading muncul terlebih dahulu.
- Setelah data berhasil dimuat, daftar artikel tampil lengkap dengan kategori.
- Tombol **Coba Lagi** memanggil ulang `viewModel.refresh()`.
- Navigasi ke detail dan Bottom Navigation tetap bekerja seperti Ch05.
- `HomeScreen` tidak mengimpor apa pun dari `ViewModel` atau `Repository`.

---

## Tugas Pertemuan 6

1. Refactor aplikasi multi-screen dari chapter 5 agar layar Home
   menggunakan `HomeViewModel`, `HomeUiState`, dan `FakeArticleRepository`
   — **sudah dikerjakan di praktikum ini**, pastikan kamu paham tiap
   lapisannya sebelum lanjut.
2. **(TODO T2.1–T2.5)** Tambahkan fitur pencarian artikel. `TextField` tetap berada di UI
   (`HomeScreen`), tetapi logika filter harus dieksekusi oleh
   `HomeViewModel` melalui event `onQueryChange()`. Tambahkan field
   `query: String` di `HomeUiState`.
3. **(TODO T3.1–T3.2)** Tambahkan state **empty result** ketika pencarian tidak menemukan
   artikel apa pun. Jangan tampilkan list kosong tanpa penjelasan.
4. **(TODO T4.1–T4.7) Tantangan:** Buat `ProfileViewModel` untuk layar `ProfileScreen`
   yang menyimpan status toggle notifikasi dan nama pengguna dalam satu
   `ProfileUiState`. Ikuti pola `HomeViewModel` + `HomeViewModelFactory`
   + `Route`/`Screen` yang sama.

**Pengumpulan:** push ke branch `pertemuan-6` → kumpulkan link di LMS
sebelum pertemuan berikutnya.

---

## Run the code

```bash
cd /Users/dhareva/Books/2026_MP/codes/praktikum/ch06_starter
./gradlew installDebug
adb shell am start -n com.example.ch06/.MainActivity
```
