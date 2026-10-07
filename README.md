# Ch07 Starter — CatatanKu dengan Room

Titik awal Praktikum Pertemuan 7. Aplikasi **CatatanKu** sudah berjalan dengan
Room di **skema versi 1**: daftar catatan, form tambah/edit, pin, hapus, dan
pencarian real-time (Tugas 1 sudah ada). Yang harus kamu kerjakan adalah
**Tugas 2–4**, ditandai di kode dengan komentar `// TODO [Tx.y]`.

> Jawaban lengkap ada di `praktikum/ch07`. Kerjakan dulu sendiri, baru
> bandingkan. Jangan menyalin sebelum semua TODO selesai.

---

## TODO — Wajib Diselesaikan Mahasiswa

**Menemukan semua TODO:** Android Studio → **View → Tool Windows → TODO**
(`⌘6` di macOS, `Alt+6` di Windows/Linux). Hapus komentar `TODO` setelah selesai,
sehingga daftar itu **kosong** saat kamu selesai.

> Tugas 1 (aplikasi CatatanKu dengan Room) sudah ada di starter — pahami tiap
> lapisannya (`Entity → DAO → Database → Repository → ViewModel → Route/Screen`)
> sebelum mengerjakan Tugas 2–4.

### Tugas 2 — Undo hapus dengan Snackbar

| ID | File | Yang harus dikerjakan |
|---|---|---|
| [ ] **T2.1** | `ui/list/NoteListViewModel.kt` | `NoteListUiState`: tambah `recentlyDeleted: NoteEntity? = null` |
| [ ] **T2.2** | `ui/list/NoteListViewModel.kt` | Simpan `recentlyDeleted` sebagai `MutableStateFlow`, masukkan ke `combine`; `deleteNote()` mengisinya setelah menghapus; tambah `undoDelete()` (masukkan kembali catatan yang **sama**, id ikut kembali) dan `onUndoDismissed()` |
| [ ] **T2.3** | `ui/list/NoteListScreen.kt` | `SnackbarHostState` + `LaunchedEffect(uiState.recentlyDeleted)`: tampilkan "Catatan dihapus" dengan tombol **Batalkan**; tekan → `onUndoDelete()`, hilang sendiri → `onUndoDismissed()` |
| [ ] **T2.4** | `ui/list/NoteListRoute.kt` | Teruskan `viewModel::undoDelete` dan `viewModel::onUndoDismissed` |

### Tugas 3 — Kolom `updatedAt` dan "Diubah 3 jam lalu"

| ID | File | Yang harus dikerjakan |
|---|---|---|
| [ ] **T3.1** | `data/NoteEntity.kt` | Tambah `updatedAt: Long = createdAt` |
| [ ] **T3.2** | `data/NoteDatabase.kt` | Naikkan versi + `Migration` yang menambah kolom (`INTEGER NOT NULL DEFAULT 0`) lalu `UPDATE notes SET updatedAt = createdAt`; ganti `fallbackToDestructiveMigration()` dengan `addMigrations(...)` |
| [ ] **T3.3** | `util/RelativeTime.kt` | Implementasikan `formatRelativeTime(now, then)`: *baru saja*, *N menit/jam/hari/bulan/tahun lalu* |
| [ ] **T3.4** | `ui/edit/NoteEditViewModel.kt` | Isi/perbarui `updatedAt = now` saat menyimpan (catatan baru **dan** edit) |
| [ ] **T3.5** | `ui/list/NoteCard.kt`, `NoteListScreen.kt` | Parameter `now` di `NoteCard`; tampilkan "Diubah …" |

### Tugas 4 (Tantangan) — Tag/label dengan TypeConverter

| ID | File | Yang harus dikerjakan |
|---|---|---|
| [ ] **T4.1** | `data/NoteEntity.kt` | Tambah `tags: List<String> = emptyList()` |
| [ ] **T4.2** | `data/Converters.kt` *(baru)*, `NoteDatabase.kt` | `TypeConverter` `List<String>` ⇄ JSON (Gson, dependensinya sudah ada); daftarkan dengan `@TypeConverters` |
| [ ] **T4.3** | `data/NoteDatabase.kt` | Naikkan versi + `Migration`: `ALTER TABLE notes ADD COLUMN tags TEXT NOT NULL DEFAULT '[]'` |
| [ ] **T4.4** | `util/Tags.kt` | Implementasikan `parseTags()` dan `formatTags()` |
| [ ] **T4.5** | `NoteEditViewModel/Screen/Route` | Field tag di form (`tagsText`, `onTagsChange`), diurai dengan `parseTags()` saat menyimpan, dimuat dengan `formatTags()` saat mengedit |
| [ ] **T4.6** | `ui/list/NoteListViewModel.kt`, `NoteListRoute.kt` | `availableTags`, `selectedTag`, `onTagSelected()`; ketuk tag aktif melepas filter; tag yang tak dipakai lagi otomatis dilepas; `isFiltering` ikut memperhitungkan tag |
| [ ] **T4.7** | `ui/list/NoteListScreen.kt` | Baris filter chip (`Semua`, `#tag`…) + pesan kosong untuk filter tag |
| [ ] **T4.8** | `ui/list/NoteCard.kt` | Tampilkan tag (`#kuliah  #ide`) |

### Migration: nomor versi mengikuti urutan pengerjaanmu

Tugas 3 dan 4 sama-sama mengubah skema. Aturannya, setiap perubahan skema
**wajib** menaikkan versi (+1) dan menyediakan `Migration(awal, akhir)` yang cocok,
jika tidak Room crash dengan `IllegalStateException` di perangkat yang sudah
punya database lama. Solusi `ch07` mengerjakan **tag dulu** (`1→2`) lalu
`updatedAt` (`2→3`); urutan yang kamu pilih boleh berbeda, asalkan konsisten.

### Aturan yang harus dipatuhi

- `NoteListScreen`, `NoteEditScreen`, dan `NoteCard` **tidak boleh** mengimpor
  apa pun dari `ViewModel` atau `Repository` — hanya menerima state dan callback.
- ViewModel **tidak boleh** memanggil DAO langsung; lewat `NoteRepository`.
- Jangan memakai `fallbackToDestructiveMigration()` di hasil akhir: itu menghapus
  data pengguna saat versi naik.
- Waktu relatif dan parsing tag adalah **fungsi murni** (tanpa Android) di `util/`.

### Membaca hasil tes

```bash
./gradlew testDebugUnitTest
```

| Berkas tes | Kondisi awal | Artinya |
|---|---|---|
| `NoteListViewModelTest` (4), `NoteEditViewModelTest` (4) | **hijau** | perilaku dasar; harus tetap hijau saat kamu menambah fitur |
| `RelativeTimeTest` (7) | **merah** | hijau setelah **T3.3** selesai |
| `TagsTest` (3) | **merah** | hijau setelah **T4.4** selesai |

Tes ViewModel memakai `FakeNoteRepository`, bukan Room — bukti bahwa ViewModel
hanya bergantung pada interface `NoteRepository`. Untuk Tugas 2 dan 4, tambahkan
tes serupa (contoh lengkap di `praktikum/ch07`).

### Dianggap selesai jika semua ini benar

Jalankan aplikasi di emulator, lalu periksa satu per satu:

**Tugas 2**
- [ ] Hapus sebuah catatan → muncul Snackbar "Catatan dihapus" dengan **Batalkan**.
- [ ] Tekan **Batalkan** → catatan kembali **persis sama** (isi, pin, tag, posisi).
- [ ] Biarkan Snackbar hilang → catatan tetap terhapus.

**Tugas 3**
- [ ] Kartu menampilkan "Diubah baru saja" untuk catatan yang baru dibuat/diubah.
- [ ] Mengedit catatan memperbarui waktunya; **menyematkan tidak** mengubahnya.
- [ ] Catatan dari versi lama (sebelum migration) tidak tampil sebagai "puluhan tahun lalu".

**Tugas 4**
- [ ] Isi tag `Kuliah, #Ide, kuliah` → tersimpan sebagai `#kuliah  #ide` (huruf kecil, tanpa duplikat).
- [ ] Baris chip menampilkan `Semua` dan semua tag; ketuk `#kuliah` → hanya catatan bertag itu; ketuk lagi → semua kembali.
- [ ] Filter tag bekerja bersama pencarian; hapus catatan bertag terakhir → chip-nya hilang.

**Kode**
- [ ] Jendela TODO kosong, `./gradlew assembleDebug` sukses, dan `./gradlew testDebugUnitTest` hijau semua.
- [ ] Data tetap ada setelah aplikasi ditutup total dan dibuka kembali.

**Pengumpulan:** push ke branch `pertemuan-7` → kumpulkan link di LMS sebelum
pertemuan berikutnya.

---

## Cara Membuka di Android Studio

1. **File → Open** → pilih folder `praktikum/ch07_starter/`
2. Tunggu Gradle sync — dependensi: Room (`room-runtime`, `room-ktx`,
   `room-compiler` via KSP) dan Gson.
3. Pilih device/emulator → klik **Run ▶**

> **Versi compiler sama dengan Ch03–Ch06** — Gradle 8.6, AGP 8.3.2, Kotlin 1.9.23,
> plus KSP `1.9.23-1.0.20` dan Room `2.6.1`.

Materi konsep Room (Entity, DAO, Database, migration, Flow) ada di buku Bab 7
dan slide `ch07`; struktur proyek dan penjelasan tiap lapisan ada di
`praktikum/ch07/README.md`.

---

## Run the code

```bash
cd /Users/dhareva/Books/2026_MP/codes/praktikum/ch07_starter
./gradlew installDebug
adb shell am start -n com.example.ch07/.MainActivity
```
