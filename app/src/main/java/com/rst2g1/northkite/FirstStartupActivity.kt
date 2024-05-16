package com.rst2g1.northkite

import android.content.Context
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
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()

        sharedPreferences = getSharedPreferences("prefs", Context.MODE_PRIVATE)

        // Initialize View Binding
        binding = FirstStartOneBinding.inflate(layoutInflater)
        binding_two = FirstStartTwoBinding.inflate(layoutInflater)
        binding_three = FirstStartThreeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonNext.setOnClickListener {
            setContentView(binding_two.root)
        }

        binding_two.buttonNext2.setOnClickListener {
            setContentView(binding_three.root)
        }

        binding_three.buttonNext3.setOnClickListener{
            sharedPreferences.edit().putBoolean("isFirstLaunch", false).apply()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}