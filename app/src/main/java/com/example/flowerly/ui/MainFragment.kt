package com.example.flowerly.ui

import androidx.navigation.fragment.findNavController
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.flowerly.PostAdapter
import com.example.flowerly.R
import com.example.flowerly.viewmodel.PostViewModel
import com.example.flowerly.viewmodel.UserViewModel

class MainFragment : Fragment() {
    private lateinit var adapter: PostAdapter
    private lateinit var recyclerView: RecyclerView
    private val postViewModel: PostViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = PostAdapter(mutableListOf(), onDelete = { post ->
            postViewModel.deletePost(post)
        }, onEdit = { post ->
            val action = MainFragmentDirections.actionMainFragmentToEditPostFragment(post)
            findNavController().navigate(action)
        })
        recyclerView.adapter = adapter

        userViewModel.currentUser.observe(viewLifecycleOwner) { currentUser ->
            currentUser?.let {
                adapter.updateCurrentUser(it)
            }
        }

        postViewModel.posts.observe(viewLifecycleOwner) { postList ->
            adapter.updatePosts(postList)
        }
    }
}
