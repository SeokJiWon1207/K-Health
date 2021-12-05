package com.example.k_health.helper

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.k_health.R
import com.example.k_health.databinding.FragmentHelperDietBinding

class HelperDietFragment: Fragment(R.layout.fragment_helper_diet) {

    private var binding: FragmentHelperDietBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentHelperDietBinding = FragmentHelperDietBinding.bind(view)
        binding = fragmentHelperDietBinding

    }

}