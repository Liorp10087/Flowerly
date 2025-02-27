package com.example.flowerly.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.flowerly.PostAdapter
import com.example.flowerly.R
import com.example.flowerly.viewmodel.ProfileViewModel

class ProfileFragment : Fragment() {
    private lateinit var profileImageView: ImageView
    private lateinit var usernameTextView: TextView
    private lateinit var editUsernameEditText: EditText
    private lateinit var saveUsernameButton: Button
    private lateinit var userPostsRecyclerView: RecyclerView

    private lateinit var viewModel: ProfileViewModel
    private var selectedImageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileImageView = view.findViewById(R.id.profile_image)
        usernameTextView = view.findViewById(R.id.profile_name)
        editUsernameEditText = view.findViewById(R.id.edit_username)
        saveUsernameButton = view.findViewById(R.id.save_username_button)
        userPostsRecyclerView = view.findViewById(R.id.user_posts_recycler_view)

        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)

        // Observe username updates
        viewModel.username.observe(viewLifecycleOwner) { username ->
            usernameTextView.text = username
        }

        // Observe profile image updates
        viewModel.profileImage.observe(viewLifecycleOwner) { imageUri ->
            if (imageUri.isNotEmpty()) {
                profileImageView.setImageURI(Uri.parse(imageUri))
            }
        }

        // Observe user posts
        userPostsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        viewModel.userPosts.observe(viewLifecycleOwner) { posts ->
            userPostsRecyclerView.adapter = PostAdapter(posts) // 🔥 REUSING PostAdapter!
        }

        profileImageView.setOnClickListener {
            pickImageFromGallery()
        }

        saveUsernameButton.setOnClickListener {
            val newUsername = editUsernameEditText.text.toString()
            if (newUsername.isNotEmpty()) {
                viewModel.updateUsername(newUsername)
            }
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            selectedImageUri?.let {
                profileImageView.setImageURI(it)
                viewModel.updateProfileImage(it.toString())
            }
        }
    }

    companion object {
        private const val REQUEST_IMAGE_PICK = 100
    }
}
