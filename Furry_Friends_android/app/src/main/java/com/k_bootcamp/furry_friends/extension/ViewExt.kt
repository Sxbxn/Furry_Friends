package com.k_bootcamp.furry_friends.extension

import android.content.Context
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.k_bootcamp.furry_friends.R
import com.k_bootcamp.furry_friends.util.provider.ResourcesProviderImpl

fun View.toVisible() {
    visibility = View.VISIBLE
}

fun View.toGone() {
    visibility = View.GONE
}

fun View.appearSnackBar(context: Context, message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_SHORT).apply{
        setTextColor(ResourcesProviderImpl(context).getColor(R.color.white))
        setBackgroundColor(ResourcesProviderImpl(context).getColor(R.color.main_color))
    }.show()
}