package com.cpe.communit.service

import com.cpe.communit.data.Credentials
import com.cpe.communit.data.JsonWebToken
import com.cpe.communit.data.Occurrence
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface EndPoints {
    @POST("/auth")
    fun authenticateUser(@Body cred: Credentials): Call<JsonWebToken>

    @GET("/occurrence")
    fun getOccurrences(): Call<List<Occurrence>>
}