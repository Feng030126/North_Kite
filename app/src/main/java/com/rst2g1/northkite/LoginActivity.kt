package com.rst2g1.northkite

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.rst2g1.northkite.databinding.LoginPageBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: LoginPageBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE)

        binding = LoginPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonLogin.setOnClickListener{

            //check if login field == prefs/database record
            //if yes, load main activity and probably call a function to read user data?
            //else display error

        }

        binding.buttonRegister.setOnClickListener{

            //redirect to register layout

        }

        binding.buttonGuest.setOnClickListener {

            //set as guest profile

        }

    }

}