package com.example.flowerly.utils

import android.widget.ImageView
import com.example.flowerly.R
import com.squareup.picasso.Picasso

fun loadImageFromFirebase(imageUrl: String, imageView: ImageView) {
    if (imageUrl.isEmpty()) {
        imageView.setImageResource(R.drawable.ic_profile)
        return
    }

    Picasso.get()
        .load(imageUrl)
        .placeholder(R.drawable.ic_profile)
        .error(R.drawable.ic_profile)
        .into(imageView)
}
