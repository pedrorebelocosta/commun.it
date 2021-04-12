package com.cpe.communit.activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.cpe.communit.CommunitApplication
import com.cpe.communit.R
import com.cpe.communit.adapter.NoteAdapter
import com.cpe.communit.entity.Note
import com.cpe.communit.viewmodel.NoteViewModel
import com.cpe.communit.viewmodel.NoteViewModelFactory
import kotlinx.android.synthetic.main.activity_add_note.*
import kotlinx.android.synthetic.main.activity_notes.*

class NotesActivity : AppCompatActivity() {
    private val noteViewModel: NoteViewModel by viewModels { NoteViewModelFactory((application as CommunitApplication).repository) }
    private val noteAdapter = NoteAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)
        notes_back_button.setOnClickListener { onBackPressed() }

        // Provide adapter and LayoutManager for RecyclerView
        notes_recycler_view.adapter = noteAdapter
        notes_recycler_view.layoutManager = LinearLayoutManager(this)

        noteViewModel.allNotes.observe(this, Observer {
            it?.let { noteAdapter.setNotes(it) }
        })

        fab_add_note.setOnClickListener {
            val intent = Intent(this@NotesActivity, AddNoteActivity::class.java)
            startActivityForResult(intent, NEW_NOTE_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == NEW_NOTE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.getParcelableExtra<Note>(AddNoteActivity.REPLY_NOTE)?.let {
                noteViewModel.insert(it)
            }
        }
    }

    private companion object {
        private const val NEW_NOTE_REQUEST_CODE = 1
    }
}
