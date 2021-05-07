package com.cpe.communit

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import com.cpe.communit.data.JWTPayload
import com.google.gson.Gson

object SessionManager {
    private var sharedPreferences: SharedPreferences? = null
    private const val NON_EXISTING_KEY = "non_existing"
    private lateinit var JWT_KEY: String

    fun setup(context: Context) {
        this.JWT_KEY = context.getString(R.string.app_jwt_key)
        this.sharedPreferences = context.getSharedPreferences(
            context.getString(R.string.user_prefs_info_key),
            Context.MODE_PRIVATE
        )
    }

    fun isLoggedIn(): Boolean {
        val rawTokenString = getRawJWTFromSharedPrefs()
        if (rawTokenString == NON_EXISTING_KEY) return false
        return true
    }

    fun getJWTPayload(): JWTPayload? {
        if (!isLoggedIn()) return null
        val rawTokenString = getRawJWTFromSharedPrefs()
        if (rawTokenString == NON_EXISTING_KEY) return null
        val payload = rawTokenString?.split(".")
        val decodedPayload = Base64.decode(payload?.get(1), Base64.DEFAULT).decodeToString()
        return Gson().fromJson(decodedPayload, JWTPayload::class.java)
    }

    fun logout() {
        with(sharedPreferences!!.edit()) {
            this.remove(JWT_KEY)
            apply()
        }
    }

    fun getRawJWTFromSharedPrefs(): String? {
       return sharedPreferences?.getString(JWT_KEY, NON_EXISTING_KEY)
    }
}