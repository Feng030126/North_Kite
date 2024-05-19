package com.rst2g1.northkite.ui.companydetail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.rst2g1.northkite.databinding.FragmentCompanyDetailBinding
import com.squareup.picasso.Picasso
import com.google.firebase.database.*
import com.rst2g1.northkite.ui.joboffer.JobOffer

class CompanyDetailFragment : Fragment() {

    private var _binding: FragmentCompanyDetailBinding? = null
    private val binding get() = _binding!!
    private val args: CompanyDetailFragmentArgs by navArgs()
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCompanyDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database = FirebaseDatabase.getInstance().getReference("jobOffers").child(args.companyId)

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("CompanyDetailFragment", "Snapshot data: ${snapshot.value}")
                val company = snapshot.getValue(JobOffer::class.java)
                company?.let {
                    binding.companyName.text = it.companyName
                    binding.companyDescription.text = it.description
                    Log.d("CompanyDetailFragment", "Detail Image URL: ${it.detailImage}")
                    if (it.detailImage.isNotEmpty()) {
                        Picasso.get().load(it.detailImage).into(binding.companyImage)
                    } else {
                        Log.d("CompanyDetailFragment", "Detail Image URL is empty")
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle possible errors
            }
        })

        binding.buttonApply.setOnClickListener {
            val action = CompanyDetailFragmentDirections.actionCompanyDetailFragmentToApplyJobFragment(args.companyId)
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
