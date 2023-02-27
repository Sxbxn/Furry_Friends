package com.k_bootcamp.furry_friends.view.adapter.viewholder.listener

import com.k_bootcamp.furry_friends.model.writing.DiagnosisModel

// 일상 기록 클릭 리스너 추상화
interface DiagnosisListListener:AdapterListener {
    fun onClickItem(model: DiagnosisModel)
}