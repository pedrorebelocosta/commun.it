package com.cpe.communit.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.cpe.communit.dao.NoteDao
import com.cpe.communit.entity.Note

class NoteRepository(private val noteDao: NoteDao) {
    val allNotes: LiveData<List<Note>> = noteDao.getAll()

    @WorkerThread
    fun insert(note: Note) {
        noteDao.insert(note)
    }

    @WorkerThread
    fun delete(note: Note) {
        noteDao.delete(note)
    }
}