package dev.akash.customcamarax.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.camera.core.ImageProxy
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import dev.akash.customcamarax.R
import java.io.File

fun AppCompatImageView.loadImage(url:String?){
    Glide.with(this.context)
        .load(url)
        .placeholder(R.drawable.loading_placeholder)
        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
        .into(this)
}

fun AppCompatImageView.loadImage(image: File?){
    Glide.with(this.context)
        .load(image)
        .placeholder(R.drawable.loading_placeholder)
        .diskCacheStrategy(DiskCacheStrategy.NONE)
        .into(this)
}

fun ImageProxy.convertImageProxyToBitmap(): Bitmap {
    val buffer = planes[0].buffer
    buffer.rewind()
    val bytes = ByteArray(buffer.capacity())
    buffer.get(bytes)
    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
}


fun View.visibilityGone(){
    this.visibility = View.GONE
}


fun View.visibilityVisible(){
    this.visibility = View.VISIBLE
}


/**
 * onCLickListener fn to prevent multiple miss-touch
 */
fun View.safeClick(action: () -> Unit) {
    this.setOnClickListener(object : SafeClickListener() {
        override fun onSafeClick(v: View?) {
            action.invoke()
        }
    })
}