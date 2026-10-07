package com.example.ch07.di

import com.example.ch07.data.NoteRepository

// Manual DI (Chapter 6): satu tempat yang tahu cara merakit dependency.
interface AppContainer {
    val noteRepository: NoteRepository
}
