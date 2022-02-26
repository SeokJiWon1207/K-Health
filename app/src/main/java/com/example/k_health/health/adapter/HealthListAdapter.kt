package com.example.k_health.health.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.k_health.databinding.ItemHealthlistBinding
import com.example.k_health.health.model.HealthList

class HealthListAdapter(private val healthData: ArrayList<HealthList>) :
    RecyclerView.Adapter<HealthListAdapter.ViewHolder>() {

    interface OnItemClickListener{
        fun onItemClick(v: View, data: HealthList, pos : Int) // View와 데이터 그리고 데이터의 위치를 가진다.
    }

    private var listener : OnItemClickListener? = null

    // 외부(액티비티나 프래그먼트)에서 사용할 수 있도록 메서드 정의하기
    fun setOnItemClickListener(Listener : OnItemClickListener) {
        this.listener = Listener
    }

    inner class ViewHolder(private val binding: ItemHealthlistBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(healthlist: HealthList) {
            val pos = adapterPosition

            // xml요소 데이터 클래스와 바인딩
            with(binding) {
                healthNameTextView.text = healthlist.name
                engHealthNameTextView.text = healthlist.engName
            }

            Glide.with(binding.healthImageView.context)
                .load(healthlist.imageUrl)
                .into(binding.healthImageView)


            if(pos!= RecyclerView.NO_POSITION) {
                itemView.setOnClickListener {
                    listener?.onItemClick(itemView, healthlist ,pos)
                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemHealthlistBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(healthData[position])
    }

    override fun getItemCount() = healthData.size

}
