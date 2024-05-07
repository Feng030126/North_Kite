package com.rst2g1.northkite

import android.content.Context
import android.content.res.Resources
import android.content.res.Resources.Theme
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import android.widget.ImageView
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.rst2g1.northkite.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        supportActionBar!!.setBackgroundDrawable(ResourcesCompat.getDrawable(resources, R.drawable.action_bar_bg, null))
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        supportActionBar!!.setDisplayShowCustomEnabled(true)

        supportActionBar!!.setCustomView(layoutInflater.inflate(R.layout.logo_layout, null))
    }
}