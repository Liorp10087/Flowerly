package com.example.flowerly.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.flowerly.PostAdapter
import com.example.flowerly.R
import com.example.flowerly.dao.AppDatabase
import com.example.flowerly.viewmodel.AuthViewModel
import com.example.flowerly.viewmodel.AuthViewModelFactory
import com.example.flowerly.viewmodel.PostViewModel

class ProfileFragment : Fragment() {
    private val authViewModel: AuthViewModel by viewModels {
        AuthViewModelFactory(AppDatabase.getDatabase(requireContext()).userDao())
    }
    private val postViewModel: PostViewModel by viewModels()
    private lateinit var adapter: PostAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var userNameTextView: TextView
    private lateinit var logoutButton: Button

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

        adapter = PostAdapter(mutableListOf()) { post -> postViewModel.deletePost(post) }
        recyclerView.adapter = adapter

        userNameTextView = view.findViewById(R.id.profile_name)
        logoutButton = view.findViewById(R.id.logout)

        authViewModel.user.value?.let { user ->
            updateUI(user)
        }

        authViewModel.user.observe(viewLifecycleOwner) { user ->
            if (user == null) {
                findNavController().navigate(R.id.loginFragment)
            } else {
                updateUI(user)
            }
        }

        logoutButton.setOnClickListener {
            authViewModel.logout()
        }
    }

    private fun updateUI(user: com.example.flowerly.model.User) {
        userNameTextView.text = user.username

        postViewModel.getUserPosts(user.id).observe(viewLifecycleOwner) { postList ->
            adapter = PostAdapter(postList.toMutableList()) { post ->
                postViewModel.deletePost(post)
            }
            recyclerView.adapter = adapter
        }
    }
}
