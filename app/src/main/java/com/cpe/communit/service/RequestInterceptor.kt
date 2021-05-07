package com.cpe.communit.service

import com.cpe.communit.SessionManager
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class RequestInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val endPointURL = chain.request().url().encodedPath()
        val endPointMethod = chain.request().method()

        // Don't inject token on request if we're logging in or signing up
        if (endPointURL == AUTH_ENDPOINT || endPointURL == SIGNUP_ENDPOINT) {
            return chain.proceed(chain.request())
        }
        // Don't inject token on request if we're using the GET HTTP verb
        if (endPointURL == OCCURRENCE_ENDPOINT && endPointMethod == HTTP_GET) {
            return chain.proceed(chain.request())
        }

        // Otherwise, inject the Token
        val interceptedRequest: Request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer ${SessionManager.getRawJWTFromSharedPrefs()}")
            .build()
        return chain.proceed(interceptedRequest)
    }

    private companion object {
        const val OCCURRENCE_ENDPOINT = "/occurrence"
        const val AUTH_ENDPOINT = "/auth"
        const val SIGNUP_ENDPOINT = "/signup"
        const val HTTP_GET = "GET"
    }
}