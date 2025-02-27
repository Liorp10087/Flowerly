package com.example.flowerly.utils

import com.example.flowerly.R

fun loadImageResource(imageName: String): Int {
    return when (imageName) {
        "rose1.jpg" -> R.drawable.rose1
        "tulip.jpg" -> R.drawable.tulip
        "dandelion.jpg" -> R.drawable.dandelion
        "ic_profile.png" -> R.drawable.ic_profile
        else -> R.drawable.ic_profile
    }
}
