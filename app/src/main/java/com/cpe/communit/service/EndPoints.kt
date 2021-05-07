package com.cpe.communit.service

import com.cpe.communit.data.Credentials
import com.cpe.communit.data.JsonWebToken
import com.cpe.communit.data.Occurrence
import com.cpe.communit.data.UploadResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface EndPoints {
    @Multipart
    @POST("/upload")
    fun upload(@Part photo: MultipartBody.Part): Call<UploadResponse>

    @POST("/auth")
    fun authenticateUser(@Body cred: Credentials): Call<JsonWebToken>

    @GET("/occurrence")
    fun getOccurrences(): Call<List<Occurrence>>

    @POST("/occurrence")
    fun createOccurrence(@Body occurrence: Occurrence): Call<Void>

}