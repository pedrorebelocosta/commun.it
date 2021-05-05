package com.cpe.communit.service

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceBuilder {
    private val httpClient = OkHttpClient.Builder().build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:1337")
        .addConverterFactory(GsonConverterFactory.create())
        .client(httpClient)
        .build()

    fun<T> buildService(service: Class<T>): T {
        return retrofit.create(service)
    }
}