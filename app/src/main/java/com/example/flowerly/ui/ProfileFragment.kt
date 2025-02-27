package com.example.flowerly.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.flowerly.PostAdapter
import com.example.flowerly.R
import com.example.flowerly.viewmodel.PostViewModel
import com.example.flowerly.viewmodel.ProfileViewModel

class ProfileFragment : Fragment() {
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var postViewModel: PostViewModel
    private lateinit var adapter: PostAdapter
    private lateinit var recyclerView: RecyclerView

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

        profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        postViewModel = ViewModelProvider(this).get(PostViewModel::class.java)

        profileViewModel.username.observe(viewLifecycleOwner, Observer { username ->
            if (!username.isNullOrEmpty()) {
                postViewModel.getUserPosts(username).observe(viewLifecycleOwner, Observer { postList ->
                    adapter = PostAdapter(postList.toMutableList(),
                        onDelete = { post -> postViewModel.deletePost(post) }
                    )
                    recyclerView.adapter = adapter
                })
            }
        })
    }
}
