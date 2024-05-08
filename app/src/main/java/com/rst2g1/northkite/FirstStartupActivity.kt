package com.rst2g1.northkite

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.rst2g1.northkite.databinding.FirstStartOneBinding
import com.rst2g1.northkite.databinding.FirstStartThreeBinding
import com.rst2g1.northkite.databinding.FirstStartTwoBinding

class FirstStartupActivity : AppCompatActivity() {

    private lateinit var binding: FirstStartOneBinding
    private lateinit var binding_two: FirstStartTwoBinding
    private lateinit var binding_three: FirstStartThreeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize View Binding
        binding = FirstStartOneBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonNext.setOnClickListener {
            setContentView(binding_two.root)
        }
    }
}