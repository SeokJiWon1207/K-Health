package com.example.k_health.food

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
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

    private var _binding: FragmentFoodBinding? = null
    private val binding get() = _binding!!
    private val foodSearchFragment = FoodSearchFragment()

    companion object {
        const val TAG = "FoodFragment"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentFoodBinding.bind(view)


        setFoodTime()


    }

    @SuppressLint("ResourceAsColor")
    private fun setFoodTime() = with(binding){
        with(layoutBreakfast) {
            timeImageView.setImageResource(FoodTime.BREAKFAST.timeImage)
            timeTextView.text = FoodTime.BREAKFAST.time
            timeTextView.setTextColor(FoodTime.BREAKFAST.textColor)
            moveSearchFood(foodAddImageButton, FoodTime.BREAKFAST.time)
        }
        with(layoutLunch) {
            timeImageView.setImageResource(FoodTime.LUNCH.timeImage)
            timeTextView.text = FoodTime.LUNCH.time
            timeTextView.setTextColor(FoodTime.LUNCH.textColor)
            moveSearchFood(foodAddImageButton, FoodTime.LUNCH.time)
        }
        with(layoutDinner) {
            timeImageView.setImageResource(FoodTime.DINNER.timeImage)
            timeTextView.text = FoodTime.DINNER.time
            timeTextView.setTextColor(FoodTime.DINNER.textColor)
            moveSearchFood(foodAddImageButton, FoodTime.DINNER.time)
        }
        with(layoutEtc) {
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
            Log.d(TAG, "time : $time")

            val foodInfoFragment = FoodInfoFragment()

            foodInfoFragment.arguments = bundle

            (activity as MainActivity).replaceFragment(foodSearchFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}