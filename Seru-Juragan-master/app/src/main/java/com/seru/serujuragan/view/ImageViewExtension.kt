package com.seru.serujuragan.view

import android.content.Context
import android.util.Log
import android.widget.ImageView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.request.RequestOptions
import com.seru.serujuragan.config.AppPreference
import com.seru.serujuragan.util.Constants
import java.io.File

fun ImageView.setRemoteImage(url: String, applyCircle: Boolean = false) {
    val glide = Glide.with(this).load(url)
    if (applyCircle) {
        glide.apply(RequestOptions.circleCropTransform()).into(this)
    } else {
        glide.into(this)
    }
}

fun ImageView.setLocalImage(file: File, applyCircle: Boolean = false) {
    val glide = Glide.with(this).load(file)
    if (applyCircle) {
        glide.apply(RequestOptions.circleCropTransform()).into(this)
    } else {
        glide.into(this)
    }
}

fun ImageView.setServerImage(imageId: String, context: Context){

    val circularProgressDrawable = CircularProgressDrawable(context)
    circularProgressDrawable.strokeWidth = 5f
    circularProgressDrawable.centerRadius = 30f
    circularProgressDrawable.start()

    val contentType = Constants.CONTENT_TYPE_IMAGE
    val token = AppPreference.token
    val imageUrl = Constants.BASE_URL + "file/image/download?id=" + imageId

    val glideUrl = GlideUrl(
        imageUrl,
        LazyHeaders.Builder()
            .addHeader("Content-Type", contentType)
            .addHeader("Authorization", token)
            .build()
    )
    Log.i("Value glide url", "$glideUrl")
    Glide.with(this)
        .load(glideUrl).timeout(60000)
        .diskCacheStrategy(DiskCacheStrategy.NONE)
        .skipMemoryCache(true)
        .placeholder(circularProgressDrawable)
        .into(this)
}
