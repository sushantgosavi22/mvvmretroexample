package com.axxessapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.axxessapp.Model.CommentModel
import com.axxessapp.R
import com.axxessapp.Utils

class CommentShowAdapter : RecyclerView.Adapter<CommentShowAdapter.ItemHolder>() {
    var items = ArrayList<CommentModel?>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        return ItemHolder(LayoutInflater.from(parent.context).inflate(R.layout.comment_item, parent, false))
    }

    fun setList(list : ArrayList<CommentModel?>){
        items = list;
    }
    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.textField.text = items[position]?.comment
        holder.tvDate.text = Utils.getLocalFormatterDate(items[position]?.date)
    }

    inner class ItemHolder(view: View): RecyclerView.ViewHolder(view) {
        var textField: TextView = view.findViewById(R.id.tvComment) as TextView
        var tvDate: TextView = view.findViewById(R.id.tvDate) as TextView
    }
}