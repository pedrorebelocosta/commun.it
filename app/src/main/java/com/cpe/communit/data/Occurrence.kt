package com.cpe.communit.data

data class Occurrence(
    val id: Int,
    val user_id: Int,
    val description: String,
    val lat: Double,
    val lng: Double,
    val photo_url: String,
    val is_road_problem: Boolean
)
