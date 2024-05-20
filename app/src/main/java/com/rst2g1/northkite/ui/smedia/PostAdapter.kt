package com.rst2g1.northkite.ui.smedia

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rst2g1.northkite.databinding.ItemPostBinding

class PostAdapter(
    private val posts: List<Post>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<PostViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.bind(post, listener)
    }

    override fun getItemCount(): Int = posts.size

    interface OnItemClickListener {
        fun onLikeClick(post: Post)
        fun onCommentClick(post: Post)
        fun onShareClick(post: Post)
        fun onBookmarkClick(post: Post)
    }
}
