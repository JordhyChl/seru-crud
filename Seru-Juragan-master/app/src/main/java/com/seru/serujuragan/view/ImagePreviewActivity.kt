package com.seru.serujuragan.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.seru.serujuragan.R
import com.seru.serujuragan.config.AppPreference
import com.seru.serujuragan.util.Constants
import kotlinx.android.synthetic.main.activity_image_preview.*

class ImagePreviewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_preview)

        val imgUrl = intent.getStringExtra("imgUrl")
        val imgTitle = intent.getStringExtra("imgTitle")

        btnBackImgPrev.setOnClickListener {
            onBackPressed()
        }
        if (imgTitle != null) {
            if (imgTitle.isNotEmpty())
                tvImgPrevTitle.text = imgTitle
        }

        if (imgUrl != null) {
            if (imgUrl.isNotEmpty()) {
                loadPreviewImage(imgUrl, this)
            }
        }
    }

    @SuppressLint("NewApi")
    private fun loadPreviewImage(imageId: String, context: Context){

        val circularProgressDrawable = CircularProgressDrawable(this@ImagePreviewActivity)
        circularProgressDrawable.strokeWidth = 10f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.setColorSchemeColors(getColor(R.color.colorWhite1))
        //circularProgressDrawable.backgroundColor = getColor(R.color.colorWhite1)
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
            .into(imgPreview)
    }

}
