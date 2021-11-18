package com.example.k_health.health

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.k_health.databinding.ItemHealthlistBinding
import com.example.k_health.model.HealthList

class HealthListAdapter(private val healthdata: ArrayList<HealthList>) : RecyclerView.Adapter<HealthListAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemHealthlistBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(healthlist: HealthList) {

            binding.healthNameTextView.text = healthlist.name
            binding.engHealthNameTextView.text = healthlist.engName

            if (healthlist.imageUrl.isNotEmpty()) {
                Glide.with(binding.healthImageView)
                    .load(healthlist.imageUrl)
                    .into(binding.healthImageView)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemHealthlistBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(healthdata[position])
    }

    override fun getItemCount() = healthdata.size

}
