package com.example.k_health.helper

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.k_health.R
import com.example.k_health.databinding.FragmentHelperNutrientBinding

class HelperNutrientFragment: Fragment(R.layout.fragment_helper_nutrient) {

    private var binding: FragmentHelperNutrientBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentHelperNutrientBinding = FragmentHelperNutrientBinding.bind(view)
        binding = fragmentHelperNutrientBinding

    }

}