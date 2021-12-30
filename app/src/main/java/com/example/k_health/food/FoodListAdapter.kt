package com.example.k_health.food

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.k_health.databinding.ItemFoodBinding
import com.example.k_health.food.data.models.FoodResponse

class FoodListAdapter() : RecyclerView.Adapter<FoodListAdapter.ViewHolder>() {

    var foods: List<FoodResponse> = emptyList()

    inner class ViewHolder(private val binding: ItemFoodBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(foodResponse: FoodResponse) = with(binding) {
                foodNameTextView.text = foodResponse.foodName
                gramTextView.text = foodResponse.gram
                kcalTextView.text = foodResponse.kcal
            }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemFoodBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(foods[position])
    }

    override fun getItemCount(): Int = foods.size

}