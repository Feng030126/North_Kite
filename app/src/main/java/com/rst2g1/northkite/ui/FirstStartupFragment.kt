package com.rst2g1.northkite.ui

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.rst2g1.northkite.R
import com.rst2g1.northkite.databinding.FirstStartOneBinding
import com.rst2g1.northkite.databinding.FirstStartTwoBinding
import com.rst2g1.northkite.databinding.FirstStartThreeBinding
import com.rst2g1.northkite.databinding.FragmentFirstStartupBinding

class FirstStartupFragment : Fragment() {

    private lateinit var _binding: FragmentFirstStartupBinding

    private val binding get() = _binding

    private lateinit var _bindingOne: FirstStartOneBinding
    private val bindingOne get() = _bindingOne

    private lateinit var _bindingTwo: FirstStartTwoBinding
    private val bindingTwo get() = _bindingTwo

    private lateinit var _bindingThree: FirstStartThreeBinding
    private val bindingThree get() = _bindingThree

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFirstStartupBinding.inflate(inflater, container, false)

        // Inflate the initial layout (FirstStartOne)
        _bindingOne = FirstStartOneBinding.inflate(inflater)

        _bindingTwo = FirstStartTwoBinding.inflate(inflater)

        _bindingThree = FirstStartThreeBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.hide()

        sharedPreferences = requireActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val container = view.findViewById<FrameLayout>(R.id.fragment_container)

        container.removeAllViews()
        container.addView(bindingOne.root)

        // Handle button clicks to switch layouts
        bindingOne.buttonNext.setOnClickListener {
            container.removeAllViews()
            container.addView(bindingTwo.root)
        }

        bindingTwo.buttonNext2.setOnClickListener {
            container.removeAllViews()
            container.addView(bindingThree.root)
        }

        bindingThree.buttonNext3.setOnClickListener {
            sharedPreferences.edit().putBoolean("isFirstLaunch", false).apply()
            findNavController().navigate(R.id.action_firstStartupFragment_to_loginFragment)
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as AppCompatActivity).supportActionBar?.show()
    }
}
