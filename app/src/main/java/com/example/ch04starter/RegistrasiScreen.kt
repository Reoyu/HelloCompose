package com.example.ch04starter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ch04starter.ui.theme.Ch04StarterTheme

// ============================================================================
// TODO Pertemuan 4 — RegistrasiScreen (soal #3 di Tugas Pertemuan 4)
//
// Form registrasi: nama, email, password, konfirmasi password. Tombol
// "Daftar" HARUS nonaktif (disabled) sampai semua validasi lolos — dihitung
// dengan `derivedStateOf`, persis pola `bmi`/`kategori` di BmiScreen.
//
// Validasi yang perlu dicek (semua harus true sebelum Submit aktif):
// [ ] 3a. `nama` tidak kosong
// [ ] 3b. `email` mengandung karakter "@" (validasi sederhana, tidak perlu regex penuh)
// [ ] 3c. `password` minimal 8 karakter
// [ ] 3d. `konfirmasiPassword` sama persis dengan `password`
// [ ] 3e. Gabungkan keempatnya jadi satu `derivedStateOf<Boolean>` bernama
//         `isFormValid`, lalu pasang ke parameter `enabled` tombol Daftar
//
// Field sudah disiapkan di bawah (controlled input, sama seperti slide
// TextField) — fokuskan energi kalian ke logika validasinya.
// ============================================================================

@Composable
fun RegistrasiScreen() {
    var nama                by rememberSaveable { mutableStateOf("") }
    var email                by rememberSaveable { mutableStateOf("") }
    var password             by rememberSaveable { mutableStateOf("") }
    var konfirmasiPassword   by rememberSaveable { mutableStateOf("") }

    // TODO 3e: ganti `true` di bawah dengan `derivedStateOf` yang menggabungkan
    //          4 validasi (3a-3d) di atas. Contoh kerangka:
    //
    val isFormValid by remember {
        derivedStateOf {
            nama.isNotBlank() &&
                    email.contains("@") &&
                    password.length >= 8 &&
                    password == konfirmasiPassword
        }
    }

    Column(
        modifier            = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text  = "Form Registrasi",
            style = MaterialTheme.typography.headlineMedium
        )

        OutlinedTextField(
            value         = nama,
            onValueChange = { nama = it },
            label         = { Text("Nama Lengkap") },
            modifier      = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value           = email,
            onValueChange   = { email = it },
            label           = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier        = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value                = password,
            onValueChange        = { password = it },
            label                = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions      = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier             = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value                = konfirmasiPassword,
            onValueChange        = { konfirmasiPassword = it },
            label                = { Text("Konfirmasi Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions      = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier             = Modifier.fillMaxWidth()
        )

        Button(
            onClick  = { /* TODO (opsional): aksi setelah submit, mis. tampilkan pesan sukses */ },
            enabled  = isFormValid,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Daftar")
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegistrasiScreenPreview() {
    Ch04StarterTheme { RegistrasiScreen() }
}
