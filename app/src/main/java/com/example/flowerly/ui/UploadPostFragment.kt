package com.example.flowerly.ui

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.flowerly.OpenAIClient
import com.example.flowerly.databinding.FragmentUploadPostBinding
import com.example.flowerly.model.Post
import com.example.flowerly.model.Model
import com.example.flowerly.viewmodel.PostViewModel

class UploadPostFragment : Fragment() {
    private lateinit var binding: FragmentUploadPostBinding
    private var imageUri: Uri? = null
    private val postViewModel: PostViewModel by viewModels()


    private val imagePickerResult =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            imageUri = uri
            binding.imageView.setImageURI(imageUri)
        }

    private val generateDescriptionButton by lazy {
        binding.generateDescriptionButton
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUploadPostBinding.inflate(inflater, container, false)

        generateDescriptionButton.setOnClickListener {
            val title = binding.titleEditText.text.toString().trim()

            if (title.isNotEmpty()) {
                generateDescription(title)
            }
        }

        binding.uploadButton.setOnClickListener {
            uploadPost()
        }

        binding.imageView.setOnClickListener {
            imagePickerResult.launch("image/*")
        }

        return binding.root
    }

    private fun generateDescription(title: String) {
        OpenAIClient.generateDescription(title) { generatedDescription ->
            if (!generatedDescription.isNullOrEmpty()) {
                binding.descriptionEditText.setText(generatedDescription)
            } else {
                Log.e(
                    "UploadPostFragment",
                    "Failed to generate description or received empty description"
                )
                binding.descriptionEditText.setText("Failed to generate description")
            }
        }
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
                imagePathUrl = "",
                userId = currentUser.uid
            )

            postViewModel.addPost(post, selectedImageUri)
            findNavController().navigateUp()
        }
    }
}
