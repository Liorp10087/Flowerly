package com.example.flowerly.utils

import android.widget.ImageView
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

fun loadImageFromFirebase(imageName: String, imageView: ImageView) {
    if (imageName.isEmpty()) {
        imageView.setImageResource(com.example.flowerly.R.drawable.ic_profile)
        return
    }

    val storageRef = FirebaseStorage.getInstance().reference.child("images/$imageName")

    storageRef.downloadUrl.addOnSuccessListener { uri ->
        Picasso.get().load(uri).into(imageView)
    }.addOnFailureListener {
        imageView.setImageResource(com.example.flowerly.R.drawable.ic_profile) // Load default image if failed
    }
}
