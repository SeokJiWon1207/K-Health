package com.example.k_health.food

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.k_health.databinding.ItemFoodrecordBinding
import com.example.k_health.food.data.models.Item

class FoodRecordListAdapter(private val foodRecordData: ArrayList<Item>) : RecyclerView.Adapter<FoodRecordListAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemFoodrecordBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Item) = with(binding) {
            foodNameTextView.text = item.foodName
            gramTextView.text = item.gram.plus("g")
            carbonTextView.text = item.carbon.plus("g")
            proteinTextView.text = item.protein.plus("g")
            fatTextView.text = item.fat.plus("g")
            kcalTextView.text = item.kcal.plus("kcal")

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemFoodrecordBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(foodRecordData[position])
    }

    override fun getItemCount(): Int = foodRecordData.size
}