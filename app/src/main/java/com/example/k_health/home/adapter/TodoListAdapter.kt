package com.example.k_health.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.k_health.databinding.ItemTodolistBinding
import com.example.k_health.home.model.TodoList

class TodoListAdapter(private val todolistData: List<TodoList>) :
    RecyclerView.Adapter<TodoListAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemTodolistBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(todoList: TodoList) = with(binding) {

            todolistNameTextView.text = todoList.todoName
            todolistTImeTextView.text = todoList.time

            if (todoList.isComplete == true) {
                todolistNotifyTextView.text = "기록이 완료 되었습니다"
            } else {
                todolistNotifyTextView.text = "기록을 해주세요"
            }

            Glide.with(todolistImageView)
                .load(todoList.image)
                .into(todolistImageView)
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

    override fun getItemCount() = todolistData.size
}