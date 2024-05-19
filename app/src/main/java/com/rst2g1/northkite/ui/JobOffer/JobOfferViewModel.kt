package com.rst2g1.northkite.ui.joboffer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.*

class JobOfferViewModel : ViewModel() {

    private val _jobOffers = MutableLiveData<List<JobOffer>>()
    val jobOffers: LiveData<List<JobOffer>> get() = _jobOffers

    private val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("job_offers")

    init {
        fetchJobOffers()
    }

    private fun fetchJobOffers() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val jobOffersList = mutableListOf<JobOffer>()
                for (data in snapshot.children) {
                    val jobOffer = data.getValue(JobOffer::class.java)
                    if (jobOffer != null) {
                        jobOffersList.add(jobOffer)
                    }
                }
                _jobOffers.value = jobOffersList
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle possible errors
            }
        })
    }
}
