package com.example.k_health.food

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.k_health.MainActivity
import com.example.k_health.R
import com.example.k_health.Repository
import com.example.k_health.databinding.FragmentFoodSearchBinding
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class FoodSearchFragment: Fragment(R.layout.fragment_food_search) {

    companion object {
        const val TAG = "network"
    }

    private var binding: FragmentFoodSearchBinding? = null
    private lateinit var foodListAdapter: FoodListAdapter
    private val scope = MainScope()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentFoodSearchBinding = FragmentFoodSearchBinding.bind(view)
        binding = fragmentFoodSearchBinding

        initFoodRecyclerView()
        fetchFoodItems()


    }

    private fun initFoodRecyclerView() {
        foodListAdapter = FoodListAdapter(itemClickListener = {
            val bundle = Bundle()
            bundle.putParcelable("item", it)

            val foodInfoFragment = FoodInfoFragment()

            foodInfoFragment.arguments = bundle

            (activity as MainActivity).replaceFragment(foodInfoFragment)


        })
        binding!!.foodRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = foodListAdapter
        }
    }

    // Retrofit 1-4) FoodApiService의 Method 사용으로 받아온 데이터를 RecyclerView에 뿌려주기
    private fun fetchFoodItems(query: String? = null) = scope.launch {
        try {
            Repository.getFoodItems()?.let {
                (binding!!.foodRecyclerView.adapter as? FoodListAdapter)?.apply {
                    Log.d(TAG,"items : ${it.body!!.items}")

                    submitList(it.body.items!!)
                }
            }
        } catch (exception: Exception) {
            Log.d(TAG,"exception : $exception")
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
        scope.cancel()
    }

}