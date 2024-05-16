package com.rst2g1.northkite

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.rst2g1.northkite.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val isFirstLaunch = sharedPreferences.getBoolean("isFirstLaunch", true)

        if (isFirstLaunch) {
            // If it's the first launch, show the startup page
            val intent = Intent(this, FirstStartupActivity::class.java)
            startActivity(intent)
            // Set isFirstLaunch to false to indicate the app has been launched before
            //sharedPreferences.edit().putBoolean("isFirstLaunch", false).apply()
            return
        }

        val isLoggedIn = sharedPreferences.getInt("login_status", -1)

        if (isLoggedIn == -1) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_smedia, R.id.navigation_profile
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        //Set custom background, action_bar_bg.xml contain solid color
        supportActionBar!!.setBackgroundDrawable(
            ResourcesCompat.getDrawable(
                resources, R.drawable.action_bar_bg, null
            )
        )
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        //Setup custom logo
        supportActionBar!!.setDisplayShowCustomEnabled(true)

        supportActionBar!!.customView = layoutInflater.inflate(R.layout.logo_layout, null)
    }



}