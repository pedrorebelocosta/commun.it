package com.cpe.communit.activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import com.cpe.communit.R
import com.cpe.communit.entity.Note
import kotlinx.android.synthetic.main.activity_add_note.*

class AddNoteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)
        add_note_back_button.setOnClickListener { onBackPressed() }

        add_note_save.setOnClickListener {
            val replyIntent = Intent()
            if (!TextUtils.isEmpty(add_note_title.text) && !TextUtils.isEmpty(add_note_description.text)) {
                val title = add_note_title.text.toString()
                val description = add_note_description.text.toString()
                replyIntent.putExtra(REPLY_NOTE, Note(title = title, description = description))
                setResult(Activity.RESULT_OK, replyIntent)
                finish()
            }

            if (TextUtils.isEmpty(add_note_title.text)) {
                title_layout.error = getString(R.string.empty_note_title_error)
            }

            if (TextUtils.isEmpty(add_note_description.text)) {
                description_layout.error = getString(R.string.empty_note_desc_error)
            }
        }
    }

    companion object {
        const val REPLY_NOTE = "com.cpe.communit.activity.AddNoteActivity.REPLY_NOTE"
    }
}
