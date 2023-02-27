package com.k_bootcamp.furry_friends.view.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.k_bootcamp.furry_friends.model.Model
import com.k_bootcamp.furry_friends.view.adapter.viewholder.listener.AdapterListener
import com.k_bootcamp.furry_friends.view.base.BaseViewModel


// viewholder 추상화
abstract class ModelViewHolder<M: Model>(
    binding: ViewBinding,
    private val viewModel: BaseViewModel,
):RecyclerView.ViewHolder(binding.root) {
    abstract fun reset()

    open fun bindData(model:M) {
        reset()
    }
    abstract fun bindViews(model: M, adapterListener: AdapterListener)
}