package com.example.ch10.util

// Sumber waktu "sekarang". Di Ch09 berupa lambda () -> Long. Diganti
// interface karena Hilt tidak bisa meng-inject lambda tanpa qualifier.
// Saat pengujian cukup: Clock { 1_000L }.
fun interface Clock {
    fun now(): Long
}
