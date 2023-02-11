package com.k_bootcamp.furry_friends.view.adapter.viewholder

import android.annotation.SuppressLint
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.k_bootcamp.furry_friends.databinding.ViewholderDiagnosisWritingBinding
import com.k_bootcamp.furry_friends.extension.clear
import com.k_bootcamp.furry_friends.extension.load
import com.k_bootcamp.furry_friends.model.writing.DiagnosisModel
import com.k_bootcamp.furry_friends.view.adapter.viewholder.listener.AdapterListener
import com.k_bootcamp.furry_friends.view.adapter.viewholder.listener.DiagnosisListListener
import com.k_bootcamp.furry_friends.view.base.BaseViewModel


class DiagnosisViewHolder(
    private val binding: ViewholderDiagnosisWritingBinding,
    viewModel: BaseViewModel
):ModelViewHolder<DiagnosisModel>(binding, viewModel) {
    override fun reset() = with(binding) {
        thumbnailDayDiagnosis.clear()
    }

    @SuppressLint("SetTextI18n")
    override fun bindData(model: DiagnosisModel) {
        super.bindData(model)
        with(binding) {
            thumbnailDayDiagnosis.load(model.imageUrl,0f, CenterCrop())
            diagnosisTextView.text = model.currdate+" 진단내용"
        }
    }

    override fun bindViews(model: DiagnosisModel, adapterListener: AdapterListener) {
        // AdapterListener를 받는 클릭리스너를 만들어서 지정함
        if(adapterListener is DiagnosisListListener) {
            binding.root.setOnClickListener {
                adapterListener.onClickItem(model)
            }
        }
    }
}