package com.cpe.communit.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.cpe.communit.CommunitApplication
import com.cpe.communit.R
import com.cpe.communit.adapter.NoteAdapter
import com.cpe.communit.viewmodel.NoteViewModel
import com.cpe.communit.viewmodel.NoteViewModelFactory
import kotlinx.android.synthetic.main.activity_notes.*

class NotesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)
        notes_back_button.setOnClickListener { onBackPressed() }

        val noteViewModel: NoteViewModel by viewModels {
            NoteViewModelFactory((application as CommunitApplication).repository)
        }

        val noteAdapter = NoteAdapter(this)

        // Provide adapter and layoutManager for RecyclerView
        notes_recycler_view.adapter = noteAdapter
        notes_recycler_view.layoutManager = LinearLayoutManager(this)

        noteViewModel.allNotes.observe(this, Observer {
            it?.let { noteAdapter.setNotes(it) }
        })
    }
}
