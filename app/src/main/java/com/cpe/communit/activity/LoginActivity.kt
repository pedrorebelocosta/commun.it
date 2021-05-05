package com.cpe.communit.activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.cpe.communit.R
import com.cpe.communit.data.Credentials
import com.cpe.communit.data.JsonWebToken
import com.cpe.communit.service.EndPoints
import com.cpe.communit.service.ServiceBuilder
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.HttpURLConnection

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        login_back_button.setOnClickListener { onBackPressed() }
        login_activity_button.setOnClickListener {
            handleFormSubmit()
        }
    }

    private fun handleFormSubmit() {
        val email = activity_login_email.text.toString()
        val password = activity_login_password.text.toString()
        val request = ServiceBuilder.buildService(EndPoints::class.java)
        val call = request.authenticateUser(Credentials(email, password))

        call.enqueue(object : Callback<JsonWebToken> {
            override fun onResponse(call: Call<JsonWebToken>, response: Response<JsonWebToken>) {
                if (response.isSuccessful) {
                    Log.d(this.javaClass.name, response.body()!!.toString())
                    val sharedPref = getSharedPreferences(getString(R.string.user_prefs_info_key), Context.MODE_PRIVATE) ?: return
                    with (sharedPref.edit()) {
                        putString(getString(R.string.app_jwt_key), response.body()!!.token)
                        apply()
                    }
                    finish()
                } else if (response.code() == HttpURLConnection.HTTP_FORBIDDEN) {
                    Toast.makeText(this@LoginActivity, getString(R.string.incorrect_email_or_password), Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<JsonWebToken>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
                Log.d(this.javaClass.name, t.message.toString())
            }
        })
    }
}