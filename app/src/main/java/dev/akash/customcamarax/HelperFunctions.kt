package dev.akash.customcamarax

import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import java.io.File

fun AppCompatImageView.loadImage(url:String?){
    Glide.with(this.context)
        .load(url)
        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
        .into(this)
}

fun AppCompatImageView.loadImage(image: File?){
    Glide.with(this.context)
        .load(image)
        .diskCacheStrategy(DiskCacheStrategy.NONE)
        .into(this)
}

fun View.visibilityGone(){
    this.visibility = View.GONE
}


fun View.visibilityVisible(){
    this.visibility = View.VISIBLE
}