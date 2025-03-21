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
import com.example.flowerly.model.Model
import com.example.flowerly.viewmodel.PostViewModel

class MainFragment : Fragment() {
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

        adapter = PostAdapter(mutableListOf(), emptyMap(), onDelete = { post -> Model.instance.deletePost(post) })
        recyclerView.adapter = adapter

        Model.instance.refreshPosts()

        Model.instance.posts.observe(viewLifecycleOwner) { postList ->
            adapter.updatePosts(postList)
        }

        Model.instance.userDetails.observe(viewLifecycleOwner) { userMap ->
            adapter.updateUsers(userMap)
        }
    }
}
