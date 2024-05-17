package com.rst2g1.northkite.ui.profile

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.rst2g1.northkite.ui.LoginFragment
import com.rst2g1.northkite.R
import com.rst2g1.northkite.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        sharedPreferences = requireContext().getSharedPreferences("prefs", Context.MODE_PRIVATE)

        _binding!!.logoutIcon.setOnClickListener {
            sharedPreferences.edit().putInt("login_status", -1).apply()
            findNavController().navigate(R.id.action_navigation_profile_to_loginFragment)
        }

        _binding!!.logoutLabel.setOnClickListener {
            sharedPreferences.edit().putInt("login_status", -1).apply()
            findNavController().navigate(R.id.action_navigation_profile_to_loginFragment)
        }

        val isLoggedIn = sharedPreferences.getInt("login_status", -1)

        if (isLoggedIn == 1) _binding!!.loginToContinueLabel.visibility =
            View.VISIBLE // guest login

        if (isLoggedIn == 0){
            //normal login, all profile button function here
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}