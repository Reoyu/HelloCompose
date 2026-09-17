package com.example.ch05starter

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// ============================================================================
// TODO Pertemuan 5 — ExploreScreen (soal #1 di Tugas Pertemuan 5)
//
// Ganti placeholder di bawah ini menjadi GRID FOTO memakai `LazyVerticalGrid`.
// Data dummy `dummyPhotos` sudah disiapkan — tinggal dirender.
//
// [ ] Tambahkan import:
//       androidx.compose.foundation.lazy.grid.GridCells
//       androidx.compose.foundation.lazy.grid.LazyVerticalGrid
//       androidx.compose.foundation.lazy.grid.items
// [ ] Ganti isi Scaffold content dengan LazyVerticalGrid(columns = GridCells.Fixed(2)) { ... }
// [ ] Render tiap item `dummyPhotos` sebagai kotak warna (Box + Modifier.background)
//     atau `Image(painter = painterResource(...))` kalau punya drawable sungguhan
// [ ] Beri jarak antar sel dengan `verticalArrangement`/`horizontalArrangement`
//     = Arrangement.spacedBy(8.dp), dan contentPadding = PaddingValues(8.dp)
//
// Kerangka kode (hapus komentar di bawah setelah import ditambahkan):
//
// LazyVerticalGrid(
//     columns             = GridCells.Fixed(2),
//     contentPadding      = PaddingValues(8.dp),
//     verticalArrangement = Arrangement.spacedBy(8.dp),
//     horizontalArrangement = Arrangement.spacedBy(8.dp)
// ) {
//     items(dummyPhotos, key = { it.id }) { photo ->
//         Box(
//             modifier = Modifier
//                 .aspectRatio(1f)
//                 .background(photo.color)
//         )
//     }
// }
// ============================================================================

data class Photo(val id: Int, val color: androidx.compose.ui.graphics.Color)

val dummyPhotos = (1..21).map { i ->
    Photo(
        id    = i,
        color = androidx.compose.ui.graphics.Color(
            red   = (i * 37 % 256) / 255f,
            green = (i * 91 % 256) / 255f,
            blue  = (i * 53 % 256) / 255f
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen() {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Eksplorasi") }) }
    ) { padding ->

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(dummyPhotos, key = { it.id }) { photo ->
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .background(photo.color)
                )
            }
        }
    }
}