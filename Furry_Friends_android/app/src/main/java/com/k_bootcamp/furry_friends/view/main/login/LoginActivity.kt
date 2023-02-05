package com.k_bootcamp.furry_friends.view.main.login

import android.content.Context
import android.content.Intent
import androidx.activity.viewModels
import androidx.core.widget.doOnTextChanged
import com.fc.baeminclone.screen.base.BaseActivity
import com.k_bootcamp.Application
import com.k_bootcamp.furry_friends.databinding.ActivityLogInBinding
import com.k_bootcamp.furry_friends.model.user.LoginUser
import com.k_bootcamp.furry_friends.util.etc.*
import com.k_bootcamp.furry_friends.view.MainActivity
import com.k_bootcamp.furry_friends.view.main.signin.SignInActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity: BaseActivity<LoginViewModel, ActivityLogInBinding>() {

    override val viewModel: LoginViewModel by viewModels()
    private lateinit var loading: LoadingDialog
    private lateinit var user: LoginUser

    override fun getViewBinding(): ActivityLogInBinding = ActivityLogInBinding.inflate(layoutInflater)
    override fun observeData() {
    }

    override fun initViews() {
        loading = LoadingDialog(this)
        loginCheck()
        initDialog()
        initLoginButton()
    }

    private fun initLoginButton() = with(binding) {
        // 회원가입 페이지 이동
        buttonLogin.setOnClickListener {
            startActivity(SignInActivity.newIntent(baseContext).apply {
                this.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            })
        }

        editTextLoginId.doOnTextChanged { text, _, _, _ ->
            initValidate(emailInputLayout)
            viewModel.emailLiveData.value = text.toString()
        }
        editTextLoginPwd.doOnTextChanged { text, _, _, _ ->
            viewModel.pwdLiveData.value = text.toString()
        }
        buttonLogin.setOnClickListener {
            user = LoginUser(editTextLoginId.text.toString(), editTextLoginPwd.text.toString())

            if (!validateId(emailInputLayout, editTextLoginId.text.toString())) {
                shake(editTextLoginId, this@LoginActivity)
            } else {
                // 로그인 요청
//                if (getConnectivityStatus(applicationContext)) {
                    requestLogin(user)
//                }
            }
        }
        gotoSignInTextView.setOnClickListener {
            startActivity(SignInActivity.newIntent(baseContext))
            finish()
        }
    }

    private fun initDialog() {
        // 요청 취소
        loading.cancelButton().setOnClickListener {
            loading.setInvisible()
        }
        // 요청 다시시도
        loading.retryButton().setOnClickListener {
            loading.setInvisible()
            requestLogin(user)
        }

        // 라이브데이터 참조하여 값이없으면 버튼 disable
        viewModel.isValidLiveData.observe(this) { isValid ->
            binding.buttonLogin.isEnabled = isValid
        }
    }

    private fun requestLogin(user: LoginUser) {
        viewModel.getSessionRequest(user)
        viewModel.isLogin.observe(this@LoginActivity) { response ->
            when (response) {
                is LoginState.Success -> {
                    // success code
                    Application.prefs.apply {
                        session = response.session
//                        userId = response.data?.get("userId")
                    }
//                    Application.prefs.session?.let { Application.prefs.userId?.let { it1 -> getInfo(it, it1) } }
                }
                is LoginState.Error -> {
                    // error code
                    loading.setError()
                }
                is LoginState.Loading -> {
                    // loading code
                    loading.setVisible()
                }
            }
        }
    }

    // 자동로그인 체크
    private fun loginCheck() {
        // 세션이 앱에 저장되어있다면 자동 로그인
        if (!Application.prefs.session.isNullOrBlank()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun getInfo(sessionId: String) {
        viewModel.getUserInfo(sessionId)
        viewModel.getInfo.observe(this@LoginActivity) { response ->
            when (response) {
                null -> {
                    // error code
                    loading.setError()
                }
                else -> {
                    // success code
                    Application.prefs.apply {
                        this.email = response
                    }
                    startActivity(Intent(this, MainActivity::class.java).apply {
                        this.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                        this.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    })

                    // finish 전에 dismiss 해야 에러 안남
                    loading.dismiss()
                    finish()
                }
            }
        }
    }

    companion object {
        fun newIntent(context: Context) = Intent(context, LoginActivity::class.java)
    }
}