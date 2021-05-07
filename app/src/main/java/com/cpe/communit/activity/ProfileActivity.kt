package com.cpe.communit.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.se.omapi.Session
import android.widget.Toast
import com.cpe.communit.R
import com.cpe.communit.SessionManager
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        initializeFields()
        profile_back_button.setOnClickListener { onBackPressed() }
        profile_save_button.setOnClickListener {
            TODO("Future development")
        }
        profile_logout_button.setOnClickListener {
            SessionManager.logout()
            finish()
        }
    }

    private fun initializeFields() {
        val jwtPayload = SessionManager.getJWTPayload()
        profile_header_name.text = String.format(getString(R.string.profile_full_name_tpl), jwtPayload!!.first_name, jwtPayload.last_name)
        profile_header_email.text = jwtPayload.email
        activity_profile_name.setText(String.format(getString(R.string.profile_full_name_tpl), jwtPayload.first_name, jwtPayload.last_name))
        activity_profile_email.setText(jwtPayload.email)
        activity_profile_password.setText(getString(R.string.profile_password_placeholder))
        profile_edit_information_toggle.setOnClickListener {
            profile_email_layout.isEnabled = profile_edit_information_toggle.isChecked
            profile_password_layout.isEnabled = profile_edit_information_toggle.isChecked
            profile_name_layout.isEnabled = profile_edit_information_toggle.isChecked
            profile_save_button.isEnabled = profile_edit_information_toggle.isChecked
        }
    }
}