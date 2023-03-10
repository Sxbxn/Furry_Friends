package com.k_bootcamp.furry_friends.extension

import android.graphics.Bitmap
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.k_bootcamp.furry_friends.R

// fade in out 애니메이션 빌더
private val factory = DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build()

// 클리어
fun ImageView.clear() = Glide.with(context).clear(this)

fun ImageView.load(url: String, corner: Float = 0f, scaleType: Transformation<Bitmap> = FitCenter()) {
    Glide.with(this)
        .load(url)
        // fade in animation
        .transition(DrawableTransitionOptions.withCrossFade(factory))
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .placeholder(R.drawable.add_screen_image_placeholder)
        // 스케일
        .transform(scaleType)
        .override(Target.SIZE_ORIGINAL)
        .apply {
            if (corner > 0) RoundedCorners(corner.fromDpToPx())
        }
        .into(this)
}