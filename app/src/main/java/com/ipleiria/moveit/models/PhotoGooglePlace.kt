package com.ipleiria.moveit.models

import android.widget.ImageView
import com.bumptech.glide.Glide

data class PhotoGooglePlace (
    val height: Int?,
    val htmlAttributions: List<String>?,
    val photoReference: String?,
    val width: Int?
) {
    companion object {
        fun loadImage(view: ImageView, image: String?) {
            Glide.with(view.context).load(image).into(view)
        }
    }
}
