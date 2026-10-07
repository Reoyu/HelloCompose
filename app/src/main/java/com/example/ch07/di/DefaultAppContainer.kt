package com.example.ch07.di

import android.content.Context
import com.example.ch07.data.NoteDatabase
import com.example.ch07.data.NoteRepository
import com.example.ch07.data.NoteRepositoryImpl

class DefaultAppContainer(context: Context) : AppContainer {

    private val database: NoteDatabase by lazy {
        NoteDatabase.getInstance(context)
    }

    override val noteRepository: NoteRepository by lazy {
        NoteRepositoryImpl(database.noteDao())
    }
}
