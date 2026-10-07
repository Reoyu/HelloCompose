package com.example.ch07.util

// *TODO [T4.4] Implementasikan dua fungsi murni ini.*
//   *parseTags("Kuliah, #Ide,  kuliah") -> ["kuliah", "ide"]*
//   *(dipisah koma; spasi dan awalan # dibuang; huruf kecil; tanpa duplikat;
//    *elemen kosong dibuang).  formatTags(["kuliah","ide"]) -> "kuliah, ide".*
//   *Tes yang sudah disediakan: TagsTest (merah sampai fungsi ini selesai).*
fun parseTags(text: String): List<String> {
    return text
        .split(",")
        .map { it.trim().removePrefix("#").lowercase() }
        .filter { it.isNotBlank() }
        .distinct()
}

fun formatTags(tags: List<String>): String {
    return tags
        .map { it.trim().removePrefix("#").lowercase() }
        .filter { it.isNotBlank() }
        .distinct()
        .joinToString(", ")
}