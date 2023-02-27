package com.k_bootcamp.furry_friends.util.dialog

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import com.k_bootcamp.furry_friends.R
import com.k_bootcamp.furry_friends.util.etc.getCameraImage
import com.k_bootcamp.furry_friends.util.etc.getGalleryImage
import com.k_bootcamp.furry_friends.view.MainActivity
import com.shashank.sony.fancydialoglib.Animation
import com.shashank.sony.fancydialoglib.FancyAlertDialog

fun setFancyDialog(
    context: Context,
    mainActivity: MainActivity,
    permissionLauncher: ActivityResultLauncher<String>,
    getCameraImageLauncher: ActivityResultLauncher<Intent>,
    getGalleryImageLauncher: ActivityResultLauncher<Intent>
): FancyAlertDialog = FancyAlertDialog.Builder
        .with(context)
        .setBackgroundColor(Color.parseColor("#00BD56"))
        .setTitle("이미지 선택")
        .setMessage("사진 가져올 곳을 선택해주세요")
        .setPositiveBtnText("카메라")
        .onPositiveClicked {
            getCameraImage(mainActivity, permissionLauncher, getCameraImageLauncher)
            it.dismiss()
        }
        .setPositiveBtnBackgroundRes(R.color.main_color)
        .setNegativeBtnText("갤러리")
        .onNegativeClicked {
            getGalleryImage(mainActivity, permissionLauncher, getGalleryImageLauncher)
            it.dismiss()
        }
        .setNegativeBtnBackgroundRes(R.color.main_color)
        .isCancellable(true)
        .setAnimation(Animation.SLIDE)
        .setIcon(R.drawable.ic_image_36, View.VISIBLE)
        .build()


fun setAiFancyDialog(context: Context, runAiProfile:() -> Unit): FancyAlertDialog =
    FancyAlertDialog.Builder.with(context)
        .setBackgroundColor(Color.parseColor("#00BD56"))
        .setTitle("AI 프로필 이미지")
        .setMessage("AI가 표현한 반려동물을 만나보세요")
        .setPositiveBtnText("네!")
        .onPositiveClicked {
            runAiProfile()
            it.dismiss()
        }
        .setPositiveBtnBackgroundRes(R.color.main_color)
        .setNegativeBtnText("다음에..")
        .onNegativeClicked {
            it.dismiss()
        }
        .setNegativeBtnBackgroundRes(R.color.main_color)
        .isCancellable(true)
        .setAnimation(Animation.SLIDE)
        .setIcon(R.drawable.ic_image_36, View.VISIBLE)
        .build()

fun setWithDrawUser(context: Context, withDraw:() -> Unit): FancyAlertDialog =
    FancyAlertDialog.Builder.with(context)
        .setBackgroundColor(Color.parseColor("#00BD56"))
        .setTitle("회원탈퇴")
        .setMessage("탈퇴 시 모든 정보가 삭제됩니다")
        .setPositiveBtnText("예")
        .onPositiveClicked {
            withDraw()
            it.dismiss()
        }
        .setPositiveBtnBackgroundRes(R.color.main_color)
        .setNegativeBtnText("아니오")
        .onNegativeClicked {
            it.dismiss()
        }
        .setNegativeBtnBackgroundRes(R.color.main_color)
        .isCancellable(true)
        .setAnimation(Animation.SLIDE)
        .setIcon(R.drawable.ic_delete_24, View.VISIBLE)
        .build()
