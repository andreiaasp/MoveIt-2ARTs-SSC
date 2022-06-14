package com.ipleiria.moveit.models

data class GooglePlace (
    val lat: Double,
    val lng: Double,
    val name: String?,
    val placeId: String?,
    val rating: Float?,
    val vicinity: String?
)