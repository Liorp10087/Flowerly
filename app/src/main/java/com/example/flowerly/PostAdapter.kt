package com.example.flowerly

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.flowerly.model.Post
import com.example.flowerly.model.User
import com.example.flowerly.utils.loadImageFromFirebase

class PostAdapter(
    private val posts: MutableList<Post>,
    private var userMap: Map<String, User> = emptyMap(),
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

        val user = userMap[post.userId] ?: User(
            id = post.userId,
            username = "Unknown User",
            profilePictureUrl = "default_profile_image.png"
        )

        holder.usernameTextView.text = user.username
        loadImageFromFirebase(user.profilePictureUrl, holder.profileImageView)

        loadImageFromFirebase(post.imagePathUrl, holder.imageView)

        holder.titleText.text = post.title
        holder.descText.text = post.description

        holder.deleteButton.setOnClickListener {
            onDelete(post)
        }
    }

    override fun getItemCount(): Int = posts.size

    fun updatePosts(newPosts: List<Post>) {
        posts.clear()
        posts.addAll(newPosts)
        notifyDataSetChanged()
    }

    fun updateUsers(newUserMap: Map<String, User>) {
        this.userMap = newUserMap
        notifyDataSetChanged()
    }

    fun removePost(post: Post) {
        val index = posts.indexOf(post)
        if (index != -1) {
            posts.removeAt(index)
            notifyItemRemoved(index)
        }
    }
}
