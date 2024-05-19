package com.rst2g1.northkite.ui.travelling

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rst2g1.northkite.databinding.ItemDestinationBinding

class DestinationAdapter(private val destinations: List<Destination>) :
    RecyclerView.Adapter<DestinationAdapter.DestinationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DestinationViewHolder {
        val binding = ItemDestinationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DestinationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DestinationViewHolder, position: Int) {
        val destination = destinations[position]
        holder.binding.imageView.setImageResource(destination.imageResId)
        holder.binding.textView.text = destination.name
    }

    override fun getItemCount(): Int = destinations.size

    class DestinationViewHolder(val binding: ItemDestinationBinding) : RecyclerView.ViewHolder(binding.root)
}
