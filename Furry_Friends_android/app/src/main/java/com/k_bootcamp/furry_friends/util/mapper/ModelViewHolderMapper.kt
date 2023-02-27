package com.k_bootcamp.furry_friends.util.mapper

import android.view.LayoutInflater
import android.view.ViewGroup
import com.k_bootcamp.furry_friends.databinding.ViewholderDayWritingBinding
import com.k_bootcamp.furry_friends.databinding.ViewholderDiagnosisWritingBinding
import com.k_bootcamp.furry_friends.model.CellType
import com.k_bootcamp.furry_friends.model.Model
import com.k_bootcamp.furry_friends.view.adapter.viewholder.DailyViewHolder
import com.k_bootcamp.furry_friends.view.adapter.viewholder.DiagnosisViewHolder
import com.k_bootcamp.furry_friends.view.adapter.viewholder.ModelViewHolder
import com.k_bootcamp.furry_friends.view.base.BaseViewModel

// 타입에 맞는 뷰홀더로 바꿔주는 매퍼
object ModelViewHolderMapper {
    // cast warn에대한 처리(캐스트 이슈 바지
    @Suppress("UNCHECKED_CAST")
    fun <M: Model> map(
        parent: ViewGroup,
        type: CellType,
        viewModel: BaseViewModel,
    ): ModelViewHolder<M> {
        val inflater = LayoutInflater.from(parent.context)
        val viewHolder = when(type) {
            CellType.DIAGNOSIS_CELL -> DiagnosisViewHolder(
                ViewholderDiagnosisWritingBinding.inflate(inflater, parent, false),
                viewModel
            )
            CellType.DAILY_CELL -> DailyViewHolder(
                ViewholderDayWritingBinding.inflate(inflater, parent, false),
                viewModel,
            )
        }
        return viewHolder as ModelViewHolder<M>
    }
}