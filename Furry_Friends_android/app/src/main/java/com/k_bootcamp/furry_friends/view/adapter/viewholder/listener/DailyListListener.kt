package com.k_bootcamp.furry_friends.view.adapter.viewholder.listener

import com.k_bootcamp.furry_friends.model.writing.DailyModel

// 일상 기록 클릭 리스너 추상화
interface DailyListListener:AdapterListener {
    fun onClickItem(model: DailyModel)
}