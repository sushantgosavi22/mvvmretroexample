package com.axxessapp.adapters


import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.axxessapp.ShapeModelData
import com.axxessapp.adapters.ImageUrlBindingAdapter.setImageUrl
import com.axxessapp.databinding.AppItemBinding
import java.util.*

class ShapeAdapter(private var listener: OnShapeClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val mCategoryList = ArrayList<ShapeModelData>()

    fun setAppList(categoryModel: List<ShapeModelData>) {
        mCategoryList.clear()
        mCategoryList.addAll(categoryModel)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        Log.d("LIST_SIZE","" + mCategoryList.size)
        return mCategoryList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val appInfo = mCategoryList[position]
        (holder as ShapeAdapter.RecyclerHolderCatIcon).bind(appInfo, listener)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val applicationBinding = AppItemBinding.inflate(layoutInflater, parent, false)
        return RecyclerHolderCatIcon(applicationBinding)
    }

    interface OnShapeClickListener {
        fun onShapeClick(data: ShapeModelData?)
    }


    inner class RecyclerHolderCatIcon(private var applicationBinding: AppItemBinding) : RecyclerView.ViewHolder(applicationBinding.root) {
        fun bind(appInfo: ShapeModelData, listener: OnShapeClickListener?) {
            applicationBinding.appModelShape  = appInfo
            setImageUrl(applicationBinding.image, appInfo.link)
            applicationBinding.itemCard.setOnClickListener {
                listener?.onShapeClick(appInfo)
            }
        }
    }
}