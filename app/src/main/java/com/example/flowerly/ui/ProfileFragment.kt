package com.example.flowerly.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.flowerly.PostAdapter
import com.example.flowerly.R
import com.example.flowerly.viewmodel.PostViewModel

class ProfileFragment : Fragment() {
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

        adapter = PostAdapter(mutableListOf()) { post -> postViewModel.deletePost(post) }
        recyclerView.adapter = adapter

        postViewModel = ViewModelProvider(this).get(PostViewModel::class.java)

        val hardcodedUserId = "mlxtRRPv7p0ZFCtXIaIF"

        postViewModel.getUserPosts(hardcodedUserId).observe(viewLifecycleOwner) { postList ->
            adapter.updatePosts(postList)
        }
    }
}
