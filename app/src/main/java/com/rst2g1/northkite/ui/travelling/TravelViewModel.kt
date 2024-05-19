package com.rst2g1.northkite.ui.travelling

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rst2g1.northkite.R

class TravelViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Travel Page"
    }
    val text: LiveData<String> = _text

    fun getDestinations(): List<Destination> {
        return listOf(
            Destination("Tokyo", R.drawable.tokyo),
            Destination("Singapore", R.drawable.singapore),
            Destination("Germany", R.drawable.germany)
        )
    }
}
