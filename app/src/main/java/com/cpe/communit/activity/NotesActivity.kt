package com.cpe.communit.activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
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
    private val noteAdapter = NoteAdapter(this@NotesActivity::openNoteOptionsMenu)

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

    /*
        "View" is required so we can inflate the menu directly below it
        "Note" will give us the uid of the note which we can use to perform actions on database
         through the ViewModel
     */
    private fun openNoteOptionsMenu(view: View, note: Note) {
        view.setOnClickListener {
            val popupMenu: PopupMenu = PopupMenu(this, view)
            popupMenu.menuInflater.inflate(R.menu.note_options_menu,popupMenu.menu)
            popupMenu.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.menu_delete_note ->
                        Log.d(CUR_ACTIVITY, "Clicked delete note")
                    R.id.menu_edit_note ->
                        Log.d(CUR_ACTIVITY, "Clicked edit note:")
                }
                true
            }
            popupMenu.show()
        }
    }

    private companion object {
        private const val NEW_NOTE_REQUEST_CODE = 1
        private const val UPDATE_NOTE_REQUEST_CODE = 2
        private const val DELETE_NOTE_REQUEST_CODE = 3

        private const val CUR_ACTIVITY = "NotesActivity: "
    }
}
