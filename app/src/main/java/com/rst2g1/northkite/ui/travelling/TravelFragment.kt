package com.rst2g1.northkite.ui.travelling

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.rst2g1.northkite.databinding.FragmentTravelBinding
import androidx.appcompat.widget.SearchView // Import SearchView from the correct package
import java.util.*

class TravelFragment : Fragment() {

    private var _binding: FragmentTravelBinding? = null
    private val binding get() = _binding!!
    private lateinit var travelViewModel: TravelViewModel
    private lateinit var database: DatabaseReference
    private lateinit var adapter: CountryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTravelBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        travelViewModel = ViewModelProvider(this).get(TravelViewModel::class.java)
        database = FirebaseDatabase.getInstance().reference.child("countries")

        setupRecyclerView()

        // Fetch data from Firebase
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val countries = mutableListOf<Country>()
                for (countrySnapshot in snapshot.children) {
                    val country = countrySnapshot.getValue(Country::class.java)
                    if (country != null) {
                        countries.add(country)
                    }
                }
                adapter.setCountries(countries)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle possible errors
            }
        })

        // Setup search functionality
        binding.searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    searchCountriesByName(query)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Optionally handle text changes
                return false
            }
        })

        // Add a listener for the DatePicker
        binding.datePicker.init(binding.datePicker.year, binding.datePicker.month, binding.datePicker.dayOfMonth,
            DatePicker.OnDateChangedListener { view, year, monthOfYear, dayOfMonth ->
                val date = "$year-${monthOfYear + 1}-$dayOfMonth"
                binding.searchBar.setQuery("", false) // Clear the search field
                searchCountriesByDate(date)
            })
    }

    private fun setupRecyclerView() {
        adapter = CountryAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter
    }

    private fun searchCountriesByDate(date: String) {
        database.orderByChild("date").equalTo(date).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val countries = mutableListOf<Country>()
                for (countrySnapshot in snapshot.children) {
                    val country = countrySnapshot.getValue(Country::class.java)
                    if (country != null) {
                        countries.add(country)
                    }
                }
                adapter.setCountries(countries)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle possible errors
            }
        })
    }

    private fun searchCountriesByName(name: String) {
        val query = name.toLowerCase(Locale.getDefault())
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val countries = mutableListOf<Country>()
                for (countrySnapshot in snapshot.children) {
                    val country = countrySnapshot.getValue(Country::class.java)
                    if (country != null && country.name.toLowerCase(Locale.getDefault()).contains(query)) {
                        countries.add(country)
                    }
                }
                adapter.setCountries(countries)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle possible errors
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
