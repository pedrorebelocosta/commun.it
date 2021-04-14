package com.cpe.communit.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.cpe.communit.entity.Note

@Dao
interface NoteDao {
    @Insert
    fun insert(note: Note)

    @Query("SELECT * FROM Note")
    fun getAll(): LiveData<List<Note>>

    @Query("SELECT * FROM Note WHERE title LIKE :title LIMIT 1")
    fun findByName(title: String): Note

    @Query("SELECT * FROM Note WHERE description LIKE :description LIMIT 1")
    fun findByDescription(description: String): Note

    @Update
    fun update(note: Note)

    @Delete
    fun delete(note: Note)

    @Query("DELETE FROM Note")
    fun deleteAll()
}