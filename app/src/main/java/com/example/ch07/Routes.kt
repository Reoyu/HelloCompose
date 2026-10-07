package com.example.ch07

const val ARG_NOTE_ID = "noteId"

// Semua route didefinisikan sebagai konstanta agar terhindar dari typo
sealed class Routes(val route: String) {
    data object NoteList : Routes("notes")

    // noteId = 0 -> catatan baru
    data object NoteEdit : Routes("edit/{$ARG_NOTE_ID}") {
        fun createRoute(noteId: Int = 0) = "edit/$noteId"
    }
}
