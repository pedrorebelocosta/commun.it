package com.cpe.communit

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val mapButton: CardView = findViewById<CardView>(R.id.map_card)
        mapButton.setOnClickListener {
            startActivity(Intent(this, MapsActivity::class.java))
        }
    }
}
