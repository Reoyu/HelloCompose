package com.example.ch09.ui

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

// Membuka URL artikel di aplikasi browser lewat implicit Intent.
// Tanpa try-catch, perangkat tanpa browser akan crash.
fun openUrl(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    try {
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(context, "Tidak ada aplikasi untuk membuka tautan", Toast.LENGTH_SHORT).show()
    }
}
