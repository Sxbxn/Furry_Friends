package com.k_bootcamp.furry_friends.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.k_bootcamp.furry_friends.data.response.animal.AnimalResponse
import com.k_bootcamp.furry_friends.databinding.ViewholderSelectAnimalBinding
import com.k_bootcamp.furry_friends.extension.load
import com.k_bootcamp.furry_friends.view.adapter.viewholder.listener.SelectAnimalListListener
import com.k_bootcamp.furry_friends.view.main.home.HomeViewModel


class SelectAnimalAdapter(
    private val infoList: List<AnimalResponse>,
    private val viewModel: HomeViewModel,
    val context: Context
) : RecyclerView.Adapter<SelectAnimalAdapter.SelectViewHolder>() {
    private lateinit var itemClickListener: SelectAnimalListListener


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SelectAnimalAdapter.SelectViewHolder =
        SelectViewHolder(
            ViewholderSelectAnimalBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: SelectAnimalAdapter.SelectViewHolder, position: Int) =
        holder.bind(infoList[position])

    override fun getItemCount(): Int = infoList.size

    fun setItemOnClickListener(listener: SelectAnimalListListener) {
        itemClickListener = listener
    }
    inner class SelectViewHolder(private val binding: ViewholderSelectAnimalBinding) :
       RecyclerView.ViewHolder(binding.root) {
            init {
                binding.root.setOnClickListener {
                    itemClickListener.onClickItem(adapterPosition)
                }
            }
            fun bind(model: AnimalResponse) {
                binding.thumbnailSelectAnimal.load(model.imageUrl)
                binding.selectAnimalName.text = model.name
            }

    }
}
