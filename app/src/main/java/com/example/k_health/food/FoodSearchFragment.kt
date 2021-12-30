package com.example.k_health.food

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.k_health.R
import com.example.k_health.Repository
import com.example.k_health.databinding.FragmentFoodSearchBinding
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class FoodSearchFragment: Fragment(R.layout.fragment_food_search) {

    companion object {
        const val TAG = "retrofit"
    }

    private var binding: FragmentFoodSearchBinding? = null
    private val foodListAdapter = FoodListAdapter()
    private val scope = MainScope()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val FragmentFoodSearchBinding = FragmentFoodSearchBinding.bind(view)
        binding = FragmentFoodSearchBinding

        initFoodRecyclerView()
        fetchFoodItems()

    }

    private fun initFoodRecyclerView() {
        binding!!.foodRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = foodListAdapter
        }
    }

    // API 데이터 맵핑
    private fun fetchFoodItems() = scope.launch {
        try {
            Repository.getFoodItems()?.let { foods ->
                (binding!!.foodRecyclerView.adapter as? FoodListAdapter)?.apply {
                    this.foods = foods
                    notifyDataSetChanged()
                }
            }
        } catch (exception: Exception) {
            Log.d(TAG,"exception : $exception")
        } finally {
            // TODO WHAT
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
        scope.cancel()
    }

}