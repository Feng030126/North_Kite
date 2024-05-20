package com.rst2g1.northkite.ui.smedia

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rst2g1.northkite.databinding.ItemPostBinding

class PostAdapter(
    private val posts: List<Post>,
    private val onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<PostViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemPostBinding.inflate(layoutInflater, parent, false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.bind(post, onItemClickListener)
    }

    override fun getItemCount(): Int = posts.size

    interface OnItemClickListener {
        fun onItemClick(post: Post)
    }
}
