package com.example.k_health.food

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.k_health.MainActivity
import com.example.k_health.R
import com.example.k_health.databinding.FragmentFoodBinding
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FoodFragment : Fragment(R.layout.fragment_food) {

    private var binding: FragmentFoodBinding? = null
    private val foodSearchFragment = FoodSearchFragment()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentFoodBinding = FragmentFoodBinding.bind(view)

        binding = fragmentFoodBinding

        setFoodTime()


    }

    @SuppressLint("ResourceAsColor")
    private fun setFoodTime() {
        with(binding!!.layoutBreakfast) {
            timeImageView.setImageResource(FoodTime.BREAKFAST.timeImage)
            timeTextView.text = FoodTime.BREAKFAST.time
            timeTextView.setTextColor(FoodTime.BREAKFAST.textColor)
            moveSearchFood(foodAddImageButton, FoodTime.BREAKFAST.time)
        }
        with(binding!!.layoutLunch) {
            timeImageView.setImageResource(FoodTime.LUNCH.timeImage)
            timeTextView.text = FoodTime.LUNCH.time
            timeTextView.setTextColor(FoodTime.LUNCH.textColor)
            moveSearchFood(foodAddImageButton, FoodTime.LUNCH.time)
        }
        with(binding!!.layoutDinner) {
            timeImageView.setImageResource(FoodTime.DINNER.timeImage)
            timeTextView.text = FoodTime.DINNER.time
            timeTextView.setTextColor(FoodTime.DINNER.textColor)
            moveSearchFood(foodAddImageButton, FoodTime.DINNER.time)
        }
        with(binding!!.layoutEtc) {
            timeImageView.setImageResource(FoodTime.ETC.timeImage)
            timeTextView.text = FoodTime.ETC.time
            timeTextView.setTextColor(FoodTime.ETC.textColor)
            moveSearchFood(foodAddImageButton, FoodTime.ETC.time)
        }
    }


    private fun moveSearchFood(foodAddImageButton: ImageButton, time: Any) {
        foodAddImageButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("time", time.toString())

            val foodInfoFragment = FoodInfoFragment()

            foodInfoFragment.arguments = bundle

            (activity as MainActivity).replaceFragment(foodSearchFragment)
        }
    }
}