package com.example.k_health.health

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.k_health.databinding.ItemHealthlistBinding
import com.example.k_health.model.HealthList

class HealthListAdapter(private val healthdata: ArrayList<HealthList>) :
    RecyclerView.Adapter<HealthListAdapter.ViewHolder>() {

    interface OnItemClickListener{
        fun onItemClick(v: View, data: HealthList, pos : Int)
    }

    private var listener : OnItemClickListener? = null

    fun setOnItemClickListener(listener : OnItemClickListener) {
        this.listener = listener
    }


    inner class ViewHolder(private val binding: ItemHealthlistBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(healthlist: HealthList) {

            binding.healthNameTextView.text = healthlist.name
            binding.engHealthNameTextView.text = healthlist.engName

            Glide.with(binding.healthImageView.context)
                .load(healthlist.imageUrl)
                .into(binding.healthImageView)

            val pos = adapterPosition

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
        holder.bind(healthdata[position])

        /*holder.itemView.setOnClickListener {
            var pos = holder.adapterPosition
            Log.d("TAG", "name : ${healthdata[pos].name} & engName : ${healthdata[pos].engName}")

            Toast.makeText(holder.itemView.context, "$pos:번째 아이템이 클릭되었습니다.",Toast.LENGTH_SHORT).show()
        }*/
    }

    override fun getItemCount() = healthdata.size

}
