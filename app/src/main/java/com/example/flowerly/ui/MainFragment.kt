package com.example.flowerly.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.flowerly.Post
import com.example.flowerly.PostAdapter
import com.example.flowerly.R
import com.example.flowerly.viewmodel.PostViewModel

class MainFragment : Fragment() {
    private lateinit var viewModel: PostViewModel
    private lateinit var adapter: PostAdapter
    private lateinit var recyclerView: RecyclerView

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

        viewModel = ViewModelProvider(this).get(PostViewModel::class.java)
        viewModel.posts.observe(viewLifecycleOwner) { postList ->
            adapter = PostAdapter(postList.toMutableList(),
                onDelete = { post -> viewModel.deletePost(post) },

            )
            recyclerView.adapter = adapter
        }
    }
}
