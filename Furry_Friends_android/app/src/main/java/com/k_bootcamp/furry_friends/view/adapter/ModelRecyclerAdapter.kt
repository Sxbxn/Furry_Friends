package com.k_bootcamp.furry_friends.view.adapter


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ListAdapter
import com.k_bootcamp.furry_friends.model.CellType
import com.k_bootcamp.furry_friends.model.Model
import com.k_bootcamp.furry_friends.model.writing.DailyModel
import com.k_bootcamp.furry_friends.util.mapper.ModelViewHolderMapper
import com.k_bootcamp.furry_friends.view.MainActivity
import com.k_bootcamp.furry_friends.view.adapter.viewholder.ModelViewHolder
import com.k_bootcamp.furry_friends.view.adapter.viewholder.listener.AdapterListener
import com.k_bootcamp.furry_friends.view.base.BaseViewModel
import com.k_bootcamp.furry_friends.view.main.writing.TabWritingViewModel
import com.k_bootcamp.furry_friends.view.main.writing.daily.DailyWritingFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

// 리사이클러뷰 어댑터 추상화
class ModelRecyclerAdapter<M : Model, VM : BaseViewModel>(
    // 바인딩 될 데이터 리스트
    private var modelList: MutableList<Model>,
    // 뷰모델
    private var viewModel: VM,
    // 이벤트 리스너
    private val adapterListener: AdapterListener,
    // context
    private val context: Context
) : ListAdapter<Model, ModelViewHolder<M>>(Model.DIFF_CALLBACK) {
    // 데이터의 사이즈
    override fun getItemCount(): Int = modelList.size

    // 위치의 데이터의 타입
    override fun getItemViewType(position: Int) = modelList[position].type.ordinal

    // 뷰홀더 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModelViewHolder<M> {
        // mapper를 통해 CellType에 맞는 뷰홀더 생성
        return ModelViewHolderMapper.map(parent, CellType.values()[viewType], viewModel)
    }

    // 뷰홀더에 데이터 바인딩
    @Suppress("UNCHECKED_CAST")
    override fun onBindViewHolder(holder: ModelViewHolder<M>, position: Int) {
        with(holder) {
            // 데이터 바인딩
            bindData(modelList[position] as M)
            // 뷰를 만들고 뷰에대한 리스너를 설정
            bindViews(modelList[position] as M, adapterListener)
        }
    }

    // 리사이클러뷰의 데이터 submit
    override fun submitList(list: MutableList<Model>?) {
        // 리스트가 null이 아니여야 어탭터 리스트 데이터를 갱신
        list?.let { modelList = it }
        super.submitList(list)
    }

    fun removeAt(position: Int) {
        // 서버에서 글을 삭제함  2/10 할거
        Log.e("model", modelList[position].toString())
        val response = (viewModel as TabWritingViewModel).deleteWriting(modelList[position])
        response.let {
            // 반환 확인
        }
        // 로컬에서도 삭제하고
        modelList.removeAt(position)
        // 새로고침한다. 여기에서 없어도 되는지 확인 필요
        notifyItemRemoved(position)
    }

    fun notifyEditItem(activity: MainActivity, position: Int) {
        // update mode
        activity.showFragment(DailyWritingFragment().apply {
            arguments = bundleOf(
                Pair("flag",2),
                Pair("title",(modelList[position] as DailyModel).title),
                Pair("imageUrl",(modelList[position] as DailyModel).imageUrl),
                Pair("currdate",getToday()),
                Pair("content",(modelList[position] as DailyModel).content),
                Pair("index", (modelList[position] as DailyModel).id)
            )
        },"2")
//        notifyItemChanged(position) // Notify any registered observers that the item at position has changed.
    }
    @SuppressLint("SimpleDateFormat")
    private fun getToday():String {
        val sdf = SimpleDateFormat("yyyy-MM-dd E요일")
        val cal = Calendar.getInstance().time
        return sdf.format(cal).toString()
    }
}

