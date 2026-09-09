# Praktikum 3 Starter - Jetpack Compose Fundamentals

Folder ini adalah **starter project** untuk Praktikum Pertemuan 3. Tujuannya
bukan menulis semua kode sekaligus, tetapi melengkapi file Kotlin satu per satu
dengan bantuan README ini.

Pada setiap file terdapat komentar `TODO`. Kerjakan sesuai urutan berikut:

1. `Color.kt`
2. `Type.kt`
3. `Theme.kt`
4. `ProfileScreen.kt`
5. `StudentListScreen.kt`
6. `MainActivity.kt`

Untuk setiap langkah, buka file yang disebutkan, pilih semua isi file, lalu ganti
dengan kode lengkap pada bagian yang sesuai di README. Setelah itu jalankan
checkpoint sebelum melanjutkan. Jangan menggabungkan beberapa langkah sekaligus;
checkpoint membantu menemukan file yang menyebabkan error.

## Hasil Akhir

Aplikasi memiliki dua tab:

- **Profil**: avatar inisial, nama, jabatan, statistik, dan informasi kontak.
- **Mahasiswa**: daftar 10 mahasiswa dalam `LazyColumn`, masing-masing dengan nama,
  NIM, dan IPK.

## Prasyarat

- Android Studio Hedgehog (2023.1.1) atau lebih baru
- Android SDK API 34
- JDK 17
- Emulator Android atau perangkat Android dengan USB debugging aktif

Buka folder `ch03_starter/` melalui **File -> Open**. Tunggu Gradle Sync selesai.
Folder ini sudah berisi konfigurasi Gradle, manifest, resource, dan Gradle wrapper.
Jangan membuat proyek baru di dalam folder ini.

## Menjalankan Build

Buka terminal di folder `ch03_starter/`:

```bash
./gradlew assembleDebug
```

Untuk memasang aplikasi pada emulator yang sedang aktif:

```bash
./gradlew installDebug
```

Pastikan perangkat terdeteksi dengan:

```bash
adb devices
```

Jika `adb` tidak ditemukan, Android SDK biasanya berada di:

```bash
export ANDROID_HOME="$HOME/Library/Android/sdk"
export PATH="$ANDROID_HOME/platform-tools:$ANDROID_HOME/emulator:$PATH"
```

## Langkah 1 - `Color.kt`

File: `app/src/main/java/com/example/ch03/ui/theme/Color.kt`

Tujuan: menyediakan warna untuk tema terang dan gelap. Ganti seluruh isi file
dengan kode berikut:

```kotlin
package com.example.ch03.ui.theme

import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650A4)
val PurpleGrey40 = Color(0xFF625B71)
val Pink40 = Color(0xFF7D5260)
```

Checkpoint:

```bash
./gradlew assembleDebug
```

Jika build gagal pada `Color`, pastikan import yang dipakai adalah
`androidx.compose.ui.graphics.Color`.

## Langkah 2 - `Type.kt`

File: `app/src/main/java/com/example/ch03/ui/theme/Type.kt`

Tujuan: menentukan gaya teks dasar Material 3. Ganti seluruh isi file:

```kotlin
package com.example.ch03.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
)
```

Checkpoint: jalankan `./gradlew assembleDebug`. Tidak ada perubahan UI yang
terlihat karena typography baru dipakai oleh `MaterialTheme` pada langkah berikut.

## Langkah 3 - `Theme.kt`

File: `app/src/main/java/com/example/ch03/ui/theme/Theme.kt`

Tujuan: menggabungkan warna, typography, dark mode, dan dynamic color. Ganti
seluruh isi file:

```kotlin
package com.example.ch03.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

@Composable
fun Ch03Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
```

Checkpoint: jalankan `./gradlew assembleDebug`. Perhatikan bahwa `Build.VERSION`
digunakan agar dynamic color hanya dipanggil pada Android API 31 atau lebih baru.

## Langkah 4 - `ProfileScreen.kt`

File: `app/src/main/java/com/example/ch03/ProfileScreen.kt`

Tujuan: berlatih `Column`, `Row`, `Box`, `Modifier`, `verticalScroll`, ikon, dan
preview. Ganti seluruh isi file:

```kotlin
package com.example.ch03

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ch03.ui.theme.Ch03Theme

@Composable
fun ProfileScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        text = "DH",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "Dr. David Hareva",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "Dosen Teknik Informatika",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }
        }

        Spacer(Modifier.height(24.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(label = "Mahasiswa", value = "120")
            StatItem(label = "Mata Kuliah", value = "4")
            StatItem(label = "Publikasi", value = "23")
        }

        Spacer(Modifier.height(24.dp))
        HorizontalDivider()
        Spacer(Modifier.height(16.dp))

        listOf(
            Icons.Default.Email to "david.hareva@university.ac.id",
            Icons.Default.Phone to "+62 812 3456 7890",
            Icons.Default.LocationOn to "Tangerang, Indonesia"
        ).forEach { (icon, text) ->
            InfoRow(icon = icon, text = text)
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun InfoRow(icon: ImageVector, text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.width(16.dp))
        Text(text = text, style = MaterialTheme.typography.bodyMedium)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProfileScreenPreview() {
    Ch03Theme {
        ProfileScreen()
    }
}
```

Checkpoint: buka `ProfileScreenPreview` pada panel **Design**. Pastikan avatar,
header, tiga statistik, dan tiga baris informasi terlihat. Jika preview tidak
muncul, jalankan **Build -> Make Project**.

## Langkah 5 - `StudentListScreen.kt`

File: `app/src/main/java/com/example/ch03/StudentListScreen.kt`

Tujuan: berlatih `data class`, list, `LazyColumn`, `items`, `Card`, dan kondisi
warna berdasarkan nilai IPK. Ganti seluruh isi file:

```kotlin
package com.example.ch03

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ch03.ui.theme.Ch03Theme

data class Mahasiswa(val nama: String, val nim: String, val ipk: Double)

val dummyMahasiswa = listOf(
    Mahasiswa("Ali Rahman", "22001", 3.85),
    Mahasiswa("Budi Santoso", "22002", 3.40),
    Mahasiswa("Cici Wulandari", "22003", 3.92),
    Mahasiswa("Dian Pratama", "22004", 2.95),
    Mahasiswa("Eka Fitriani", "22005", 3.75),
    Mahasiswa("Fandi Ahmad", "22006", 3.50),
    Mahasiswa("Gita Permata", "22007", 3.88),
    Mahasiswa("Hendra Kusuma", "22008", 2.80),
    Mahasiswa("Indah Lestari", "22009", 3.65),
    Mahasiswa("Joko Pratama", "22010", 3.20)
)

@Composable
fun StudentListScreen() {
    DaftarMahasiswa(mahasiswaList = dummyMahasiswa)
}

@Composable
fun DaftarMahasiswa(
    mahasiswaList: List<Mahasiswa>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = mahasiswaList,
            key = { it.nim }
        ) { mahasiswa ->
            MahasiswaCard(mahasiswa)
        }
    }
}

@Composable
fun MahasiswaCard(mahasiswa: Mahasiswa) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = mahasiswa.nama,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = mahasiswa.nim,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "IPK ${mahasiswa.ipk}",
                style = MaterialTheme.typography.labelLarge,
                color = if (mahasiswa.ipk >= 3.5) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun StudentListScreenPreview() {
    Ch03Theme {
        StudentListScreen()
    }
}
```

Checkpoint: buka preview dan pastikan ada 10 card. Uji juga nilai tepat `3.50`:
warna harus masuk kategori IPK `>= 3.5`. `key = { it.nim }` memastikan setiap item
memiliki identitas unik dan stabil.

## Langkah 6 - `MainActivity.kt`

File: `app/src/main/java/com/example/ch03/MainActivity.kt`

Tujuan: menghubungkan aplikasi, tema, `Scaffold`, state tab, `TopAppBar`, dan
`NavigationBar`. Ganti seluruh isi file:

```kotlin
package com.example.ch03

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.ch03.ui.theme.Ch03Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Ch03Theme {
                MainScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    var selectedTab by remember { mutableIntStateOf(0) }
    val title = if (selectedTab == 0) "Profil" else "Daftar Mahasiswa"

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(title) })
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    label = { Text("Profil") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.List, contentDescription = null) },
                    label = { Text("Mahasiswa") }
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                0 -> ProfileScreen()
                1 -> StudentListScreen()
            }
        }
    }
}
```

Checkpoint akhir:

```bash
./gradlew clean
./gradlew assembleDebug
./gradlew installDebug
```

Buka aplikasi. Tab Profil harus tampil pertama kali, tab Mahasiswa harus dapat
dipilih, judul toolbar harus berubah, dan daftar harus dapat di-scroll.

## Tugas Pengembangan

Setelah aplikasi dasar selesai:

1. Ganti data profil dengan data diri sendiri.
2. Tambahkan minimal 10 mahasiswa lain.
3. Tambahkan header dan footer memakai `item { }` di dalam `LazyColumn`.
4. Tambahkan preview light dan dark untuk screen utama.
5. Tantangan: tambahkan `LazyRow` berisi kategori di atas daftar mahasiswa.

## Pengumpulan

Buat branch `pertemuan-3`, commit perubahan, lalu push ke repository. Kumpulkan
link repository atau commit melalui LMS sesuai instruksi dosen.

Saat meminta bantuan, sertakan file yang sedang dikerjakan, `TODO` terakhir yang
diganti, pesan error lengkap, dan hasil `./gradlew assembleDebug`.
