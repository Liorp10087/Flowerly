package com.example.flowerly.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
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
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {
    private val postViewModel: PostViewModel by viewModels()
    private val firebaseModel = FirebaseModel

    private lateinit var adapter: PostAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var userNameTextView: TextView
    private lateinit var editUsername: EditText
    private lateinit var saveUsernameButton: Button
    private lateinit var logoutButton: Button
    private var user: User? = null

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

        adapter = PostAdapter(mutableListOf(),emptyMap(), onDelete = { post ->
            postViewModel.deletePost(post)
        }, onEdit = { post ->
            val action = MainFragmentDirections.actionMainFragmentToEditPostFragment(post)
            findNavController().navigate(action)
        })
        recyclerView.adapter = adapter

        userNameTextView = view.findViewById(R.id.profile_name)
        editUsername = view.findViewById(R.id.edit_username)
        saveUsernameButton = view.findViewById(R.id.save_username_button)
        logoutButton = view.findViewById(R.id.logout)

        Firebase.auth.currentUser?.uid?.let {
            FirebaseModel.getUserById(it) { user ->
                this.user = user
                if (user != null) {
                    updateUI(user)
                } else {
                    findNavController().navigate(R.id.loginFragment)
                }
            }
        }

        saveUsernameButton.setOnClickListener {
            val newUsername = editUsername.text.toString().trim()
            if (newUsername.isNotEmpty()) {
                lifecycleScope.launch {
                    updateUsername(newUsername)
                    editUsername.setText("")
                }
            }
        }

        logoutButton.setOnClickListener {
            firebaseModel.signOut()
        }
    }

    private fun updateUsername(newUsername: String) {
        user?.let {
            Model.instance.updateUserUsername(it.id, newUsername, requireContext()) {
                updateUI(it)
            }
        }
    }

    private fun updateUI(user: User) {
        userNameTextView.text = user.username
        editUsername.setText(user.username)

        postViewModel.getUserPosts(user.id).observe(viewLifecycleOwner) { postList ->
            adapter = PostAdapter(mutableListOf(),emptyMap(), onDelete = { post ->
                postViewModel.deletePost(post)
            }, onEdit = { post ->
                val action = MainFragmentDirections.actionMainFragmentToEditPostFragment(post)
                findNavController().navigate(action)
            })
            recyclerView.adapter = adapter
        }
    }
}
