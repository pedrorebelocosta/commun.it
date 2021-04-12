package com.cpe.communit.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.cpe.communit.R
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        login_back_button.setOnClickListener { onBackPressed() }
    }
}