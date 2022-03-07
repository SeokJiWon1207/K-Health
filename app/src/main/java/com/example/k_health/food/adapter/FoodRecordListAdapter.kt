package com.example.k_health.food.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.k_health.databinding.ItemFoodrecordBinding
import com.example.k_health.food.data.models.Item

//2. delete button이 눌렸을때 onclickDeleteIcon을 실행하라는뜻, 0->Unit이기때문에 함수자체에 return없다는뜻
class FoodRecordListAdapter(val foodRecordData: ArrayList<Item>, val onClickDeleteButton: (item: Item) -> Unit) :
    RecyclerView.Adapter<FoodRecordListAdapter.ViewHolder>() {

    companion object {
        const val TAG = "FoodRecordListAdapter"
    }

    inner class ViewHolder(val binding: ItemFoodrecordBinding) :
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
        // 1. deleteimage가 눌렸을때 listposition를 전달하면서 onClickDeleteIcon함수를 실행한다.
        holder.binding.removeImageButton.setOnClickListener {
            onClickDeleteButton.invoke(foodRecordData[position])
        }
    }

    override fun getItemCount(): Int = foodRecordData.size
}