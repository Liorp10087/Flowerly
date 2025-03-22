package com.example.flowerly.ui

import android.net.Uri
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
import com.example.flowerly.model.User
import com.example.flowerly.utils.loadImageFromFirebase
import com.example.flowerly.viewmodel.PostViewModel
import com.example.flowerly.viewmodel.UserViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private val postViewModel: PostViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PostAdapter

    private lateinit var emailTextView: TextView
    private lateinit var profileImageView: ImageView
    private lateinit var editProfileImageButton: ImageView
    private lateinit var editUsername: TextInputEditText
    private lateinit var editUsernameLayout: TextInputLayout
    private lateinit var saveUsernameButton: Button
    private lateinit var logoutButton: ImageView

    private var user: User? = null

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            updateProfilePicture(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        setupRecyclerView()
        setupListeners()

        postViewModel.refreshPosts()
        userViewModel.loadCurrentUser()

        userViewModel.currentUser.observe(viewLifecycleOwner) { currentUser ->
            if (currentUser == null) {
                findNavController().navigate(R.id.loginFragment)
            } else {
                user = currentUser
                updateUI(currentUser)
                adapter.updateCurrentUser(currentUser)

                postViewModel.getUserPosts(currentUser.id).observe(viewLifecycleOwner) { postList ->
                    adapter.updatePosts(postList)
                }
            }
        }
    }

    private fun initViews(view: View) {
        recyclerView = view.findViewById(R.id.user_posts_recycler_view)
        emailTextView = view.findViewById(R.id.profile_name)
        profileImageView = view.findViewById(R.id.profile_image_view)
        editProfileImageButton = view.findViewById(R.id.edit_profile_image_button)
        editUsername = view.findViewById(R.id.edit_username)
        editUsernameLayout = view.findViewById(R.id.edit_username_layout)
        saveUsernameButton = view.findViewById(R.id.save_username_button)
        logoutButton = view.findViewById(R.id.logout_icon)
    }

    private fun setupRecyclerView() {
        adapter = PostAdapter(mutableListOf(), onDelete = { post ->
            postViewModel.deletePost(post)
        }, onEdit = { post ->
            val action = ProfileFragmentDirections.actionProfileFragmentToEditPostFragment(post)
            findNavController().navigate(action)
        })
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    private fun setupListeners() {
        logoutButton.setOnClickListener {
            userViewModel.signOut()
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
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Update Profile Picture")
                .setMessage("Choose a new profile picture from your gallery.")
                .setPositiveButton("Choose") { _, _ ->
                    pickImage.launch("image/*")
                }
                .setNegativeButton("Cancel", null)
                .show()
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
        user?.let {
            userViewModel.updateUserUsername(it.id, newUsername, requireContext()) {
                it.username = newUsername
                userViewModel.updateCurrentUser(it)
                updateUI(it)
            }
        }
    }

    private fun updateProfilePicture(imageUri: Uri) {
        user?.let {
            userViewModel.updateProfilePicture(it, imageUri, {
                loadImageFromFirebase(imageUri.toString(), profileImageView)
            }, {
                Toast.makeText(requireContext(), "Failed to update profile picture", Toast.LENGTH_SHORT).show()
            })
        }
    }

    private fun updateUI(user: User) {
        emailTextView.text = user.email
        editUsername.setText(user.username)
        loadImageFromFirebase(user.profilePictureUrl, profileImageView)
    }
}
