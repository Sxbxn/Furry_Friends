package com.k_bootcamp.furry_friends.view.main.signin

import android.content.Context
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.k_bootcamp.furry_friends.view.base.BaseViewModel
import com.k_bootcamp.furry_friends.R
import com.k_bootcamp.furry_friends.data.repository.user.UserRepository
import com.k_bootcamp.furry_friends.model.user.SignInUser
import com.k_bootcamp.furry_friends.util.etc.IoDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val userRepository: UserRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @ApplicationContext private val context: Context
): BaseViewModel() {
    private val _state: MutableLiveData<SignInState> = MutableLiveData()
    val state: MutableLiveData<SignInState>
        get() = _state
    val idLiveData: MutableLiveData<String> = MutableLiveData()
    val pwdLiveData: MutableLiveData<String> = MutableLiveData()
    val repwdLiveData: MutableLiveData<String> = MutableLiveData()
    val emailLiveData: MutableLiveData<String> = MutableLiveData()
    val isValidLiveData: MediatorLiveData<Boolean> = MediatorLiveData<Boolean>().apply {
        // 입력이 valid 하지않으면 버튼 disabled
        this.value = false

        addSource(idLiveData) {
            this.value = validateForm()
        }

        addSource(pwdLiveData) {
            this.value = validateForm()
        }

        addSource(repwdLiveData) {
            this.value = validateForm()
        }

        addSource(emailLiveData) {
            this.value = validateForm()
        }

    }
    private fun validateForm(): Boolean {
        return !emailLiveData.value.isNullOrBlank() && !pwdLiveData.value.isNullOrBlank()
                && !repwdLiveData.value.isNullOrBlank() && !emailLiveData.value.isNullOrBlank()
    }

    fun signInUser(user: SignInUser) {
        _state.value = SignInState.Loading
        viewModelScope.launch(ioDispatcher)  {
            val response = userRepository.signInUser(user)
            response?.let{
                _state.postValue(SignInState.Success(it))
            } ?: kotlin.run {
                _state.postValue(SignInState.Error(context.getString(R.string.failed_sign_in)))
            }
        }
    }
}