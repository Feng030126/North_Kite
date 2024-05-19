package com.rst2g1.northkite.ui.joboffer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.rst2g1.northkite.R
import com.rst2g1.northkite.databinding.ItemJobOfferBinding
import com.squareup.picasso.Picasso

class JobOfferAdapter : RecyclerView.Adapter<JobOfferAdapter.JobOfferViewHolder>() {

    private var jobOffers = listOf<Pair<String, JobOffer>>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobOfferViewHolder {
        val binding = ItemJobOfferBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return JobOfferViewHolder(binding)
    }

    override fun onBindViewHolder(holder: JobOfferViewHolder, position: Int) {
        val (id, jobOffer) = jobOffers[position]
        holder.bind(id, jobOffer)
    }

    override fun getItemCount(): Int = jobOffers.size

    fun setJobOffers(jobOffers: List<Pair<String, JobOffer>>) {
        this.jobOffers = jobOffers
        notifyDataSetChanged()
    }

    class JobOfferViewHolder(private val binding: ItemJobOfferBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(id: String, jobOffer: JobOffer) {
            binding.textViewCompanyName.text = jobOffer.companyName
            Picasso.get().load(jobOffer.imageResId).into(binding.imageViewCompanyLogo)
            binding.root.setOnClickListener {
                val bundle = Bundle().apply {
                    putString("companyId", id)
                }
                it.findNavController().navigate(R.id.action_jobOfferFragment_to_companyDetailFragment, bundle)
            }
        }
    }
}
