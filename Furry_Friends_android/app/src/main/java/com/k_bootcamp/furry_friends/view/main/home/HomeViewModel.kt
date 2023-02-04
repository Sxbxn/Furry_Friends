package com.k_bootcamp.furry_friends.view.main.home

import com.fc.baeminclone.screen.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(

): BaseViewModel() {
    fun getAnimalInfo() {
        // 해당 유저의 등록된 반려동물 정보를 가져와서 반환함
    }

}