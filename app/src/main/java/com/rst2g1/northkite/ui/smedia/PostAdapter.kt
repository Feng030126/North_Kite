package com.rst2g1.northkite.ui.smedia

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rst2g1.northkite.R
import com.rst2g1.northkite.databinding.ItemPostBinding
import com.squareup.picasso.Picasso

class PostAdapter(
    private val posts: List<Post>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    interface OnItemClickListener {
        fun onLikeClick(post: Post)
        fun onCommentClick(post: Post)
        fun onShareClick(post: Post)
        fun onBookmarkClick(post: Post)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.bind(post, listener)
    }

    override fun getItemCount() = posts.size

    inner class PostViewHolder(private val binding: ItemPostBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(post: Post, listener: PostAdapter.OnItemClickListener) {
            binding.textUsername.text = post.username
            binding.textlike.text = String.format("%d likes", post.likes)
            binding.textDescription.text = post.description

            Picasso.get().load(post.profilePicUrl).into(binding.imageProfilePic)
            Picasso.get().load(post.postImageUrl).into(binding.imagePost)

            val currentUserID = "test_user_id" // Replace with method to get current user ID
            if (post.likedBy.contains(currentUserID)) {
                binding.imageLike.setImageResource(R.drawable.heart_red) // Set red heart if liked
            } else {
                binding.imageLike.setImageResource(R.drawable.heart_grey) // Set grey heart if not liked
            }

            binding.imageLike.setOnClickListener { listener.onLikeClick(post) }
            binding.imageComment.setOnClickListener { listener.onCommentClick(post) }
            binding.imageShare.setOnClickListener { listener.onShareClick(post) }
            binding.imageBookmark.setOnClickListener { listener.onBookmarkClick(post) }
        }
    }
}
