package com.ipleiria.moveit.models

import com.google.maps.model.AddressType
import com.google.maps.model.PlaceIdScope

data class GooglePlace (
    val lat: Double,
    val lng: Double,
    //val icon: String?,
    val name: String?,
    //val photos: List<PhotoGooglePlace>?,
    val placeId: String?,
    val rating: Float?,
    //val types: Array<AddressType>?,
    val vicinity: String?
)