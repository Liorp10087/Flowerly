package com.example.flowerly.ui

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
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
import com.example.flowerly.model.User
import com.example.flowerly.utils.loadImageFromFirebase
import com.example.flowerly.viewmodel.PostViewModel
import com.example.flowerly.viewmodel.UserViewModel
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {
    private val postViewModel: PostViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()

    private val firebaseModel = FirebaseModel
    private lateinit var adapter: PostAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var emailTextView: TextView
    private lateinit var profileImageView: ImageView
    private lateinit var editUsername: EditText
    private lateinit var saveUsernameButton: Button
    private lateinit var logoutButton: Button
    private lateinit var changeProfileButton: Button

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

        recyclerView = view.findViewById(R.id.user_posts_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = PostAdapter(mutableListOf(), emptyMap(), onDelete = { post ->
            postViewModel.deletePost(post)
        }, onEdit = { post ->
            val action = MainFragmentDirections.actionMainFragmentToEditPostFragment(post)
            findNavController().navigate(action)
        })
        recyclerView.adapter = adapter

        emailTextView = view.findViewById(R.id.profile_name)
        profileImageView = view.findViewById(R.id.profile_image_view)
        editUsername = view.findViewById(R.id.edit_username)
        saveUsernameButton = view.findViewById(R.id.save_username_button)
        logoutButton = view.findViewById(R.id.logout)
        changeProfileButton = view.findViewById(R.id.change_profile_button)


        userViewModel.loadCurrentUser()

        userViewModel.currentUser.observe(viewLifecycleOwner) { currentUser ->
            currentUser?.let {
                user = it
                updateUI(it)

                postViewModel.getUserPosts(it.id).observe(viewLifecycleOwner) { postList ->
                    adapter.updatePosts(postList)
                }
            } ?: run {
                findNavController().navigate(R.id.loginFragment)
            }
        }



        saveUsernameButton.setOnClickListener {
            val newUsername = editUsername.text.toString().trim()
            if (newUsername.isNotEmpty()) {
                lifecycleScope.launch {
                    updateUsername(newUsername)
                }
            }
        }

        logoutButton.setOnClickListener {
            firebaseModel.signOut()
        }

        changeProfileButton.setOnClickListener {
            pickImage.launch("image/*")
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
                loadImageFromFirebase(it.profilePictureUrl, view?.findViewById(R.id.profile_image_view)!!)
                updateUI(it)
            }, {
            })
        }
    }

    private fun updateUI(user: User?) {
        user?.let {
            emailTextView.text = it.email
            editUsername.setText(it.username)
            loadImageFromFirebase(it.profilePictureUrl, view?.findViewById(R.id.profile_image_view)!!)
        } ?: run {
            Log.e("ProfileFragment", "User is null. Cannot update UI.")
        }
    }
}
