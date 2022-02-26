package com.example.k_health.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.k_health.databinding.ItemTodolistBinding
import com.example.k_health.home.adapter.TodoList

class TodoListAdapter(private val todolistData: ArrayList<TodoList>) :
    RecyclerView.Adapter<TodoListAdapter.ViewHolder>() {
    inner class ViewHolder(private val binding: ItemTodolistBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(todoList: TodoList) {

            binding.todolistNameTextView.text = todoList.name

            Glide.with(binding.todolistImageView)
                .load(todoList.image)
                .into(binding.todolistImageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemTodolistBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(todolistData[position])
    }

    override fun getItemCount() = 4
}