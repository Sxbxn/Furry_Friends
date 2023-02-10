package com.k_bootcamp.furry_friends.view.adapter.viewholder

import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.k_bootcamp.furry_friends.databinding.ViewholderDayWritingBinding
import com.k_bootcamp.furry_friends.extension.clear
import com.k_bootcamp.furry_friends.extension.load
import com.k_bootcamp.furry_friends.model.writing.DailyModel
import com.k_bootcamp.furry_friends.view.adapter.viewholder.listener.AdapterListener
import com.k_bootcamp.furry_friends.view.adapter.viewholder.listener.DailyListListener
import com.k_bootcamp.furry_friends.view.base.BaseViewModel

class DailyViewHolder(
    private val binding: ViewholderDayWritingBinding,
    viewModel: BaseViewModel
):ModelViewHolder<DailyModel>(binding, viewModel) {
    override fun reset() = with(binding) {
        thumbnailDayWriting.clear()
    }

    override fun bindData(model: DailyModel) {
        super.bindData(model)
        with(binding) {
            thumbnailDayWriting.load(model.imageUrl,0f, CenterCrop())
            writingTextView.text = model.title
            dateTextView.text = model.currdate
        }
    }

    override fun bindViews(model: DailyModel, adapterListener: AdapterListener) {
        // AdapterListener를 받는 클릭리스너를 만들어서 지정함
        if(adapterListener is DailyListListener) {
            binding.root.setOnClickListener {

            }
        }
    }
}