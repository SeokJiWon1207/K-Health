package com.example.k_health.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.k_health.databinding.ItemTodolistBinding
import com.example.k_health.home.model.TodoList

class TodoListAdapter(private val todolistData: List<TodoList>) :
    RecyclerView.Adapter<TodoListAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(v: View, data: TodoList, pos : Int) // View와 데이터 그리고 데이터의 위치를 가진다.
    }

    private var listener : OnItemClickListener? = null

    // 외부(액티비티나 프래그먼트)에서 사용할 수 있도록 메서드 정의하기
    fun setOnItemClickListener(Listener : OnItemClickListener) {
        this.listener = Listener
    }

    inner class ViewHolder(private val binding: ItemTodolistBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(todoList: TodoList) = with(binding) {
            val pos = adapterPosition

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

            if(pos!= RecyclerView.NO_POSITION) {
                itemView.setOnClickListener {
                    listener?.onItemClick(itemView, todoList ,pos)
                }
            }
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