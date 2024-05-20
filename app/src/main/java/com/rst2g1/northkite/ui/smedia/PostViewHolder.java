package com.rst2g1.northkite.ui.smedia;

import androidx.recyclerview.widget.RecyclerView;

import com.rst2g1.northkite.databinding.ItemPostBinding;
import com.squareup.picasso.Picasso;

public class PostViewHolder extends RecyclerView.ViewHolder {

    private ItemPostBinding binding;

    public PostViewHolder(ItemPostBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(final Post post, final PostAdapter.OnItemClickListener listener) {
        binding.textUsername.setText(post.getUsername());
        binding.textlike.setText(String.format("%d likes", post.getLikes()));
        binding.textDescription.setText(post.getDescription());
        Picasso.get().load(post.getProfilePicUrl()).into(binding.imageProfilePic);
        Picasso.get().load(post.getPostImageUrl()).into(binding.imagePost);

        binding.imageLike.setOnClickListener(v -> listener.onItemClick(post));
        binding.imageComment.setOnClickListener(v -> listener.onItemClick(post));
        binding.imageShare.setOnClickListener(v -> listener.onItemClick(post));
        binding.imageBookmark.setOnClickListener(v -> listener.onItemClick(post));
    }
}
