package com.example.flowerly.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.flowerly.PostAdapter
import com.example.flowerly.R
import com.example.flowerly.model.FirebaseModel
import com.example.flowerly.model.Model
import com.example.flowerly.model.User
import com.example.flowerly.viewmodel.PostViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private val postViewModel: PostViewModel by viewModels()
    private val firebaseModel = FirebaseModel

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PostAdapter

    private lateinit var userNameTextView: TextView
    private lateinit var editUsername: TextInputEditText
    private lateinit var editUsernameLayout: TextInputLayout
    private lateinit var saveUsernameButton: Button
    private lateinit var logoutButton: Button
    private lateinit var editProfileImageButton: ImageView

    private var user: User? = null

    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                Toast.makeText(requireContext(), "Image selected: $uri", Toast.LENGTH_SHORT).show()
                // TODO: upload image to Firestore/Storage and update profile picture
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_profile, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setupListeners()
        setupRecyclerView()

        Firebase.auth.currentUser?.uid?.let { uid ->
            FirebaseModel.getUserById(uid) { fetchedUser ->
                if (fetchedUser == null) {
                    findNavController().navigate(R.id.loginFragment)
                } else {
                    user = fetchedUser
                    updateUI(fetchedUser)
                }
            }
        }
    }

    private fun initViews(view: View) {
        recyclerView = view.findViewById(R.id.user_posts_recycler_view)
        userNameTextView = view.findViewById(R.id.profile_name)
        editUsername = view.findViewById(R.id.edit_username)
        editUsernameLayout = view.findViewById(R.id.edit_username_layout)
        saveUsernameButton = view.findViewById(R.id.save_username_button)
        logoutButton = view.findViewById(R.id.logout)
        editProfileImageButton = view.findViewById(R.id.edit_profile_image_button)
    }

    private fun setupRecyclerView() {
        adapter = PostAdapter(mutableListOf()) { post ->
            postViewModel.deletePost(post)
        }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    private fun setupListeners() {
        logoutButton.setOnClickListener {
            firebaseModel.signOut()
        }

        editUsernameLayout.setEndIconOnClickListener {
            editUsername.isEnabled = true
            editUsername.requestFocus()
            saveUsernameButton.visibility = View.VISIBLE
        }

        editUsername.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                trySaveUsername()
                true
            } else false
        }

        saveUsernameButton.setOnClickListener {
            trySaveUsername()
        }

        editProfileImageButton.setOnClickListener {
            showImagePickerDialog()
        }
    }

    private fun trySaveUsername() {
        val newUsername = editUsername.text.toString().trim()
        if (newUsername.isNotEmpty()) {
            lifecycleScope.launch {
                updateUsername(newUsername)
                editUsername.isEnabled = false
                saveUsernameButton.visibility = View.GONE
            }
        }
    }

    private fun updateUsername(newUsername: String) {
        user?.let { currentUser ->
            Model.instance.updateUserUsername(currentUser.id, newUsername, requireContext()) {
                FirebaseModel.getUserById(currentUser.id) { updatedUser ->
                    updatedUser?.let {
                        user = it
                        updateUI(it)
                    }
                }
            }
        }
    }

    private fun updateUI(user: User) {
        userNameTextView.text = user.username
        editUsername.setText(user.username)

        postViewModel.getUserPosts(user.id).observe(viewLifecycleOwner) { posts ->
            adapter = PostAdapter(posts.toMutableList()) { post ->
                postViewModel.deletePost(post)
            }
            recyclerView.adapter = adapter
        }
    }

    private fun showImagePickerDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Update Profile Picture")
            .setMessage("Choose a new profile picture from your gallery.")
            .setPositiveButton("Choose") { _, _ ->
                imagePickerLauncher.launch("image/*")
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
