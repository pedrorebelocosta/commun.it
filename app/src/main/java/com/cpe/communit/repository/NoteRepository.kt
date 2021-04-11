package com.cpe.communit.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.cpe.communit.dao.NoteDao
import com.cpe.communit.entity.Note

class NoteRepository(private val noteDao: NoteDao) {
    val allNotes: LiveData<List<Note>> = noteDao.getAll()

    @WorkerThread
    suspend fun insert(note: Note) {
        println("Repository insert has been called!")
        noteDao.insert(note)
    }
}