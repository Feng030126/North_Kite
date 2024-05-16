package com.rst2g1.northkite.ui.home

import android.graphics.drawable.Drawable
import android.media.Image
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.rst2g1.northkite.R
import com.rst2g1.northkite.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val imageList = ArrayList<SlideModel>()

        imageList.add(SlideModel(R.drawable.home_banner_1))
        imageList.add(SlideModel(R.drawable.first_start_one_banner))

        binding.slideshow.setImageList(imageList, ScaleTypes.FIT)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}