package com.example.flowerly

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PostAdapter(private val posts: List<Post>) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    class PostViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.post_image)
        val nameText: TextView = view.findViewById(R.id.post_name)
        val descText: TextView = view.findViewById(R.id.post_description)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.imageView.setImageResource(post.imageResId)
        holder.nameText.text = post.name
        holder.descText.text = post.description
    }

    override fun getItemCount(): Int = posts.size
}
