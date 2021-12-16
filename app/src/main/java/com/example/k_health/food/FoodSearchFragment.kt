package com.example.k_health.food

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.k_health.R
import com.example.k_health.databinding.FragmentFoodSearchBinding
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FoodSearchFragment: Fragment(R.layout.fragment_food_search) {

    companion object {
        const val BASE_URL = "http://apis.data.go.kr/1471000/FoodNtrIrdntInfoService1/"
    }

    private var binding: FragmentFoodSearchBinding? = null
    private val foodListAdapter = FoodListAdapter()
    private lateinit var foodService: FoodService

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val FragmentFoodSearchBinding = FragmentFoodSearchBinding.bind(view)
        binding = FragmentFoodSearchBinding

        // Retrofit 1-3) Retrofit 인스턴스 생성
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL) // baseUrl은 꼭 '/' 로 끝나야 함, 아니면 예외 발생
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Retrofit 1-4) instance로 interface 객체 구현
        foodService = retrofit.create(FoodService::class.java)


        initFoodRecyclerView()

    }

    private fun initFoodRecyclerView() {
        binding!!.foodRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = foodListAdapter
        }
    }

}