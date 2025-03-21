package com.example.flowerly.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.flowerly.databinding.FragmentUploadPostBinding
import com.example.flowerly.model.FirebaseModel
import com.example.flowerly.model.Model
import com.example.flowerly.model.Post

class UploadPostFragment : Fragment() {
    private lateinit var binding: FragmentUploadPostBinding
    private var imageUri: Uri? = null

    private val imagePickerResult =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            imageUri = uri
            binding.imageView.setImageURI(imageUri)
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUploadPostBinding.inflate(inflater, container, false)

        binding.uploadButton.setOnClickListener {
            uploadPost()
        }

        binding.imageView.setOnClickListener {
            imagePickerResult.launch("image/*")
        }

        return binding.root
    }

    private fun uploadPost() {
        val title = binding.titleEditText.text.toString().trim()
        val description = binding.descriptionEditText.text.toString().trim()
        val selectedImageUri = imageUri
        val currentUser = Model.instance.getCurrentUser()

        if (title.isNotEmpty() && selectedImageUri != null && currentUser != null) {
            val post = Post(
                id = System.currentTimeMillis().toString(),
                title = title,
                description = description,
                imagePathUrl = "", // filled in after upload
                userId = currentUser.uid
            )

            Model.instance.addPost(post, selectedImageUri)
            findNavController().navigateUp()
        }
    }
}
