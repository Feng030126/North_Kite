package com.rst2g1.northkite

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.rst2g1.northkite.databinding.FirstStartOneBinding
import com.rst2g1.northkite.databinding.FirstStartTwoBinding
import com.rst2g1.northkite.databinding.FirstStartThreeBinding

class FirstStartupFragment : Fragment() {

    private var _bindingOne: FirstStartOneBinding? = null
    private val bindingOne get() = _bindingOne!!

    private var _bindingTwo: FirstStartTwoBinding? = null
    private val bindingTwo get() = _bindingTwo!!

    private var _bindingThree: FirstStartThreeBinding? = null
    private val bindingThree get() = _bindingThree!!

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the initial layout (FirstStartOne)
        _bindingOne = FirstStartOneBinding.inflate(inflater, container, false)
        return bindingOne.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = requireActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE)

        // Set initial view
        setInitialView()

        // Handle button clicks to switch layouts
        bindingOne.buttonNext.setOnClickListener {
            switchToSecondLayout()
        }

        bindingTwo.buttonNext2.setOnClickListener {
            switchToThirdLayout()
        }

        bindingThree.buttonNext3.setOnClickListener {
            sharedPreferences.edit().putBoolean("isFirstLaunch", false).apply()
            parentFragmentManager.commit {
                replace(R.id.nav_host_fragment_activity_main, LoginFragment())
                addToBackStack(null)
            }
        }
    }

    private fun setInitialView() {
        val container = view?.findViewById<FrameLayout>(R.id.fragment_container)
        container?.removeAllViews()
        container?.addView(bindingOne.root)
    }

    private fun switchToSecondLayout() {
        _bindingTwo = FirstStartTwoBinding.inflate(layoutInflater)
        val container = view?.findViewById<FrameLayout>(R.id.fragment_container)
        container?.removeAllViews()
        container?.addView(bindingTwo.root)
    }

    private fun switchToThirdLayout() {
        _bindingThree = FirstStartThreeBinding.inflate(layoutInflater)
        val container = view?.findViewById<FrameLayout>(R.id.fragment_container)
        container?.removeAllViews()
        container?.addView(bindingThree.root)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _bindingOne = null
        _bindingTwo = null
        _bindingThree = null
    }
}
