package com.example.k_health.health

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.k_health.databinding.ItemHealthsetBinding
import com.example.k_health.model.HealthList
import com.example.k_health.model.HealthRecord

class RecordHealthListAdapter() : RecyclerView.Adapter<RecordHealthListAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(v: View, data: HealthRecord, pos: Int) // View와 데이터 그리고 데이터의 위치를 가진다.
    }

    private var listener: OnItemClickListener? = null

    // 외부(액티비티나 프래그먼트)에서 사용할 수 있도록 메서드 정의하기
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    inner class ViewHolder(private val binding: ItemHealthsetBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(healthRecord: HealthRecord) {

            // xml요소 데이터 클래스와 바인딩

            val pos = adapterPosition

            if (pos != RecyclerView.NO_POSITION) {
                itemView.setOnClickListener {
                    listener?.onItemClick(itemView, healthrecord, pos)
                }
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
        holder.bind(healthrecord[position])
    }

    override fun getItemCount() = hea

}
