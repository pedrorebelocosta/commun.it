package com.cpe.communit

import android.content.Context
import android.util.Base64
import com.cpe.communit.data.JWTPayload
import com.google.gson.Gson

object SessionManager {
    private const val NON_EXISTING_KEY = "non_existing"

    fun isLoggedIn(context: Context): Boolean {
        val rawTokenString = getJWTFromSharedPrefs(context)
        if (rawTokenString == NON_EXISTING_KEY) return false
        return true
    }

    fun getJWTPayload(context: Context): JWTPayload? {
        if (!isLoggedIn(context)) return null
        val rawTokenString = getJWTFromSharedPrefs(context)
        if (rawTokenString == NON_EXISTING_KEY) return null
        val payload = rawTokenString?.split(".")
        val decodedPayload = Base64.decode(payload?.get(1), Base64.DEFAULT).decodeToString()
        return Gson().fromJson(decodedPayload, JWTPayload::class.java)
    }

    fun logout(context: Context) {
        val sharedPreferences = context.getSharedPreferences(
            context.getString(R.string.user_prefs_info_key),
            Context.MODE_PRIVATE
        )

        with(sharedPreferences.edit()) {
            this.remove(context.getString(R.string.app_jwt_key))
            apply()
        }
    }

    private fun getJWTFromSharedPrefs(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences(
            context.getString(R.string.user_prefs_info_key),
            Context.MODE_PRIVATE
        )
       return sharedPreferences.getString(context.getString(R.string.app_jwt_key), NON_EXISTING_KEY)
    }
}