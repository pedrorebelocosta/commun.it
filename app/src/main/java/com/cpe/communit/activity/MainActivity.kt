package com.cpe.communit.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cpe.communit.R
import com.cpe.communit.SessionManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onResume() {
        super.onResume()
        SessionManager.setup(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initializing the SessionManager so it can access the SharedPreferences
        map_card.setOnClickListener {
            startActivity(Intent(this, MapsActivity::class.java))
        }

        note_card.setOnClickListener {
            startActivity(Intent(this, NotesActivity::class.java))
        }

        profile_card.setOnClickListener {
            if (SessionManager.isLoggedIn()) {
                startActivity(Intent(this, ProfileActivity::class.java))
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }
    }
}
