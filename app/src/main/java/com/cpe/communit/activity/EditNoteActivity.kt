package com.cpe.communit.activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import com.cpe.communit.R
import com.cpe.communit.entity.Note
import kotlinx.android.synthetic.main.activity_add_note.*
import kotlinx.android.synthetic.main.activity_edit_note.*

class EditNoteActivity : AppCompatActivity() {
    private lateinit var note: Note
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_note)
        edit_note_back_button.setOnClickListener { onBackPressed() }

        intent.getParcelableExtra<Note>(NotesActivity.NOTE_EXTRA)?.let {
            Log.i(CUR_ACTIVITY, "Received note from intent")
            note = it
            edit_note_title.setText(note.title)
            edit_note_description.setText(note.description)
        }
        edit_note_save.setOnClickListener {
            val replyIntent = Intent()
            if (!TextUtils.isEmpty(edit_note_title.text) && !TextUtils.isEmpty(edit_note_description.text)) {
                this.note.title = edit_note_title.text.toString()
                this.note.description = edit_note_description.text.toString()
                replyIntent.putExtra(REPLY_EDITED_NOTE, note)
                setResult(Activity.RESULT_OK, replyIntent)
                finish()
            }
            if (TextUtils.isEmpty(edit_note_title.text)) {
                title_layout.error = getString(R.string.empty_note_title_error)
            }
            if (TextUtils.isEmpty(edit_note_description.text)) {
                description_layout.error = getString(R.string.empty_note_desc_error)
            }
        }
    }

    companion object {
        const val REPLY_EDITED_NOTE = "com.cpe.communit.activity.EditNoteActivity.REPLY_EDITED_NOTE"
        private const val CUR_ACTIVITY = "EditNoteActivity: "
    }
}