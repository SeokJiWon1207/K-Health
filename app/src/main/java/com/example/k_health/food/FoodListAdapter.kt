package com.example.k_health.food

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.k_health.databinding.ItemFoodBinding
import com.example.k_health.data.Food

class FoodListAdapter() : RecyclerView.Adapter<FoodListAdapter.ViewHolder>() {

    var foods: List<Food> = emptyList()

    inner class ViewHolder(private val binding: ItemFoodBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(food: Food) {
            with(binding) {
                foodNameTextView.text = food.foodname
                gramTextView.text = food.gram
                kcalTextView.text = food.kcal
            }
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