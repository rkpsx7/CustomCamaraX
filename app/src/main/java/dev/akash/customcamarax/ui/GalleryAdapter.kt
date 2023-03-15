package dev.akash.customcamarax.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.akash.customcamarax.databinding.GalleryItemBinding
import dev.akash.customcamarax.utils.loadImage

class GalleryAdapter : RecyclerView.Adapter<GalleryAdapter.GalleryHV>() {

    var imagesList: List<String> = listOf()

    inner class GalleryHV(private val binding: GalleryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(url:String) {
            binding.apply {
                ivImagePrev.loadImage(url)
                tvUrl.text = url
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryHV {
        return GalleryHV(
            GalleryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount() = imagesList.size

    override fun onBindViewHolder(holder: GalleryHV, position: Int) {
        holder.bind(imagesList[position])
    }
}