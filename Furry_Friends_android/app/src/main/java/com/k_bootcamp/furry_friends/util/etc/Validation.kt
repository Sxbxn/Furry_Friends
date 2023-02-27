package com.k_bootcamp.furry_friends.util.etc

import android.content.Context
import android.util.Patterns
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.EditText
import com.google.android.material.textfield.TextInputLayout
import com.k_bootcamp.furry_friends.R


fun shake(editText: EditText, context: Context) {
    val animation: Animation = AnimationUtils.loadAnimation(context, R.anim.shake)
    editText.startAnimation(animation)
    editText.requestFocus()
}

fun denySemiColon(str: String): Boolean = !str.contains(";")

fun validateEmpty(inputLayout: TextInputLayout, str: String): Boolean {
    return if (str.isEmpty() && denySemiColon(str)) {
        inputLayout.error = "입력되지 않았습니다."
        false
    } else {
        initValidate(inputLayout)
        true
    }
}

fun validateLoginEmail(inputLayout: TextInputLayout, email: String): Boolean {
    return if (email.isEmpty() && denySemiColon(email)) {
        inputLayout.error = "이메일이 입력되지 않았습니다."
        false
    } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches() && denySemiColon(email)) {
        inputLayout.error = "아이디가 이메일 형식이 아닙니다."
        false
    } else {
        initValidate(inputLayout)
        true
    }
}

fun validateEmail(inputLayout: TextInputLayout, email: String): Boolean {
    return if (email.isEmpty()) {
        inputLayout.error = "아이디가 입력되지 않았습니다."
        false
    } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches() && denySemiColon(email)) {
        inputLayout.error = "아이디가 이메일 형식이 아닙니다."
        false
    } else {
        initValidate(inputLayout)
        true
    }
}


fun validateSignInPassword(inputLayout: TextInputLayout, pwd: String): Boolean {
    // 최소 8자리이상, 숫자, 특수문자 각각최소1개이상
    val pwdValidation = Regex("""^(?=.*?[0-9])(?=.*?[#?!@\$ %^&*-]).{8,}$""")
    return if (pwd.isEmpty()) {
        inputLayout.error = "비밀번호가 입력되지 않았습니다."
        false
    } else if (!pwd.matches(pwdValidation) && denySemiColon(pwd)) {
        inputLayout.error = "비밀번호는 최소 8자리이상, 숫자, 특수문자 각각 1개이상이여야 합니다."
        false
    } else {
        initValidate(inputLayout)
        true
    }
}

fun validateSignInRePassword(inputLayout: TextInputLayout, pwd: String, re_pwd: String): Boolean {
    return if (pwd != re_pwd && denySemiColon(re_pwd)) {
        inputLayout.error = "비밀번호가 일치하지 않습니다."
        false
    } else {
        initValidate(inputLayout)
        true
    }
}

fun validateId(inputLayout: TextInputLayout, id: String): Boolean {
    val idValidation = Regex("""^[a-zA-Z0-9]{2,20}$""")
    return if (id.isEmpty()) {
        inputLayout.error = "아이디가 입력되지 않았습니다."
        false
    } else if (!id.matches(idValidation) && denySemiColon(id)) {
        inputLayout.error = "아이디는 영문, 숫자 20자까지만 가능합니다."
        false
    } else {
        initValidate(inputLayout)
        true
    }
}

fun initValidate(inputLayout: TextInputLayout) {
    inputLayout.apply {
        error = null
        isErrorEnabled = false
    }
}