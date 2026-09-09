package com.example.ch03

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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

data class Mahasiswa(
    val nama: String,
    val nim: String,
    val ipk: Double
)

val dummyMahasiswa = listOf(
    Mahasiswa("Benaya", "22001", 3.85),
    Mahasiswa("Jayden", "22002", 3.40),
    Mahasiswa("Fronli", "22003", 3.92),
    Mahasiswa("Darren Keith", "22004", 2.95),
    Mahasiswa("Christopher GIjoh", "22005", 3.75),
    Mahasiswa("Gabriel Emil", "22006", 3.50),
    Mahasiswa("Michael Yulianto", "22007", 3.88),
    Mahasiswa("Ben Majesty", "22008", 2.80),
    Mahasiswa("Evan Laluan", "22009", 3.65),
    Mahasiswa("Christian Hiroshi", "22010", 3.20)
)

@Composable
fun StudentListScreen() {
    val categories = listOf(
        "Semua",
        "IPK ≥ 3.5",
        "IPK < 3.5"
    )

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // LazyRow kategori
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(
                horizontal = 16.dp,
                vertical = 8.dp
            ),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Text(
                        text = category,
                        modifier = Modifier.padding(
                            horizontal = 16.dp,
                            vertical = 10.dp
                        ),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        DaftarMahasiswa(
            mahasiswaList = dummyMahasiswa,
            modifier = Modifier.weight(1f)
        )
    }
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

        // HEADER
        item {
            Text(
                text = "Daftar Mahasiswa",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        // DATA MAHASISWA
        items(
            items = mahasiswaList,
            key = { it.nim }
        ) { mahasiswa ->
            MahasiswaCard(mahasiswa)
        }

        // FOOTER
        item {
            Text(
                text = "Total Mahasiswa: ${mahasiswaList.size}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(
                    top = 8.dp,
                    bottom = 16.dp
                )
            )
        }
    }
}

@Composable
fun MahasiswaCard(mahasiswa: Mahasiswa) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
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

// PREVIEW LIGHT
@Preview(
    showBackground = true,
    showSystemUi = true,
    name = "Light Mode"
)
@Composable
fun StudentListScreenLightPreview() {
    Ch03Theme(darkTheme = false) {
        StudentListScreen()
    }
}

// PREVIEW DARK
@Preview(
    showBackground = true,
    showSystemUi = true,
    name = "Dark Mode"
)
@Composable
fun StudentListScreenDarkPreview() {
    Ch03Theme(darkTheme = true) {
        StudentListScreen()
    }
}