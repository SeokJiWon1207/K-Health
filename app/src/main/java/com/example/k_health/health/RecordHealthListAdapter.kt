package com.example.k_health.health

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.k_health.databinding.ItemHealthsetBinding
import com.example.k_health.model.HealthRecord

class RecordHealthListAdapter(private val healthRecordData: ArrayList<HealthRecord>) : RecyclerView.Adapter<RecordHealthListAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemHealthsetBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(healthRecord: HealthRecord) {
            val pos = adapterPosition

            // xml요소 데이터 클래스와 바인딩
            with(binding) {
                setTextView.text = healthRecord.set
                weightEditText.setText(healthRecord.weight)
                countEditText.setText(healthRecord.count)
            }

            val bundle = Bundle()
            bundle.putString("weight",healthRecordData[pos].weight)
            bundle.putString("count",healthRecordData[pos].count)
            bundle.putInt("pos", pos)

            val recordHealthListFragment = RecordHealthListFragment()
            recordHealthListFragment.arguments = bundle

            // 첫 번째 운동세트는 못지우게 삭제버튼 GONE처리
            if (pos == 0) binding.deleteImageButton.visibility = View.GONE

            binding.deleteImageButton.setOnClickListener {
                Log.d("record","${healthRecordData[pos]}, $pos")
                healthRecordData.removeAt(pos)
                notifyDataSetChanged()
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemHealthsetBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(healthRecordData[position])
    }

    override fun getItemCount() = healthRecordData.size


}
