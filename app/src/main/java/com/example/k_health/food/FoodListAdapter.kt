package com.example.k_health.food

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.k_health.databinding.ItemFoodBinding
import com.example.k_health.food.data.models.FoodResponse

class FoodListAdapter() : ListAdapter<FoodResponse, FoodListAdapter.ViewHolder>(diffUtil) {

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
        holder.bind(currentList[position])
    }

    companion object {
        // DiffUtil은 RecyclerView의 성능을 한층 더 개선할 수 있게 해주는 유틸리티 클래스다.
        // 기존의 데이터 리스트와 교체할 데이터 리스트를 비교해서 실질적으로 업데이트가 필요한 아이템들을 추려낸다.
        val diffUtil = object : DiffUtil.ItemCallback<FoodResponse>() {
            // 두 아이템이 동일한 내용물을 가지고 있는지 체크한다.
            override fun areContentsTheSame(oldItem: FoodResponse, newItem: FoodResponse) =
                oldItem == newItem
            // 두 아이템이 동일한 아이템인지 체크한다. Ex) item이 자신만의 고유한 foodName 같은걸 가지고 있다면 그걸 기준으로 삼으면 된다.
            override fun areItemsTheSame(oldItem: FoodResponse, newItem: FoodResponse) =
                oldItem.foodName == newItem.foodName
        }
    }


}