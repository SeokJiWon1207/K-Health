package com.example.k_health.food

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.k_health.R
import com.example.k_health.databinding.FragmentFoodSearchBinding

class FoodSearchFragment: Fragment(R.layout.fragment_food_search) {

    private var binding: FragmentFoodSearchBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val FragmentFoodSearchBinding = FragmentFoodSearchBinding.bind(view)
        binding = FragmentFoodSearchBinding


    }

}