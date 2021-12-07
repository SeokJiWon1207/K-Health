package com.example.k_health.helper

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.k_health.databinding.ItemNutrientBinding
import com.example.k_health.model.Nutrient

class HelperNutrientAdapter(private val nutrientData: ArrayList<Nutrient>): RecyclerView.Adapter<HelperNutrientAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemNutrientBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(nutrient: Nutrient) {
            with(binding){
                nutrientNameTextView.text = nutrient.name
                nutrientAmountTextView.text = nutrient.amount
                nutrientCommentTextView.text = nutrient.comment
                nutrientTargetTextView.text = nutrient.target
                nutrientWayTextView.text = nutrient.way
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HelperNutrientAdapter.ViewHolder {
        return ViewHolder(
            ItemNutrientBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ))
    }

    override fun onBindViewHolder(holder: HelperNutrientAdapter.ViewHolder, position: Int) {
        holder.bind(nutrientData[position])
    }

    override fun getItemCount() = nutrientData.size
}