package com.k_bootcamp.furry_friends.util.dialog

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.Button
import android.app.AlertDialog
import android.view.View
import com.k_bootcamp.furry_friends.R

class CustomAlertDialog(context: Context) : AlertDialog(context) {

    private lateinit var dialog: AlertDialog

    fun init(title: String, positiveText: String, negativeText: String, run:() -> Unit) {
        dialog = Builder(context, R.style.dialog)
            .setMessage(title)
            .setPositiveButton(positiveText) { _, _ -> run() }
            .setNegativeButton(negativeText) { _, _ -> }
            .create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(true)
        dialog.show()
    }
    fun initWithView(title: String, viewId: View){

        dialog = Builder(context, R.style.dialog)
            .setMessage(title)
            .setView(viewId)
            .setPositiveButton("확인") { _, _ -> }
            .create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(true)
        dialog.show()
    }

    fun getPositive(): Button {
        return dialog.getButton(BUTTON_POSITIVE)
    }

    fun getNegative(): Button {
        return dialog.getButton(BUTTON_NEGATIVE)
    }

    fun exit() {
        dialog.dismiss()
    }
}