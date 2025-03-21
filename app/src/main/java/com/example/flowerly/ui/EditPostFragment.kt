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
import androidx.navigation.fragment.navArgs
import androidx.navigation.fragment.findNavController
import com.example.flowerly.databinding.FragmentEditPostBinding
import com.example.flowerly.model.Post
import com.example.flowerly.utils.loadImageFromFirebase
import com.example.flowerly.viewmodel.PostViewModel

class EditPostFragment : Fragment() {
    private lateinit var binding: FragmentEditPostBinding
    private val args: EditPostFragmentArgs by navArgs()
    private val postViewModel: PostViewModel by viewModels()
    private var imageUri: Uri? = null

    private val imagePickerResult =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            imageUri = uri
            binding.editImageView.setImageURI(uri)
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditPostBinding.inflate(inflater, container, false)

        val post = args.post
        binding.editTitleEditText.setText(post.title)
        binding.editDescriptionEditText.setText(post.description)

        loadImageFromFirebase(post.imagePathUrl, binding.editImageView)

        binding.editImageView.setOnClickListener {
            imagePickerResult.launch("image/*")
        }

        binding.editSaveButton.setOnClickListener {
            savePost(post)
        }

        return binding.root
    }

    private fun savePost(post: Post) {
        val updatedTitle = binding.editTitleEditText.text.toString().trim()
        val updatedDescription = binding.editDescriptionEditText.text.toString().trim()

        if (updatedTitle.isNotEmpty() && updatedDescription.isNotEmpty()) {
            val updatedPost = post.copy(
                title = updatedTitle,
                description = updatedDescription,
                imagePathUrl = imageUri?.toString() ?: post.imagePathUrl
            )

            postViewModel.updatePost(updatedPost, imageUri, {
                findNavController().navigateUp()
            }, {
                Log.e("EditPostFragment", "Failed to update post")
            })
        } else {
            Log.e("EditPostFragment", "Title or description cannot be empty")
        }
    }
}
