package com.bonsaizen.bonsaizenapp.ui.addbonsai

import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bonsaizen.bonsaizenapp.R
import com.squareup.picasso.Picasso

class ImageSliderAdapter(
    private val imageUris: List<Uri>,
    private val viewPager: ViewPager2,
) : RecyclerView.Adapter<ImageSliderAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.iv_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image_slider, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        if (imageUris.isNotEmpty()) {
            Picasso.get()
                .load(imageUris[position])
                .rotate(90f)
                .placeholder(R.drawable.ic_image)
                .into(holder.imageView)


            viewPager.background = null
        } else {

            viewPager.setBackgroundColor(Color.WHITE)
        }
    }

    override fun getItemCount(): Int {
        return imageUris.size
    }
}