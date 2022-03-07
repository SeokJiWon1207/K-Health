package com.example.k_health.health.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.k_health.databinding.ItemHealthrecordBinding
import com.example.k_health.health.model.UserHealthRecord

class GetHealthListAdapter(
    private val healthRecordData: ArrayList<UserHealthRecord>,
    val onClickDeleteButton: (userHealthRecord: UserHealthRecord) -> Unit
) : RecyclerView.Adapter<GetHealthListAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemHealthrecordBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(userHealthRecord: UserHealthRecord) = with(binding) {
            healthNameTextView.text = userHealthRecord.name
            healthSetTextView.text = userHealthRecord.set.plus("set")
            healthWeightTextView.text = userHealthRecord.weight.plus("kg")
            healthCountTextView.text = userHealthRecord.count.plus("회")

            val marginTopValue = 20

            if (userHealthRecord.set.equals("1")) {
                healthNameTextView.visibility = View.VISIBLE
                removeImageButton.visibility = View.VISIBLE
                view.visibility = View.VISIBLE
                (view.layoutParams as ViewGroup.MarginLayoutParams).topMargin = marginTopValue
                (healthNameTextView.layoutParams as ViewGroup.MarginLayoutParams).topMargin =
                    marginTopValue
                (healthSetTextView.layoutParams as ViewGroup.MarginLayoutParams).topMargin =
                    marginTopValue
                (healthWeightTextView.layoutParams as ViewGroup.MarginLayoutParams).topMargin =
                    marginTopValue
                (healthCountTextView.layoutParams as ViewGroup.MarginLayoutParams).topMargin =
                    marginTopValue
                (removeImageButton.layoutParams as ViewGroup.MarginLayoutParams).topMargin =
                    marginTopValue

            } else {
                // 자리는 차지하지만 보이지 않게 설정해야함 -> constraint 제약
                healthNameTextView.visibility = View.INVISIBLE
                removeImageButton.visibility = View.INVISIBLE
                view.visibility = View.INVISIBLE
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemHealthrecordBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: GetHealthListAdapter.ViewHolder, position: Int) {
        holder.bind(healthRecordData[position])
        holder.binding.removeImageButton.setOnClickListener {
            onClickDeleteButton.invoke(healthRecordData[position])
        }
    }

    override fun getItemCount() = healthRecordData.size


}