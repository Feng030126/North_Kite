package com.rst2g1.northkite.ui.travelling

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rst2g1.northkite.databinding.ItemDestinationBinding
import com.squareup.picasso.Picasso

class CountryAdapter : RecyclerView.Adapter<CountryAdapter.CountryViewHolder>() {

    private var countries = listOf<Country>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryViewHolder {
        val binding = ItemDestinationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CountryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CountryViewHolder, position: Int) {
        val country = countries[position]
        holder.bind(country)
    }

    override fun getItemCount(): Int = countries.size

    fun setCountries(countries: List<Country>) {
        this.countries = countries
        notifyDataSetChanged()
    }

    class CountryViewHolder(private val binding: ItemDestinationBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(country: Country) {
            binding.textView.text = country.name
            Picasso.get().load(country.imageUrl).into(binding.imageView)
        }
    }
}
