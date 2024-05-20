package com.rst2g1.northkite.ui.smedia

import androidx.recyclerview.widget.RecyclerView
import com.rst2g1.northkite.databinding.ItemPostBinding
import com.squareup.picasso.Picasso

class PostViewHolder(private val binding: ItemPostBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(post: Post, listener: PostAdapter.OnItemClickListener) {
        binding.textUsername.text = post.username
        binding.textlike.text = String.format("%d likes", post.likes)
        binding.textDescription.text = post.description
        Picasso.get().load(post.profilePicUrl).into(binding.imageProfilePic)
        Picasso.get().load(post.postImageUrl).into(binding.imagePost)

        binding.imageLike.setOnClickListener { listener.onLikeClick(post) }
        binding.imageComment.setOnClickListener { listener.onCommentClick(post) }
        binding.imageShare.setOnClickListener { listener.onShareClick(post) }
        binding.imageBookmark.setOnClickListener { listener.onBookmarkClick(post) }
    }
}
