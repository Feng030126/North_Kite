package com.rst2g1.northkite.ui.joboffer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.rst2g1.northkite.databinding.FragmentJobOfferBinding

class JobOfferFragment : Fragment() {

    private var _binding: FragmentJobOfferBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: JobOfferViewModel
    private lateinit var database: DatabaseReference
    private lateinit var adapter: JobOfferAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentJobOfferBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(JobOfferViewModel::class.java)
        database = FirebaseDatabase.getInstance().getReference("jobOffers")

        setupRecyclerView()

        // Fetch data from Firebase
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val jobOffers = mutableListOf<Pair<String, JobOffer>>()
                for (jobOfferSnapshot in snapshot.children) {
                    val jobOffer = jobOfferSnapshot.getValue(JobOffer::class.java)
                    val id = jobOfferSnapshot.key ?: ""
                    if (jobOffer != null) {
                        jobOffers.add(id to jobOffer)
                    }
                }
                adapter.setJobOffers(jobOffers)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle possible errors
            }
        })
    }

    private fun setupRecyclerView() {
        adapter = JobOfferAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
