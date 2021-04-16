package com.cpe.communit

import android.app.Application
import com.cpe.communit.db.NoteDatabase
import com.cpe.communit.repository.NoteRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class CommunitApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob())
    private val database by lazy { NoteDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { NoteRepository(database.noteDao()) }
}