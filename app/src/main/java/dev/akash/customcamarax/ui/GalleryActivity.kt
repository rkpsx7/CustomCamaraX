package dev.akash.customcamarax.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import dev.akash.customcamarax.*
import dev.akash.customcamarax.databinding.ActivityGalleryBinding
import dev.akash.customcamarax.utils.visibilityGone
import dev.akash.customcamarax.viewmodel.GalleryViewModel
import dev.akash.customcamarax.viewmodel.ViewModelFactory
import javax.inject.Inject

class GalleryActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModel: GalleryViewModel

    private var totalImages = 0


    private val galleryAdapter: GalleryAdapter by lazy {
        GalleryAdapter()
    }

    private lateinit var binding: ActivityGalleryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_gallery)
        (application as JukshioApplication).daggerAppComponent.inject(this)
        viewModel = ViewModelProvider(this, viewModelFactory)[GalleryViewModel::class.java]

        setUpViewPager()
        observeData()
    }

    private fun observeData() {
        viewModel.getAllImages()
        viewModel.imageUrlList.observe(this) {
            binding.progressBar.visibilityGone()
            if (it.isNotEmpty()) {
                galleryAdapter.imagesList = it
                totalImages = it.size
                binding.tvImageCounter.text = "1/$totalImages"
                galleryAdapter.notifyDataSetChanged()
            } else {
                Toast.makeText(this, "Please save new pictures to see here", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    private fun setUpViewPager() {
        binding.viewPager.apply {
            adapter = galleryAdapter
            registerOnPageChangeCallback(object : OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    binding.tvImageCounter.text = "${position + 1}/$totalImages"
                }
            })
        }
    }
}