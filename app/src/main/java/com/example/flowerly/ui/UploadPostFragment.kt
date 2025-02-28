package com.example.flowerly.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.flowerly.Post
import com.example.flowerly.databinding.FragmentUploadPostBinding
import com.example.flowerly.viewmodel.PostViewModel
import com.example.flowerly.model.User

class UploadPostFragment : Fragment() {
    private lateinit var binding: FragmentUploadPostBinding
    private val postViewModel: PostViewModel by activityViewModels()

    private var imageUri: Uri? = null

    private val imagePickerResult =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            imageUri = uri
            binding.imageView.setImageURI(imageUri)
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUploadPostBinding.inflate(inflater, container, false)

        val hardcodedUser = User(
            id = "mlxtRRPv7p0ZFCtXIaIF",
            username = "Lior",
            profilePictureUrl = "rose1.jpg"
        )

        binding.uploadButton.setOnClickListener {
            val title = binding.titleEditText.text.toString()
            val description = binding.descriptionEditText.text.toString()

            if (imageUri != null) {
                val post = Post(
                    id = System.currentTimeMillis().toString(),
                    title = title,
                    description = description,
                    imagePathUrl = "",
                    user = hardcodedUser
                )

                postViewModel.addPost(post, imageUri!!)

                findNavController().navigateUp()

            }
        }

        binding.imageView.setOnClickListener {
            imagePickerResult.launch("image/*")
        }

        return binding.root
    }
}
