package com.example.k_health.food

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.k_health.R
import com.example.k_health.databinding.FragmentFoodBinding

class FoodFragment: Fragment(R.layout.fragment_food) {

    private var binding: FragmentFoodBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentFoodBinding = FragmentFoodBinding.bind(view)
        binding = fragmentFoodBinding

    }
}