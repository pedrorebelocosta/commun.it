package com.cpe.communit.service

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceBuilder {
    private val httpClient = OkHttpClient.Builder().addInterceptor(RequestInterceptor()).build()
    private const val BASE_URL = "http://10.0.2.2:1337"
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(httpClient)
        .build()

    fun<T> buildService(service: Class<T>): T {
        return retrofit.create(service)
    }
}