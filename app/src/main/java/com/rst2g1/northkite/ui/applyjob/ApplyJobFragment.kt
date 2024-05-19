package com.rst2g1.northkite.ui.applyjob

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.rst2g1.northkite.databinding.FragmentApplyJobBinding
import android.widget.Toast
import com.rst2g1.northkite.ui.joboffer.JobApplication

class ApplyJobFragment : Fragment() {

    private var _binding: FragmentApplyJobBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: DatabaseReference
    private var companyId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentApplyJobBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve the companyId from the bundle
        companyId = arguments?.getString("companyId")

        if (companyId != null) {
            database = FirebaseDatabase.getInstance().getReference("applications").child(companyId!!)
        }

        binding.buttonSubmit.setOnClickListener {
            val name = binding.editTextName.text.toString().trim()
            val phone = binding.editTextPhone.text.toString().trim()

            if (name.isEmpty() || phone.isEmpty()) {
                Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            saveApplication(name, phone)
        }
    }

    private fun saveApplication(name: String, phone: String) {
        val applicationId = database.push().key
        if (applicationId != null) {
            val application = JobApplication(name, phone)
            database.child(applicationId).setValue(application)
                .addOnCompleteListener {
                    Toast.makeText(context, "Application submitted", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Failed to submit application", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
