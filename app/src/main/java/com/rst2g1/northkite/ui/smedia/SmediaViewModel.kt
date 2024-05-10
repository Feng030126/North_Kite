package com.rst2g1.northkite.ui.smedia

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SmediaViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is Sosial Media Fragment"
    }
    val text: LiveData<String> = _text
}