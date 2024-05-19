package com.rst2g1.northkite

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.rst2g1.northkite.databinding.ActivityMainBinding
import com.rst2g1.northkite.ui.Notifier

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Notifier.createChannel(this)

        sharedPreferences = getSharedPreferences("prefs", Context.MODE_PRIVATE)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val navController = navHostFragment.navController

        val isFirstLaunch = sharedPreferences.getBoolean("isFirstLaunch", true)
        if (isFirstLaunch) {
            navController.navigate(R.id.firstStartupFragment)
        }

        val isLoggedIn = sharedPreferences.getInt("login_status", -1)
        if (isLoggedIn == -1) {
            navController.navigate(R.id.loginFragment)
        }

        val navView: BottomNavigationView = binding.navView

        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.navigation_home, R.id.navigation_smedia, R.id.navigation_profile)
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // hiding bottom bar
        navController.addOnDestinationChangedListener { _, nd: NavDestination, _ ->
            // the IDs of fragments as defined in the `navigation_graph`
            if (nd.id == R.id.navigation_home || nd.id == R.id.navigation_smedia
                || nd.id == R.id.navigation_profile
            ) {
                navView.visibility = View.VISIBLE
            } else {
                navView.visibility = View.GONE
            }
        }

        // Set custom background
        supportActionBar?.setBackgroundDrawable(
            ResourcesCompat.getDrawable(resources, R.drawable.action_bar_bg, null)
        )
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Setup custom logo
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.customView = layoutInflater.inflate(R.layout.logo_layout, null)


    }
}
