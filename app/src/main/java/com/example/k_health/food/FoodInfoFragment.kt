package com.example.k_health.food

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.example.k_health.R
import com.example.k_health.databinding.FragmentFoodInfoBinding
import com.example.k_health.food.data.models.Item

class FoodInfoFragment : Fragment(R.layout.fragment_food_info) {

    companion object {
        const val TAG = "nutrient"
    }

    private var binding: FragmentFoodInfoBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentFoodInfoBinding = FragmentFoodInfoBinding.bind(view)
        binding = fragmentFoodInfoBinding

        fetchFoodNutrientInfo()

    }

    private fun fetchFoodNutrientInfo() {
        val items = arguments?.getParcelable<Item>("item")
        Log.d(TAG, "items: $items")

        with(binding!!) {
            foodNameInfoTextView.text = items?.foodName
            foodGramInfoTextView.text = items?.gram
            foodKcalInfoTextView.text = items?.kcal
            foodCarbonInfoTextView.text = items?.carbon
            foodProteinInfoTextView.text = items?.protein
            foodFatInfoTextView.text = items?.fat
            foodSugarInfoTextView.text = items?.sugar
            foodSodiumInfoTextView.text = items?.sodium
            foodCholesterolInfoTextView.text = items?.cholesterol
            foodSaturatedFattyAcidsInfoTextView.text = items?.saturatedFattyAcids
            foodUnsaturatedFattyAcidsInfoTextView.text = items?.unsaturatedFattyAcids
        }
    }
}