package com.example.flowerly

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PostAdapter(
    private val posts: MutableList<Post>,
    private val onDelete: (Post) -> Unit
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    class PostViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val profileImageView: ImageView = view.findViewById(R.id.post_profile_image)
        val usernameTextView: TextView = view.findViewById(R.id.post_username)
        val imageView: ImageView = view.findViewById(R.id.post_image)
        val titleText: TextView = view.findViewById(R.id.post_title)
        val descText: TextView = view.findViewById(R.id.post_description)
        val deleteButton: Button = view.findViewById(R.id.delete_post_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.profileImageView.setImageResource(post.userProfileImageResId)
        holder.usernameTextView.text = post.username
        holder.imageView.setImageResource(post.imageResId)
        holder.titleText.text = post.title
        holder.descText.text = post.description

        holder.deleteButton.setOnClickListener {
            onDelete(post)
            posts.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    override fun getItemCount(): Int = posts.size
}
