package com.axxessapp.adapters

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions





object ImageUrlBindingAdapter {
    @JvmStatic
    @BindingAdapter("android:img")
    fun setImageUrl(view: ImageView, url: String) {
        val requestOptions = RequestOptions()
        requestOptions.placeholder(android.R.drawable.ic_menu_gallery)
        requestOptions.error(android.R.drawable.ic_menu_gallery)
        Glide.with(view.context)
                .load(url)
                .apply(requestOptions)
                .into(view)
    }
}