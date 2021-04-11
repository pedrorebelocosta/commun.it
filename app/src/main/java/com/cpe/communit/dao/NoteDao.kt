package com.cpe.communit.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.cpe.communit.entity.Note

@Dao
interface NoteDao {
    @Query("SELECT * FROM Note")
    fun getAll(): LiveData<List<Note>>

    @Query("SELECT * FROM Note WHERE title LIKE :title LIMIT 1")
    fun findByName(title: String): Note

    @Query("SELECT * FROM Note WHERE description LIKE :description LIMIT 1")
    fun findByDescription(description: String): Note

    @Insert
    fun insert(note: Note)

    @Delete
    fun delete(note: Note)

    @Query("DELETE FROM Note")
    fun deleteAll()
}