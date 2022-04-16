package com.example.k_health.sns.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.k_health.R
import com.example.k_health.databinding.ItemSnsBinding
import com.example.k_health.sns.model.SNS

class SnsArticleListAdapter() : ListAdapter<SNS, SnsArticleListAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(private val binding: ItemSnsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(sns: SNS) = with(binding) {
            boardNumberTextView.text = sns.boardNumber.plus("번째 게시물")
            userNicknameTextView.text = sns.userNickname
            userSnsContentTextView.text = sns.userSnsContent

            Glide.with(userProfileImageView.context)
                .load(sns.userProfile)
                .circleCrop()
                .into(userProfileImageView)

            Glide.with(userUploadImageImageView.context)
                .load(sns.userUploadImage)
                .fitCenter()
                .placeholder(R.drawable.app_logo)
                .into(userUploadImageImageView)

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemSnsBinding.inflate(
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
        val diffUtil = object : DiffUtil.ItemCallback<SNS>() {
            // 두 아이템이 동일한 아이템인지 체크한다. 같으면 areContentsTheSame 으로, 다르면 갱신
            // Ex) item이 자신만의 고유한 foodName 같은걸 가지고 있다면 그걸 기준으로 삼으면 된다.
            override fun areItemsTheSame(oldItem: SNS, newItem: SNS) =
                oldItem.boardNumber == newItem.boardNumber

            // 두 아이템이 동일한 내용물을 가지고 있는지 체크한다.
            override fun areContentsTheSame(oldItem: SNS, newItem: SNS) =
                oldItem.hashCode() == newItem.hashCode()

        }

        const val TAG = "FoodListAdapter"
    }


}