package com.example.flowerly

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.flowerly.model.FirebaseModel
import com.example.flowerly.model.Post
import com.example.flowerly.model.PostWithUser
import com.example.flowerly.model.User
import com.example.flowerly.utils.loadImageFromFirebase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class PostAdapter(
    private val posts: MutableList<PostWithUser>,
    private var userMap: Map<String, User> = emptyMap(),
    private val onDelete: (Post) -> Unit,
    private val onEdit: (Post) -> Unit
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    class PostViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val profileImageView: ImageView = view.findViewById(R.id.post_profile_image)
        val usernameTextView: TextView = view.findViewById(R.id.post_username)
        val imageView: ImageView = view.findViewById(R.id.post_image)
        val titleText: TextView = view.findViewById(R.id.post_title)
        val descText: TextView = view.findViewById(R.id.post_description)
        val deleteButton: Button = view.findViewById(R.id.delete_post_button)
        val editButton: Button = view.findViewById(R.id.edit_post_button)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val postWithUser = posts[position]

        val post = postWithUser.post
        val user = postWithUser.user

        if (user != null) {
            holder.usernameTextView.text = user.username
            loadImageFromFirebase(user.profilePictureUrl, holder.profileImageView)
        }

        loadImageFromFirebase(post.imagePathUrl, holder.imageView)
        holder.titleText.text = post.title
        holder.descText.text = post.description

//        GlobalScope.launch {
//            val currentUser = FirebaseModel.getCurrentUser()
//
//            if (currentUser != null && post.userId == currentUser.id) {
//                holder.deleteButton.visibility = View.VISIBLE
//                holder.editButton.visibility = View.VISIBLE
//            } else {
//                // Hide buttons if the post doesn't belong to the current user
//                holder.deleteButton.visibility = View.GONE
//                holder.editButton.visibility = View.GONE
//            }
//        }

        holder.deleteButton.setOnClickListener {
            onDelete(post)
        }

        holder.editButton.setOnClickListener {
            onEdit(post)
        }
    }


    override fun getItemCount(): Int = posts.size

    fun updatePosts(newPosts: List<PostWithUser>) {
        posts.clear()
        posts.addAll(newPosts)
        notifyDataSetChanged()
    }

    fun updateUsers(newUserMap: Map<String, User>) {
        this.userMap = newUserMap
        notifyDataSetChanged()
    }

}
