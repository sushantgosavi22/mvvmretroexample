package com.axxessapp.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.axxessapp.Model.CommentModel
import com.axxessapp.R
import kotlinx.android.synthetic.main.comment_item.view.*
import java.util.*

class CommentAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val mCategoryList = ArrayList<CommentModel>()

    fun setCommentList(categoryModel: List<CommentModel>) {
        mCategoryList.clear()
        mCategoryList.addAll(categoryModel)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return mCategoryList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val appInfo = mCategoryList[position]
       // (holder as CommentAdapter.RecyclerHolderShapeDetails).bind(appInfo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        var view =  layoutInflater.inflate(R.layout.comment_item,parent)
        return RecyclerHolderShapeDetails(view)
    }

    inner class RecyclerHolderShapeDetails(private var view: View) : RecyclerView.ViewHolder(view) {
        fun bind(appInfo: CommentModel?) {
            appInfo?.let {
                view.tvComment.text = appInfo?.comment
            }
        }
    }
}