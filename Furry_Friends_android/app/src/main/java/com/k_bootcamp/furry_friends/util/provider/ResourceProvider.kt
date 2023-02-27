package com.k_bootcamp.furry_friends.util.provider

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import androidx.annotation.ColorRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes

interface ResourceProvider {
    fun getString(@StringRes resId:Int): String

    fun getString(@StringRes resId:Int, vararg formArgs:Any): String

    fun getColor(@ColorRes resId:Int):Int

    fun getColorStateList(@ColorRes resId:Int): ColorStateList

    fun getDrawable(@IdRes id: Int): Drawable?
}