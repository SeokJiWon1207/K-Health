package com.example.k_health.food

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.k_health.databinding.ItemFoodBinding
import com.example.k_health.food.data.models.Item

class FoodListAdapter(private val itemClickListener: (Item) -> Unit) :
    ListAdapter<Item, FoodListAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(private val binding: ItemFoodBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Item) = with(binding) {
            foodNameTextView.text = item.foodName
            gramTextView.text = item.gram.plus("g")
            kcalTextView.text = item.kcal.plus("kcal")

            root.setOnClickListener {
                itemClickListener(item)
            }

            checkBox.apply {
                setOnCheckedChangeListener(null) // 체크박스 리스너 초기화
                isChecked = item.isSelected // 체크박스의 체크 여부를
                setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener { // 체크박스의 상태값을 알기 위해 리스너 등록
                    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                        Log.d(TAG, "checkbox : ${adapterPosition}${isChecked}")
                        item.setSelected(isChecked) // 데이터 클래스의 객체와 동일
                    }
                })
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemFoodBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        // DiffUtil은 RecyclerView의 성능을 한층 더 개선할 수 있게 해주는 유틸리티 클래스다.
        // 기존의 데이터 리스트와 교체할 데이터 리스트를 비교해서 실질적으로 업데이트가 필요한 아이템들을 추려낸다.
        val diffUtil = object : DiffUtil.ItemCallback<Item>() {
            // 두 아이템이 동일한 내용물을 가지고 있는지 체크한다.
            override fun areContentsTheSame(oldItem: Item, newItem: Item) =
                oldItem == newItem

            // 두 아이템이 동일한 아이템인지 체크한다. Ex) item이 자신만의 고유한 foodName 같은걸 가지고 있다면 그걸 기준으로 삼으면 된다.
            override fun areItemsTheSame(oldItem: Item, newItem: Item) =
                oldItem.foodName == newItem.foodName
        }

        const val TAG = "FoodListAdapter"
    }


}