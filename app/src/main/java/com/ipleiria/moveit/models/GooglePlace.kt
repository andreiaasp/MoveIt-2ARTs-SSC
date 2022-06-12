package com.ipleiria.moveit.models

data class GooglePlace (
    val lat: Double,
    val lng: Double,
    val icon: String?,
    val name: String?,
    val photos: List<PhotoGooglePlace>?,
    val placeId: String?,
    val rating: Float?,
    val scope: String?,
    val types: List<String>?,
    val vicinity: String?
)