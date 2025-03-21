package com.example.flowerly.ui

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.flowerly.OpenAIClient
import com.example.flowerly.databinding.FragmentUploadPostBinding
import com.example.flowerly.viewmodel.AuthViewModel
import com.example.flowerly.viewmodel.PostViewModel
import com.example.flowerly.model.Post

class UploadPostFragment : Fragment() {
    private lateinit var binding: FragmentUploadPostBinding
    private val postViewModel: PostViewModel by activityViewModels()
    private val authViewModel: AuthViewModel by activityViewModels()

    private var imageUri: Uri? = null

    private val imagePickerResult =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            imageUri = uri
            binding.imageView.setImageURI(imageUri)
        }

    private val generateDescriptionButton by lazy {
        binding.generateDescriptionButton // Ensure you have a button in the layout for this action
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUploadPostBinding.inflate(inflater, container, false)

        // Handle the Generate Description button click
        generateDescriptionButton.setOnClickListener {
            val title = binding.titleEditText.text.toString().trim()

            if (title.isNotEmpty()) {
                // Call the API to generate the description for the flower
                generateDescription(title)
            }
        }

        // Handle the Upload button click
        binding.uploadButton.setOnClickListener {
            val title = binding.titleEditText.text.toString().trim()
            val description = binding.descriptionEditText.text.toString().trim()
            val currentUser = authViewModel.user.value

            val selectedImageUri = imageUri

            if (title.isNotEmpty() && selectedImageUri != null && currentUser != null) {
                val post = Post(
                    id = System.currentTimeMillis().toString(),
                    title = title,
                    description = description,  // This description is what the user filled or generated
                    imagePathUrl = "",
                    userId = currentUser.id
                )

                postViewModel.addPost(post, selectedImageUri)
                findNavController().navigateUp()
            }
        }

        // Handle image selection
        binding.imageView.setOnClickListener {
            imagePickerResult.launch("image/*")
        }

        return binding.root
    }

    // Function to call the API and generate description for the post
    private fun generateDescription(title: String) {
        // Set the prompt for generating the description
        val prompt = "Give a detailed description of the flower: $title"

        // Call your API here
        OpenAIClient.generateDescription(prompt) { generatedDescription ->
            // Ensure generatedDescription is a String before checking if it is not empty
            if (!generatedDescription.isNullOrEmpty()) {
                // Update the description field with the generated text
                binding.descriptionEditText.setText(generatedDescription)
            } else {
                Log.e("UploadPostFragment", "Failed to generate description or received empty description")
                binding.descriptionEditText.setText("Failed to generate description")
            }
        }
    }
}
