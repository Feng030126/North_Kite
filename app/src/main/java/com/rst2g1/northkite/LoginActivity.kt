package com.rst2g1.northkite

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.rst2g1.northkite.databinding.LoginPageBinding
import com.rst2g1.northkite.databinding.RegisterPageBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: LoginPageBinding
    private lateinit var bindingRegister: RegisterPageBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE)
        sharedPreferences.edit().putInt("login_status", -1).apply()
        //login status: -1 for no login, 0 for logged in, 1 for guest

        binding = LoginPageBinding.inflate(layoutInflater)
        bindingRegister = RegisterPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonLogin.setOnClickListener {

            //check if login field == prefs/database record
            //if yes, load main activity and probably call a function to read user data?
            //else display error

        }

        binding.buttonRegister.setOnClickListener {

            //redirect to register layout
            setContentView(bindingRegister.root)

        }

        binding.buttonGuest.setOnClickListener {
            //set as guest profile
            sharedPreferences.edit().putInt("login_status", 1).apply()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        bindingRegister.buttonRegister2.setOnClickListener {
            //Read register data
            //save to database
        }

    }

}