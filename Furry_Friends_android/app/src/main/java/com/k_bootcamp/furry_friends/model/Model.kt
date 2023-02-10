package com.k_bootcamp.furry_friends.model

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil

abstract class Model (
    open val id: Int,
    open val type: CellType
) {
    companion object{
        // 리사이클러뷰에서 Model의 id를 통해 리사이클러뷰의 데이터에 변화가 있을 때 변화 감지
        val DIFF_CALLBACK: DiffUtil.ItemCallback<Model> = object: DiffUtil.ItemCallback<Model>() {
            // 아이템이 같은지
            override fun areItemsTheSame(oldItem: Model, newItem: Model): Boolean {
                return oldItem.id == newItem.id && oldItem.type == newItem.type
            }
            // 아이템 객체 자체가 같은지
            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: Model, newItem: Model): Boolean {
                return oldItem === newItem
            }

        }
    }
}