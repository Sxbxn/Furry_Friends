package com.k_bootcamp.furry_friends.view.main.signin

import android.content.Context
import android.content.Intent
import androidx.activity.viewModels
import androidx.core.widget.doOnTextChanged
import com.k_bootcamp.furry_friends.view.base.BaseActivity
import com.k_bootcamp.furry_friends.databinding.ActivitySignInBinding
import com.k_bootcamp.furry_friends.extension.appearSnackBar
import com.k_bootcamp.furry_friends.extension.toast
import com.k_bootcamp.furry_friends.model.user.SignInUser
import com.k_bootcamp.furry_friends.util.etc.*
import com.k_bootcamp.furry_friends.view.main.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignInActivity: BaseActivity<SignInViewModel, ActivitySignInBinding>() {

    override val viewModel: SignInViewModel by viewModels()
    private lateinit var loading: LoadingDialog
    private lateinit var user: SignInUser
    private lateinit var email: String
    private lateinit var pwd: String
    private lateinit var re_pwd: String
    private lateinit var id: String

    override fun getViewBinding(): ActivitySignInBinding = ActivitySignInBinding.inflate(layoutInflater)

    override fun observeData() {
    }

    override fun initViews() {
        loading = LoadingDialog(this)
        initDialog()
        initSignInButton()
    }

    private fun initDialog() {
        // 요청 취소
        loading.cancelButton().setOnClickListener {
            loading.setInvisible()
        }
        // 요청 다시시도
        loading.retryButton().setOnClickListener {
            loading.setInvisible()
            requestSignIn(user)
        }

        viewModel.isValidLiveData.observe(this) { isValid ->
            binding.buttonSignIn.isEnabled = isValid
        }
    }
    private fun initSignInButton() = with(binding) {
        // 로그인 페이지 이동
        buttonLogIn.setOnClickListener {
            startActivity(LoginActivity.newIntent(baseContext).apply {
                this.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            })
            finish()
        }

        editTextSigninId.doOnTextChanged { text, _, _, _ ->
            initValidate(emailInputLayout)
            viewModel.idLiveData.value = text.toString()
            id = text.toString()
        }

        editTextSigninPwd.doOnTextChanged { text, _, _, _ ->
            initValidate(pwdInputLayout)
            viewModel.pwdLiveData.value = text.toString()
            pwd = text.toString()
        }

        editTextSigninPwdChk.doOnTextChanged { text, _, _, _ ->
            initValidate(rePwdInputLayout)
            viewModel.repwdLiveData.value = text.toString()
            re_pwd = text.toString()
        }

        editTextSigninName.doOnTextChanged { text, _, _, _ ->
            initValidate(nameInputLayout)
            viewModel.emailLiveData.value = text.toString()
            email = text.toString()
        }
        buttonSignIn.setOnClickListener {
            user = SignInUser(id, pwd, email)
            if (checkValidation()) {
//                if (getConnectivityStatus(applicationContext)) {
                    // 회원가입 요청
                    requestSignIn(user)
//                }
            }
        }
    }

    // 회원가입 요청
    private fun requestSignIn(user: SignInUser) {
        viewModel.signInUser(user)
        viewModel.state.observe(this) { response ->
            when (response) {
                is SignInState.Success -> {
                    // success code
                    toast("회원가입에 성공했습니다 :)")
                    loading.dismiss()
                    startActivity(Intent(this, LoginActivity::class.java).apply {
                        this@SignInActivity.intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                    })
                }
                is SignInState.Error -> {
                    // error code
                    loading.setError()
                    when(response.message) {
                        "user id taken" -> binding.root.appearSnackBar(this@SignInActivity,"아이디가 중복되었습니다.")
                        "email already exists" -> binding.root.appearSnackBar(this@SignInActivity,"이메일이 이미 존재합니다.")
                    }
                }
                is SignInState.Loading -> {
                    // loading code
                    loading.setVisible()
                }
            }
        }
    }
    private fun checkValidation(): Boolean {
        var check = true
        with(binding) {
            if (!validateId(emailInputLayout, id)) {
                shake(editTextSigninId, this@SignInActivity)
                check = false
            }
            if (!validateSignInPassword(pwdInputLayout, pwd)) {
                shake(editTextSigninPwd, this@SignInActivity)
                check = false
            }
            if (!validateSignInRePassword(rePwdInputLayout, pwd, re_pwd)) {
                shake(editTextSigninPwdChk, this@SignInActivity)
                check = false
            }
            if (!validateEmail(nameInputLayout, email)) {
                shake(editTextSigninName, this@SignInActivity)
                check = false
            }
            return check
        }
    }
    companion object {
        fun newIntent(context: Context) = Intent(context, SignInActivity::class.java)
    }
}