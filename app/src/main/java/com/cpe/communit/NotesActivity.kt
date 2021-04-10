package com.cpe.communit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.cpe.communit.adapters.NoteAdapter
import com.cpe.communit.dataclasses.Note
import kotlinx.android.synthetic.main.activity_notes.*

class NotesActivity : AppCompatActivity() {
    private lateinit var myList: ArrayList<Note>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)
        notes_back_button.setOnClickListener { onBackPressed() }

        myList = ArrayList<Note>()

        for (i in 0 until 500) {
            myList.add(Note("Title: $i", "Description $i*10"))
        }
        notes_recycler_view.adapter = NoteAdapter(myList)
        notes_recycler_view.layoutManager = LinearLayoutManager(this)
    }
}
