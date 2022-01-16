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
        const val TAG = "FoodinfoFragment"
    }

    private var _binding: FragmentFoodInfoBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentFoodInfoBinding.bind(view)

        fetchFoodNutrientInfo()

    }

    private fun fetchFoodNutrientInfo() {
        val items = arguments?.getParcelable<Item>("item")
        val mealtime = arguments?.getString("mealtime")
        Log.d(TAG, "items: $items")
        Log.d(TAG, "mealtime: $mealtime")

        with(binding) {
            foodNameInfoTextView.text = items!!.foodName
            foodGramInfoTextView.text = items.gram.plus("g")
            foodKcalInfoTextView.text = items.kcal
            foodCarbonInfoTextView.text = items.carbon
            foodProteinInfoTextView.text = items.protein
            foodFatInfoTextView.text = items.fat
            foodSugarInfoTextView.text = items.sugar
            foodSodiumInfoTextView.text = items.sodium
            foodCholesterolInfoTextView.text = items.cholesterol
            foodSaturatedFattyAcidsInfoTextView.text = items.saturatedFattyAcids
            foodUnsaturatedFattyAcidsInfoTextView.text = items.unsaturatedFattyAcids
            foodTimeInfoTextView.text = mealtime
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}