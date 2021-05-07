package com.cpe.communit.data

data class Occurrence(
    val id: Int? = null,
    val user_id: Int? = null,
    val description: String,
    val lat: Double,
    val lng: Double,
    val photo_url: String,
    val is_road_problem: Boolean
)
