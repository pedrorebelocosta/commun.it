package com.cpe.communit

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        map_card.setOnClickListener {
            startActivity(Intent(this, MapsActivity::class.java))
        }

        note_card.setOnClickListener {
            startActivity(Intent(this, NotesActivity::class.java))
        }
    }
}
