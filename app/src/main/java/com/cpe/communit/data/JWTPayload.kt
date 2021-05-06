package com.cpe.communit.data

data class JWTPayload(
    val iss: String,
    val aud: String,
    val iat: Int,
    val exp: Int,
    val user_id: Int,
    val email: String,
    val first_name: String,
    val last_name: String
)